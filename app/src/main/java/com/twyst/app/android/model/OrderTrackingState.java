package com.twyst.app.android.model;

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
}
