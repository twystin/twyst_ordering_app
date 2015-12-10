package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class AddonSet implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("addon_value")
    private String addonValue;

    @SerializedName("addon_cost")
    private String addonCost;

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

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getAddonValue() {
        return addonValue;
    }

    public void setAddonValue(String addonValue) {
        this.addonValue = addonValue;
    }

    public String getAddonCost() {
        return addonCost;
    }

    public void setAddonCost(String addonCost) {
        this.addonCost = addonCost;
    }
}
