package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 12/8/15.
 */
public class OutletDetailData implements Serializable{

    @SerializedName("outlet")
    private Outlet outlet;

    @SerializedName("twyst_cash")
    private String twystCash;

    public String getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(String twystCash) {
        this.twystCash = twystCash;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }
}
