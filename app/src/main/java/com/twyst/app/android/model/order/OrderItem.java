package com.twyst.app.android.model.order;

import com.google.gson.annotations.SerializedName;
import com.twyst.app.android.model.menu.Addons;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubOptions;

import java.io.Serializable;
import java.util.ArrayList;

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

    @SerializedName("options")
    private ArrayList<String> optionsList = new ArrayList<>();

    @SerializedName("sub_option_set_ids")
    private ArrayList<String> subOptionsSetIdList = new ArrayList<>();

    @SerializedName("addon_set_ids")
    private ArrayList<String> addonSetIdList = new ArrayList<>();

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("addons")
    private ArrayList<Addons> addonsList = new ArrayList<>();

    @SerializedName("sub_options")
    private ArrayList<SubOptions> subOptionsList = new ArrayList<>();

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

    public ArrayList<String> getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(ArrayList<String> optionsList) {
        this.optionsList = optionsList;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public ArrayList<String> getSubOptionsSetIdList() {
        return subOptionsSetIdList;
    }

    public void setSubOptionsSetIdList(ArrayList<String> subOptionsSetIdList) {
        this.subOptionsSetIdList = subOptionsSetIdList;
    }

    public ArrayList<String> getAddonSetIdList() {
        return addonSetIdList;
    }

    public void setAddonSetIdList(ArrayList<String> addonSetIdList) {
        this.addonSetIdList = addonSetIdList;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Addons> getAddonsList() {
        return addonsList;
    }

    public void setAddonsList(ArrayList<Addons> addonsList) {
        this.addonsList = addonsList;
    }

    public ArrayList<SubOptions> getSubOptionsList() {
        return subOptionsList;
    }

    public void setSubOptionsList(ArrayList<SubOptions> subOptionsList) {
        this.subOptionsList = subOptionsList;
    }
}
