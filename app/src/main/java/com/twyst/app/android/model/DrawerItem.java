package com.twyst.app.android.model;

/**
 * Created by satish on 14/12/14.
 */
public class DrawerItem {

    public DrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    private int icon;
//    private int selectedIcon;

    private boolean selected;
    private String title;
    private boolean notifcation_needed = false;
    private int notification_text;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

/*    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }*/

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isNotifcation_needed() {
        return notifcation_needed;
    }

    public void setNotifcation_needed(boolean notifcation_needed) {
        this.notifcation_needed = notifcation_needed;
    }

    public int getNotification_text() {
        return notification_text;
    }

    public void setNotification_text(int notification_text) {
        this.notification_text = notification_text;
    }
}
