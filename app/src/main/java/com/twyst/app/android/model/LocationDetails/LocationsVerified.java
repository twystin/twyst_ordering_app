package com.twyst.app.android.model.LocationDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 1/29/2016.
 */
public class LocationsVerified implements Serializable {
    @SerializedName("lat")
    private String latitude;

    @SerializedName("long")
    private String longitude;

    @SerializedName("valid_zone")
    private ValidZone validZone;

    @SerializedName("is_deliver")
    private boolean isDeliverable;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public ValidZone getValidZone() {
        return validZone;
    }

    public void setValidZone(ValidZone validZone) {
        this.validZone = validZone;
    }

    public boolean isDeliverable() {
        return isDeliverable;
    }

    public void setIsDeliverable(boolean isDeliverable) {
        this.isDeliverable = isDeliverable;
    }
}
