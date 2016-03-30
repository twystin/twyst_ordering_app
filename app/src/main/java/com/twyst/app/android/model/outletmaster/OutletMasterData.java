package com.twyst.app.android.model.outletmaster;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.Outlet;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class OutletMasterData implements Serializable {
    @SerializedName("outlet")
    private OutletMaster outletMaster;

    public OutletMaster getOutletMaster() {
        return outletMaster;
    }

    public void setOutletMaster(OutletMaster outletMaster) {
        this.outletMaster = outletMaster;
    }
}
