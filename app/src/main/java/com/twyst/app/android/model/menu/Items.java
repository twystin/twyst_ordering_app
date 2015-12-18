package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class Items implements Serializable {
    public Items(Items itemOriginal) {
        this.itemOriginalReference = itemOriginal;
        this.id = itemOriginal.getId();
        this.isVegetarian = itemOriginal.isVegetarian();
        this.itemCost = itemOriginal.getItemCost();
        this.itemName = itemOriginal.getItemName();
        this.itemDescription = itemOriginal.getItemDescription();
        this.optionTitle = itemOriginal.getOptionTitle();
        this.optionIsAddon = itemOriginal.isOptionIsAddon();
        this.optionsList.addAll(itemOriginal.getOptionsList());
    }

    // Compulsory field for the selected item in cart.
    @SerializedName("_id")
    private String id;

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

    @SerializedName("options")
    private ArrayList<Options> optionsList = new ArrayList<>();
    // Optional field for the selected item in cart.

    private Items itemOriginalReference;

    private int itemQuantity;

    @SerializedName("item_available_on")
    private ArrayList<String> itemAvailableOnList;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("item_tags")
    private ArrayList<String> itemTagsList;

    @SerializedName("item_availability")
    private ItemAvailability itemAvailability;

    public Items getItemOriginalReference() {
        return itemOriginalReference;
    }

    public void setItemOriginalReference(Items itemOriginalReference) {
        this.itemOriginalReference = itemOriginalReference;
    }

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
        Items newItem = (Items) this;
        if (obj instanceof Items) {
            Items oldItem = (Items) obj;
            if (oldItem != null &&
                    newItem.id.equals(oldItem.id) &&
                    isOptionListEqual(newItem, oldItem)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOptionListEqual(Items newItem, Items oldItem) {
        if (newItem.getOptionsList().size() > 0) {
            Options newOption = newItem.getOptionsList().get(0);
            Options oldOption = oldItem.getOptionsList().get(0);
            return (newOption.getId().equals(oldOption.getId()) &&
                    isSubOptionListEqual(newOption, oldOption) &&
                    isAddOnListEqual(newOption, oldOption));
        }
        return true;
    }

    private boolean isSubOptionListEqual(Options newOption, Options oldOption) {
        if (newOption.getSubOptionsList().size() > 0) {
            for (int i = 0; i < newOption.getSubOptionsList().size(); i++) {
                if (!newOption.getSubOptionsList().get(i).getSubOptionSetList().get(0).getId().equals(
                        oldOption.getSubOptionsList().get(i).getSubOptionSetList().get(0).getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAddOnListEqual(Options newOption, Options oldOption) {
        if (newOption.getAddonsList().size() > 0) {
            for (int i = 0; i < newOption.getAddonsList().size(); i++) {
                if (!isAddOnSetListEqual(newOption.getAddonsList().get(i).getAddonSetList(), oldOption.getAddonsList().get(i).getAddonSetList())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAddOnSetListEqual(ArrayList<AddonSet> newAddonSetList, ArrayList<AddonSet> oldAddonSetList) {
        if (newAddonSetList.size() != oldAddonSetList.size()) {
            return false;
        } else {
            for (int i = 0; i < newAddonSetList.size(); i++) {
                AddonSet newAddonSet = newAddonSetList.get(i);
                boolean newAddonSetFound = false;
                for (int j = 0; j < oldAddonSetList.size(); j++) {
                    AddonSet oldAddonSet = oldAddonSetList.get(j);
                    if (newAddonSet.getId().equals(oldAddonSet.getId())) {
                        newAddonSetFound = true;
                        break;
                    }
                }//inner loop
                if (!newAddonSetFound) {
                    return false;
                }
            } //outer loop
        }
        return true;
    }

    public ArrayList<String> getCustomisationList() {
        ArrayList<String> customisationList = new ArrayList<String>();
        if (this.getOptionsList().size() == 1) {
            String optionValue = this.getOptionsList().get(0).getOptionValue();
            customisationList.add(optionValue);
            for (int i = 0; i < this.getOptionsList().get(0).getSubOptionsList().size(); i++) {
                String subOptionValue = this.getOptionsList().get(0).getSubOptionsList().get(i).getSubOptionSetList().get(0).getSubOptionValue();
                customisationList.add(subOptionValue);
            }
            for (int i = 0; i < this.getOptionsList().get(0).getAddonsList().size(); i++) {
                for (int j = 0; j < this.getOptionsList().get(0).getAddonsList().get(i).getAddonSetList().size(); j++) {
                    String addonValue = this.getOptionsList().get(0).getAddonsList().get(i).getAddonSetList().get(j).getAddonValue();
                    customisationList.add(addonValue);
                }
            }
        }
        return customisationList;
    }
}
