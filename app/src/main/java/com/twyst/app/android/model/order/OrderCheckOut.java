package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.AddressDetailsLocationData;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 1/30/2016.
 */
public class OrderCheckOut implements Serializable {
    public OrderCheckOut(String orderID, String outletID, AddressDetailsLocationData addressDetailsLocationData) {
        this.orderID = orderID;
        this.outletID = outletID;
        this.addressDetailsLocationData = addressDetailsLocationData;
    }

    @SerializedName("order_number")
    private String orderID;

    @SerializedName("outlet")
    private String outletID;

    @SerializedName("address")
    private AddressDetailsLocationData addressDetailsLocationData;
}
