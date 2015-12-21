package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class SubOptionsOrder implements Serializable {
    @SerializedName("sub_option_id")
    private String subOptionId;

    @SerializedName("sub_option_set_id")
    private String subOptionSetId;

    public String getSubOptionId() {
        return subOptionId;
    }

    public void setSubOptionId(String subOptionId) {
        this.subOptionId = subOptionId;
    }

    public String getSubOptionSetId() {
        return subOptionSetId;
    }

    public void setSubOptionSetId(String subOptionSetId) {
        this.subOptionSetId = subOptionSetId;
    }
}
