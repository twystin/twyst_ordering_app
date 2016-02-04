package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 2/4/2016.
 */
public class CancelOrder implements Serializable {
    public CancelOrder(String orderID, String reason) {
        this.orderID = orderID;
        this.reason = reason;
    }

    @SerializedName("order_id")
    private String orderID;

    private String reason;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
