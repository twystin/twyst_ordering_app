package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.AvailableOffersActivity;
import com.twyst.app.android.activities.OfferDisplayActivity;
import com.twyst.app.android.activities.OrderSummaryActivity;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raman on 11/18/2015.
 */
public class ScrollingOffersAdapter extends PagerAdapter {
    private ArrayList<Offer> mOffersList = new ArrayList<>();
    private final Context mContext;

    public ScrollingOffersAdapter(Context context, ArrayList<Offer> offerList) {
        mContext = context;
        this.mOffersList = offerList;
    }

    @Override
    public int getCount() {
        return mOffersList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Offer offer = mOffersList.get(position);

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.layout_scrolling_offers, container, false);

        TextView tvHeader = (TextView) itemView.findViewById(R.id.tv_header);
        tvHeader.setText(offer.getHeader());

        String line12 = "";
        if (offer.getLine1() != null) {
            line12 = offer.getLine1();
            if (offer.getLine2() != null) {
                line12 = line12 + " " + offer.getLine2();
            }
        }
        TextView tvLine12 = (TextView) itemView.findViewById(R.id.tv_line12);
        tvLine12.setText(line12);

        TextView twystBucksTextView = (TextView) itemView.findViewById(R.id.twyst_bucks_textView);
        int twystBucks = mOffersList.get(position).getOfferCost();
        if (twystBucks == 0) {
            itemView.findViewById(R.id.bucks_layout).setVisibility(View.INVISIBLE);
        } else {
            twystBucksTextView.setText(String.valueOf(twystBucks));
        }


        ((ViewPager) container).addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offerDisplayIntent = new Intent(mContext, OfferDisplayActivity.class);

                Bundle offerDisplayBundle = new Bundle();
                offerDisplayBundle.putSerializable(AppConstants.INTENT_OFFER_LIST, mOffersList);
                offerDisplayBundle.putInt(AppConstants.INTENT_CLICKED_OFFER_POSITION, position);

                offerDisplayIntent.putExtras(offerDisplayBundle);
                mContext.startActivity(offerDisplayIntent);
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.95f;
    }

}
