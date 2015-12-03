package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Sections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vipul Sharma on 11/20/2015.
 */
public class MenuAdapter extends BaseExpandableListAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    ArrayList<Sections> mSectionsList;
    private List<Integer> mGroupPositionsInflated = new ArrayList<>();

    Map<String,ChildViewHolder> mChildViewHolderMap = new HashMap<>();

    public MenuAdapter(Context context, ArrayList<Sections> sectionsList) {
        mContext = context;
        mSectionsList = sectionsList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String positionString = String.valueOf(groupPosition) + String.valueOf(childPosition);
        ChildViewHolder childViewHolder = (ChildViewHolder) mChildViewHolderMap.get(positionString);

//        int positions = Integer.parseInt(String.valueOf(groupPosition) + String.valueOf(childPosition));
        if (childViewHolder==null) {
//            mGroupPositionsInflated.add(positions);
            convertView = mLayoutInflater.inflate(R.layout.layout_menu_card, parent, false);
            childViewHolder = new ChildViewHolder(convertView);
            mChildViewHolderMap.put(positionString, childViewHolder);
//            convertView.setTag(childViewHolder);
        }
//        } else {
//
//            childViewHolder = (ChildViewHolder) convertView.getTag();
//        }

        childViewHolder.mIvPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }

        });
        childViewHolder.menuItemName.setText(mSectionsList.get(groupPosition).getItemsList().get(childPosition).getItemName());
        return convertView;
    }

    private void add() {
//        String positionString = String.valueOf(get) + String.valueOf(childPosition);
//        ChildViewHolder childViewHolder = mChildViewHolderMap.get(positionString);
//        childViewHolder.mIvMinus.setVisibility(View.VISIBLE);
    }

    @Override
    public int getGroupCount() {
        int groupCount = mSectionsList.size();
        return groupCount;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = mSectionsList.get(groupPosition).getItemsList().size();
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSectionsList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSectionsList.get(groupPosition).getItemsList().get(childPosition);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public final class GroupViewHolder {

        TextView mContactName;
        ImageView mContactImage;
    }

    private class ChildViewHolder {
        ImageView mIvMinus;
        ImageView mIvPLus;
        TextView menuItemName;

        public ChildViewHolder(View itemView) {
            this.mIvMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
            this.mIvPLus = (ImageView) itemView.findViewById(R.id.ivPlus);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
        }
    }

}
