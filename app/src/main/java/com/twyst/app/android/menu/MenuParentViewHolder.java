package com.twyst.app.android.menu;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 12/22/2015.
 */
public class MenuParentViewHolder extends ParentViewHolder {
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public TextView text;
    public ImageView expandedImage;
    public RelativeLayout rlGroup;

    public MenuParentViewHolder(View itemView) {
        super(itemView);

        text = (TextView) itemView.findViewById(R.id.menu_group_text);
        expandedImage = (ImageView) itemView.findViewById(R.id.menu_group_arrow);
        rlGroup = (RelativeLayout) itemView.findViewById(R.id.rlGroup);
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (!HONEYCOMB_AND_ABOVE) {
            return;
        }

        if (expanded) {
            expandedImage.setRotation(INITIAL_POSITION);
        } else {
            expandedImage.setRotation(ROTATED_POSITION);
        }
    }
}