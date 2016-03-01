package com.twyst.app.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twyst.app.android.R;
import com.twyst.app.android.model.order.OrderAction;
import com.twyst.app.android.util.AppConstants;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
            trackOrderStatesList.add(getDefaultOrderTrackingState(context, getTimeArray(new Date())));
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

    private static OrderTrackingState getDefaultOrderTrackingState(Context context, String[] timeArray) {
        return new OrderTrackingState(timeArray[0], timeArray[1], context.getResources().getString(R.string.order_placed_message), STATE_PLACED);
    }

    public static OrderTrackingState getCancelledOrderTrackingState(Context context) {
        String[] timeArray = getTimeArray(new Date());
        return new OrderTrackingState(timeArray[0], timeArray[1], context.getResources().getString(R.string.order_cancelled_message), STATE_CANCELLED);
    }

    public static OrderTrackingState getDeliveredOrderTrackingState(Context context) {
        String[] timeArray = getTimeArray(new Date());
        return new OrderTrackingState(timeArray[0], timeArray[1], context.getResources().getString(R.string.order_delivered_message), STATE_DELIVERED);
    }

    public static OrderTrackingState getNotDeliveredOrderTrackingState(Context context) {
        String[] timeArray = getTimeArray(new Date());
        return new OrderTrackingState(timeArray[0], timeArray[1], context.getResources().getString(R.string.order_not_delivered_message), STATE_NOT_DELIVERED);
    }

    public static void addToList(String orderIDServer, String stateServer, String messageServer, String timeServer, Context context) {
        String[] timeArray = getTimeArray(new Date(Long.parseLong(timeServer)));
        addNewStateToList(context, orderIDServer, getInitialList(orderIDServer, context), new OrderTrackingState(timeArray[0], timeArray[1], messageServer, stateServer));
    }

    public static void addToListLocally(String orderIDServer, OrderTrackingState orderTrackingState, Context context) {
        addNewStateToList(context, orderIDServer, getInitialList(orderIDServer, context), orderTrackingState);
    }

    private static void addNewStateToList(Context context, String orderIDServer, ArrayList<OrderTrackingState> initialList, OrderTrackingState orderTrackingState) {
        //To be added after time based sorting
        initialList.add(0, orderTrackingState);
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

    public static Date getFormattedDate(String orderDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        Date formattedDate = null;
        try {
            formattedDate = sdf.parse(orderDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static void updateOverrideList(Context context, String orderID, ArrayList<OrderAction> orderActionsList, String orderPlacedDate) {
        ArrayList<OrderTrackingState> trackOrderStatesList = new ArrayList<>();

        trackOrderStatesList.add(getDefaultOrderTrackingState(context, getTimeArray(getFormattedDate(orderPlacedDate))));
        if (orderActionsList != null && orderActionsList.size() > 0) {
            for (OrderAction orderAction : orderActionsList) {
                trackOrderStatesList.add(0, getOrderState(context, orderAction));
            }
        }

        updateListLocally(context, orderID, trackOrderStatesList);
    }

    private static OrderTrackingState getOrderState(Context context, OrderAction orderAction) {
        String[] timeArray = getTimeArray(getFormattedDate(orderAction.getActionTime()));
        String message = orderAction.getMessage();
        switch (orderAction.getActionType()) {
            case STATE_ACCEPTED:
                message = context.getResources().getString(R.string.order_accepted_message);
                break;
            case STATE_DISPATCHED:
                message = context.getResources().getString(R.string.order_dispatched_message);
                break;
            case STATE_CANCELLED:
                message = context.getResources().getString(R.string.order_cancelled_message);
                break;
            case STATE_REJECTED:
                message = context.getResources().getString(R.string.order_rejected_message);
                break;
            case STATE_ASSUMED_DELIVERED:
                message = context.getResources().getString(R.string.order_assumed_delivery_message);
                break;
            case STATE_NOT_DELIVERED:
                message = context.getResources().getString(R.string.order_not_delivered_message);
                break;
            case STATE_ABANDONED:
                message = context.getResources().getString(R.string.order_abandoned_message);
                break;
            case STATE_DELIVERED:
                message = context.getResources().getString(R.string.order_delivered_message);
                break;
        }

        return new OrderTrackingState(timeArray[0], timeArray[1], message, orderAction.getActionType());
//        // To do for default case
//        return null;
    }
}
