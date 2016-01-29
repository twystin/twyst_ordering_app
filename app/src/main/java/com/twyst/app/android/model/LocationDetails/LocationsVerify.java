package com.twyst.app.android.model.LocationDetails;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.order.Coords;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 1/29/2016.
 */
public class LocationsVerify implements Serializable {
    public LocationsVerify(String outletID, ArrayList<Coords> coordsList) {
        this.outletID = outletID;
        this.coordsList = coordsList;
    }

    @SerializedName("outlet")
    private String outletID;

    @SerializedName("locations")
    private ArrayList<Coords> coordsList = new ArrayList<>();
}
