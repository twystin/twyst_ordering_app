package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 26/02/16.
 */
public class ShoppingVoucherResponse implements Serializable {

    @SerializedName("data")
    private boolean showResendOption;

    public boolean isShowResendOption() {
        return showResendOption;
    }

    public void setShowResendOption(boolean showResendOption) {
        this.showResendOption = showResendOption;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}
