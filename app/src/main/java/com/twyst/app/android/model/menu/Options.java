package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class Options implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("addons")
    private ArrayList<Addons> addonsList;

    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("option_value")
    private String optionValue;

    @SerializedName("sub_options")
    private ArrayList<SubOptions> subOptionsList;

    @SerializedName("option_cost")
    private String optionCost;

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

    public ArrayList<SubOptions> getSubOptionsList() {
        return subOptionsList;
    }

    public void setSubOptionsList(ArrayList<SubOptions> subOptionsList) {
        this.subOptionsList = subOptionsList;
    }

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

    public String getOptionCost() {
        return optionCost;
    }

    public void setOptionCost(String optionCost) {
        this.optionCost = optionCost;
    }
}
