package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Sections;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/20/2015.
 */
public class MenuAdapter extends BaseExpandableListAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    ArrayList<Sections> mSectionsList;

//    private final String[] mGroups = {
//            "Cupcake",
//            "Donut",
//            "Eclair",
//            "Froyo",
//            "Gingerbread",
//            "Honeycomb",
//            "Ice Cream Sandwich",
//            "Jelly Bean",
//            "KitKat"
//    };

//    private final int[] mGroupDrawables = {
//            R.drawable.cupcake,
//            R.drawable.donut,
//            R.drawable.eclair,
//            R.drawable.froyo,
//            R.drawable.gingerbread,
//            R.drawable.honeycomb,
//            R.drawable.ice_cream_sandwich,
//            R.drawable.jelly_bean,
//            R.drawable.kitkat
//    };

//    private final String[][] mChilds = {
//            {"1.5"},
//            {"1.6"},
//            {"2.0","2.0.1","2.1"},
//            {"2.2","2.2.1","2.2.2","2.2.3"},
//            {"2.3","2.3.1","2.3.2","2.3.3","2.3.4","2.3.5","2.3.6","2.3.7"},
//            {"3.0","3.1","3.2","3.2.1","3.2.2","3.2.3","3.2.4","3.2.5","3.2.6"},
//            {"4.0", "4.0.1", "4.0.2", "4.0.3", "4.0.4"},
//            {"4.1", "4.1.1", "4.1.2", "4.2", "4.2.1", "4.2.2", "4.3", "4.3.1"},
//            {"4.4"}
//    };

    public MenuAdapter(Context context, ArrayList<Sections> sectionsList) {
        mContext = context;
        mSectionsList = sectionsList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_menu_group, parent, false);
        }

        final TextView text = (TextView) convertView.findViewById(R.id.menu_group_text);
        text.setText(mSectionsList.get(groupPosition).getSectionName());

        final ImageView expandedImage = (ImageView) convertView.findViewById(R.id.menu_group_arrow);
        final int resId = isExpanded ? R.drawable.minus : R.drawable.plus;
        expandedImage.setImageResource(resId);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_menu_card, parent, false);
        }

        final TextView text = (TextView) convertView.findViewById(R.id.menuItem);
        text.setText(mSectionsList.get(groupPosition).getItemsList().get(childPosition).getItemName());//mChilds[groupPosition][childPosition]);

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return mSectionsList.size(); //mGroups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSectionsList.get(groupPosition).getItemsList().size(); //mChilds[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
       return mSectionsList.get(groupPosition);//mGroups[i];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSectionsList.get(groupPosition).getItemsList();//mChilds[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_card, parent, false);
//
//        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.setMargins(10, 10, 10, -5);
//        //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, actionBarActivity.getResources().getDisplayMetrics());
//        v.setLayoutParams(layoutParams);
//
//        MenuViewHolder vh = new MenuViewHolder(v);
//        return vh;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
//
//        TextView menuItem = (TextView) menuViewHolder.itemView.findViewById(R.id.menuItem);
//        if (position == 1) {
//            menuItem.setText("position1");
//        }
//    }

//    @Override
//    public int getItemCount() {
//        return 7;
//    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView menuItem;

        public MenuViewHolder(View itemView) {
            super(itemView);
            menuItem = (TextView) itemView.findViewById(R.id.menuItem);
        }
    }
}
