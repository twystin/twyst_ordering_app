package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class Coords implements Serializable {
    public Coords(){}
    public Coords(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @SerializedName("lat")
    private String lat;

    @SerializedName("long")
    private String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
