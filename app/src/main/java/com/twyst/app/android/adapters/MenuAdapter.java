package com.twyst.app.android.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 11/20/2015.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_card, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 10, 10, -5);
        //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, actionBarActivity.getResources().getDisplayMetrics());
        v.setLayoutParams(layoutParams);

        MenuViewHolder vh = new MenuViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MenuViewHolder menuViewHolder = (MenuViewHolder) holder;

        TextView menuItem = (TextView) menuViewHolder.itemView.findViewById(R.id.menuItem);
        if (position == 1) {
            menuItem.setText("position1");
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView menuItem;

        public MenuViewHolder(View itemView) {
            super(itemView);
            menuItem = (TextView) itemView.findViewById(R.id.menuItem);
        }
    }
}
