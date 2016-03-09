package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 3/9/2016.
 */
public class Recharge implements Serializable {
    private static final String POSTPAID = "postpaid";

    public Recharge(long phone, int amount, int operatorCode, int circleCode) {
        this.phone = phone;
        this.amount = amount;
        this.operatorCode = operatorCode;
        this.circleCode = circleCode;
    }

    private long phone;
    private int amount;
    @SerializedName("operator")
    private int operatorCode;
    @SerializedName("circle")
    private int circleCode;
    @SerializedName("connType")
    private String conntype;
}
