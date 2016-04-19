package com.twyst.app.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderSummaryActivity;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.CouponCode;
import com.twyst.app.android.model.CouponCodeResponse;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    private SummaryViewHolderFooter mSummaryViewHolderFooter;
    private double grandTotal;
    private boolean isSuggestionToShow;

    public SummaryViewHolderFooter getmSummaryViewHolderFooter() {
        return mSummaryViewHolderFooter;
    }

    public SummaryAdapter(Context context, OrderSummary orderSummary, int freeItemIndex, boolean isSuggestionToShow) {
        this.mOrderSummary = orderSummary;
        mContext = context;
        mFreeItemIndex = freeItemIndex;
        mCartItemsList = orderSummary.getmCartItemsList();
        this.isSuggestionToShow = isSuggestionToShow;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_summary, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 15, 0, 15);
            v.setLayoutParams(layoutParams);

            SummaryViewHolder vh = new SummaryViewHolder(v);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_summary_footer, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 15, 0, 15);
            v.setLayoutParams(layoutParams);

            SummaryViewHolderFooter vh = new SummaryViewHolderFooter(v);
            mSummaryViewHolderFooter = vh;
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

            summaryViewHolder.tvCost.setText(costFormatter(item.getItemCost() * item.getItemQuantity()));

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
                                mVegIconHeight = tvMenuItemName.getLineHeight() * 7 / 8;
                                ViewGroup.LayoutParams lp = summaryViewHolder.vegNonVegIcon.getLayoutParams();
                                lp.width = mVegIconHeight;
                                lp.height = mVegIconHeight;
                                summaryViewHolder.vegNonVegIcon.setLayoutParams(lp);
                                summaryViewHolder.vegNonVegIcon.setImageDrawable(img);
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llOfferAppliedSpecificFinal.getLayoutParams();
                                params.setMargins((mVegIconHeight + tvMenuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
//                                llCustomisationsFinal.setLayoutParams(params);
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
                ViewGroup.LayoutParams lp = summaryViewHolder.vegNonVegIcon.getLayoutParams();
                lp.width = mVegIconHeight;
                lp.height = mVegIconHeight;
                summaryViewHolder.vegNonVegIcon.setLayoutParams(lp);
                summaryViewHolder.vegNonVegIcon.setImageDrawable(img);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) summaryViewHolder.llOfferAppliedSpecific.getLayoutParams();
                params.setMargins((mVegIconHeight + summaryViewHolder.menuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
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
                    summaryViewHolder.tvOfferSpecificCost.setText("- " + costFormatter(mOrderSummary.getOrderActualValueWithOutTax() - mOrderSummary.getOfferUsed().getOrderValueWithOutTax()));

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

            if (mOrderSummary.getDelivery_charges() == 0) {
                summaryViewHolderFooter.llDeliveryCharge.setVisibility(View.GONE);
            } else {
                summaryViewHolderFooter.llDeliveryCharge.setVisibility(View.VISIBLE);
                summaryViewHolderFooter.tvDeliveryCharge.setText(costFormatter(mOrderSummary.getDelivery_charges()));
            }

            if (mOrderSummary.getPackaging_charges() == 0) {
                summaryViewHolderFooter.llPackagingCharge.setVisibility(View.GONE);
            } else {
                summaryViewHolderFooter.llPackagingCharge.setVisibility(View.VISIBLE);
                summaryViewHolderFooter.tvPackagingCharge.setText(costFormatter(mOrderSummary.getDelivery_charges()));
            }

            if (mOrderSummary.getOfferUsed() != null) {
                // Offer applied
                if (mFreeItemIndex >= 0) {
                    summaryViewHolderFooter.llOfferApplied.setVisibility(View.GONE);
                } else {
                    summaryViewHolderFooter.llOfferApplied.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvOfferTitle.setText("Offer Applied : " + mOrderSummary.getOfferUsed().getHeader());
                    summaryViewHolderFooter.tvOfferApplied.setText("- " + costFormatter(mOrderSummary.getOrderActualValueWithOutTax() - mOrderSummary.getOfferUsed().getOrderValueWithOutTax()));
                }

                //Item Total
                summaryViewHolderFooter.tvItemTotal.setText(costFormatter(mOrderSummary.getOfferUsed().getOrderValueWithOutTax()));

                //Grand Total
                summaryViewHolderFooter.tvGrandTotal.setText(costFormatter(mOrderSummary.getOfferUsed().getOrderValueWithTax()));
                grandTotal = mOrderSummary.getOfferUsed().getOrderValueWithTax();

                //Vat
                if (mOrderSummary.getOfferUsed().getVatValue() != 0) {
                    summaryViewHolderFooter.llVat.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvVat.setText(costFormatter(mOrderSummary.getOfferUsed().getVatValue()));
                } else {
                    summaryViewHolderFooter.llVat.setVisibility(View.GONE);
                }

                //Service Tax
                if (mOrderSummary.getOfferUsed().getServiceTaxValue() != 0) {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvServiceTax.setText(costFormatter(mOrderSummary.getOfferUsed().getServiceTaxValue()));
                } else {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.GONE);
                }

            } else {
                // Offer Not applied
                summaryViewHolderFooter.llOfferApplied.setVisibility(View.GONE);

                //Item Total
                summaryViewHolderFooter.tvItemTotal.setText(costFormatter(mOrderSummary.getOrderActualValueWithOutTax()));

                //Grand Total
                summaryViewHolderFooter.tvGrandTotal.setText(costFormatter(mOrderSummary.getOrderActualValueWithTax()));
                grandTotal = mOrderSummary.getOrderActualValueWithTax();

                //Vat
                if (mOrderSummary.getVatValue() != 0) {
                    summaryViewHolderFooter.llVat.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvVat.setText(costFormatter(mOrderSummary.getVatValue()));
                } else {
                    summaryViewHolderFooter.llVat.setVisibility(View.GONE);
                }

                //Service Tax
                if (mOrderSummary.getServiceTaxValue() != 0) {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.VISIBLE);
                    summaryViewHolderFooter.tvServiceTax.setText(costFormatter(mOrderSummary.getServiceTaxValue()));
                } else {
                    summaryViewHolderFooter.llServiceTax.setVisibility(View.GONE);
                }
            }

            //Coupon code
            summaryViewHolderFooter.cbCouponApply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        summaryViewHolderFooter.llCouponInitial.setVisibility(View.GONE);
                        summaryViewHolderFooter.llCoupon.setVisibility(View.VISIBLE);
                    }
                }
            });

            summaryViewHolderFooter.bApplyCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyCouponCode(summaryViewHolderFooter);
                }
            });

            if (isSuggestionToShow) {
                summaryViewHolderFooter.etSuggestion.setVisibility(View.VISIBLE);
            } else {
                summaryViewHolderFooter.etSuggestion.setVisibility(View.GONE);
            }

            if (isSuggestionToShow) {
                summaryViewHolderFooter.rlMainCoupon.setVisibility(View.VISIBLE);
                summaryViewHolderFooter.dividerCoupon.setVisibility(View.VISIBLE);
            } else {
                summaryViewHolderFooter.rlMainCoupon.setVisibility(View.GONE);
                summaryViewHolderFooter.dividerCoupon.setVisibility(View.GONE);
            }

        }
    }

    private void applyCouponCode(final SummaryViewHolderFooter summaryViewHolderFooter) {
        String couponCodeString = summaryViewHolderFooter.etCouponCode.getText().toString();

        if (TextUtils.isEmpty(couponCodeString)) {
            summaryViewHolderFooter.etCouponCode.setError("Please enter coupon code!");
            return;
        }

        summaryViewHolderFooter.etCouponCode.setError(null);

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(mContext, false, null);
        CouponCode couponCode = new CouponCode(mOrderSummary.getOrderNumber(), mOrderSummary.getOutletId(), couponCodeString);
        HttpService.getInstance().postApplyCouponCode(UtilMethods.getUserToken(mContext), couponCode, new Callback<BaseResponse<CouponCodeResponse>>() {
            @Override
            public void success(BaseResponse<CouponCodeResponse> baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    CouponCodeResponse couponCodeResponse = baseResponse.getData();
                    String cashBack = couponCodeResponse.getCashBack();
                    String cashBackText = "Smart move! You earned ₹ " + cashBack + " Twyst Cash for this order";
                    summaryViewHolderFooter.tvCouponDesc.setText(cashBackText);
                    summaryViewHolderFooter.tvCouponDesc.setVisibility(View.VISIBLE);

                    summaryViewHolderFooter.tvApplyCoupon.setText("Remove");
                    summaryViewHolderFooter.etCouponCode.setEnabled(false);

                    summaryViewHolderFooter.bApplyCoupon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeCouponCode(summaryViewHolderFooter);
                        }
                    });

                } else {
                    Toast.makeText(mContext, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError((OrderSummaryActivity) mContext, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    private void removeCouponCode(final SummaryViewHolderFooter summaryViewHolderFooter) {
        String couponCodeString = summaryViewHolderFooter.etCouponCode.getText().toString();

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(mContext, false, null);
        CouponCode couponCode = new CouponCode(mOrderSummary.getOrderNumber(), mOrderSummary.getOutletId(), couponCodeString);
        HttpService.getInstance().postRemoveCouponCode(UtilMethods.getUserToken(mContext), couponCode, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    summaryViewHolderFooter.tvCouponDesc.setVisibility(View.GONE);

                    summaryViewHolderFooter.tvApplyCoupon.setText("Apply");
                    summaryViewHolderFooter.etCouponCode.setEnabled(true);

                    summaryViewHolderFooter.bApplyCoupon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyCouponCode(summaryViewHolderFooter);
                        }
                    });

                } else {
                    Toast.makeText(mContext, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError((OrderSummaryActivity) mContext, error);
                UtilMethods.hideSnackbar();
            }
        });

    }

    private String costFormatter(double d) {
        String sCost = Utils.costString(d);

        // Need to always show 2 places of decimal.
        if (!sCost.contains(".")) {
            return (sCost + ".00");
        }
        return sCost;
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
        LinearLayout llDeliveryCharge;
        TextView tvDeliveryCharge;
        LinearLayout llPackagingCharge;
        TextView tvPackagingCharge;
        LinearLayout llGrandTotal;
        TextView tvGrandTotal;

        RelativeLayout rlMainCoupon;
        View dividerCoupon;
        LinearLayout llCoupon;
        LinearLayout llCouponInitial;
        CheckBox cbCouponApply;
        EditText etCouponCode;
        FrameLayout bApplyCoupon;
        TextView tvApplyCoupon;
        TextView tvCouponDesc;

        public EditText getEtSuggestion() {
            return etSuggestion;
        }

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

            this.llDeliveryCharge = (LinearLayout) itemView.findViewById(R.id.llDeliveryCharges);
            this.tvDeliveryCharge = (TextView) itemView.findViewById(R.id.tvDelieveryCharges);

            this.llPackagingCharge = (LinearLayout) itemView.findViewById(R.id.llPackagingCharges);
            this.tvPackagingCharge = (TextView) itemView.findViewById(R.id.tvPackagingCharges);

            this.llGrandTotal = (LinearLayout) itemView.findViewById(R.id.llGrandTotal);
            this.tvGrandTotal = (TextView) itemView.findViewById(R.id.tvGrandTotal);
            this.etSuggestion = (EditText) itemView.findViewById(R.id.etSuggestion);

            this.rlMainCoupon = (RelativeLayout) itemView.findViewById(R.id.rl_main_coupon);
            this.dividerCoupon = (View) itemView.findViewById(R.id.divider_coupon);
            this.llCoupon = (LinearLayout) itemView.findViewById(R.id.llCoupon);
            this.llCouponInitial = (LinearLayout) itemView.findViewById(R.id.llCouponInitial);
            this.cbCouponApply = (CheckBox) itemView.findViewById(R.id.cb_coupon_apply);
            this.etCouponCode = (EditText) itemView.findViewById(R.id.et_coupon_code);
            this.bApplyCoupon = (FrameLayout) itemView.findViewById(R.id.bApplyCoupon);
            this.tvApplyCoupon = (TextView) itemView.findViewById(R.id.tv_apply_coupon);
            this.tvCouponDesc = (TextView) itemView.findViewById(R.id.tv_coupon_desc);
        }
    }

    public static class SummaryViewHolder extends RecyclerView.ViewHolder {
        ImageView vegNonVegIcon;
        TextView menuItemName;
        TextView tvItemQuantity;
        TextView tvCost;
        TextView tvOfferSpecificCost;
        LinearLayout llCustomisations;
        LinearLayout llOfferAppliedSpecific;
        View divider;

        public SummaryViewHolder(View itemView) {
            super(itemView);
            this.vegNonVegIcon = (ImageView) itemView.findViewById(R.id.iv_vegNonVegIcon);
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


