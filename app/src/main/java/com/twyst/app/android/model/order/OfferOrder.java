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

    private String type;

    private String line1;

    private String line2;

    @SerializedName("offer_cost")
    private int offerCost;

    @SerializedName("meta")
    private OfferOrderMeta offerOrderMeta;

    private String header;

    @SerializedName("available_now")
    private boolean availableNow;

    @SerializedName("order_value_without_tax")
    private double orderValueWithOutTax;

    @SerializedName("vat")
    private double vatValue;

    @SerializedName("st")
    private double serviceTaxValue;

    @SerializedName("order_value_with_tax")
    private double orderValueWithTax;

    @SerializedName("is_applicable")
    private boolean isApplicable;

    @SerializedName("expiry")
    private String expiryDate;

    @SerializedName("free_item_index")
    private int freeItemIndex = -1;

    public int getFreeItemIndex() {
        return freeItemIndex;
    }

    public void setFreeItemIndex(int freeItemIndex) {
        this.freeItemIndex = freeItemIndex;
    }

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

    public double getOrderValueWithOutTax() {
        return orderValueWithOutTax;
    }

    public void setOrderValueWithOutTax(double orderValueWithOutTax) {
        this.orderValueWithOutTax = orderValueWithOutTax;
    }

    public double getOrderValueWithTax() {
        return orderValueWithTax;
    }

    public void setOrderValueWithTax(double orderValueWithTax) {
        this.orderValueWithTax = orderValueWithTax;
    }

    public double getVatValue() {
        return vatValue;
    }

    public void setVatValue(double vatValue) {
        this.vatValue = vatValue;
    }

    public double getServiceTaxValue() {
        return serviceTaxValue;
    }

    public void setServiceTaxValue(double serviceTaxValue) {
        this.serviceTaxValue = serviceTaxValue;
    }

    public boolean isApplicable() {
        return isApplicable;
    }

    public void setIsApplicable(boolean isApplicable) {
        this.isApplicable = isApplicable;
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

    public int getOfferCost() {
        return offerCost;
    }

    public void setOfferCost(int offerCost) {
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
