package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by tushar on 29/01/16.
 */
public class OrderFeedback implements Serializable {

    @SerializedName("Outlet_Id")
    private String outletId;

    @SerializedName("Order_Id")
    private String orderId;

    private boolean fb_foodDeliveredOnTime;

    private int fb_foodOverallRating;

    private HashMap<String, Integer> fb_dishRating;

    public void setFb_dishRating(HashMap<String, Integer> fb_dishRating) {
        this.fb_dishRating = fb_dishRating;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isFb_foodDeliveredOnTime() {
        return fb_foodDeliveredOnTime;
    }

    public void setFb_foodDeliveredOnTime(boolean fb_foodDeliveredOnTime) {
        this.fb_foodDeliveredOnTime = fb_foodDeliveredOnTime;
    }

    public int getFb_foodOverallRating() {
        return fb_foodOverallRating;
    }

    public void setFb_foodOverallRating(int fb_foodOverallRating) {
        this.fb_foodOverallRating = fb_foodOverallRating;
    }

    public HashMap<String, Integer> getFb_dishRating() {
        return fb_dishRating;
    }

}
