package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.menu.Addons;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class OrderSummary implements Serializable {
    public OrderSummary(List<Items> cartItemsList, String outletId,Coords coords) {
        this.mCartItemsList = cartItemsList;
        this.outletId = outletId;
        this.coordinates = coords;
        createOrder();
    }

    private void createOrder() {
        for (int i = 0; i < mCartItemsList.size(); i++) {
            Items cartItem = mCartItemsList.get(i);

            OrderItem orderItem = new OrderItem();
            orderItem.setCategoryId(cartItem.getCategoryID());
            orderItem.setSubCategoryId(cartItem.getSubCategoryID());
            orderItem.setItemId(cartItem.getId());
            orderItem.setQuantity(cartItem.getItemQuantity());

            if (cartItem.getOptionsList().size() > 0) {
                Options option = cartItem.getOptionsList().get(0);
                orderItem.setOptionId(option.getId());
                orderItem.getSubOptionsList().addAll(option.getSubOptionsList());
                orderItem.getAddonsList().addAll(option.getAddonsList());

                for (int j = 0; j < option.getSubOptionsList().size(); j++) {
                    SubOptions subOption = option.getSubOptionsList().get(j);
                    orderItem.getSubOptionsSetIdList().add(subOption.getSubOptionSetList().get(0).getId());
                } // i loop

                for (int k = 0; k < option.getAddonsList().size(); k++) {
                    Addons addon = option.getAddonsList().get(k);
                    for (int l = 0; l < addon.getAddonSetList().size(); l++) {
                        orderItem.getAddonSetIdList().add(addon.getAddonSetList().get(l).getId());
                    } // l loop
                } // k loop
            }

            orderItemList.add(orderItem);
        }
    }

    private List<Items> mCartItemsList = new ArrayList<>();

    @SerializedName("items")
    private ArrayList<OrderItem> orderItemList = new ArrayList<>();

    @SerializedName("offers")
    private ArrayList<OfferOrder> offerOrderList = new ArrayList<>();

    @SerializedName("outlet")
    private String outletId;

    @SerializedName("offer_used")
    private OfferOrder offerUsed;

    @SerializedName("offer_id")
    private String selectedOfferID;

    @SerializedName("coords")
    private Coords coordinates = new Coords();

    @SerializedName("address")
    private AddressDetailsLocationData addressDetailsLocationData;

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("order_actual_value_without_tax")
    private double orderActualValueWithOutTax;

    @SerializedName("vat")
    private double vatValue;

    @SerializedName("st")
    private double serviceTaxValue;

    @SerializedName("order_actual_value_with_tax")
    private double orderActualValueWithTax;

    public ArrayList<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(ArrayList<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public OfferOrder getOfferUsed() {
        return offerUsed;
    }

    public void setOfferUsed(OfferOrder offerUsed) {
        this.offerUsed = offerUsed;
    }

    public String getSelectedOfferID() {
        return selectedOfferID;
    }

    public void setSelectedOfferID(String selectedOfferID) {
        this.selectedOfferID = selectedOfferID;
    }

    public AddressDetailsLocationData getAddressDetailsLocationData() {
        return addressDetailsLocationData;
    }

    public void setAddressDetailsLocationData(AddressDetailsLocationData addressDetailsLocationData) {
        this.addressDetailsLocationData = addressDetailsLocationData;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public Coords getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coords coordinates) {
        this.coordinates = coordinates;
    }

    public List<Items> getmCartItemsList() {
        return mCartItemsList;
    }

    public void setmCartItemsList(List<Items> mCartItemsList) {
        this.mCartItemsList = mCartItemsList;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ArrayList<OfferOrder> getOfferOrderList() {
        return offerOrderList;
    }

    public void setOfferOrderList(ArrayList<OfferOrder> offerOrderList) {
        this.offerOrderList = offerOrderList;
    }

    public double getOrderActualValueWithOutTax() {
        return orderActualValueWithOutTax;
    }

    public void setOrderActualValueWithOutTax(double orderActualValueWithOutTax) {
        this.orderActualValueWithOutTax = orderActualValueWithOutTax;
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

    public double getOrderActualValueWithTax() {
        return orderActualValueWithTax;
    }

    public void setOrderActualValueWithTax(double orderActualValueWithTax) {
        this.orderActualValueWithTax = orderActualValueWithTax;
    }
}
