package com.twyst.app.android.adapters;

import android.content.Context;
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
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.menu.DataTransferInterface;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.MenuCategories;
import com.twyst.app.android.model.menu.SubCategories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/3/2015.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final Context mContext;
    DataTransferInterface mDataTransferInterface;
    private List<Items> mCartItemsList = new ArrayList<>();

    public CartAdapter(Context context) {
        mContext = context;
        mDataTransferInterface = (DataTransferInterface) context;
    }

    public List<Items> getmCartItemsList() {
        return mCartItemsList;
    }

    public void setmCartItemsList(List<Items> mCartItemsList) {
        this.mCartItemsList = mCartItemsList;
    }

    public void addToAdapter(Items item){
        if (mCartItemsList.contains(item)){
            mCartItemsList.set(mCartItemsList.indexOf(item),item);
        }else{
            mCartItemsList.add(item);
        }
        this.notifyDataSetChanged();
    }

    public void removeFromAdapter(Items item){
        if (mCartItemsList.contains(item)){
            int index = mCartItemsList.indexOf(item);
            if (item.getItemQuantity()==0){
                mCartItemsList.remove(index);
            }else{
                mCartItemsList.set(index,item);
            }
        }else{
            mCartItemsList.add(item);
        }
        this.notifyDataSetChanged();
    }

    public void updateList(ArrayList<Items> items) {
        mCartItemsList.clear();
        mCartItemsList.addAll(items);
        this.notifyDataSetChanged();
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {

        final View view = holder.itemView;
        final Resources resources = view.getContext().getResources();

        final Items item = mCartItemsList.get(position);
        holder.menuItemName.setText(item.getItemName());
        holder.tvCost.setText(item.getItemCost());
        int calculatedCost = Integer.parseInt(item.getItemCost()) * item.getItemQuantity();
        holder.tvCalculatedCost.setText(String.valueOf(calculatedCost));
        if (item.getItemQuantity() == 0) {
            holder.mIvMinus.setVisibility(View.INVISIBLE);
            holder.tvQuantity.setVisibility(View.INVISIBLE);
        } else {
            holder.mIvMinus.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(String.valueOf(item.getItemQuantity()));
        }

        holder.mIvPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(item);
            }
        });

        holder.mIvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(item);
            }
        });

    }

    public int getTotalCost(){
        int totalCost = 0;
        for (int i=0;i<mCartItemsList.size();i++){
            int itemCost = Integer.parseInt(mCartItemsList.get(i).getItemCost()) * mCartItemsList.get(i).getItemQuantity();
            totalCost = totalCost + itemCost;
        }
        return totalCost;
    }

    private void add(Items item) {
        item.setItemQuantity(item.getItemQuantity() + 1);
        this.notifyDataSetChanged();

        mDataTransferInterface.addToCart(item);
    }

    private void remove(Items item) {
        item.setItemQuantity(item.getItemQuantity() - 1);
        this.notifyDataSetChanged();

        mDataTransferInterface.removeFromCart(item);
    }

    @Override
    public int getItemCount() {
        return mCartItemsList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{

        ImageView mIvMinus;
        ImageView mIvPLus;
        TextView menuItemName;
        TextView tvQuantity;
        TextView tvCost;
        TextView tvCalculatedCost;

        public CartViewHolder(View itemView) {
            super(itemView);
            this.mIvMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
            this.mIvPLus = (ImageView) itemView.findViewById(R.id.ivPlus);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
            this.tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);
            this.tvCalculatedCost = (TextView) itemView.findViewById(R.id.tvCalculatedCost);
        }
    }
}
