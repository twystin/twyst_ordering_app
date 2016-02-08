package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 2/5/2016.
 */
public class OrderUpdate implements Serializable {
    public static final String FAVOURITE = "update_favourite";

    public OrderUpdate(String orderId, String updateType, boolean isFavourite) {
        this.orderId = orderId;
        this.updateType = updateType;
        this.isFavourite = isFavourite;
    }

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("update_type")
    private String updateType;

    @SerializedName("is_favourite")
    private boolean isFavourite;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }
}