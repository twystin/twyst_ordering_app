package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/10/2015.
 */
public class ItemAvailability implements Serializable {
    @SerializedName("regular_item")
    private String regularItem;

    public String getRegularItem() {
        return regularItem;
    }

    public void setRegularItem(String regularItem) {
        this.regularItem = regularItem;
    }
}
