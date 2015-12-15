package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    private static int mVegIconHeight = 0; //menuItemName height fixed for a specific device

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

    public void addToAdapter(Items item) {
        if (mCartItemsList.contains(item)) {
            mCartItemsList.set(mCartItemsList.indexOf(item), item);
        } else {
            mCartItemsList.add(item);
        }
        this.notifyDataSetChanged();
    }

    public void removeFromAdapter(Items item) {
        if (mCartItemsList.contains(item)) {
            int index = mCartItemsList.indexOf(item);
            if (item.getItemQuantity() == 0) {
                mCartItemsList.remove(index);
            } else {
                mCartItemsList.set(index, item);
            }
        } else {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu, parent, false);
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
//        holder.tvCalculatedCost.setText(String.valueOf(calculatedCost));
        if (item.getItemQuantity() == 0) {
            holder.mIvMinus.setVisibility(View.INVISIBLE);
            holder.tvQuantity.setVisibility(View.INVISIBLE);
        } else {
            holder.mIvMinus.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(String.valueOf(item.getItemQuantity()));
        }

        if (mVegIconHeight == 0) {
            final TextView tvMenuItemName = holder.menuItemName;
            tvMenuItemName.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Drawable img;
                            if (item.isVegetarian()) {
                                img = mContext.getResources().getDrawable(
                                        R.drawable.veg);
                            } else {
                                img = mContext.getResources().getDrawable(
                                        R.drawable.nonveg);
                            }
                            mVegIconHeight = tvMenuItemName.getMeasuredHeight() * 2 / 3;
                            img.setBounds(0, 0, mVegIconHeight, mVegIconHeight);
                            tvMenuItemName.setCompoundDrawables(img, null, null, null);
                            tvMenuItemName.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    });
        } else {
            Drawable img;
            if (item.isVegetarian()) {
                img = mContext.getResources().getDrawable(
                        R.drawable.veg);
            } else {
                img = mContext.getResources().getDrawable(
                        R.drawable.nonveg);
            }
            img.setBounds(0, 0, mVegIconHeight, mVegIconHeight);
            holder.menuItemName.setCompoundDrawables(img, null, null, null);
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

        String customisations = "9inches dip chicken 9inches dip chicken 9inches dip chicken 9inches dip chicken";
        Spannable wordtoSpan = new SpannableString(customisations);
        wordtoSpan.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.selected_text_customisations)), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.selected_text_customisations)), 8, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvCustomisations.setText(wordtoSpan);

    }

    public int getTotalCost() {
        int totalCost = 0;
        for (int i = 0; i < mCartItemsList.size(); i++) {
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

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvMinus;
        ImageView mIvPLus;
        TextView menuItemName;
        TextView tvQuantity;
        TextView tvCost;
        TextView tvCustomisations;

        public CartViewHolder(View itemView) {
            super(itemView);
            this.mIvMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
            this.mIvPLus = (ImageView) itemView.findViewById(R.id.ivPlus);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
            this.tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);
            this.tvCustomisations = (TextView) itemView.findViewById(R.id.tvCustomisations);
        }
    }
}
