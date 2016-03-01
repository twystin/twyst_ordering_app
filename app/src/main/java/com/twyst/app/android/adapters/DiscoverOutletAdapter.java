package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.RoundedTransformation;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 06/06/15.
 */
public class DiscoverOutletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Outlet> items = new ArrayList<>();
    private Context mContext;

    private boolean outletsNotFound = false;

    public List<Outlet> getItems() {
        return items;
    }

    public void setmContext(Context context) {
        mContext = context;
    }

    public void setItems(List<Outlet> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_outlet, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 10, 10, -5);
        v.setLayoutParams(layoutParams);

        OutletViewHolder vh = new OutletViewHolder(v);
        addImagesToLayoutDynamically(vh);
        return vh;
    }

    public void addImagesToLayoutDynamically(final OutletViewHolder vh) {
        vh.outletAddress.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.location);
                        int height = vh.outletAddress.getMeasuredHeight() * 1 / 2;
                        int offset = height / 4;
                        int smallOffset = height / 8;
                        img.setBounds(0, 0, offset + height + smallOffset, offset + height - smallOffset);
                        vh.outletAddress.setCompoundDrawables(img, null, null, null);
                        vh.outletAddress.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });


        vh.deliveryTime.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.clock);
                        int height = vh.deliveryTime.getMeasuredHeight() * 2 / 3;
                        int offset = height / 6;
                        img.setBounds(0, 0, offset + height, offset + height);
                        vh.deliveryTime.setCompoundDrawables(img, null, null, null);
                        vh.deliveryTime.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        vh.minimumOrder.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.bill);
                        int height = vh.minimumOrder.getMeasuredHeight() * 2 / 3;
                        int offset = height / 6;
                        img.setBounds(0, 0, offset + height, offset + height);
                        vh.minimumOrder.setCompoundDrawables(img, null, null, null);
                        vh.minimumOrder.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        vh.noOfOffers.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.offerblack);
                        int height = vh.noOfOffers.getMeasuredHeight() * 2 / 3;
                        int offset = height / 6;
                        img.setBounds(0, 0, offset + height, offset + height);
                        vh.noOfOffers.setCompoundDrawables(img, null, null, null);
                        vh.noOfOffers.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        System.out.println("DiscoverOutletAdapter.onBindViewHolder data called... position: " + position);

        final OutletViewHolder outletViewHolder = (OutletViewHolder) holder;
        final Outlet outlet = items.get(position);

        View view = outletViewHolder.itemView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Order online
                if (outlet.getMenuId() != null && outlet.getMenuId() != "") {
                    Intent intent = new Intent(view.getContext(), OrderOnlineActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
                    view.getContext().startActivity(intent);
                } else {
                    Toast.makeText(mContext, "No menuId available!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        outletViewHolder.outletName.setText(outlet.getName());

        String address = "";
        boolean added = false;
        if (!TextUtils.isEmpty(outlet.getLocality_1())) {
            address += outlet.getLocality_1();
            added = true;
        }

        if (!TextUtils.isEmpty(outlet.getLocality_2())) {
            if (added) {
                address += ", ";
            }
            address += outlet.getLocality_2();
            added = true;
        }

        if (!TextUtils.isEmpty(outlet.getCity())) {
            if (added) {
                address += ", ";
            }
            address += outlet.getCity();
        }

        outletViewHolder.outletAddress.setText(address);

        if (outlet.getDeliveryExperience() > 0) {
            outletViewHolder.deliveryExperience.setText(String.valueOf(outlet.getDeliveryExperience()));
            outletViewHolder.ivStar.setVisibility(View.VISIBLE);
        } else {
            outletViewHolder.deliveryExperience.setText("- -");
            outletViewHolder.ivStar.setVisibility(View.GONE);
        }

        if (outlet.getCuisines() != null && outlet.getCuisines().size() > 0) {
            String cuisines = outlet.getCuisines().toString();
            outletViewHolder.cuisinesNames.setText(cuisines.substring(1, cuisines.length() - 1));
        }

        if (outlet.getDeliveryTime() != null) {
            String deliveryString = "Delivers in " + outlet.getDeliveryTime() + " minutes";
            outletViewHolder.deliveryTime.setText(deliveryString);
        } else {
            String deliveryString = "Delivers in 0 minutes";
            outletViewHolder.deliveryTime.setText(deliveryString);
        }

        if (outlet.getMinimumOrder() != null) {
            String minOrderString = "Minimum order : " + Utils.costString(Double.parseDouble(outlet.getMinimumOrder()));
            outletViewHolder.minimumOrder.setText(minOrderString);
        } else {
            String minOrderString = "Minimum order : " + Utils.costString(Double.parseDouble("0"));
            outletViewHolder.minimumOrder.setText(minOrderString);
        }
        String maxCashBack = "0";
        if (outlet.getCashback() != null) {
            maxCashBack = outlet.getCashback();
        }

        if (Double.parseDouble(maxCashBack) < 10 && !maxCashBack.contains(".")) {
            outletViewHolder.twystCashPercentage.setText(" " + maxCashBack + "%");
        } else {
            outletViewHolder.twystCashPercentage.setText(maxCashBack + "%");
        }

        Picasso picasso = Picasso.with(view.getContext());
        picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
        picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);

        if (outlet.getBackground() != null && outlet.getLogo() != null)
            picasso.load(outlet.getBackground())
                    .noFade()
                    .transform(new RoundedTransformation(10, 0))
                    .into(outletViewHolder.outletImage);

        if (outlet.getOfferCount() == 0) {
            ((OutletViewHolder) holder).noOfOffers.setVisibility(View.INVISIBLE);
        } else {
            ((OutletViewHolder) holder).noOfOffers.setVisibility(View.VISIBLE);
            ((OutletViewHolder) holder).noOfOffers.setText(outlet.getOfferCount() + " Offers Available");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOutletsNotFound(boolean outletsNotFound) {
        this.outletsNotFound = outletsNotFound;
    }

    public static class OutletViewHolder extends RecyclerView.ViewHolder {
        TextView outletName;
        TextView outletAddress;
        ImageView outletImage;
        TextView cuisinesNames;
        TextView deliveryTime;
        TextView minimumOrder;
        TextView twystCashPercentage;
        TextView noOfOffers;
        TextView deliveryExperience;
        ImageView ivStar;

        public OutletViewHolder(View itemView) {
            super(itemView);
            outletName = (TextView) itemView.findViewById(R.id.outletName);
            outletAddress = (TextView) itemView.findViewById(R.id.outletAddress);
            deliveryTime = (TextView) itemView.findViewById(R.id.delivery_time);
            cuisinesNames = (TextView) itemView.findViewById(R.id.cuisines_names);
            minimumOrder = (TextView) itemView.findViewById(R.id.min_order);
            twystCashPercentage = (TextView) itemView.findViewById(R.id.tv_twyst_cash_percentage);
            noOfOffers = (TextView) itemView.findViewById(R.id.no_of_offers);
            deliveryExperience = (TextView) itemView.findViewById(R.id.tvDeliveryExperience);
            ivStar = (ImageView) itemView.findViewById(R.id.iv_star);
            outletImage = (ImageView) itemView.findViewById(R.id.outlet_logo);
        }
    }

}


