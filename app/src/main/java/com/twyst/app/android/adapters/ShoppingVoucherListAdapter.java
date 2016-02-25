package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.CashbackOffers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Raman on 2/1/2016.
 */
public class ShoppingVoucherListAdapter extends RecyclerView.Adapter<ShoppingVoucherListAdapter.ViewHolder> {
    ArrayList<CashbackOffers> mOffers;
    Context mContext;

    public ShoppingVoucherListAdapter(Context context, ArrayList<CashbackOffers> offers) {
        mContext = context;
        mOffers = offers;
    }

    @Override
    public ShoppingVoucherListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_list_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShoppingVoucherListAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
//        holder.offerAmountTV.setText(mOffers.get(position).getOffer_cost());
//        holder.bucksAmountTV.setText(mOffers.get(position).getOffer_processing_fee());
//        holder.validDate.setText(String.format("Valid till %s",getDate(mOffers.get(position).getOffer_end_date())));
    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView offerAmountTV;
        public TextView bucksAmountTV;
        public TextView validDate;

        public ViewHolder(View view) {
            super(view);
            offerAmountTV = (TextView) view.findViewById(R.id.offer_amount);
            bucksAmountTV = (TextView) view.findViewById(R.id.tv_bucks_amount_voucher);
            validDate = (TextView) view.findViewById(R.id.tvValidDate);
        }
    }

    public String getDate(String validDate) {
        String date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);

        try {
            Date d = sdf.parse(validDate);
            date = dateInstance.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
