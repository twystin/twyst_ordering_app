package com.twyst.app.android.model.banners;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class OrderBanner implements Serializable{
    @SerializedName("_id")
    private String id;

    @SerializedName("banner_type")
    private String bannerType;

    private String header;

    @SerializedName("banner_image")
    private String bannerImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }
}
