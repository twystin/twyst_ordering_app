package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.FoodVouchersActivity;

import java.util.ArrayList;

/**
 * Created by Raman on 2/3/2016.
 */
public class FoodVouchersAdapter extends RecyclerView.Adapter<FoodVouchersAdapter.ViewHolder> {
    ArrayList<FoodVouchersActivity.MyData> mVoucherList;
    private final Context mContext;
    public FoodVouchersAdapter(Context context,ArrayList<FoodVouchersActivity.MyData> VouchersList) {
        mContext = context;
        mVoucherList =VouchersList;
    }

    @Override
    public FoodVouchersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_vouchers_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodVouchersAdapter.ViewHolder holder, int position) {
        holder.headerTV.setText(mVoucherList.get(position).getAmount1());
        holder.bucksTV.setText(mVoucherList.get(position).getAmount2());
    }

    @Override
    public int getItemCount() {
        return mVoucherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView headerTV;
        public TextView bucksTV;

        public ViewHolder(View view) {
            super(view);
            headerTV = (TextView) view.findViewById(R.id.tv_header_food_vouchers);
            bucksTV = (TextView) view.findViewById(R.id.tv_twystbucks_food_voucher);
        }
    }
}
