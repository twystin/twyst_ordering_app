package com.twyst.app.android.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.util.Utils;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * Created by Raman on 3/8/2016.
 */
public class TwystCashHistory implements Serializable {
    @SerializedName("type")
    private String type;

    @SerializedName("earn")
    private boolean earn;

    @SerializedName("twyst_cash")
    private int twystCash;

    @SerializedName("earn_at")
    private String earnAt;

    @SerializedName("message")
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEarn() {
        return earn;
    }

    public void setEarn(boolean earn) {
        this.earn = earn;
    }

    public int getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(int twystCash) {
        this.twystCash = twystCash;
    }

    public String getEarnAt() {
        return earnAt;
    }

    public void setEarnAt(String earnAt) {
        this.earnAt = earnAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
