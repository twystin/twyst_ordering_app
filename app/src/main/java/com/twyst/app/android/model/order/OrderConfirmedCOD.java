package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vipul Sharma on 1/30/2016.
 */
public class OrderConfirmedCOD {
    public OrderConfirmedCOD(String orderNumber, String outletID) {
        this.orderNumber = orderNumber;
        this.outletID = outletID;
    }

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("outlet")
    private String outletID;

    @SerializedName("payment_mode")
    private final String paymentMode = "COD";


}
