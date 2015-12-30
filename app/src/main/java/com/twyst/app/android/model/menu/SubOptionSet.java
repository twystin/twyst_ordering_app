package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/10/2015.
 */
public class SubOptionSet implements Serializable {
    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("_id")
    private String id;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("sub_option_value")
    private String subOptionValue;

    @SerializedName("sub_option_cost")
    private Float subOptionCost;

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getSubOptionValue() {
        return subOptionValue;
    }

    public void setSubOptionValue(String subOptionValue) {
        this.subOptionValue = subOptionValue;
    }

    public Float getSubOptionCost() {
        return subOptionCost;
    }

    public void setSubOptionCost(Float subOptionCost) {
        this.subOptionCost = subOptionCost;
    }
}
