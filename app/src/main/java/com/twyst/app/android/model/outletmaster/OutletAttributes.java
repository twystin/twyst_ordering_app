package com.twyst.app.android.model.outletmaster;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class OutletAttributes implements Serializable {
    @SerializedName("home_delivery")
    private boolean isHomeDelivery;

    @SerializedName("alcohol")
    private boolean isAlcohol;

    @SerializedName("dine_in")
    private boolean isDineIn;

    @SerializedName("tags")
    private ArrayList<String> tagList = new ArrayList<>();

    @SerializedName("cuisines")
    private ArrayList<String> cuisinesList = new ArrayList<>();

    @SerializedName("delivery")
    private OutletDelivery outletDelivery;

    public boolean isHomeDelivery() {
        return isHomeDelivery;
    }

    public void setIsHomeDelivery(boolean isHomeDelivery) {
        this.isHomeDelivery = isHomeDelivery;
    }

    public boolean isAlcohol() {
        return isAlcohol;
    }

    public void setIsAlcohol(boolean isAlcohol) {
        this.isAlcohol = isAlcohol;
    }

    public boolean isDineIn() {
        return isDineIn;
    }

    public void setIsDineIn(boolean isDineIn) {
        this.isDineIn = isDineIn;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    public ArrayList<String> getCuisinesList() {
        return cuisinesList;
    }

    public void setCuisinesList(ArrayList<String> cuisinesList) {
        this.cuisinesList = cuisinesList;
    }

    public OutletDelivery getOutletDelivery() {
        return outletDelivery;
    }

    public void setOutletDelivery(OutletDelivery outletDelivery) {
        this.outletDelivery = outletDelivery;
    }
}
