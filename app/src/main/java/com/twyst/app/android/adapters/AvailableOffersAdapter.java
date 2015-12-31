package com.twyst.app.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.order.OfferOrder;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.Utils;

/**
 * Created by Vipul Sharma on 12/24/2015.
 */
public class AvailableOffersAdapter extends RecyclerView.Adapter<AvailableOffersAdapter.OfferAvailableHolder> {
    private final Context mContext;
    private final OrderSummary mOrderSummary;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private int selectedPosition = -1;
    private OnViewHolderListener onViewHolderListener;
    private static int mCheckIconWidth = 0; //checkIcon width fixed for a specific device

    public AvailableOffersAdapter(Context context, OrderSummary orderSummary) {
        this.mOrderSummary = orderSummary;
        this.mContext = context;
    }

    @Override
    public OfferAvailableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_available, parent, false);
        OfferAvailableHolder offerAvailableHolder = new OfferAvailableHolder(v);
        return offerAvailableHolder;
    }

    @Override
    public void onBindViewHolder(OfferAvailableHolder offerAvailableHolder, final int position) {
        final View view = offerAvailableHolder.itemView;
        OfferOrder offerOrder = mOrderSummary.getOfferOrderList().get(position);

        if (offerOrder.isApplicable()) { //applicable offer
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onViewHolderListener != null) {
                        onViewHolderListener.onItemClicked(position);
                    }
                }
            });

            offerAvailableHolder.tvSave.setText("Save " + Utils.costString(mOrderSummary.getOrderActualValueWithOutTax() - offerOrder.getOrderValueWithOutTax()));
        } else { // not applicable offer
            offerAvailableHolder.tvHeader.setTextColor(mContext.getResources().getColor(R.color.semi_black_faded));
            offerAvailableHolder.tvLine12.setTextColor(mContext.getResources().getColor(R.color.semi_black_faded));
            offerAvailableHolder.ivChecked.setVisibility(View.INVISIBLE);
            offerAvailableHolder.tvSave.setVisibility(View.INVISIBLE);
        }

        offerAvailableHolder.tvBucksCount.setText(String.valueOf(offerOrder.getOfferCost()));
        offerAvailableHolder.tvHeader.setText(offerOrder.getHeader());
        offerAvailableHolder.tvLine12.setText(offerOrder.getLine1() + ", " + offerOrder.getLine2());

        //Setting divider
        if (position + 1 == mOrderSummary.getOfferOrderList().size()) {
            offerAvailableHolder.divider.setVisibility(View.GONE);
        } else {
            offerAvailableHolder.divider.setVisibility(View.VISIBLE);
        }

        if (mCheckIconWidth == 0) {
            final ImageView ivCheckedFinal = offerAvailableHolder.ivChecked;
            ivCheckedFinal.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mCheckIconWidth = ivCheckedFinal.getMeasuredWidth();
                            ivCheckedFinal.getLayoutParams().height = mCheckIconWidth;
                            ivCheckedFinal.requestLayout();
                            ivCheckedFinal.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    });
        } else {
            offerAvailableHolder.ivChecked.getLayoutParams().height = mCheckIconWidth;
            offerAvailableHolder.ivChecked.requestLayout();
        }

        if (position == selectedPosition) {
            offerAvailableHolder.ivChecked.setImageResource(R.drawable.checked);
        } else {
            offerAvailableHolder.ivChecked.setImageResource(R.drawable.unchecked);
        }

    }

    @Override
    public int getItemCount() {
        return mOrderSummary.getOfferOrderList().size();
    }

    public interface OnViewHolderListener {
        void onItemClicked(int position);
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener) {
        this.onViewHolderListener = onViewHolderListener;
    }

    public static class OfferAvailableHolder extends RecyclerView.ViewHolder {
        ImageView ivChecked;
        TextView tvHeader;
        TextView tvLine12;
        TextView tvBucksCount;
        TextView tvSave;
        View divider;
        LinearLayout llBucks;

        public OfferAvailableHolder(View itemView) {
            super(itemView);
            this.ivChecked = (ImageView) itemView.findViewById(R.id.ivChecked);
            this.tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
            this.tvLine12 = (TextView) itemView.findViewById(R.id.tvLine12);
            this.tvBucksCount = (TextView) itemView.findViewById(R.id.tvBucksCount);
            this.tvSave = (TextView) itemView.findViewById(R.id.tvSave);
            this.divider = (View) itemView.findViewById(R.id.divider);
            this.llBucks = (LinearLayout) itemView.findViewById(R.id.llBucks);
        }
    }

}
