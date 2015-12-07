package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class Options implements Serializable {
    @SerializedName("addons")
    private ArrayList<Addons> addonsList;

    @SerializedName("option_value")
    private String optionValue;

    @SerializedName("sub_options")
    private ArrayList<String> subOptionsList;

    @SerializedName("option_cost")
    private String optionCost;

    public ArrayList<Addons> getAddonsList() {
        return addonsList;
    }

    public void setAddonsList(ArrayList<Addons> addonsList) {
        this.addonsList = addonsList;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public ArrayList<String> getSubOptionsList() {
        return subOptionsList;
    }

    public void setSubOptionsList(ArrayList<String> subOptionsList) {
        this.subOptionsList = subOptionsList;
    }

    public String getOptionCost() {
        return optionCost;
    }

    public void setOptionCost(String optionCost) {
        this.optionCost = optionCost;
    }
}
