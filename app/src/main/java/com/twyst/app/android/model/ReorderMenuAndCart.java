package com.twyst.app.android.model;

import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.MenuData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anshul on 1/24/2016.
 */
public class ReorderMenuAndCart implements Serializable {

    private MenuData menuData;

    private ArrayList<Items> cartItemsList;

    public MenuData getMenuData() {
        return menuData;
    }

    public void setMenuData(MenuData menuData) {
        this.menuData = menuData;
    }

    public ArrayList<Items> getCartItemsList() {
        return cartItemsList;
    }

    public void setCartItemsList(ArrayList<Items> cartItemsList) {
        this.cartItemsList = cartItemsList;
    }
}
