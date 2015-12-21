package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class AddonsOrder implements Serializable {
    @SerializedName("add_ons_id")
    private String addonId;

    @SerializedName("add_ons_set_id")
    private ArrayList<String> addonSetsIdList = new ArrayList<>();

    public String getAddonId() {
        return addonId;
    }

    public void setAddonId(String addonId) {
        this.addonId = addonId;
    }

    public ArrayList<String> getAddonSetsIdList() {
        return addonSetsIdList;
    }

    public void setAddonSetsIdList(ArrayList<String> addonSetsIdList) {
        this.addonSetsIdList = addonSetsIdList;
    }
}
