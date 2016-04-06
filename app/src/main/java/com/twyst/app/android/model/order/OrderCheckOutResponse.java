package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

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

    @SerializedName("payment_options")
    private ArrayList<String> paymentOptionsList;

    @SerializedName("mobikwik_cashback")
    private String mobikwikCashback;

    public String getMobikwikCashback() {
        return mobikwikCashback;
    }

    public void setMobikwikCashback(String mobikwikCashback) {
        this.mobikwikCashback = mobikwikCashback;
    }

    public ArrayList<String> getPaymentOptionsList() {
        return paymentOptionsList;
    }

    public void setPaymentOptionsList(ArrayList<String> paymentOptionsList) {
        this.paymentOptionsList = paymentOptionsList;
    }

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

    @SerializedName("cod_cashback_percenatage")
    private double cod_cashback_percent;

    public double getCod_cashback_percent() {
        return cod_cashback_percent;
    }

    public void setCod_cashback_percent(double cod_cashback_percent) {
        this.cod_cashback_percent = cod_cashback_percent;
    }

    public double getInapp_cashback_percent() {
        return inapp_cashback_percent;
    }

    public void setInapp_cashback_percent(double inapp_cashback_percent) {
        this.inapp_cashback_percent = inapp_cashback_percent;
    }

    @SerializedName("inapp_cashback_percenatage")
    private double inapp_cashback_percent;

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
