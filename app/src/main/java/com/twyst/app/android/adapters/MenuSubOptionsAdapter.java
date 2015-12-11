package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubOptionSet;
import com.twyst.app.android.model.menu.SubOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/11/2015.
 */
public class MenuSubOptionsAdapter extends BaseAdapter {
    private final Context mContext;
    private List<SubOptionSet> mSubOptionsSetList = new ArrayList<>();
    private int selectedPosition = -1;

    public MenuSubOptionsAdapter(Context context, List<SubOptionSet> subOptionsList) {
        super();
        mContext = context;
        mSubOptionsSetList = subOptionsList;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_menu_options, null);
        }
//        TextView tv = (TextView)v.findViewById(R.id.textview);
//        tv.setText("Text view #" + position);
        RadioButton r = (RadioButton)v.findViewById(R.id.rbOption);
        TextView tvCost = (TextView)v.findViewById(R.id.tvCost);
        tvCost.setText(mSubOptionsSetList.get(position).getSubOptionCost());
        r.setText(mSubOptionsSetList.get(position).getSubOptionValue());
        r.setChecked(position == selectedPosition);
        r.setTag(position);
//        r.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectedPosition = (Integer)view.getTag();
//                notifyDataSetChanged();
//            }
//        });
        return v;
    }

    @Override
    public int getCount() {
        return mSubOptionsSetList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}