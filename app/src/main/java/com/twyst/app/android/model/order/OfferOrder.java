package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 12/23/2015.
 */
public class OfferOrder implements Serializable {
    @SerializedName("_id")
    private String id;

    private String description;

    private String terms;

    @SerializedName("order_value")
    private float orderValue;

    private String type;

    private String line1;

    private String line2;

    @SerializedName("offer_cost")
    private float offerCost;

    @SerializedName("meta")
    private OfferOrderMeta offerOrderMeta;

    private String header;

    @SerializedName("available_now")
    private boolean availableNow;

    @SerializedName("expiry")
    private String expiryDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public float getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(float orderValue) {
        this.orderValue = orderValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public float getOfferCost() {
        return offerCost;
    }

    public void setOfferCost(float offerCost) {
        this.offerCost = offerCost;
    }

    public OfferOrderMeta getOfferOrderMeta() {
        return offerOrderMeta;
    }

    public void setOfferOrderMeta(OfferOrderMeta offerOrderMeta) {
        this.offerOrderMeta = offerOrderMeta;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(boolean availableNow) {
        this.availableNow = availableNow;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
