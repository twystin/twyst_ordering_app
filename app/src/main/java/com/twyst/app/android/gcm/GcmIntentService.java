package com.twyst.app.android.gcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
                    sendNotification(extras.getString("title"), extras.getString("message"), extras.getString("url"));
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
        String title = extras.getString(OrderTrackingState.TITLE);
        String message = extras.getString(OrderTrackingState.MESSAGE);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, OrderTrackingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationIntent.putExtra(AppConstants.INTENT_ORDER_ID, orderID);

//        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        String ll = "";
//        String orderTrackingClass = this.getPackageName() + ".activities.OrderTrackingActivity";
//        if (cn.getClassName().equals(orderTrackingClass)) {
//            // OrderTrackingActivity in foreground, refresh the list
//        }

        Activity currentActivity = ((TwystApplication) this.getApplicationContext()).getCurrentActivity();
        if (currentActivity != null) {
            OrderTrackingActivity orderTrackingActivity = (OrderTrackingActivity) currentActivity;
            orderTrackingActivity.refreshList();
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String notificationTitle = TextUtils.isEmpty(title) ? getString(R.string.app_name) : title;

        NotificationCompat.Style style = new NotificationCompat.BigTextStyle()
                .bigText(message)
                .setBigContentTitle(notificationTitle);

        if (style != null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setColor(getResources().getColor(R.color.app_accent_color))
                    .setStyle(style)
                    .setContentTitle(notificationTitle)
                    .setContentText(message);

            mNotificationManager.notify(getNotificationCount(), mBuilder.build());
        }
    }

    private void sendNotification(String title, String message, String url) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra(AppConstants.INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED, true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String notificationTitle = TextUtils.isEmpty(title) ? getString(R.string.app_name) : title;

        NotificationCompat.Style style = null;
        if (TextUtils.isEmpty(url)) {
            style = new NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setBigContentTitle(notificationTitle);
        } else {
            BitmapAjaxCallback cb = new BitmapAjaxCallback();
            style = new NotificationCompat.BigPictureStyle()
                    .setBigContentTitle(notificationTitle)
                    .setSummaryText(message)
                    .bigPicture(cb.url(url).getResult());
        }


        if (style != null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)

                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))

                    .setColor(getResources().getColor(R.color.app_accent_color))

                    .setStyle(style)
                    .setContentTitle(notificationTitle)
                    .setContentText(message);


            mNotificationManager.notify(getNotificationCount(), mBuilder.build());
        }

    }

    private int getNotificationCount() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int lastCount = prefs.getInt(AppConstants.PREFERENCE_NOTIFICATION_COUNT, 0);
        prefs.edit().putInt(AppConstants.PREFERENCE_NOTIFICATION_COUNT, lastCount + 1).apply();
        return lastCount;
    }
}
