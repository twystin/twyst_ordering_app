package com.twyst.app.android.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.Outlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 11/18/2015.
 */
public class ScrollingOffersAdapter extends PagerAdapter {
    private List<Offer> items = new ArrayList<>();
    private final int[] GalImages = new int[] {
            R.drawable.offer_scroll_11,
            R.drawable.offer_scroll_22,
            R.drawable.offer_scroll_22,
            R.drawable.offer_scroll_11,
            R.drawable.offer_scroll_22,
            R.drawable.offer_scroll_22,
            R.drawable.offer_scroll_11
    };

    public ScrollingOffersAdapter(List<Offer> items) {
        this.items = getOfferList();
    }

    private List<Offer> getOfferList() {

        return null;
    }

    @Override
    public int getCount() {
        return GalImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.layout_scrolling_offers, container, false);
        ImageView imageViewOffer = (ImageView) itemView.findViewById(R.id.imageViewOffer);
        imageViewOffer.setImageResource(GalImages[position]);
        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth(int position) {
        return (super.getPageWidth(position) / 4);
    }

}
