package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;
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
    public OrderSummary(List<Items> cartItemsList, String outletId, String lat, String lon) {
        this.mCartItemsList = cartItemsList;
        this.outletId = outletId;
        this.coordinates.setLat(lat);
        this.coordinates.setLon(lon);
        createOrder();
    }

    private void createOrder() {
        for (int i = 0; i < mCartItemsList.size(); i++) {
            Items cartItem = mCartItemsList.get(i);

            OrderItem orderItem = new OrderItem();
            orderItem.setCategoryId(cartItem.getCategoryID());
            orderItem.setSubCategoryId(cartItem.getSubCategoryID());
            orderItem.setItemId(cartItem.getId());
            orderItem.setOptionId(cartItem.getOptionsList().get(0).getId());
            orderItem.setQuantity(cartItem.getItemQuantity());

            Options option = cartItem.getOptionsList().get(0);
            for (int j = 0; j < option.getSubOptionsList().size(); j++) {
                SubOptions subOption = option.getSubOptionsList().get(j);
                SubOptionsOrder subOptionsOrder = new SubOptionsOrder();
                subOptionsOrder.setSubOptionId(subOption.getId());
                subOptionsOrder.setSubOptionSetId(subOption.getSubOptionSetList().get(0).getId());
                orderItem.getSubOptionsList().add(subOptionsOrder);
            }

            for (int k = 0; k < option.getSubOptionsList().size(); k++) {
                Addons addon = option.getAddonsList().get(k);
                AddonsOrder addonsOrder = new AddonsOrder();
                addonsOrder.setAddonId(addon.getId());
                for (int l = 0; l < addon.getAddonSetList().size(); l++) {
                    addonsOrder.getAddonSetsIdList().add(addon.getAddonSetList().get(l).getId());
                }
                orderItem.getAddonsList().add(addonsOrder);
            }

        }
    }

    private List<Items> mCartItemsList = new ArrayList<>();

    @SerializedName("items")
    private ArrayList<OrderItem> orderItemList = new ArrayList<>();

    @SerializedName("outlet")
    private String outletId;

    @SerializedName("coords")
    private Coords coordinates = new Coords();

    public ArrayList<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(ArrayList<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
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

}
