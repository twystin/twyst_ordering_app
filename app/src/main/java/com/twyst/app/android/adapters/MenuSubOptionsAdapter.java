package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubOptionSet;
import com.twyst.app.android.model.menu.SubOptions;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/11/2015.
 */
public class MenuSubOptionsAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<SubOptionSet> mSubOptionsSetList = new ArrayList<>();
    private int selectedPosition = -1;

    public MenuSubOptionsAdapter(Context context, List<SubOptionSet> subOptionSetListList) {
        super();
        mContext = context;
        mSubOptionsSetList = subOptionSetListList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_menu_options, null);
        }

        CheckBox cbOption = (CheckBox) convertView.findViewById(R.id.cbOption);
        RadioButton rbOption = (RadioButton) convertView.findViewById(R.id.rbOption);
        TextView tvCost = (TextView) convertView.findViewById(R.id.tvCost);

        cbOption.setVisibility(View.GONE);
        rbOption.setVisibility(View.VISIBLE);

        rbOption.setText(mSubOptionsSetList.get(position).getSubOptionValue());
        rbOption.setChecked(position == selectedPosition);

        tvCost.setText(Utils.costString(mSubOptionsSetList.get(position).getSubOptionCost()));
        return convertView;
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