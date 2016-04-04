package com.twyst.app.android.model.outletmaster;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.OpenNext;
import com.twyst.app.android.model.menu.MenuData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class OutletMaster implements Serializable {
    @SerializedName("_id")
    private String id;

    private String name;

    private String city;

    private String address;

    @SerializedName("locality_1")
    private String locality1;

    @SerializedName("locality_2")
    private String locality2;

    private String lat;

    @SerializedName("long")
    private String lng;

    private String distance;

    @SerializedName("open")
    private boolean isOpen;

    private String phone;

    @SerializedName("offers")
    private ArrayList<Offer> offerList = new ArrayList<>();

    @SerializedName("attributes")
    private OutletAttributes outletAttributes;

    @SerializedName("following")
    private boolean isFollowing;

    private String logo;

    private String background;

    @SerializedName("open_next")
    private OpenNext openNext;

    @SerializedName("menu")
    private MenuData menuData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality1() {
        return locality1;
    }

    public void setLocality1(String locality1) {
        this.locality1 = locality1;
    }

    public String getLocality2() {
        return locality2;
    }

    public void setLocality2(String locality2) {
        this.locality2 = locality2;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Offer> getOfferList() {
        return offerList;
    }

    public void setOfferList(ArrayList<Offer> offerList) {
        this.offerList = offerList;
    }

    public OutletAttributes getOutletAttributes() {
        return outletAttributes;
    }

    public void setOutletAttributes(OutletAttributes outletAttributes) {
        this.outletAttributes = outletAttributes;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public OpenNext getOpenNext() {
        return openNext;
    }

    public void setOpenNext(OpenNext openNext) {
        this.openNext = openNext;
    }

    public MenuData getMenuData() {
        return menuData;
    }

    public void setMenuData(MenuData menuData) {
        this.menuData = menuData;
    }
}
