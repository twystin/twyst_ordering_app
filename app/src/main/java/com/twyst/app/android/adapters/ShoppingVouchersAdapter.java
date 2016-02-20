package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.VoucherDetailsActivity;

import java.util.ArrayList;

/**
 * Created by Raman on 2/1/2016.
 */
public class ShoppingVouchersAdapter extends RecyclerView.Adapter<ShoppingVouchersAdapter.ViewHolder> {
    private final ArrayList<Integer> mVouchersList;
    private final Context mContext;

    public ShoppingVouchersAdapter(Context context, ArrayList<Integer> mVouchersList) {
        this.mVouchersList = mVouchersList;
        mContext = context;
    }

    @Override
    public ShoppingVouchersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_vouchers_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        View itemView = viewHolder.itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VoucherDetailsActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShoppingVouchersAdapter.ViewHolder holder, int position) {
        holder.voucherIV.setImageResource(mVouchersList.get(position));
    }

    @Override
    public int getItemCount() {
        return mVouchersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView voucherIV;

        public ViewHolder(View view) {
            super(view);
            voucherIV = (ImageView) view.findViewById(R.id.iv_voucher_logo);
        }
    }
}
