package com.twyst.app.android.model.LocationDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 1/29/2016.
 */
public class ValidZone implements Serializable {
    @SerializedName("delivery_estimated_time")
    private String deliveryEstimatedTime;

    @SerializedName("min_amt_for_delivery")
    private String minDeliveryAmount;

    @SerializedName("delivery_charge")
    private String deliveryCharge;

    public String getDeliveryEstimatedTime() {
        return deliveryEstimatedTime;
    }

    public void setDeliveryEstimatedTime(String deliveryEstimatedTime) {
        this.deliveryEstimatedTime = deliveryEstimatedTime;
    }

    public String getMinDeliveryAmount() {
        return minDeliveryAmount;
    }

    public void setMinDeliveryAmount(String minDeliveryAmount) {
        this.minDeliveryAmount = minDeliveryAmount;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
