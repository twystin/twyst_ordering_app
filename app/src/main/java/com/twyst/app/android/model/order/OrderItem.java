package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.menu.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class OrderItem implements Serializable {

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("sub_category_id")
    private String subCategoryId;

    @SerializedName("item_id")
    private String itemId;

    @SerializedName("option_id")
    private String optionId;

    @SerializedName("sub_options")
    private ArrayList<SubOptionsOrder> subOptionsList = new ArrayList<>();

    @SerializedName("add_ons")
    private ArrayList<AddonsOrder> addonsList = new ArrayList<>();

    @SerializedName("quantity")
    private int quantity;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public ArrayList<SubOptionsOrder> getSubOptionsList() {
        return subOptionsList;
    }

    public void setSubOptionsList(ArrayList<SubOptionsOrder> subOptionsList) {
        this.subOptionsList = subOptionsList;
    }

    public ArrayList<AddonsOrder> getAddonsList() {
        return addonsList;
    }

    public void setAddonsList(ArrayList<AddonsOrder> addonsList) {
        this.addonsList = addonsList;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
