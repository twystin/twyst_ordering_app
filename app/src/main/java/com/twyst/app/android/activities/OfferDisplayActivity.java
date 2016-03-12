package com.twyst.app.android.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.OfferDisplayPagerAdpater;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

public class OfferDisplayActivity extends FragmentActivity {

    private RelativeLayout mRlOfferDisplayScreen;
    private ArrayList<Offer> mOffersList = new ArrayList<>();
    private ViewPager mOfferDisplayViewPager;
    private OfferDisplayPagerAdpater mOfferDisplayPagerAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_display);

        Bundle bundle = getIntent().getExtras();
        int clickedOfferPosition = bundle.getInt(AppConstants.INTENT_CLICKED_OFFER_POSITION);
        mOffersList = (ArrayList<Offer>) bundle.getSerializable(AppConstants.INTENT_OFFER_LIST);

        mOfferDisplayPagerAdpater = new OfferDisplayPagerAdpater(OfferDisplayActivity.this, mOffersList);

        mOfferDisplayViewPager = (ViewPager) findViewById(R.id.ViewPagerOfferDisplay);
        mOfferDisplayViewPager.setAdapter(mOfferDisplayPagerAdpater);
        mOfferDisplayViewPager.setCurrentItem(clickedOfferPosition);
        mOfferDisplayViewPager.setClipToPadding(false);

        mOfferDisplayViewPager.setPadding(40, 0, 40, 0);
        mOfferDisplayViewPager.setPageMargin(20);
        mRlOfferDisplayScreen = (RelativeLayout) findViewById(R.id.offer_display_screen);
        mRlOfferDisplayScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }
}