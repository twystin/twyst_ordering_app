package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class Items implements Serializable {
    private int itemQuantity;

    @SerializedName("is_vegetarian")
    private String isVegetarian;

    @SerializedName("item_cost")
    private String itemCost;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("item_description")
    private String itemDescription;

    @SerializedName("option_title")
    private String optionTitle;

    @SerializedName("option_is_addon")
    private String optionIsAddon;

    @SerializedName("item_tags")
    private ArrayList<String> itemTagsList;

    @SerializedName("options")
    private ArrayList<Options> optionsList;

    public String getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(String isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public String getItemCost() {
        return itemCost;
    }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public String getOptionIsAddon() {
        return optionIsAddon;
    }

    public void setOptionIsAddon(String optionIsAddon) {
        this.optionIsAddon = optionIsAddon;
    }

    public ArrayList<String> getItemTagsList() {
        return itemTagsList;
    }

    public void setItemTagsList(ArrayList<String> itemTagsList) {
        this.itemTagsList = itemTagsList;
    }

    public ArrayList<Options> getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(ArrayList<Options> optionsList) {
        this.optionsList = optionsList;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
