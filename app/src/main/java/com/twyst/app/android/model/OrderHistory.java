package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.Address;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 1/15/2016.
 */
public class OrderHistory implements Serializable {
    @SerializedName("outlet_name")
    private String outletName;

    @SerializedName("items")
    private ArrayList<Items> items = new ArrayList<Items>();

    @SerializedName("address")
    private Address address;

    @SerializedName("is_favourite")
    private boolean isFavourite;

    @SerializedName("order_cost")
    private Double orderCost;

    @SerializedName("cashback")
    private Double cashBack;

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("order_status")
    private boolean orderStatus;

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public Double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(Double orderCost) {
        this.orderCost = orderCost;
    }

    public Double getCashBack() {
        return cashBack;
    }

    public void setCashBack(Double cashBack) {
        this.cashBack = cashBack;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(boolean orderStatus) {
        this.orderStatus = orderStatus;
    }
}