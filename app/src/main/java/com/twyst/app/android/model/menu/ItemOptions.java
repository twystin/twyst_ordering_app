package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/24/2015.
 */
public class ItemOptions implements Serializable {
    @SerializedName("add_on")
    private ArrayList<String> addOnList;

    @SerializedName("option_cost")
    private String optionCost;

    private String option;

    public ArrayList<String> getAddOnList() {
        return addOnList;
    }

    public void setAddOnList(ArrayList<String> addOnList) {
        this.addOnList = addOnList;
    }

    public String getOptionCost() {
        return optionCost;
    }

    public void setOptionCost(String optionCost) {
        this.optionCost = optionCost;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
