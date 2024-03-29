package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.LocationDetails.LocationsVerified;
import com.twyst.app.android.model.order.Coords;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 1/8/2016.
 */
public class AddressDetailsLocationData implements Serializable {
    public final static String TAG_HOME = "Home";
    public final static String TAG_WORK = "Work";
    public final static String TAG_OTHER = "Other";

    @SerializedName("line1")
    private String line1;

    @SerializedName("line2")
    private String line2;

    private String landmark;

    private String pin;

    private String city;

    private String state;

    private Coords coords;

    private String tag;

    private String name;

    private LocationsVerified locationsVerified;

    public LocationsVerified getLocationsVerified() {
        return locationsVerified;
    }

    public void setLocationsVerified(LocationsVerified locationsVerified) {
        this.locationsVerified = locationsVerified;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
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

//    @Override
//    public String toString() {
//        return "AddressDetailsLocationData{" +
//                "line2='" + line2 + '\'' +
//                ", landmark='" + landmark + '\'' +
//                ", line1='" + line1 + '\'' +
//                '}';
//    }


    @Override
    public String toString() {
        return "AddressDetailsLocationData{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", landmark='" + landmark + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(((AddressDetailsLocationData)o).toString());
    }
}
