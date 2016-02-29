package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.ShoppingVoucherListActivity;
import com.twyst.app.android.model.Cashback;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

/**
 * Created by Raman on 2/1/2016.
 */
public class ShoppingVouchersAdapter extends RecyclerView.Adapter<ShoppingVouchersAdapter.ViewHolder> {
    private final ArrayList<Cashback> mVouchersList;
    private final Context mContext;

    public ShoppingVouchersAdapter(Context context, ArrayList<Cashback> mVouchersList) {
        this.mVouchersList = mVouchersList;
        mContext = context;
    }

    @Override
    public ShoppingVouchersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_vouchers_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShoppingVouchersAdapter.ViewHolder holder, final int position) {

        // Load image into holder from Picasso
        Picasso picasso = Picasso.with(mContext);
        final String imgUri = mVouchersList.get(position).getMerchant_logo();
        if (imgUri != null) {
            picasso.load(imgUri)
                    .noFade()
                    .into(holder.voucherIV);
        }

        // Set ClickListener and take to all vouchers display activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingVoucherListActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable(AppConstants.BUNDLE_CASHBACK_OFFERS, mVouchersList.get(position));
                intent.putExtras(extra);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVouchersList.size();
    }

    /*
        ViewHolder Class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView voucherIV;

        public ViewHolder(View view) {
            super(view);
            voucherIV = (ImageView) view.findViewById(R.id.iv_voucher_logo);
        }
    }
}
