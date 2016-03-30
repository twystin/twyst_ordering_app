package com.twyst.app.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twyst.app.android.R;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.banners.OrderBanner;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 3/30/2016.
 */
public class ScrollingOrderBanners extends PagerAdapter {
    private final Context mContext;
    private ArrayList<OrderBanner> mOrderBannerList = new ArrayList<>();
    private final LayoutInflater mInflater;

    public ScrollingOrderBanners(Context context, ArrayList<OrderBanner> orderBannerList) {
        this.mContext = context;
        this.mOrderBannerList = orderBannerList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = mInflater.inflate(R.layout.banners_order, container, false);

        OrderBanner orderBanner = mOrderBannerList.get(position);
        ViewHolder viewHolder = new ViewHolder(itemView);

        viewHolder.tvHeader.setText(orderBanner.getHeader());

        Glide.with(mContext)
                .load(orderBanner.getBannerImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(viewHolder.ivBannerImage);

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public int getCount() {
        return mOrderBannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private static class ViewHolder {
        ImageView ivBannerImage;
        TextView tvHeader;

        public ViewHolder(View view) {
            ivBannerImage = (ImageView) view.findViewById(R.id.iv_banner_image);
            tvHeader = (TextView) view.findViewById(R.id.tv_header);
        }
    }
}
