package com.twyst.app.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import com.twyst.app.android.CirclePageIndicator;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.AddressAddNewActivity;
import com.twyst.app.android.activities.AddressMapActivity;
import com.twyst.app.android.activities.FiltersActivity;
import com.twyst.app.android.activities.MainActivity;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.adapters.ScrollingOrderBanners;
import com.twyst.app.android.layout.NoDataHolder;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.banners.OrderBanner;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.FilterOptions;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.SharedPreferenceSingleton;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anshul on 1/12/2016.
 */
public class DiscoverOutletFragment extends Fragment implements LocationFetchUtil.LocationFetchResultCodeListener {
    private DiscoverOutletAdapter discoverAdapter;
    private RecyclerView mRecyclerView;
    private NoDataHolder noDataHolder;

    private String mDate;
    private String mTime;

    protected boolean search;
    private boolean fetchingOutlets;
    private boolean outletsNotFound;

    private CirclePageIndicator mIndicator;
    private MainActivity mActivity;
    private FloatingActionButton fabFilter;
    private HashMap<String, long[]> filterTagsMap = new HashMap<>();

    public ArrayList<Outlet> getFetchedOutlets() {
        return fetchedOutlets;
    }

    private ArrayList<Outlet> fetchedOutlets;
    private HashMap<String, ArrayList<String>> optionsMap = FilterOptions.getMyMap();

    private Location mLocation;
    private AddressDetailsLocationData mAddressDetailsLocationData;
    private LocationFetchUtil locationFetchUtil;
    private TextView currentAddressName;
    SharedPreferenceSingleton sharedPreferenceSingleton = SharedPreferenceSingleton.getInstance();

