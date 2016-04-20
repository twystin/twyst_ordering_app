package com.twyst.app.android.model.banners;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class OrderBanner implements Serializable {
    public static final String TYPE_THIRD_PARTY_BANNER = "third_party";
    public static final String TYPE_FOOD_BANNER = "single_outlet";
    public static final String TYPE_LANDING_PAGE_BANNER = "landing_page";
    public static final String TYPE_OUTLET_BANNER = "multi_outlets";
    @SerializedName("_id")
    private String id;

    @SerializedName("banner_type")
    private String bannerType;

    private String header;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("banner_name")
    private String bannerName;

    @SerializedName("outlets")
    private ArrayList<String> outletIdList = new ArrayList<>();

    @SerializedName("coupon_code")
    private String couponCode;

    public String getId() {
        return id;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getBannerType() {
        return bannerType;
    }

    public void setBannerType(String bannerType) {
        this.bannerType = bannerType;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ArrayList<String> getOutletIdList() {
        return outletIdList;
    }

    public void setOutletIdList(ArrayList<String> outletIdList) {
        this.outletIdList = outletIdList;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }
}
