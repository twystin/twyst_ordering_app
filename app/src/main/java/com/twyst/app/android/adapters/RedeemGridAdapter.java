package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;

/**
 * Created by tushar on 20/01/16.
 */
public class RedeemGridAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mText;
    private int[] mImageIds;

    public RedeemGridAdapter(Context c, String[] s, int[] ids) {
        mContext = c;
        mText = s;
        mImageIds = ids;
    }

    @Override
    public int getCount() {
        return mText.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem = convertView;
        if (gridItem == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInflater inflater = LayoutInflater.from(mContext);
            gridItem = inflater.inflate(R.layout.redeem_grid_item, parent, false);
            ((TextView) gridItem.findViewById(R.id.tv_redeem_grid)).setText(mText[position]);
            ((ImageView) gridItem.findViewById(R.id.iv_redeem_grid)).setImageResource(mImageIds[position]);
        }

        return gridItem;
    }
}
