package com.twyst.app.android.model;

/**
 * Created by Vipul Sharma on 1/27/2016.
 */
public class OrderTrackingState {
    //Order States
    public static final int ORDER_PLACED = 1;
    public static final int ORDER_ACCEPTED = 2;
    public static final int ORDER_DISPATCHED = 3;
    public static final int ORDER_ASSUMED_DELIVERY = 4;
    public static final int ORDER_NOT_DELIVERED = 5;

    // AMPM time
    public static final String ORDER_TIME_AM = "AM";
    public static final String ORDER_TIME_PM = "PM";

    private String time;
    private String ampm;
    private String message;
    private int orderState;
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

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }
}
