package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 08/02/16.
 */
public class DeliveryZone implements Serializable{

    @SerializedName("min_amt_for_delivery")
    private String minDeliveryAmt;

    @SerializedName("delivery_estimated_time")
    private String deliveryEstimatedTime;

    public String getMinDeliveryAmt() {
        return minDeliveryAmt;
    }

    public void setMinDeliveryAmt(String minDeliveryAmt) {
        this.minDeliveryAmt = minDeliveryAmt;
    }

    public String getDeliveryEstimatedTime() {
        return deliveryEstimatedTime;
    }

    public void setDeliveryEstimatedTime(String deliveryEstimatedTime) {
        this.deliveryEstimatedTime = deliveryEstimatedTime;
    }
}
