package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 4/5/2016.
 */
public class CouponCodeResponse implements Serializable {
    @SerializedName("cashback")
    private double cashBack;

    public double getCashBack() {
        return cashBack;
    }

    public void setCashBack(double cashBack) {
        this.cashBack = cashBack;
    }
}
