package com.twyst.app.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/10/2015.
 */
public class MenuOptionsAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Options> mOptionsList = new ArrayList<>();
    private int selectedPosition = -1;

    public MenuOptionsAdapter(Context context, List<Options> optionsList) {
        super();
        mContext = context;
        mOptionsList = optionsList;
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

        rbOption.setText(mOptionsList.get(position).getOptionValue());
        rbOption.setChecked(position == selectedPosition);

        tvCost.setText(Utils.costString(mOptionsList.get(position).getOptionCost()));
        return convertView;
    }

    @Override
    public int getCount() {
        return mOptionsList.size();
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
