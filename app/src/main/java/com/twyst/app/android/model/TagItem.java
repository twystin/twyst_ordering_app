package com.twyst.app.android.model;

/**
 * Created by anshul on 1/14/2016.
 */
public class TagItem {

    private String name;
    private int icon;

    public TagItem(String name, int icon){
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
