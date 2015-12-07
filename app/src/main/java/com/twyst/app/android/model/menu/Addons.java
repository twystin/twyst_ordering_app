package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class Addons implements Serializable {
    @SerializedName("addon_set")
    private ArrayList<AddonSet> addonSetList;

    @SerializedName("addon_title")
    private String addonTitle;

    public ArrayList<AddonSet> getAddonSetList() {
        return addonSetList;
    }

    public void setAddonSetList(ArrayList<AddonSet> addonSetList) {
        this.addonSetList = addonSetList;
    }

    public String getAddonTitle() {
        return addonTitle;
    }

    public void setAddonTitle(String addonTitle) {
        this.addonTitle = addonTitle;
    }
}