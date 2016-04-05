package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 4/5/2016.
 */
public class CouponCode implements Serializable {
    public CouponCode(String orderNumber, String outletID, String couponCode) {
        this.orderNumber = orderNumber;
        this.outletID = outletID;
        this.couponCode = couponCode;
    }

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("outlet")
    private String outletID;

    @SerializedName("coupon_code")
    private String couponCode;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOutletID() {
        return outletID;
    }

    public void setOutletID(String outletID) {
        this.outletID = outletID;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
