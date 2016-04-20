package com.twyst.app.android.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.callback.BitmapAjaxCallback;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twyst.app.android.R;
import com.twyst.app.android.TwystApplication;
import com.twyst.app.android.activities.NotificationActivity;
import com.twyst.app.android.activities.OrderTrackingActivity;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by satish on 03/01/15.
 */

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
//    public static final int NOTIFICATION_ID = 1;
    //private NotificationManager mNotificationManager;
    //NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(getClass().getSimpleName(), "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(getClass().getSimpleName(), "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                Log.i(getClass().getSimpleName(), "Received: " + extras.toString());
                if (!TextUtils.isEmpty(extras.getString(OrderTrackingState.ORDER_ID))) {
                    sendOrderTrackingNotification(extras);
                } else {
                    sendNotification(extras);
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendOrderTrackingNotification(Bundle extras) {
        String time = extras.getString(OrderTrackingState.TIME);
        String orderID = extras.getString(OrderTrackingState.ORDER_ID);
        String state = extras.getString(OrderTrackingState.STATE);
        String message = extras.getString(OrderTrackingState.MESSAGE);

        OrderTrackingState.addToList(orderID, state, message, time, this);

        // If OrderTrackingActivity in foreground, refresh the list
        Activity currentActivity = ((TwystApplication) this.getApplicationContext()).getCurrentActivity();
        if (currentActivity != null) {
            OrderTrackingActivity orderTrackingActivity = (OrderTrackingActivity) currentActivity;
            orderTrackingActivity.refreshListServer(orderID);
        }


        Intent notificationIntent = new Intent(this, OrderTrackingActivity.class);
        notificationIntent.putExtra(AppConstants.INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED, true);
        notificationIntent.putExtra(AppConstants.INTENT_ORDER_ID, orderID);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 10, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        new GeneratePictureStyleNotification(this, extras, contentIntent).execute();
    }

    private void sendNotification(Bundle extras) {
//        Intent notificationIntent = new Intent(this, NotificationActivity.class);
//        notificationIntent.putExtra(AppConstants.INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED, true);

        PackageManager manager = this.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

        new GeneratePictureStyleNotification(this, extras, contentIntent).execute();
    }

    private int getNotificationCount() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int lastCount = prefs.getInt(AppConstants.PREFERENCE_NOTIFICATION_COUNT, 0);
        prefs.edit().putInt(AppConstants.PREFERENCE_NOTIFICATION_COUNT, lastCount + 1).apply();
        return lastCount;
    }

    public class GeneratePictureStyleNotification extends AsyncTask<Void, Void, Bitmap> {
        private Context mContext;
        private Bundle mExtras;
        private PendingIntent mPendingIntent;

        public GeneratePictureStyleNotification(Context context, Bundle extras, PendingIntent pendingIntent) {
            super();
            this.mContext = context;
            this.mExtras = extras;
            this.mPendingIntent = pendingIntent;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String imageUrl = mExtras.getString("image");

            if (TextUtils.isEmpty(imageUrl)) {
                return null;
            }

            InputStream in;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            String title = mExtras.getString("title");
            String message = mExtras.getString("message");

            String notificationTitle = TextUtils.isEmpty(title) ? getString(R.string.app_name) : title;

            NotificationCompat.Style style = null;
            if (result == null) {
                style = new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(notificationTitle);
            } else {
                style = new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle(notificationTitle)
                        .setSummaryText(message)
                        .bigPicture(result);
            }

            if (style != null) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                        .setContentIntent(mPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_stat_notify)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setColor(getResources().getColor(R.color.app_accent_color))
                        .setStyle(style)
                        .setContentTitle(notificationTitle)
                        .setContentText(message);

                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(getNotificationCount(), mBuilder.build());
            }

        }
    }
}
