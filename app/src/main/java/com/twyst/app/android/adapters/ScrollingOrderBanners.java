package com.twyst.app.android.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.MainActivity;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.activities.WebViewActivity;
import com.twyst.app.android.fragments.DiscoverOutletFragment;
import com.twyst.app.android.model.banners.OrderBanner;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

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
        final OrderBanner orderBanner = mOrderBannerList.get(position);
        ViewHolder viewHolder = new ViewHolder(itemView);

        viewHolder.tvHeader.setText(orderBanner.getHeader());

        Glide.with(mContext)
                .load(orderBanner.getBannerImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(viewHolder.ivBannerImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (orderBanner.getBannerType()) {
                    case OrderBanner.TYPE_FOOD_BANNER:
                        mContext.startActivity(Utils.getOutletIntent(mContext, orderBanner.getOutletIdList().get(0)));
                        break;

                    case OrderBanner.TYPE_LANDING_PAGE_BANNER:
                        mContext.startActivity(Utils.getURLLandingPageIntent(mContext, orderBanner.getHeader(), AppConstants.HOST + "/api/v4/banners/landing/page/" + orderBanner.getBannerName()));
                        break;

                    case OrderBanner.TYPE_OUTLET_BANNER:
                        //fetch lists of outlets on the basis of banner id : alternative of reco API
                        DiscoverOutletFragment outletsFragment = (DiscoverOutletFragment) ((MainActivity) mContext).getSupportFragmentManager().getFragments().get(0);
                        if (outletsFragment != null) {
                            outletsFragment.fetchOutletsBanners(orderBanner.getId());
                        }

                        break;

                    case OrderBanner.TYPE_COPY_CODE:
                        break;
                }
            }
        });

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
