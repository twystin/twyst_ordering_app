package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Raman on 3/8/2016.
 */
public class TwystCashHistory implements Serializable {
    @SerializedName("type")
    private String type;

    @SerializedName("earn")
    private Boolean earn;

    @SerializedName("twyst_cash")
    private Double twyst_cash;

    @SerializedName("earn_at")
    private String earn_at;

    @SerializedName("message")
    String message;

    public Boolean isEarn() {
        return earn;
    }

    public void setEarn(Boolean earn) {
        this.earn = earn;
    }

    public Double getTwyst_cash() {
        return twyst_cash;
    }

    public void setTwyst_cash(Double twyst_cash) {
        this.twyst_cash = twyst_cash;
    }

    public String getEarn_at() {
        return earn_at;
    }

    public void setEarn_at(String earn_at) {
        this.earn_at = earn_at;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
