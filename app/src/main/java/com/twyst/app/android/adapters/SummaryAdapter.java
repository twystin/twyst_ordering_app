package com.twyst.app.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Items> mCartItemsList;
    private static int mVegIconHeight = 0; //menuItemName height fixed for a specific device
    private final Context mContext;
    private final OrderSummary mOrderSummary;
    private final int mFreeItemIndex;
    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_FOOTER = 1;

    private double grandTotal;

    public SummaryAdapter(Context context, OrderSummary orderSummary, int freeItemIndex) {
        this.mOrderSummary = orderSummary;
        mContext = context;
        mFreeItemIndex = freeItemIndex;
        mCartItemsList = orderSummary.getmCartItemsList();
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_summary, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(10, 10, 10, -5);
            v.setLayoutParams(layoutParams);

            SummaryViewHolder vh = new SummaryViewHolder(v);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_summary_footer, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 15, 0, 15);
            v.setLayoutParams(layoutParams);

            SummaryViewHolderFooter vh = new SummaryViewHolderFooter(v);
            return vh;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mCartItemsList.size()) {
            return VIEW_FOOTER;
        } else {
            return VIEW_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SummaryViewHolder) {
            final SummaryViewHolder summaryViewHolder = (SummaryViewHolder) holder;
            View view = summaryViewHolder.itemView;

            final Items item = mCartItemsList.get(position);
            summaryViewHolder.menuItemName.setText(item.getItemName());
            summaryViewHolder.tvCost.setText(Utils.costString(item.getItemCost() * item.getItemQuantity()));

            //Setting divider
            if (position + 1 == mCartItemsList.size()) {
                summaryViewHolder.divider.setVisibility(View.GONE);
            } else {
                summaryViewHolder.divider.setVisibility(View.VISIBLE);
            }

            summaryViewHolder.tvItemQuantity.setText("x " + String.valueOf(item.getItemQuantity()));

            if (mVegIconHeight == 0) {
                final TextView tvMenuItemName = summaryViewHolder.menuItemName;
                final LinearLayout llCustomisationsFinal = summaryViewHolder.llCustomisations;
                final LinearLayout llOfferAppliedSpecificFinal = summaryViewHolder.llOfferAppliedSpecific;
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
                                llOfferAppliedSpecificFinal.setLayoutParams(params);

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
                summaryViewHolder.menuItemName.setCompoundDrawables(img, null, null, null);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) summaryViewHolder.llCustomisations.getLayoutParams();
                params.setMargins((mVegIconHeight + summaryViewHolder.menuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
                summaryViewHolder.llCustomisations.setLayoutParams(params);
                summaryViewHolder.llOfferAppliedSpecific.setLayoutParams(params);

            }

            ArrayList<String> customisationList = item.getCustomisationList();

            final LinearLayout hiddenLayout = summaryViewHolder.llCustomisations;
            final TextView menuItemNameFinal = summaryViewHolder.menuItemName;
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
            }

            final LinearLayout llOfferAppliedSpecificFinal = summaryViewHolder.llOfferAppliedSpecific;
            if (mFreeItemIndex == position) {
                if (mOrderSummary.getOfferUsed() != null) {
                    summaryViewHolder.tvOfferSpecificCost.setText("- " + Utils.costString(mOrderSummary.getOrderActualValueWithOutTax() - mOrderSummary.getOfferUsed().getOrderValueWithOutTax()));

                    final TextView[] textViews = new TextView[1];
                    textViews[0] = new TextView(mContext);
                    textViews[0].setText("Offer Applied : " + mOrderSummary.getOfferUsed().getHeader());
                    textViews[0].setTextColor(mContext.getResources().getColor(R.color.white));
                    textViews[0].setTextSize(12.0f);
                    textViews[0].setPadding(15, 4, 15, 4);
                    textViews[0].setBackgroundResource(R.drawable.border_customisations_colored);

                    llOfferAppliedSpecificFinal.post(new Runnable() {
                        @Override
                        public void run() {
                            int maxWidth = llOfferAppliedSpecificFinal.getWidth();
                            Utils.populateText(llOfferAppliedSpecificFinal, textViews, mContext, maxWidth - mVegIconHeight - menuItemNameFinal.getCompoundDrawablePadding());
                        }
                    });
                }

            } else {
                summaryViewHolder.tvOfferSpecificCost.setVisibility(View.GONE);
                if (llOfferAppliedSpecificFinal.getChildCount() != 0) {
                    llOfferAppliedSpecificFinal.removeAllViews();
                }
            }


        } else {
            // Footer
            final SummaryViewHolderFooter summaryViewHolderFooter = (SummaryViewHolderFooter) holder;

            if (mOrderSummary.getOfferUsed() != null) {
                // Offer applied
                if (mFreeItemIndex >= 0) {
                    summaryViewHolderFooter.llOfferApplied.setVisibility(View.GONE);
                } else {
                    summaryViewHolderFooter.llOfferApplied.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvOfferTitle.setText("Offer Applied : " + mOrderSummary.getOfferUsed().getHeader());
                    summaryViewHolderFooter.tvOfferApplied.setText("- " + Utils.costString(mOrderSummary.getOrderActualValueWithOutTax() - mOrderSummary.getOfferUsed().getOrderValueWithOutTax()));
                }

                //Item Total
                summaryViewHolderFooter.tvItemTotal.setText(Utils.costString(mOrderSummary.getOfferUsed().getOrderValueWithOutTax()));

                //Grand Total
                summaryViewHolderFooter.tvGrandTotal.setText(Utils.costString(mOrderSummary.getOfferUsed().getOrderValueWithTax()));
                grandTotal = mOrderSummary.getOfferUsed().getOrderValueWithTax();

                //Vat
                if (mOrderSummary.getOfferUsed().getVatValue() != 0) {
                    summaryViewHolderFooter.llVat.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvVat.setText(Utils.costString(mOrderSummary.getOfferUsed().getVatValue()));
                } else {
                    summaryViewHolderFooter.llVat.setVisibility(View.GONE);
                }

                //Service Tax
                if (mOrderSummary.getOfferUsed().getServiceTaxValue() != 0) {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvServiceTax.setText(Utils.costString(mOrderSummary.getOfferUsed().getServiceTaxValue()));
                } else {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.GONE);
                }

            } else {
                // Offer applied
                summaryViewHolderFooter.llOfferApplied.setVisibility(View.GONE);

                //Item Total
                summaryViewHolderFooter.tvItemTotal.setText(Utils.costString(mOrderSummary.getOrderActualValueWithOutTax()));

                //Grand Total
                summaryViewHolderFooter.tvGrandTotal.setText(Utils.costString(mOrderSummary.getOrderActualValueWithTax()));
                grandTotal = mOrderSummary.getOrderActualValueWithTax();

                //Vat
                if (mOrderSummary.getVatValue() != 0) {
                    summaryViewHolderFooter.llVat.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvVat.setText(Utils.costString(mOrderSummary.getVatValue()));
                } else {
                    summaryViewHolderFooter.llVat.setVisibility(View.GONE);
                }

                //Service Tax
                if (mOrderSummary.getServiceTaxValue() != 0) {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvServiceTax.setText(Utils.costString(mOrderSummary.getServiceTaxValue()));
                } else {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return mCartItemsList.size() + 1;
    }

    public static class SummaryViewHolderFooter extends RecyclerView.ViewHolder {
        LinearLayout llItemTotal;
        TextView tvItemTotal;
        LinearLayout llOfferApplied;
        TextView tvOfferTitle;
        TextView tvOfferApplied;
        LinearLayout llVat;
        TextView tvVat;
        LinearLayout llServiceTax;
        TextView tvServiceTax;
        LinearLayout llGrandTotal;
        TextView tvGrandTotal;
        EditText etSuggestion;

        public SummaryViewHolderFooter(View itemView) {
            super(itemView);
            this.llItemTotal = (LinearLayout) itemView.findViewById(R.id.llItemTotal);
            this.tvItemTotal = (TextView) itemView.findViewById(R.id.tvItemTotal);
            this.llOfferApplied = (LinearLayout) itemView.findViewById(R.id.llOfferApplied);
            this.tvOfferTitle = (TextView) itemView.findViewById(R.id.tvOfferTitle);
            this.tvOfferApplied = (TextView) itemView.findViewById(R.id.tvOfferApplied);
            this.llVat = (LinearLayout) itemView.findViewById(R.id.llVat);
            this.tvVat = (TextView) itemView.findViewById(R.id.tvVat);
            this.llServiceTax = (LinearLayout) itemView.findViewById(R.id.llServiceTax);
            this.tvServiceTax = (TextView) itemView.findViewById(R.id.tvServiceTax);
            this.llGrandTotal = (LinearLayout) itemView.findViewById(R.id.llGrandTotal);
            this.tvGrandTotal = (TextView) itemView.findViewById(R.id.tvGrandTotal);
            this.etSuggestion = (EditText) itemView.findViewById(R.id.etSuggestion);
        }
    }

    public static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView menuItemName;
        TextView tvItemQuantity;
        TextView tvCost;
        TextView tvOfferSpecificCost;
        LinearLayout llCustomisations;
        LinearLayout llOfferAppliedSpecific;
        View divider;

        public SummaryViewHolder(View itemView) {
            super(itemView);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
            this.tvItemQuantity = (TextView) itemView.findViewById(R.id.tvItemQuantity);
            this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);
            this.tvOfferSpecificCost = (TextView) itemView.findViewById(R.id.tvOfferSpecificCost);
            this.llCustomisations = (LinearLayout) itemView.findViewById(R.id.llCustomisations);
            this.llOfferAppliedSpecific = (LinearLayout) itemView.findViewById(R.id.llOfferAppliedSpecific);
            this.divider = (View) itemView.findViewById(R.id.divider);
        }
    }

}


