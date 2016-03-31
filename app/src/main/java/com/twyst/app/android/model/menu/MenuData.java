package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/24/2015.
 */
public class MenuData implements Serializable{
    @SerializedName("menu_categories")
    private ArrayList<MenuCategories> menuCategoriesList;

    private String outlet;

    @SerializedName("_id")
    private String id;

    @SerializedName("menu_type")
    private String menuType;

    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<MenuCategories> getMenuCategoriesList() {
        return menuCategoriesList;
    }

    public void setMenuCategoriesList(ArrayList<MenuCategories> menuCategoriesList) {
        this.menuCategoriesList = menuCategoriesList;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
