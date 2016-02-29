package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anshul on 1/14/2016.
 */
public class Cashback implements Serializable {

    @SerializedName("source")
    private String merchant;
    @SerializedName("logo")
    private String merchant_logo;

    @SerializedName("offers")
    private ArrayList<CashbackOffers> cashbackOffers;

    public ArrayList<CashbackOffers> getCashbackOffers() {
        return cashbackOffers;
    }

    public void setCashbackOffers(ArrayList<CashbackOffers> cashbackOffers) {
        this.cashbackOffers = cashbackOffers;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getMerchant_logo() {
        return merchant_logo;
    }

    public void setMerchant_logo(String merchant_logo) {
        this.merchant_logo = merchant_logo;
    }
}
