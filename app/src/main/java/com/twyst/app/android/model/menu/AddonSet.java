package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class AddonSet implements Serializable {
    @SerializedName("addon_value")
    private String addonValue;

    @SerializedName("addon_cost")
    private String addonCost;

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
