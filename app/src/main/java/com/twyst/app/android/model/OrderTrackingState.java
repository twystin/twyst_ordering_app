package com.twyst.app.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vipul Sharma on 1/27/2016.
 */
public class OrderTrackingState {
    public OrderTrackingState(String time, String ampm, String message, String orderState) {
        this.time = time;
        this.ampm = ampm.toUpperCase();
        this.message = message;
        this.orderState = orderState;
    }

    private String time;
    private String ampm;
    private String message;
    private String orderState;

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

    public static ArrayList<OrderTrackingState> getInitialList(String orderID, Context context) {
        final SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        ArrayList<OrderTrackingState> trackOrderStatesList = new ArrayList<>();
        String orderTrackingStateListString = sharedPreferences.getString(orderID, "");

        if (TextUtils.isEmpty(orderTrackingStateListString)) {
            //List is empty, get the list with default Order State (ORDER_PLACED)
            trackOrderStatesList.add(getDefaultOrderTrackingState(context));
            if (updateListLocally(context, orderID, trackOrderStatesList)) {
                return trackOrderStatesList;
            }
        } else {
            //Ordering states already saved, get the list with previous Ordering States
            Gson gson1 = new Gson();
            Type type = new TypeToken<List<OrderTrackingState>>() {
            }.getType();
            trackOrderStatesList = gson1.fromJson(orderTrackingStateListString, type);
        }

        return trackOrderStatesList;
    }

    private static OrderTrackingState getDefaultOrderTrackingState(Context context) {
        String[] timeArray = getTimeArray(new Date());
        return new OrderTrackingState(timeArray[0], timeArray[1], context.getResources().getString(R.string.order_placed_message), STATE_PLACED);
    }

    public static void addToList(String orderIDServer, String stateServer, String messageServer, String timeServer, Context context) {
        String[] timeArray = getTimeArray(new Date(Long.parseLong(timeServer)));
        addNewStateToList(context, orderIDServer, getInitialList(orderIDServer, context), new OrderTrackingState(timeArray[0], timeArray[1], messageServer, stateServer));
    }

    private static void addNewStateToList(Context context, String orderIDServer, ArrayList<OrderTrackingState> initialList, OrderTrackingState orderTrackingState) {
        //To be added after time based sorting
        initialList.add(orderTrackingState);
        updateListLocally(context, orderIDServer, initialList);
    }

    private static boolean updateListLocally(Context context, String orderIDServer, ArrayList<OrderTrackingState> list) {
        final SharedPreferences.Editor sharedPreferences = context.getApplicationContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        sharedPreferences.putString(orderIDServer, json);
        return sharedPreferences.commit();
    }

    private static String[] getTimeArray(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String formattedDate = dateFormat.format(date).toString();
        return formattedDate.split("\\s+");
    }
}
