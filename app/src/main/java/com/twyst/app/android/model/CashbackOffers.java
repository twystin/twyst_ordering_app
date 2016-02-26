package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tushar on 24/02/16.
 */
public class CashbackOffers implements Serializable {

    private String offer_source;
    private String offer_banner;
    private String offer_status;
    private String offer_type;
    private String offer_voucher_count;
    private String offer_text;
    private String offer_cost;
    private String offer_value;
    private String min_bill_value;
    private String offer_tnc;
    private String offer_processing_fee;
    private String offer_end_date;
    @SerializedName("_id")
    private String offer_id;


    public String getMin_bill_value() {
        return min_bill_value;
    }

    public void setMin_bill_value(String min_bill_value) {
        this.min_bill_value = min_bill_value;
    }

    public String getOffer_banner() {
        return offer_banner;
    }

    public void setOffer_banner(String offer_banner) {
        this.offer_banner = offer_banner;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_status() {
        return offer_status;
    }

    public void setOffer_status(String offer_status) {
        this.offer_status = offer_status;
    }

    public String getOffer_tnc() {
        return offer_tnc;
    }

    public void setOffer_tnc(String offer_tnc) {
        this.offer_tnc = offer_tnc;
    }

    public String getOffer_value() {
        return offer_value;
    }

    public void setOffer_value(String offer_value) {
        this.offer_value = offer_value;
    }

    public String getOffer_cost() {
        return offer_cost;
    }

    public void setOffer_cost(String offer_cost) {
        this.offer_cost = offer_cost;
    }

    public String getOffer_end_date() {
        return offer_end_date;
    }

    public void setOffer_end_date(String offer_end_date) {
        this.offer_end_date = offer_end_date;
    }

    public String getOffer_processing_fee() {
        return offer_processing_fee;
    }

    public void setOffer_processing_fee(String offer_processing_fee) {
        this.offer_processing_fee = offer_processing_fee;
    }

    public String getOffer_source() {
        return offer_source;
    }

    public void setOffer_source(String offer_source) {
        this.offer_source = offer_source;
    }

    public String getOffer_text() {
        return offer_text;
    }

    public void setOffer_text(String offer_text) {
        this.offer_text = offer_text;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public void setOffer_type(String offer_type) {
        this.offer_type = offer_type;
    }

    public String getOffer_voucher_count() {
        return offer_voucher_count;
    }

    public void setOffer_voucher_count(String offer_voucher_count) {
        this.offer_voucher_count = offer_voucher_count;
    }

    public String getOffer_logo() {
        return offer_banner;
    }

    public void setOffer_logo(String offer_logo) {
        this.offer_banner = offer_logo;
    }
}
