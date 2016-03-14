package com.twyst.app.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.TimeStamp;
import com.twyst.app.android.model.TwystCashHistory;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

/**
 * Created by raman on 2/4/2016.
 */
public class CashRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<TwystCashHistory> mList = new ArrayList<>();

    public CashRowAdapter(Context mContext, ArrayList<TwystCashHistory> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cash_row, parent, false);
        RowsViewHolder vh = new RowsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowsViewHolder mHolder = (RowsViewHolder) holder;
        TimeStamp timeStamp = Utils.getTimeStamp(mList.get(position).getEarn_at());
        String date = timeStamp.getDate();
        String day = date.substring(0, 6);
        String year = date.substring(7, 11);
        mHolder.dateTV.setText(day);
        mHolder.yearTV.setText(year);
        mHolder.messageTV.setText(mList.get(position).getMessage());
        boolean transaction = mList.get(position).isEarn();
        if (transaction) {
            mHolder.transactionAmountTV.setText("+" + mList.get(position).getTwyst_cash());
            mHolder.transactionAmountTV.setTextColor(mContext.getResources().getColor(R.color.transaction_green));
        } else if (!transaction) {
            mHolder.transactionAmountTV.setText("-" + mList.get(position).getTwyst_cash());
            mHolder.transactionAmountTV.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class RowsViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV;
        TextView yearTV;
        TextView messageTV;
        TextView transactionAmountTV;

        public RowsViewHolder(View itemView) {
            super(itemView);
            dateTV = (TextView) itemView.findViewById(R.id.tv_date);
            yearTV = (TextView) itemView.findViewById(R.id.tv_year);
            messageTV = (TextView) itemView.findViewById(R.id.tv_message);
            transactionAmountTV = (TextView) itemView.findViewById(R.id.tv_transaction_amount);
        }
    }

}
