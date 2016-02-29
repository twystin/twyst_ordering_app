package com.twyst.app.android.offer;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.Data;
import com.twyst.app.android.model.DeliveryZone;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anshul on 2/25/2016.
 */
public class FoodOffer extends Data implements Serializable {

    private String header;

    @SerializedName("line1")
    private String headerSuffix;

    @SerializedName("line2")
    private String maxOfferAvailalbleDesc;

    @SerializedName("offer_cost")
    private int offerCost;

    @SerializedName("expiry")
    private String expiryDate;

    @SerializedName("outlet")
    private OutletHeader outletHeader;

    @SerializedName("menu_id")
    private String menuId;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeaderSuffix() {
        return headerSuffix;
    }

    public void setHeaderSuffix(String headerSuffix) {
        this.headerSuffix = headerSuffix;
    }

    public String getMaxOfferAvailalbleDesc() {
        return maxOfferAvailalbleDesc;
    }

    public void setMaxOfferAvailalbleDesc(String maxOfferAvailalbleDesc) {
        this.maxOfferAvailalbleDesc = maxOfferAvailalbleDesc;
    }

    public int getOfferCost() {
        return offerCost;
    }

    public void setOfferCost(int offerCost) {
        this.offerCost = offerCost;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public OutletHeader getOutletHeader() {
        return outletHeader;
    }

    public void setOutletHeader(OutletHeader outletHeader) {
        this.outletHeader = outletHeader;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public static class OutletHeader extends Data implements Serializable{
        @SerializedName("name")
        private String outletName;

        @SerializedName("loc1")
        private String outletLocationLine1;

        @SerializedName("loc2")
        private String outletLocationLine2;

        @SerializedName("background")
        private String background;

        @SerializedName("delivery_zones")
        private ArrayList<DeliveryZone> delivery_zone;

        @SerializedName("logo")
        private String logo;

        public String getOutletLocationLine2() {
            return outletLocationLine2;
        }

        public void setOutletLocationLine2(String outletLocationLine2) {
            this.outletLocationLine2 = outletLocationLine2;
        }

        public String getOutletName() {
            return outletName;
        }

        public void setOutletName(String outletName) {
            this.outletName = outletName;
        }

        public String getOutletLocationLine1() {
            return outletLocationLine1;
        }

        public void setOutletLocationLine1(String outletLocationLine1) {
            this.outletLocationLine1 = outletLocationLine1;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public ArrayList<DeliveryZone> getDelivery_zone() {
            return delivery_zone;
        }

        public void setDelivery_zone(ArrayList<DeliveryZone> delivery_zone) {
            this.delivery_zone = delivery_zone;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }

}
