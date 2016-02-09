package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 1/30/2016.
 */
public class OrderCheckOutResponse implements Serializable {
    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("outlet")
    private String outletID;

    @SerializedName("actual_amount_paid")
    private String actualAmountPaid;

    @SerializedName("_id")
    private String orderID;

    public int getCod_cashback() {
        return cod_cashback;
    }

    public void setCod_cashback(int cod_cashback) {
        this.cod_cashback = cod_cashback;
    }

    public int getInapp_cashback() {
        return inapp_cashback;
    }

    public void setInapp_cashback(int inapp_cashback) {
        this.inapp_cashback = inapp_cashback;
    }

    private int cod_cashback;

    private int inapp_cashback;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOutletID() {
        return outletID;
    }

    public void setOutletID(String outletID) {
        this.outletID = outletID;
    }

    public String getActualAmountPaid() {
        return actualAmountPaid;
    }

    public void setActualAmountPaid(String actualAmountPaid) {
        this.actualAmountPaid = actualAmountPaid;
    }
}
