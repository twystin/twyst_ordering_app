package com.twyst.app.android.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.twyst.app.android.fragments.DiscoverOutletFragment;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.banners.OrderBanner;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;

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
                        //open Outlet detail page on the basis of outlet ID
                        Intent intent = new Intent(mContext, OrderOnlineActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, orderBanner.getOutletIdList().get(0));
                        mContext.startActivity(intent);
                        break;

                    case OrderBanner.TYPE_LANDING_PAGE_BANNER:
                        //open a webview on the basis of banner name
                        openURL(AppConstants.HOST + "/api/v4/banners/landing/page/" + orderBanner.getBannerName());
                        break;

                    case OrderBanner.TYPE_OUTLET_BANNER:
                        //fetch lists of outlets on the basis of banner id : alternative of reco API
                        // TODO
//                        DiscoverOutletFragment outletsFragment = (DiscoverOutletFragment) ((MainActivity) mContext).getSupportFragmentManager().getFragments().get(0);
//                        if (outletsFragment != null) {
//                            outletsFragment.fetchOutletsBanners(orderBanner.getId());
//                        }

                        break;
                }
            }
        });

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    private void openURL(String url) {
        //Create a new dialog
        final Dialog webViewDialog = new Dialog(mContext);
        //Remove the dialog's title
        webViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Inflate the contents of this dialog with the Views defined at 'webviewdialog.xml'
        webViewDialog.setContentView(R.layout.webviewdialog);
        //With this line, the dialog can be dismissed by pressing the back key
        webViewDialog.setCancelable(true);

        //Initialize the Button object with the data from the 'webviewdialog.xml' file
        Button btClose = (Button) webViewDialog.findViewById(R.id.bt_close);
        //Define what should happen when the close button is pressed.
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dismiss the dialog
                webViewDialog.dismiss();
            }
        });

        final CircularProgressBar web_view_circular_progress_bar = (CircularProgressBar) webViewDialog.findViewById(R.id.web_view_circular_progress_bar);

        //Initialize the WebView object with data from the 'webviewdialog.xml' file
        final WebView webView = (WebView) webViewDialog.findViewById(R.id.wb_webview);
        //Scroll bars should not be hidden
        webView.setScrollbarFadingEnabled(false);
        //Disable the horizontal scroll bar
        webView.setHorizontalScrollBarEnabled(false);
        //Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        //Set the user agent
        webView.getSettings().setUserAgentString("AndroidWebView");
        //Clear the cache
        webView.clearCache(true);
        //Make the webview load the specified URL
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                web_view_circular_progress_bar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                web_view_circular_progress_bar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        webViewDialog.show();
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
