package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private int selectedPosition = -1;
    private boolean ivCheckedIsSelected;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

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
            offerAvailableHolder.tvSave.setText(Utils.costString(mOrderSummary.getOrderActualValueWithOutTax() - offerOrder.getOrderValueWithOutTax()));

        } else { // not applicable offer
            offerAvailableHolder.tvSave.setVisibility(View.INVISIBLE);
            offerAvailableHolder.tvSaveLabel.setVisibility(View.INVISIBLE);
            view.setAlpha(.5f);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        offerAvailableHolder.tvCashCount.setText(String.valueOf(offerOrder.getOfferCost()));
        offerAvailableHolder.tvHeader.setText(offerOrder.getHeader());
        String offerDesc;

        // Setting offer description
        if (offerOrder.getLine1() == null) {
            offerDesc = "";
        } else {
            offerDesc = offerOrder.getLine1();
            if (offerOrder.getLine2() != null) {
                offerDesc = offerDesc + ", " + offerOrder.getLine2();
            }
        }
        offerAvailableHolder.tvLine12.setText(offerDesc);

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

        ivCheckedIsSelected = offerAvailableHolder.ivChecked.isSelected();

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
        TextView tvCashCount;
        TextView tvSaveLabel;
        TextView tvSave;
        View divider;
        LinearLayout llTwystCash;

        public OfferAvailableHolder(View itemView) {
            super(itemView);
            this.ivChecked = (ImageView) itemView.findViewById(R.id.ivChecked);
            this.tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
            this.tvLine12 = (TextView) itemView.findViewById(R.id.tvLine12);
            this.tvCashCount = (TextView) itemView.findViewById(R.id.tvCashCount);
            this.tvSaveLabel = (TextView) itemView.findViewById(R.id.save_label);
            this.tvSave = (TextView) itemView.findViewById(R.id.tvSave);
            this.divider = (View) itemView.findViewById(R.id.divider);
            this.llTwystCash = (LinearLayout) itemView.findViewById(R.id.llTwystCash);
        }
    }

}
