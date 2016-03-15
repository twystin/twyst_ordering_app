package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.VoucherDetailsActivity;
import com.twyst.app.android.model.CashbackOffers;
import com.twyst.app.android.util.AppConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Raman on 2/1/2016.
 */
public class ShoppingVoucherListAdapter extends RecyclerView.Adapter<ShoppingVoucherListAdapter.ViewHolder> {
    private ArrayList<CashbackOffers> mOffers;
    private String mMerchant_logo;
    private Context mContext;

    public ShoppingVoucherListAdapter(Context context, ArrayList<CashbackOffers> offers, String merchant_logo) {
        mContext = context;
        mOffers = offers;
        mMerchant_logo = merchant_logo;
    }

    @Override
    public ShoppingVoucherListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_list_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /*
        1. Set On click Listener
        2. Display only Usable items by checking twyst cash (display faded.)
        3. Bind all widgets.
     */
    @Override
    public void onBindViewHolder(final ShoppingVoucherListAdapter.ViewHolder holder, final int position) {

        // Bind all widgets
        if (mMerchant_logo != null) {

            Glide.with(mContext)
                    .load(mMerchant_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.merchantLogo);
        }

        holder.offerAmountTV.setText(AppConstants.INDIAN_RUPEE_SYMBOL + mOffers.get(position).getOffer_value());

        holder.validDate.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(R.drawable.voucher_validity_icon);
                        int height = holder.validDate.getLineHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        holder.validDate.setCompoundDrawables(img, null, null, null);
                        holder.validDate.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        holder.validDate.setText(String.format("Valid till %s", getValidityDate(mOffers.get(position).getOffer_end_date())));

        if (mOffers.get(position).getMin_bill_value() != null) {
            holder.minBillVal.setText(String.format("Minimum Bill Rs. %s", mOffers.get(position).getMin_bill_value()));
        } else {
            holder.minBillVal.setVisibility(View.GONE);
        }

        String freeText = mOffers.get(position).getOffer_text();
        if (freeText != null) {
            holder.freeText.setText(freeText);
        } else {
            holder.freeText.setVisibility(View.INVISIBLE);
        }

        int twystCashAmount = Integer.MAX_VALUE; //TODO -- from where do we get twsytCashAmnt
        if (twystCashAmount < Integer.parseInt(mOffers.get(position).getOffer_cost())) {
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        // Set click Listener
        holder.viewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VoucherDetailsActivity.class);
                intent.putExtra(AppConstants.INTENT_VOUCHER_DETAIL, mOffers.get(position));
                intent.putExtra(AppConstants.INTENT_MERCHANT_LOGO, mMerchant_logo);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout viewOffer;
        private TextView freeText;
        private TextView minBillVal;
        public ImageView merchantLogo;
        public TextView offerAmountTV;
        public TextView validDate;

        public ViewHolder(View view) {
            super(view);
            merchantLogo = (ImageView) view.findViewById(R.id.iv_merchantLogo);
            offerAmountTV = (TextView) view.findViewById(R.id.tv_offerCost);
            minBillVal = (TextView) view.findViewById(R.id.tv_minBillVal);
            freeText = (TextView) view.findViewById(R.id.tv_freeText);
            validDate = (TextView) view.findViewById(R.id.tv_voucherValidity);
            viewOffer = (FrameLayout) view.findViewById(R.id.bUseOffer);
        }
    }

    public String getValidityDate(String validDate) {
        String date = null;
        SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date d = inputsdf.parse(validDate);
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTime(d);
            String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            int day = cal.get(Calendar.DAY_OF_MONTH);
            date = day + " " + month;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
