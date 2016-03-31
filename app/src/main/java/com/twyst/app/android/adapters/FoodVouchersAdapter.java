package com.twyst.app.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.model.DeliveryZone;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.offer.FoodOffer;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Raman on 2/3/2016.
 */
public class FoodVouchersAdapter extends RecyclerView.Adapter<FoodVouchersAdapter.ViewHolder> {
    ArrayList<FoodOffer> mFoodOffersList;
    public final Context mContext;

    public FoodVouchersAdapter(Context context, ArrayList<FoodOffer> FoodOffersList) {
        mContext = context;
        mFoodOffersList = FoodOffersList;
    }

    @Override
    public FoodVouchersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_vouchers_card, null);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodVouchersAdapter.ViewHolder holder, int position) {
        final FoodOffer foodOffer = mFoodOffersList.get(position);
        holder.populateData(foodOffer);


        holder.flUseOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuPage(foodOffer, Utils.getFilteredDeliveryZone(foodOffer.getOutletHeader().getDelivery_zone()));
            }
        });

    }

    private void openMenuPage(final FoodOffer foodOffer, final DeliveryZone filteredDeliveryZone) {
        if (filteredDeliveryZone == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_generic, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            TextView tvTitle = ((TextView) (dialog.findViewById(R.id.tv_title)));
            tvTitle.setText("Continue?");
            TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
            tvMessage.setText("This outlet does not deliver to your selected location. You can change the location later. Continue?");
            TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
            tvOk.setText("Yes");
            TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
            tvCancel.setText("No");

            dialog.findViewById(R.id.fOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMenuPage(foodOffer, Utils.getMinimumDeliveryZone(foodOffer.getOutletHeader().getDelivery_zone()));
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.fCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } else {
            FoodOffer.OutletHeader outletHeader = foodOffer.getOutletHeader();
            final Outlet outlet = new Outlet();
            outlet.setName(outletHeader.getOutletName());
            outlet.setMenuId(foodOffer.getMenuId());
            outlet.set_id(outletHeader.get_id());

            outlet.setDeliveryTime(filteredDeliveryZone.getDeliveryEstimatedTime());
            outlet.setMinimumOrder(filteredDeliveryZone.getMinDeliveryAmt());
            outlet.setPaymentOptions(filteredDeliveryZone.getPaymentOptions());

            outlet.setBackground(outletHeader.getBackground());
            outlet.setLogo(outletHeader.getBackground());
            outlet.setPhone(outletHeader.getPhone());
            Intent intent = new Intent(mContext, OrderOnlineActivity.class);
            intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
            intent.putExtra(AppConstants.INTENT_PARAM_FROM_FOOD_OFFER, true);
            mContext.startActivity(intent);
        }
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

        public void populateData(FoodOffer foodOffer) {

            outletNameTV.setText(foodOffer.getOutletHeader().getOutletName());
            headerTV.setText(foodOffer.getHeader() + " " + foodOffer.getHeaderSuffix());
            maxOfferYouCanAwailTV.setText(foodOffer.getMaxOfferAvailalbleDesc());
            if (foodOffer.getOfferCost() != 0) {
                linlayOfferCost.setVisibility(View.VISIBLE);
                offerCostTV.setText(String.valueOf(foodOffer.getOfferCost()));
            } else {
                linlayOfferCost.setVisibility(View.GONE);
            }
            String[] timeArray = getTimeArray(OrderTrackingState.getFormattedDate(foodOffer.getExpiryDate()));
            expiryDateTV.setText(" Valid till " + timeArray[1] + " " + timeArray[2]);

        }

        private String[] getTimeArray(Date date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
            String formattedDate = dateFormat.format(date).toString();
            return formattedDate.split("\\s+");
        }
    }
}
