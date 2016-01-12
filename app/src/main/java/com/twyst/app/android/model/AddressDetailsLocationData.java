package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.order.Coords;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 1/8/2016.
 */
public class AddressDetailsLocationData implements Serializable {
    @SerializedName("line1")
    private String address;

    @SerializedName("line2")
    private String neighborhood;

    private String landmark;

    private String pin;

    private String city;

    private String state;

    private Coords coords;

    private String tag;

    private String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}