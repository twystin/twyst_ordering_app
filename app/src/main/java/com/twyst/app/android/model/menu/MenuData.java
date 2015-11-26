package com.twyst.app.android.model.menu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/24/2015.
 */
public class MenuData implements Serializable{
    private Outlet outlet;

    @SerializedName("menu_type")
    private String menuType;

    private String _id;

    private String status;

    @SerializedName("menu_description")
    private ArrayList<MenuDescription> menuDescriptionList;

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<MenuDescription> getMenuDescriptionList() {
        return menuDescriptionList;
    }

    public void setMenuDescriptionList(ArrayList<MenuDescription> menuDescriptionList) {
        this.menuDescriptionList = menuDescriptionList;
    }
}
