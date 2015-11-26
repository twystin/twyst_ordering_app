package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/24/2015.
 */
public class MenuDescription implements Serializable {
    @SerializedName("sections")
    private ArrayList<Sections> sectionsList;

    @SerializedName("menu_category")
    private String menuCategory;

    public ArrayList<Sections> getSectionList() {
        return sectionsList;
    }

    public void setSectionList(ArrayList<Sections> sectionList) {
        this.sectionsList = sectionList;
    }

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }
}
