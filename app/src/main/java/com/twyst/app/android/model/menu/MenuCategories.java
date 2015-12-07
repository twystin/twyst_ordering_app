package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 12/7/2015.
 */
public class MenuCategories implements Serializable {
    @SerializedName("sub_categories")
    private ArrayList<SubCategories> subCategoriesList;

    @SerializedName("category_name")
    private String categoryName;

    public ArrayList<SubCategories> getSubCategoriesList() {
        return subCategoriesList;
    }

    public void setSubCategoriesList(ArrayList<SubCategories> subCategoriesList) {
        this.subCategoriesList = subCategoriesList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
