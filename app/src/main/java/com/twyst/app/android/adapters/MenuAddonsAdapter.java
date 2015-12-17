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
import com.twyst.app.android.model.menu.AddonSet;
import com.twyst.app.android.model.menu.SubOptionSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/17/2015.
 */
public class MenuAddonsAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<AddonSet> mAddonsSetList = new ArrayList<>();
    private int selectedPosition = -1;

    public MenuAddonsAdapter(Context context, List<AddonSet> addonSetList) {
        super();
        mContext = context;
        mAddonsSetList = addonSetList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_menu_options, null);
        }

        CheckBox cbOption = (CheckBox) convertView.findViewById(R.id.cbOption);
        RadioButton rbOption = (RadioButton) convertView.findViewById(R.id.rbOption);
        TextView tvCost = (TextView) convertView.findViewById(R.id.tvCost);

        cbOption.setVisibility(View.VISIBLE);
        rbOption.setVisibility(View.GONE);

        cbOption.setText(mAddonsSetList.get(position).getAddonValue());
        cbOption.setChecked(position == selectedPosition);

        tvCost.setText(mAddonsSetList.get(position).getAddonCost());
        return convertView;
    }

    @Override
    public int getCount() {
        return mAddonsSetList.size();
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