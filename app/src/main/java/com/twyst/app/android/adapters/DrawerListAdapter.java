package com.twyst.app.android.adapters;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.twyst.app.android.R;
import com.twyst.app.android.model.DrawerItem;

/**
 * Created by satish on 05/12/14.
 */
public class DrawerListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<DrawerItem> drawerItems;
    private ActionBarActivity actionBarActivity;

    public DrawerListAdapter(ActionBarActivity actionBarActivity, LayoutInflater layoutInflater, ArrayList<DrawerItem> drawerItems) {
        this.layoutInflater = layoutInflater;
        this.actionBarActivity = actionBarActivity;
        this.drawerItems = drawerItems;
    }

    @Override
    public int getCount() {
        return drawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, null);
            holder = new DrawerItemHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.drawer_item_title);
            holder.iconView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
            holder.tvNotification = (TextView) convertView.findViewById(R.id.drawer_item_notification);

            convertView.setTag(holder);
        } else {
            holder = (DrawerItemHolder) convertView.getTag();
        }

        DrawerItem drawerItem = drawerItems.get(position);
        holder.titleTextView.setText(drawerItem.getTitle());
        holder.iconView.setImageResource(drawerItem.getIcon());

        if (drawerItem.isNotifcation_needed()) {
            holder.tvNotification.setVisibility(View.VISIBLE);
            holder.tvNotification.setText(Integer.toString(drawerItem.getNotification_text()));
        } else {
            holder.tvNotification.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    static class DrawerItemHolder {
        ImageView iconView;
        TextView titleTextView;
        TextView tvNotification;
    }

}
