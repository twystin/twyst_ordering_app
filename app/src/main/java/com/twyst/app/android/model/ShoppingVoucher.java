package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 26/02/16.
 */
public class ShoppingVoucher implements Serializable {

    public ShoppingVoucher(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_id() {
        return offer_id;
    }

    final private String offer_id;

}
