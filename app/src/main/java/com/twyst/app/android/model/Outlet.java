package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/5/2016.
 */
public class Outlet extends Data implements Serializable {
    private String locality_2;

    private String locality_1;

    private String phone;

    @SerializedName("menu")
    private String menuId;

    @SerializedName("delivery_time")
    private String deliveryTime;

    @SerializedName("minimum_order")
    private String minimumOrder;

    @SerializedName("offer_count")
    private int offerCount;

    private ArrayList<Offer> offers = new ArrayList<>();

    private List<String> cuisines;

    private String lng;

    private String city;

    private Boolean open;

    private Boolean following;

    private Double distance;

    private String address;

    private String background;

    private String name;

    @SerializedName("open_next")
    private OpenNext openNext;

    private Cashback cashback;

    private String lat;

    private String logo;

    public Cashback getCashback() {
        return cashback;
    }

    public void setCashback(Cashback cashback) {
        this.cashback = cashback;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getMinimumOrder() {
        return minimumOrder;
    }

    public void setMinimumOrder(String minimumOrder) {
        this.minimumOrder = minimumOrder;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public String getLocality_2() {
        return locality_2;
    }

    public void setLocality_2(String locality_2) {
        this.locality_2 = locality_2;
    }

    public String getLocality_1() {
        return locality_1;
    }

    public void setLocality_1(String locality_1) {
        this.locality_1 = locality_1;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMenuId() {
        return menuId;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public OpenNext getOpenNext() {
        return openNext;
    }

    public void setOpenNext(OpenNext openNext) {
        this.openNext = openNext;
    }

    public int getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(int offerCount) {
        this.offerCount = offerCount;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


}
