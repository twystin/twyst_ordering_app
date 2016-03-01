package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 21/8/15.
 */
public class UseOfferData implements Serializable{

    @SerializedName("twyst_cash")
    private String twystCash;

    @SerializedName("code")
    private String code;

    public String getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(String twystCash) {
        this.twystCash = twystCash;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
