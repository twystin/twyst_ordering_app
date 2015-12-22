package com.twyst.app.android.model.menu;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class SubCategories implements Serializable, ParentListItem {
    @SerializedName("_id")
    private String id;

    @SerializedName("items")
    private ArrayList<Items> itemsList;

    @SerializedName("sub_category_name")
    private String subCategoryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Items> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<Items> itemsList) {
        this.itemsList = itemsList;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    // implementing ParentListItem interface
    @Override
    public List<?> getChildItemList() {
        return getItemsList();
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
