package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rahuls on 12/8/15.
 */
public class DiscoverData implements Serializable{

    @SerializedName("outlets")
    private ArrayList<Outlet> outlets;

    @SerializedName("twyst_cash")
    private String twystCash;

    public ArrayList<Outlet> getOutlets() {
        return outlets;
    }

    public void setOutlets(ArrayList<Outlet> outlets) {
        this.outlets = outlets;
    }

    public String getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(String twystCash) {
        this.twystCash = twystCash;
    }
}
