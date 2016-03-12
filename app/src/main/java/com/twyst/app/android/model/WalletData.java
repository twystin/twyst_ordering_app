package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rahuls on 10/8/15.
 */
public class WalletData implements Serializable {

    @SerializedName("coupons")
    private ArrayList<Outlet> outlets = new ArrayList<>();

    @SerializedName("twyst_cash")
    private String twystCash;

    public String getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(String twystCash) {
        this.twystCash = twystCash;
    }

    public ArrayList<Outlet> getOutlets() {
        return outlets;
    }

    public void setOutlets(ArrayList<Outlet> outlets) {
        this.outlets = outlets;
    }
}
