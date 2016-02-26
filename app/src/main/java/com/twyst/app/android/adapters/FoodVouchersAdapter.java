package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.offer.FoodOffer;

import java.util.ArrayList;

/**
 * Created by Raman on 2/3/2016.
 */
public class FoodVouchersAdapter extends RecyclerView.Adapter<FoodVouchersAdapter.ViewHolder> {
    ArrayList<FoodOffer> mFoodOffersList;
    public final Context mContext;
    public FoodVouchersAdapter(Context context,ArrayList<FoodOffer> FoodOffersList) {
        mContext = context;
        mFoodOffersList =FoodOffersList;
    }

    @Override
    public FoodVouchersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_vouchers_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodVouchersAdapter.ViewHolder holder, int position) {
        holder.populateData(mFoodOffersList.get(position));
        holder.flUseOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, OrderOnlineActivity.class);
//                intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
//                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFoodOffersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView outletNameTV;
        private TextView headerTV;
        private TextView maxOfferYouCanAwailTV;
        private TextView offerCostTV;
        private TextView expiryDateTV;
        private LinearLayout linlayOfferCost;
        public FrameLayout flUseOffer;

        public ViewHolder(View view) {
            super(view);
            outletNameTV = (TextView) view.findViewById(R.id.tv_outlet_name);
            headerTV = (TextView) view.findViewById(R.id.tv_header_line);
            maxOfferYouCanAwailTV = (TextView) view.findViewById(R.id.tv_maximum_offer_you_can_avail);
            offerCostTV = (TextView) view.findViewById(R.id.tv_offer_cost);
            expiryDateTV = (TextView) view.findViewById(R.id.tv_expiry_date);
            linlayOfferCost = (LinearLayout) view.findViewById(R.id.tv_offer_cost_linlay);
            flUseOffer = (FrameLayout) view.findViewById(R.id.fl_use_offer);

        }

        public void populateData(FoodOffer foodOffer){
            outletNameTV.setText(foodOffer.getOutletHeader().getOutletName());
            headerTV.setText(foodOffer.getHeader() + " " + foodOffer.getHeaderSuffix());
            maxOfferYouCanAwailTV.setText(foodOffer.getMaxOfferAvailalbleDesc());
            if (foodOffer.getOfferCost() !=0) {
                linlayOfferCost.setVisibility(View.VISIBLE);
                offerCostTV.setText(String.valueOf(foodOffer.getOfferCost()));
            } else {
                linlayOfferCost.setVisibility(View.GONE);
            }
            String[] timeArray = OrderTrackingState.getTimeArray(OrderTrackingState.getFormattedDate(foodOffer.getExpiryDate()));
            expiryDateTV.setText(" Valid till " + timeArray[1] + " " + timeArray[2]);

        }
    }
}
