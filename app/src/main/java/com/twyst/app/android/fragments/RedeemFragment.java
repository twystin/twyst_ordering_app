package com.twyst.app.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.primitives.Ints;
import com.twyst.app.android.CirclePageIndicator;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.FoodVouchersActivity;
import com.twyst.app.android.activities.TwystBucksHistoryActivity;
import com.twyst.app.android.adapters.ImagePagerAdapter;
import com.twyst.app.android.adapters.RedeemRVAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedeemFragment extends Fragment {
    public static final String REDEEM_FOOD_OFFERS = "FOOD OFFERS";
    public static final String REDEEM_RECHARGE = "RECHARGE";
    public static final String REDEEM_SHOPPING = "SHOPPING";
    public static final String REDEEM_COMING_SOON = "COMING SOON";

    private RecyclerView mGridtile;
    private cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager viewPager;
    private CirclePageIndicator mIndicator;

    private List<RedeemRVAdapter.CardItemRedeem> cardItemList = new ArrayList<>();

    private String[] TextForGrid =
            {
                    REDEEM_FOOD_OFFERS,
                    REDEEM_RECHARGE,
                    REDEEM_SHOPPING,
                    REDEEM_COMING_SOON
            };
    private int[] imageIds =
            {
                    R.drawable.redeem_food_offers,
                    R.drawable.redeem_recharge,
                    R.drawable.redeem_shopping,
                    R.drawable.redeem_coming_soon
            };
    private int[] add_imageIds =
            {
                    R.drawable.app_redeem_slide1,
                    R.drawable.app_redeem_slide2
            };

    public void createList() {
        for (int i = 0; i < imageIds.length; i++) {
            RedeemRVAdapter.CardItemRedeem ci = new RedeemRVAdapter.CardItemRedeem();
            ci.setImageId(imageIds[i]);
            ci.setTextBelowImage(TextForGrid[i]);
            cardItemList.add(ci);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.redeem_fragment, null);

        view.findViewById(R.id.ll_twyst_bucks_launcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent twystBucksIntent = new Intent(view.getContext(), TwystBucksHistoryActivity.class);
//                view.getContext().startActivity(twystBucksIntent);
            }
        });

        setAddsViewPager(view);
        setGridAdapter(view);
        return view;
    }

    private void setAddsViewPager(View view) {
        viewPager = (AutoScrollViewPager) view.findViewById(R.id.ads_pager);

        viewPager.setAdapter(new ImagePagerAdapter(getActivity(), Ints.asList(add_imageIds)));

        mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        mIndicator.setOnPageChangeListener(new MyOnPageChangeListener());

        viewPager.setInterval(5000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(1);
        viewPager.setScrollDurationFactor(5);
    }

    private void setGridAdapter(View view) {
        mGridtile = (RecyclerView) view.findViewById(R.id.gv_redeem);
        mGridtile.setHasFixedSize(true);
//        RedeemGridAdapter adapter = new RedeemGridAdapter(getContext(), TextForGrid, imageIds);
//        mGridtile.setAdapter(adapter);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mGridtile.setNestedScrollingEnabled(true);
        GridLayoutManager glmRedeem = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mGridtile.setLayoutManager(glmRedeem);
        createList();
        RedeemRVAdapter adapter = new RedeemRVAdapter(getActivity(), cardItemList);
        mGridtile.setAdapter(adapter);


    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

}
