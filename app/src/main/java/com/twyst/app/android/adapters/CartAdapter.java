package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.menu.MenuChildViewHolder;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/3/2015.
 */
public class CartAdapter extends RecyclerView.Adapter<MenuChildViewHolder> {
    private static int mVegIconHeight = 0; //menuItemName height fixed for a specific device

    private final Context mContext;
    DataTransferInterfaceCart mDataTransferInterfaceCart;
    private ArrayList<Items> mCartItemsList = new ArrayList<>();

    public CartAdapter(Context context) {
        mContext = context;
        mDataTransferInterfaceCart = (DataTransferInterfaceCart) context;
    }

    public ArrayList<Items> getmCartItemsList() {
        return mCartItemsList;
    }

    public void setmCartItemsList(ArrayList<Items> mCartItemsList) {
        this.mCartItemsList = mCartItemsList;
    }

    @Override
    public MenuChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu, parent, false);
        MenuChildViewHolder viewHolder = new MenuChildViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MenuChildViewHolder holder, int position) {
        final View view = holder.itemView;
        final Resources resources = view.getContext().getResources();

        final Items item = mCartItemsList.get(position);
        holder.menuItemName.setText(item.getItemName());
        holder.tvCost.setText(Utils.costString(item.getItemCost()));

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
            final LinearLayout llCustomisationsFinal = holder.llCustomisations;
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
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llCustomisationsFinal.getLayoutParams();
                            params.setMargins((mVegIconHeight + tvMenuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
                            llCustomisationsFinal.setLayoutParams(params);

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
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.llCustomisations.getLayoutParams();
            params.setMargins((mVegIconHeight + holder.menuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
            holder.llCustomisations.setLayoutParams(params);

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

        ArrayList<String> customisationList = item.getCustomisationList();

        final LinearLayout hiddenLayout = holder.llCustomisations;
        final TextView menuItemNameFinal = holder.menuItemName;
        if (customisationList.size() != 0) {
            final TextView[] textViews = new TextView[customisationList.size()];
            for (int i = 0; i < customisationList.size(); i++) {
                textViews[i] = new TextView(mContext);
                textViews[i].setText(customisationList.get(i));
                textViews[i].setTextColor(mContext.getResources().getColor(R.color.customisations_text_color));
                textViews[i].setTextSize(12.0f);
                textViews[i].setPadding(15, 4, 15, 4);
                textViews[i].setBackgroundResource(R.drawable.border_customisations);
            }

            hiddenLayout.setVisibility(View.VISIBLE);
            hiddenLayout.post(new Runnable() {
                @Override
                public void run() {
                    int maxWidth = hiddenLayout.getWidth();
                    Utils.populateText(hiddenLayout, textViews, mContext, maxWidth - mVegIconHeight - menuItemNameFinal.getCompoundDrawablePadding());
                }
            });

        } else {
            if (hiddenLayout.getChildCount() != 0) {
                hiddenLayout.removeAllViews();
            }
            hiddenLayout.setVisibility(View.GONE);
        }

        holder.menuItemBreadCrumb.setVisibility(View.GONE);
        holder.menuItemDesc.setVisibility(View.GONE);

    }

    public void addToCart(Items cartItemToBeAdded) {
        if (mCartItemsList.contains(cartItemToBeAdded)) {
            int index = mCartItemsList.indexOf(cartItemToBeAdded);
            Items existingCartItem = mCartItemsList.get(index);
            existingCartItem.setItemQuantity(existingCartItem.getItemQuantity() + 1);
        } else {
            cartItemToBeAdded.setItemQuantity(1);
            mCartItemsList.add(cartItemToBeAdded);
        }
    }

    public String getTotalCost() {
        double totalCost = 0;
        for (int i = 0; i < mCartItemsList.size(); i++) {
            double itemCost = mCartItemsList.get(i).getItemCost() * mCartItemsList.get(i).getItemQuantity();
            totalCost = totalCost + itemCost;
        }
        return Utils.costString(totalCost);
    }

    private void add(Items cartItem) {
        mDataTransferInterfaceCart.addCart(cartItem);
    }

    private void remove(Items cartItem) {
        mDataTransferInterfaceCart.removeCart(cartItem);
    }

    @Override
    public int getItemCount() {
        return mCartItemsList.size();
    }

    public interface DataTransferInterfaceCart {
        void addCart(Items item);

        void removeCart(Items item);
    }
}
