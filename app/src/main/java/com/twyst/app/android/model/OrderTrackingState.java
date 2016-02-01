package com.twyst.app.android.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 1/27/2016.
 */
public class OrderTrackingState {
    // Order fields
    public static final String TIME = "time";
    public static final String ORDER_ID = "order_id";
    public static final String STATE = "state";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    //Order States
    public static final String STATE_PLACED = "PLACED";
    public static final String STATE_CANCELLED = "CANCELLED";
    public static final String STATE_REJECTED = "REJECTED";
    public static final String STATE_ACCEPTED = "ACCEPTED";
    public static final String STATE_DISPATCHED = "DISPATCHED";
    public static final String STATE_ASSUMED_DELIVERED = "ASSUMED_DELIVERED";
    public static final String STATE_NOT_DELIVERED = "NOT_DELIVERED";
    public static final String STATE_ABANDONED = "ABANDONED";
    public static final String STATE_DELIVERED = "DELIVERED";
    public static final String STATE_DEFAULT = "DEFAULT";

    // AMPM time
    public static final String ORDER_TIME_AM = "AM";
    public static final String ORDER_TIME_PM = "PM";

    private String time;
    private String ampm;
    private String message;
    private String orderState;
    private boolean isCurrent;

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public static ArrayList<OrderTrackingState> getInitialList(String orderID, Activity activity) {
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        ArrayList<OrderTrackingState> trackOrderStatesList = new ArrayList<>();
        String orderTrackingStateListString = sharedPreferences.getString(orderID, "");

        if (TextUtils.isEmpty(orderTrackingStateListString)) {
            //List is empty, get the list with default Order State (ORDER_PLACED)
            trackOrderStatesList.add(getDefaultOrderTrackingState(activity));
        } else {
            //Ordering states already saved, get the list with previous Ordering States
            Gson gson1 = new Gson();
            Type type = new TypeToken<List<OrderTrackingState>>() {
            }.getType();
            trackOrderStatesList = gson1.fromJson(orderTrackingStateListString, type);
        }

        Gson gson = new Gson();
        String json = gson.toJson(trackOrderStatesList);
        sharedPreferences.edit().putString(orderID, json);
        if (sharedPreferences.edit().commit()) {
            return trackOrderStatesList;
        }
        return trackOrderStatesList;
    }

    private static OrderTrackingState getDefaultOrderTrackingState(Activity activity) {
        OrderTrackingState orderTrackingState = new OrderTrackingState();
        orderTrackingState.setMessage(activity.getResources().getString(R.string.order_placed_message));
        orderTrackingState.setOrderState(STATE_PLACED);
        orderTrackingState.setTime("07:30");
        orderTrackingState.setAmpm(ORDER_TIME_PM);
        orderTrackingState.setIsCurrent(true);
        return orderTrackingState;
    }
}
