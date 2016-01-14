package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twyst.app.android.CirclePageIndicator;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.MainActivity;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.adapters.ImagePagerAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anshul on 1/12/2016.
 */
public class DiscoverOutletFragment extends Fragment {

    private DiscoverOutletAdapter discoverAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private String mDate;
    private String mTime;

    protected boolean search;
    private boolean fetchingOutlets;
    private boolean outletsNotFound;

    private cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager viewPager;
    private List<Integer> imageIdList;
    private CirclePageIndicator mIndicator;
    private MainActivity mActivity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_outlet_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.outletRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        viewPager = (cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager)view.findViewById(R.id.ads_pager);
        imageIdList = new ArrayList<Integer>();
        imageIdList.add(R.drawable.banner1);
        imageIdList.add(R.drawable.banner2);
        imageIdList.add(R.drawable.banner3);
        imageIdList.add(R.drawable.banner4);
        viewPager.setAdapter(new ImagePagerAdapter(getActivity(), imageIdList));

        mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        mIndicator.setOnPageChangeListener(new MyOnPageChangeListener());


        viewPager.setInterval(2000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(0);
        viewPager.setScrollDurationFactor(5);

        mActivity = (MainActivity)getActivity();

        setupSwipeRefresh(view);
        setupDiscoverAdapter();
        refreshDateTime();
        fetchOutlets(1);





        return view;

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    }



    private void setupDiscoverAdapter() {
        discoverAdapter = new DiscoverOutletAdapter();
        discoverAdapter.setmContext(getActivity());
        mRecyclerView.setAdapter(discoverAdapter);
    }

    private void fetchOutlets(final int start) {
//        String latitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, null);
//        String longitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, null);
        String latitude = "28.4733044";
        String longitude = "77.0994511";

        Log.d(getTagName(), "Going to fetch outlets with, start: " + start + ", lat " + latitude + ", long: " + longitude + ", date: " + mDate + ", time: " + mTime);
        fetchingOutlets = true;
        mSwipeRefreshLayout.setEnabled(true);

        int end = start + AppConstants.DISCOVER_LIST_PAGESIZE - 1;

        HttpService.getInstance().getRecommendedOutlets(getUserToken(), start, end, latitude, longitude, mDate, mTime, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;

                ArrayList<Outlet> outlets = arrayListBaseResponse.getData().getOutlets();
                String twystBucks = arrayListBaseResponse.getData().getTwystBucks();

                if (outlets != null) {
                    if (outlets.isEmpty()) {
                        outletsNotFound = true;
                        discoverAdapter.setOutletsNotFound(true);
                    } else {
                        if (start == 1) {//Clear the items in adapter, fresh download
                            Log.d(getTagName(), "clearing outlets on discover screen");
                            discoverAdapter.getItems().clear();
                        }

                        if (outlets.size() < AppConstants.DISCOVER_LIST_PAGESIZE) {
                            outletsNotFound = true;
                            discoverAdapter.setOutletsNotFound(true);
                        } else {
                            outletsNotFound = false;
                            discoverAdapter.setOutletsNotFound(false);
                        }

                        discoverAdapter.getItems().addAll(outlets);
                        discoverAdapter.notifyDataSetChanged();
                    }
                }

                onItemsLoadComplete();
                mActivity.hideProgressHUDInLayout();
                mActivity.hideSnackbar();

            }

            @Override
            public void failure(RetrofitError error) {
                fetchingOutlets = false;
                mActivity.hideProgressHUDInLayout();
                onItemsLoadComplete();
                mActivity.handleRetrofitError(error);
            }
        });

    }

    public String getUserToken() {
//        return "us5lxmyPyqnA4Ow20GmbhG362ZuMS4qB";
        return "vH8quBjd1C-2lgZBcFcjrUjQMMbnInLQ";
//        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }

    private void setupSwipeRefresh(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_orange);
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (search) {
//                    fetchSearchedOutlets();
                }
            }
        });
    }

    private void refreshDiscoverAdapter() {
        if (discoverAdapter != null && discoverAdapter.getItems().size() > 0) {
//            refreshOutletsBackground();
        }
    }



    private void refreshDateTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        mDate = (String) DateFormat.format("MM-dd-yyyy", currentDateTime);
        int hoursIn = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        mTime = convertHoursToString(hoursIn) + ":" + convertMinutesToString(minutes);
    }

    private String convertHoursToString(int hours) {
        String hoursStr = "";
        if (hours == 0 || hours == 24) {
            hoursStr = "00";
        } else if (hours < 10) {
            hoursStr = "0" + hours;
        } else {
            hoursStr = "" + hours;
        }

        return hoursStr;
    }


    protected String getTagName() {
        return getActivity().getClass().getSimpleName();
    }


    private String convertMinutesToString(int minutes) {
        String timeStr = "";
        if (minutes == 0) {
            timeStr = "00";
        } else {
            timeStr = "" + minutes;
        }
        return timeStr;
    }

    private void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void onSuccessDownload(ArrayList<Outlet> outlets) {
        if (outlets != null) { //success
            if (outlets.isEmpty()) {
                outletsNotFound = true;
                discoverAdapter.setOutletsNotFound(true);
            } else {
                if (outlets.size() < AppConstants.DISCOVER_LIST_PAGESIZE) {
                    outletsNotFound = true;
                    discoverAdapter.setOutletsNotFound(true);
                } else {
                    outletsNotFound = false;
                    discoverAdapter.setOutletsNotFound(false);
                }

                discoverAdapter.getItems().addAll(outlets);
                discoverAdapter.notifyDataSetChanged();
            }

//            findViewById(R.id.planAhead).setVisibility(View.VISIBLE);
//
//            String lat = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LAT,"");
//            String lng = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LNG,"");
//            setTextLocationFetch(LOCATION_CURRENT, lat, lng, false);

        } else {
//            handleRetrofitError(new RetrofitError);
        }

        onItemsLoadComplete();
        mActivity.hideProgressHUDInLayout();
    }

}