    private LinearLayout showErrorLayout;
    private LinearLayout showDefaultLocationError;
    private TextView errorDescription;
    private CircularProgressBar circularProgressBarOutlet;
    private LinearLayout showAddressLayout;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_outlet_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.outletRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        noDataHolder = new NoDataHolder(view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setupBannersViewPager(view);

        fabFilter = (FloatingActionButton) view.findViewById(R.id.fab_filter);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, FiltersActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(AppConstants.FILTER_MAP, filterTagsMap);
                intent.putExtras(extras);
                startActivityForResult(intent, AppConstants.GET_FILTER_ACTIVITY);
            }
        });

        setupDiscoverAdapter();
        refreshDateTime();
        String optionSelected = mActivity.getIntent().getStringExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED);
        currentAddressName = (TextView) view.findViewById(R.id.tv_current_address_location);
        circularProgressBarOutlet = (CircularProgressBar) view.findViewById(R.id.circularProgressBarOutlet);
        showErrorLayout = (LinearLayout) view.findViewById(R.id.linlay_discover_fragment_error_layout);
        showDefaultLocationError = (LinearLayout) view.findViewById(R.id.linlay_discover_fragment_show_default_error_message);
        errorDescription = (TextView) view.findViewById(R.id.tv_error_description);
        showAddressLayout = (LinearLayout) view.findViewById(R.id.linlay_display_address);
        showProgressBar();
        if (savedInstanceState == null) {
            switch (optionSelected) {
                case AppConstants.CHOOSE_LOCATION_OPTION_CURRENT:
                    mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
                    updateCurrentAddressName(mAddressDetailsLocationData);
                    fetchOutlets();
                    break;
                case AppConstants.CHOOSE_LOCATION_OPTION_SAVED:
                    mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
                    updateCurrentAddressName(mAddressDetailsLocationData);
                    fetchOutlets();
                    break;
                case AppConstants.CHOOSE_LOCATION_OPTION_ADD:
                    mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
                    updateCurrentAddressName(mAddressDetailsLocationData);
                    if (mActivity.getIntent().getBooleanExtra(AppConstants.CHOOSE_LOCATION_DEFAULT, false)) {
                        showDefaultLocationError.setVisibility(View.VISIBLE);
                    }
                    fetchOutlets();
                    break;
                case AppConstants.CHOOSE_LOCATION_OPTION_SKIPPED:
                    Fragment discoverOutletFragment = (DiscoverOutletFragment) (mActivity.getSupportFragmentManager().getFragments().get(0));
                    locationFetchUtil = new LocationFetchUtil(mActivity, discoverOutletFragment);
                    locationFetchUtil.requestLocation(true);
                    break;
                default:
                    break;
            }
        } else {
            mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
            updateCurrentAddressName(mAddressDetailsLocationData);
            fetchOutlets();
        }

        showErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorLayout.setVisibility(View.GONE);
                showProgressBar();
                noDataHolder.noDataOutlet.setVisibility(View.GONE);
                locationFetchUtil.requestLocation(true);
            }

        });

        showAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddressDetailsLocationData != null) {
                    filterTagsMap.clear();
                    showDefaultLocationError.setVisibility(View.GONE);
                    SharedPreferenceSingleton.getInstance().setPassedCartCheckoutStage(false);
                    Intent intent = new Intent(mActivity, AddressAddNewActivity.class);
                    startActivityForResult(intent, AppConstants.EDIT_ADDRESS);
                }
            }
        });

        return view;

    }

    private void setupBannersViewPager(final View view) {
        HttpService.getInstance().getOrderBanners(UtilMethods.getUserToken(mActivity), new Callback<BaseResponse<ArrayList<OrderBanner>>>() {
            @Override
            public void success(BaseResponse<ArrayList<OrderBanner>> arrayListBaseResponse, Response response) {
                if (arrayListBaseResponse.isResponse()) {
                    ArrayList<OrderBanner> orderBannerList = arrayListBaseResponse.getData();
                    if (orderBannerList.size() > 0) {
                        AutoScrollViewPager bannersViewPager = (AutoScrollViewPager) view.findViewById(R.id.ads_pager);
                        bannersViewPager.setAdapter(new ScrollingOrderBanners(mActivity, orderBannerList));
                        view.findViewById(R.id.rl_scrolling_pager).setVisibility(View.VISIBLE);

                        mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
                        mIndicator.setViewPager(bannersViewPager);
                        mIndicator.setOnPageChangeListener(new MyOnPageChangeListener());

                        bannersViewPager.setInterval(5000);
                        bannersViewPager.startAutoScroll();
                        bannersViewPager.setCurrentItem(0);
                        bannersViewPager.setScrollDurationFactor(5);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAddressDetailsLocationData != null) {
            SharedPreferenceSingleton.getInstance().saveCurrentUsedLocation(mAddressDetailsLocationData);
        }
    }

    public void updateCurrentAddressName(AddressDetailsLocationData mAddressDetailsLocationData) {
        if (!TextUtils.isEmpty(mAddressDetailsLocationData.getLandmark())) {
            currentAddressName.setText(mAddressDetailsLocationData.getLine1() + ", " + mAddressDetailsLocationData.getLine2() + ", " + mAddressDetailsLocationData.getLandmark() + ", " +
                    mAddressDetailsLocationData.getCity() + ", " + mAddressDetailsLocationData.getState());
        } else {
            currentAddressName.setText(mAddressDetailsLocationData.getLine1() + ", " + mAddressDetailsLocationData.getLine2() + ", " +
                    mAddressDetailsLocationData.getCity() + ", " + mAddressDetailsLocationData.getState());
        }
    }

    public void fetchLocation() {
        locationFetchUtil.requestLocation(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == AppConstants.GET_FILTER_ACTIVITY) {
            if (resultCode == AppConstants.GOT_FILTERS_SUCCESS) {
                noDataHolder.noDataOutlet.setVisibility(View.GONE);
                showProgressBar();
                Bundle extras = data.getExtras();
                filterTagsMap.clear();
                filterTagsMap = (HashMap<String, long[]>) extras.getSerializable(AppConstants.FILTER_TAGS);
                ArrayList<String> tags = new ArrayList<>();

                for (String tagName : filterTagsMap.keySet()) {
                    if (filterTagsMap.get(tagName) != null && filterTagsMap.get(tagName).length > 0) {
                        for (long position : filterTagsMap.get(tagName)) {
                            tags.add(optionsMap.get(tagName).get((int) position));
                        }
                    }
                }

                if (tags.size() > 0) {

                    ArrayList<Outlet> list = fetchOutletsWithFilters();
                    discoverAdapter.getItems().clear();
                    discoverAdapter.getItems().addAll(list);
                    discoverAdapter.notifyDataSetChanged();
                    if (list.size() == 0) {
                        noDataHolder.noDataOutlet.setVisibility(View.VISIBLE);
                        noDataHolder.tvNoData.setText(getResources().getString(R.string.no_data_outlet));
                        noDataHolder.ivNoData.setImageResource(R.drawable.no_data_order);
                    }
                } else {
                    discoverAdapter.getItems().clear();
                    discoverAdapter.getItems().addAll(fetchedOutlets);
                    discoverAdapter.notifyDataSetChanged();
                    if (fetchedOutlets.size() == 0) {
                        noDataHolder.noDataOutlet.setVisibility(View.VISIBLE);
                        noDataHolder.tvNoData.setText(getResources().getString(R.string.no_data_outlet));
                        noDataHolder.ivNoData.setImageResource(R.drawable.no_data_order);
                    }
                }

                hideProgressBar();


            }
        } else if (requestCode == AppConstants.EDIT_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferenceSingleton.getInstance().setSkipLocationClicked(false);
                AddressDetailsLocationData newAddressLocationData = SharedPreferenceSingleton.getInstance().getDeliveryLocation();
                if (newAddressLocationData != null) {

                    if (newAddressLocationData.getLandmark().equals("Unnamed Address")) {
                        currentAddressName.setText(newAddressLocationData.getLine1());
                    } else {
                        updateCurrentAddressName(newAddressLocationData);
                    }

                    if (!newAddressLocationData.equals(mAddressDetailsLocationData)) {
                        mAddressDetailsLocationData = newAddressLocationData;
                        fetchOutlets();
                    }
                }
            }

        }
    }

    @Override
    public void onReceiveAddress(int resultCode, Address address) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            int index = address.getMaxAddressLineIndex();
            if (index >= 0) mAddressDetailsLocationData.setLine1(address.getAddressLine(0));
            if (index >= 1) mAddressDetailsLocationData.setLine2(address.getAddressLine(1));

            if (address.getSubAdminArea() != null) {
                mAddressDetailsLocationData.setCity(address.getSubAdminArea()); // to be checked
            } else {
                mAddressDetailsLocationData.setCity(address.getLocality()); // to be checked
            }
            mAddressDetailsLocationData.setState(address.getAdminArea()); // to be checked

            sharedPreferenceSingleton.saveCurrentUsedLocation(mAddressDetailsLocationData);
            updateCurrentAddressName(mAddressDetailsLocationData);
            fetchOutlets();
        } else {
            Toast.makeText(mActivity, R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mActivity, AddressMapActivity.class);
            intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
            startActivity(intent);
            mActivity.finish();
        }
    }

    @Override
    public void onReceiveLocation(int resultCode, Location location) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            mLocation = location;
            mAddressDetailsLocationData = new AddressDetailsLocationData();
            Coords coords = new Coords(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
            mAddressDetailsLocationData.setCoords(coords);
            locationFetchUtil.requestAddress(location, false);
        } else {
            Toast.makeText(mActivity, R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            resolveError(resultCode);
        }
    }

    public void resolveError(int resultCode) {
        mAddressDetailsLocationData = sharedPreferenceSingleton.getCurrentUsedLocation();

        if (mAddressDetailsLocationData == null) {
            Intent intent = new Intent(mActivity, AddressMapActivity.class);
            intent.putExtra("Choose activity directed to map", true);
            startActivity(intent);
            hideProgressBar();
        } else {
            showErrorLayout.setVisibility(View.VISIBLE);
            if (resultCode == AppConstants.SHOW_FETCH_LOCATION_AGAIN) {
                errorDescription.setText(R.string.fetch_location_again_desc);
            } else if (resultCode == AppConstants.SHOW_TURN_ON_GPS) {
                errorDescription.setText(R.string.turn_on_gps_desc);
            }
            updateCurrentAddressName(mAddressDetailsLocationData);
            setupDiscoverAdapter();
            refreshDateTime();
            fetchOutlets();
        }
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
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

    private void setupDiscoverAdapter() {
        discoverAdapter = new DiscoverOutletAdapter();
        discoverAdapter.setmContext(mActivity);
        mRecyclerView.setAdapter(discoverAdapter);
    }

    private void fetchOutlets() {
        discoverAdapter.getItems().clear();
        discoverAdapter.notifyDataSetChanged();
        noDataHolder.noDataOutlet.setVisibility(View.GONE);
        showProgressBar();
        fabFilter.setVisibility(View.GONE);

        String latitude = mAddressDetailsLocationData != null ? mAddressDetailsLocationData.getCoords().getLat() : "28.4733044";
        String longitude = mAddressDetailsLocationData != null ? mAddressDetailsLocationData.getCoords().getLon() : "77.0994511";

        fetchingOutlets = true;
        HttpService.getInstance().getRecommendedOutlets(getUserToken(), latitude, longitude, mDate, mTime, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;
                if (arrayListBaseResponse.isResponse()) {
                    fetchedOutlets = arrayListBaseResponse.getData().getOutlets();
                    String twystCash = arrayListBaseResponse.getData().getTwystCash();
                    updateOutlets(fetchedOutlets);
                } else {
                    Toast.makeText(mActivity, arrayListBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                hideProgressBar();
                fabFilter.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                fetchingOutlets = false;
                hideProgressBar();
                mActivity.handleRetrofitError(error);
            }
        });
    }

    public void fetchOutletsBanners(String bannerID) {
        discoverAdapter.getItems().clear();
        discoverAdapter.notifyDataSetChanged();
        noDataHolder.noDataOutlet.setVisibility(View.GONE);
        showProgressBar();
        fabFilter.setVisibility(View.GONE);

        fetchingOutlets = true;
        HttpService.getInstance().getBannerOutlets(bannerID, getUserToken(), new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;

                if (arrayListBaseResponse.isResponse()) {
                    fetchedOutlets = arrayListBaseResponse.getData().getOutlets();
                    String twystCash = arrayListBaseResponse.getData().getTwystCash();

                    updateOutlets(fetchedOutlets);
                } else {
                    Toast.makeText(mActivity, arrayListBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                hideProgressBar();
                fabFilter.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                fetchingOutlets = false;
                hideProgressBar();
                mActivity.handleRetrofitError(error);
            }
        });
    }

    private void updateOutlets(ArrayList<Outlet> fetchedOutlets) {
        if (fetchedOutlets != null) {
            if (fetchedOutlets.isEmpty()) {
                outletsNotFound = true;
                discoverAdapter.setOutletsNotFound(true);
                discoverAdapter.getItems().clear();
                discoverAdapter.notifyDataSetChanged();
                noDataHolder.noDataOutlet.setVisibility(View.VISIBLE);
                noDataHolder.tvNoData.setText(getResources().getString(R.string.no_data_outlet));
                noDataHolder.ivNoData.setImageResource(R.drawable.no_data_order);
            } else {
                outletsNotFound = false;
                discoverAdapter.setOutletsNotFound(false);
                discoverAdapter.getItems().addAll(fetchedOutlets);
                discoverAdapter.notifyDataSetChanged();
                LinkedHashSet<String> cuisinesSet = new LinkedHashSet<String>();
                for (Outlet outlet : fetchedOutlets) {
                    cuisinesSet.addAll(outlet.getCuisines());
                }

                ArrayList<String> cuisinesList = new ArrayList<String>();
                for (String cuisine : cuisinesSet) {
                    cuisinesList.add(cuisine);
                }
                cuisinesSet = null;
                FilterOptions.updateCuisinesList(cuisinesList);

                int a = 0;
            }
        }
    }

    private void hideProgressBar() {
        if (circularProgressBarOutlet != null) {
            circularProgressBarOutlet.setVisibility(View.GONE);
        }
    }

    private void showProgressBar() {
        if (circularProgressBarOutlet != null) {
            circularProgressBarOutlet.setVisibility(View.VISIBLE);
        }

    }

    private ArrayList<Outlet> fetchOutletsWithFilters() {
        ArrayList<Outlet> filteredOutlets = new ArrayList<>();

        if ((filterTagsMap.get(AppConstants.cuisinetag) != null && filterTagsMap.get(AppConstants.cuisinetag).length > 0) || (filterTagsMap.get(AppConstants.offersTag) != null && filterTagsMap.get(AppConstants.offersTag).length > 0)) {
            for (Outlet outlet : fetchedOutlets) {
                boolean qualified = true;

                if (filterTagsMap.get(AppConstants.offersTag) != null && filterTagsMap.get(AppConstants.offersTag).length > 0) {
                    if (outlet.getOfferCount() <= 0) {
                        qualified = false;
                        continue;
                    }
                }

                if (filterTagsMap.get(AppConstants.paymentTag) != null && filterTagsMap.get(AppConstants.paymentTag).length > 0) {
                    if (outlet.getPaymentOptions() != null && !outlet.getPaymentOptions().contains("cod")) {
                        qualified = false;
                        continue;
                    }
                }

                if (qualified && filterTagsMap.get(AppConstants.cuisinetag) != null && filterTagsMap.get(AppConstants.cuisinetag).length > 0) {
                    for (long optionPosition : filterTagsMap.get(AppConstants.cuisinetag)) {
                        qualified = false;
                        String option = optionsMap.get(AppConstants.cuisinetag).get((int) optionPosition);
                        if (outlet.getCuisines().contains(option)) {
                            qualified = true;
                            break;
                        }
                    }
                }
                if (qualified) {
                    filteredOutlets.add(outlet);
                }
            }
        } else {
            filteredOutlets = fetchedOutlets;
        }

        if (filterTagsMap.get(AppConstants.sortTag) != null && filterTagsMap.get(AppConstants.sortTag).length > 0) {
            long position = filterTagsMap.get(AppConstants.sortTag)[0];
            switch (optionsMap.get(AppConstants.sortTag).get((int) position)) {
                case "Delivery Time - Low to High":
                    Collections.sort(filteredOutlets, Outlet.ComparatorDeliveryTimeLowToHigh);
                    break;
                case "Minimum Bill - Low to High":
                    Collections.sort(filteredOutlets, Outlet.ComparatorMinimumBillLowToHigh);
                    break;
                case "Minimum Bill - High to Low":
                    Collections.sort(filteredOutlets, Outlet.ComparatorMinimumBillHighToLow);
                    break;
                default:
                    break;
            }
        }

        return filteredOutlets;
    }

    public String getUserToken() {
        SharedPreferences prefs = mActivity.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");
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
        return "DiscoverOutletFragment";
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
}
