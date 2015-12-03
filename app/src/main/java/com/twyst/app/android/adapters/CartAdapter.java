package com.twyst.app.android.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.NotificationData;

/**
 * Created by Vipul Sharma on 12/3/2015.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_card, parent, false);

        CartViewHolder viewHolder = new CartViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {

        final View view = holder.itemView;
        final Resources resources = view.getContext().getResources();

        holder.menuItemName.setText("Item" + position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{

        ImageView mIvMinus;
        ImageView mIvPLus;
        TextView menuItemName;
        TextView tvQuantity;
        TextView tvCost;


        public CartViewHolder(View itemView) {
            super(itemView);
            this.mIvMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
            this.mIvPLus = (ImageView) itemView.findViewById(R.id.ivPlus);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
            this.tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);

        }
    }
}
