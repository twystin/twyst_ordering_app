package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class Items implements Serializable {
    private int itemQuantity;

    @SerializedName("_id")
    private String id;

    @SerializedName("item_available_on")
    private ArrayList<String> itemAvailableOnList;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("is_vegetarian")
    private boolean isVegetarian;

    @SerializedName("item_cost")
    private String itemCost;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("item_description")
    private String itemDescription;

    @SerializedName("option_title")
    private String optionTitle;

    @SerializedName("option_is_addon")
    private boolean optionIsAddon;

    @SerializedName("item_tags")
    private ArrayList<String> itemTagsList;

    @SerializedName("item_availability")
    private ItemAvailability itemAvailability;

    @SerializedName("options")
    private ArrayList<Options> optionsList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getItemAvailableOnList() {
        return itemAvailableOnList;
    }

    public void setItemAvailableOnList(ArrayList<String> itemAvailableOnList) {
        this.itemAvailableOnList = itemAvailableOnList;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public ItemAvailability getItemAvailability() {
        return itemAvailability;
    }

    public void setItemAvailability(ItemAvailability itemAvailability) {
        this.itemAvailability = itemAvailability;
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

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public boolean isOptionIsAddon() {
        return optionIsAddon;
    }

    public void setOptionIsAddon(boolean optionIsAddon) {
        this.optionIsAddon = optionIsAddon;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Items){
            Items item = (Items) obj;
            if(item != null && this.itemName.equals(item.itemName)){
                return true;
            }
        }
        return false;
    }
}
