package com.twyst.app.android.layout;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 2/9/2016.
 */
public class NoDataHolder {
    public TextView tvNoData;
    public View noDataOutlet;
    public ImageView ivNoData;

    public NoDataHolder(View view) {
        this.tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        this.noDataOutlet = view.findViewById(R.id.no_data_outlet);
        this.ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);

    }
}
