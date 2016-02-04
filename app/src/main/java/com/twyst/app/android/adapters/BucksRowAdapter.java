package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.TwystBucksHistoryActivity;

import java.util.ArrayList;

/**
 * Created by Raman on 2/4/2016.
 */
public class BucksRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<TwystBucksHistoryActivity.BucksHistory> mList;

    public BucksRowAdapter(Context mContext, ArrayList<TwystBucksHistoryActivity.BucksHistory> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bucks_row, parent, false);
        RowsViewHolder vh = new RowsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowsViewHolder mHolder = (RowsViewHolder) holder;
        mHolder.dateTV.setText(mList.get(position).getDate());
        mHolder.yearTV.setText(mList.get(position).getYear());
        mHolder.orderNameTV.setText(mList.get(position).getItemName());
        mHolder.orderAmountTV.setText(mList.get(position).getTransactionAmount());
        mHolder.transactionTV.setText(mList.get(position).getTransaction());
        mHolder.transactionAmountTV.setText(mList.get(position).getAmount());
        mHolder.balanceTV.setText(mList.get(position).getBalance());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class RowsViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV;
        TextView yearTV;
        TextView orderNameTV;
        TextView orderAmountTV;
        TextView transactionTV;
        TextView transactionAmountTV;
        TextView balanceTV;

        public RowsViewHolder(View itemView) {
            super(itemView);
            dateTV = (TextView) itemView.findViewById(R.id.tv_date);
            yearTV = (TextView) itemView.findViewById(R.id.tv_year);
            orderNameTV = (TextView) itemView.findViewById(R.id.tv_order_name);
            orderAmountTV = (TextView) itemView.findViewById(R.id.tv_order_amount);
            transactionTV = (TextView) itemView.findViewById(R.id.tv_transaction);
            transactionAmountTV = (TextView) itemView.findViewById(R.id.tv_transaction_amount);
            balanceTV = (TextView) itemView.findViewById(R.id.tv_balance);
        }
    }

}
