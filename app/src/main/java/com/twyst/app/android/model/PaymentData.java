package com.twyst.app.android.model;

/**
 * Created by tushar on 15/01/16.
 */
public class PaymentData {
    private String paymentMode;
    private double cashBackPercent;
    private int cashBackAmount;
    private String promoCode;

    public int getCashBackAmount() {
        return cashBackAmount;
    }

    public void setCashBackAmount(int cashBackAmount) {
        this.cashBackAmount = cashBackAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getCashBackPercent() {
        return cashBackPercent;
    }

    public void setCashBackPercent(double cashBackPercent) {
        this.cashBackPercent = cashBackPercent;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
