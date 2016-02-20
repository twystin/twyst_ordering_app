package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.VoucherDetailsActivity;

import java.util.ArrayList;

/**
 * Created by Raman on 2/1/2016.
 */
public class VoucherDetailsAdapter extends RecyclerView.Adapter<VoucherDetailsAdapter.ViewHolder> {
    ArrayList<VoucherDetailsActivity.MyData> mData;
    Context mContext;

    public VoucherDetailsAdapter(Context context, ArrayList<VoucherDetailsActivity.MyData> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public VoucherDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_details_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VoucherDetailsAdapter.ViewHolder holder, int position) {
        holder.offerAmountTV.setText(mData.get(position).getAmount1());
        holder.bucksAmountTV.setText(mData.get(position).getAmount2());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView offerAmountTV;
        public TextView bucksAmountTV;

        public ViewHolder(View view) {
            super(view);
            offerAmountTV = (TextView) view.findViewById(R.id.offer_amount);
            bucksAmountTV = (TextView) view.findViewById(R.id.tv_bucks_amount_voucher);
        }
    }

}
