package com.twyst.app.android.model;

/**
 * Created by tushar on 15/01/16.
 */
public class PaymentData {
    private String paymentMode;
    private float cashBackPercent;

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public float getCashBackPercent() {
        return cashBackPercent;
    }

    public void setCashBackPercent(float cashBackPercent) {
        this.cashBackPercent = cashBackPercent;
    }
}
