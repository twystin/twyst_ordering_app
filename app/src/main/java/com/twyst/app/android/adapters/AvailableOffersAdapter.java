package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
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
public class AvailableOffersAdapter extends ArrayAdapter<OfferOrder> {
    private final Context mContext;
    private final OrderSummary mOrderSummary;
    private boolean ivCheckedIsSelected;
    private int selectedPosition = -1;

    private OnViewHolderListener onViewHolderListener;


    private static int mCheckIconWidth = 0; //checkIcon width fixed for a specific device

    public AvailableOffersAdapter(Context context, OrderSummary orderSummary) {
        super(context,R.layout.layout_offer_available,orderSummary.getOfferOrderList());
        this.mOrderSummary = orderSummary;
        this.mContext = context;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OfferAvailableHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.layout_offer_available, parent, false);
            holder = new OfferAvailableHolder(row);
            row.setTag(holder);
        } else {
            holder = (OfferAvailableHolder) row.getTag();
        }


        OfferOrder offerOrder = mOrderSummary.getOfferOrderList().get(position);
        if (!offerOrder.isApplicable()) {
            holder.tvSave.setVisibility(View.INVISIBLE);
            holder.tvSaveLabel.setVisibility(View.INVISIBLE);
            row.setAlpha(.5f);
            row.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onViewHolderListener != null) {
                        onViewHolderListener.onItemClicked(position);
                    }
                }
            });
            holder.tvSave.setText(Utils.costString(mOrderSummary.getOrderActualValueWithOutTax() - offerOrder.getOrderValueWithOutTax()));
        }



        holder.tvCashCount.setText(String.valueOf(offerOrder.getOfferCost()));
        holder.tvHeader.setText(offerOrder.getHeader());
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
        holder.tvLine12.setText(offerDesc);

        //Setting divider
        if (position + 1 == mOrderSummary.getOfferOrderList().size()) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        if (mCheckIconWidth == 0) {
            final ImageView ivCheckedFinal = holder.ivChecked;
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
            holder.ivChecked.getLayoutParams().height = mCheckIconWidth;
            holder.ivChecked.requestLayout();
        }

        ivCheckedIsSelected = holder.ivChecked.isSelected();

        if (selectedPosition == position){
            holder.ivChecked.setImageResource(R.drawable.checked);
        } else {
            holder.ivChecked.setImageResource(R.drawable.unchecked);
        }

        return row;
    }

    public interface OnViewHolderListener {
        void onItemClicked(int position);
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener) {
        this.onViewHolderListener = onViewHolderListener;
    }


    public static class OfferAvailableHolder {
        ImageView ivChecked;
        TextView tvHeader;
        TextView tvLine12;
        TextView tvCashCount;
        TextView tvSaveLabel;
        TextView tvSave;
        View divider;
        LinearLayout llTwystCash;

        public OfferAvailableHolder(View itemView) {
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
