package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.order.OfferOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/24/2015.
 */
public class AvailableOffersAdapter extends RecyclerView.Adapter<AvailableOffersAdapter.OfferAvailableHolder> {
    private List<OfferOrder> mOfferOrderList = new ArrayList<>();
    private final Context mContext;

    public AvailableOffersAdapter(Context context, List<OfferOrder> offerOrderList) {
        this.mOfferOrderList = offerOrderList;
        this.mContext = context;
    }

    @Override
    public OfferAvailableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_available, parent, false);
        OfferAvailableHolder offerAvailableHolder = new OfferAvailableHolder(v);
        return offerAvailableHolder;
    }

    @Override
    public void onBindViewHolder(OfferAvailableHolder offerAvailableHolder, int position) {
        final View view = offerAvailableHolder.itemView;
        offerAvailableHolder.rbOfferText.setText(mOfferOrderList.get(position).getHeader());
    }

    @Override
    public int getItemCount() {
        return mOfferOrderList.size();
    }

    public static class OfferAvailableHolder extends RecyclerView.ViewHolder {
        RadioButton rbOfferText;

        public OfferAvailableHolder(View itemView) {
            super(itemView);
            this.rbOfferText = (RadioButton) itemView.findViewById(R.id.rbOfferText);
        }
    }

}
