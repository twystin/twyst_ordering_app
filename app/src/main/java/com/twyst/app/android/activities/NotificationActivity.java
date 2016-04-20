package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.NotificationAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.NotificationData;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationActivity extends BaseActionActivity {
    private RecyclerView notifyRecyclerView;
    NotificationAdapter adapter;
    private boolean fromDrawer, fromPushNotificationClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setupToolBar();

        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        notifyRecyclerView = (RecyclerView) findViewById(R.id.notifyRecyclerView);
        notifyRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false);
        notifyRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new NotificationAdapter();
        notifyRecyclerView.setAdapter(adapter);
        getNotifications();

    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    private void getNotifications() {
        HttpService.getInstance().getNotification(UtilMethods.getUserToken(NotificationActivity.this), new Callback<BaseResponse<ArrayList<NotificationData>>>() {
            @Override
            public void success(BaseResponse<ArrayList<NotificationData>> notificationDataBaseResponse, Response response) {
                hideProgressHUDInLayout();
                if (notificationDataBaseResponse.isResponse()) {
                    List<NotificationData> notificationData = notificationDataBaseResponse.getData();
                    if (notificationData.size() > 0) {
                        adapter.setItems(notificationData);
                        adapter.notifyDataSetChanged();

                        findViewById(R.id.blankDataLayout).setVisibility(View.GONE);

                    } else {
                        findViewById(R.id.ivNoData).setBackgroundResource(R.drawable.no_notification);
                        ((TextView) findViewById(R.id.tvNoData)).setText("You have no notifications!");
                        findViewById(R.id.blankDataLayout).setVisibility(View.VISIBLE);
//                        Toast.makeText(NotificationActivity.this,"You have no notifications!",Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(NotificationActivity.this, notificationDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    UtilMethods.handleRetrofitError(NotificationActivity.this, error);
                } else {
                    UtilMethods.buildAndShowSnackbarWithMessage(NotificationActivity.this, "Unable to load notifications!");
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fromDrawer = intent.getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        fromPushNotificationClicked = intent.getBooleanExtra(AppConstants.INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED, false);
        if (fromPushNotificationClicked) {
            getNotifications();
        }

    }

}
