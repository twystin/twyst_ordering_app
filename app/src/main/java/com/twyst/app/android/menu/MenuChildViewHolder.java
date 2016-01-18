package com.twyst.app.android.menu;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 12/22/2015.
 */
public class MenuChildViewHolder extends ChildViewHolder {
    public ImageView mIvMinus;
    public ImageView mIvPLus;
    public TextView menuItemName;
    public LinearLayout llCustomisations;
    public TextView menuItemBreadCrumb;
    public TextView menuItemDesc;
    public TextView tvQuantity;
    public TextView tvCost;

    public MenuChildViewHolder(View itemView) {
        super(itemView);
        this.mIvMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
        this.mIvPLus = (ImageView) itemView.findViewById(R.id.ivPlus);
        this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
        this.llCustomisations = (LinearLayout) itemView.findViewById(R.id.llCustomisations);
        this.menuItemBreadCrumb = (TextView) itemView.findViewById(R.id.item_bread_crumb);
        this.menuItemDesc = (TextView) itemView.findViewById(R.id.item_desc);
        this.tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
        this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);
    }
}
