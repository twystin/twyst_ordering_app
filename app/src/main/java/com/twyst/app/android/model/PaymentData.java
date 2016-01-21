package com.twyst.app.android.model;

/**
 * Created by tushar on 15/01/16.
 */
public class PaymentData {
    private String mPaymentMode;
    private String mCashBackPercent;

    public String getCashBackPercent() {
        return mCashBackPercent;
    }

    public void setCashBackPercent(String mCashBackPercent) {
        this.mCashBackPercent = mCashBackPercent;
    }

    public String getPaymentMode() {
        return mPaymentMode;
    }

    public void setPaymentMode(String mPaymentMode) {
        this.mPaymentMode = mPaymentMode;
    }
}
