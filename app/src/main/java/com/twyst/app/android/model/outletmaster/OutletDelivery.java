package com.twyst.app.android.model.outletmaster;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.DeliveryZone;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class OutletDelivery implements Serializable {
    @SerializedName("delivery_zone")
    private ArrayList<DeliveryZone> deliveryZonesList = new ArrayList<>();

    public ArrayList<DeliveryZone> getDeliveryZonesList() {
        return deliveryZonesList;
    }

    public void setDeliveryZonesList(ArrayList<DeliveryZone> deliveryZonesList) {
        this.deliveryZonesList = deliveryZonesList;
    }
}
