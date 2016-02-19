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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.CirclePageIndicator;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.AddressAddNewActivity;
import com.twyst.app.android.activities.AddressMapActivity;
import com.twyst.app.android.activities.FiltersActivity;
import com.twyst.app.android.activities.MainActivity;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.adapters.ImagePagerAdapter;
import com.twyst.app.android.layout.NoDataHolder;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.DiscoverData;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.FilterOptions;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.SharedPreferenceSingleton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

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

    private cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager viewPager;
    private List<Integer> imageIdList;
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
    private TextView errorDescription;
    private CircularProgressBar circularProgressBarOutlet;
    private LinearLayout showAddressLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_outlet_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.outletRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        noDataHolder = new NoDataHolder(view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        viewPager = (cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager) view.findViewById(R.id.ads_pager);
        imageIdList = new ArrayList<Integer>();
        imageIdList.add(R.drawable.banner1);
        imageIdList.add(R.drawable.banner2);
        imageIdList.add(R.drawable.banner3);
        imageIdList.add(R.drawable.banner4);
        viewPager.setAdapter(new ImagePagerAdapter(getActivity(), imageIdList));

        mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        mIndicator.setOnPageChangeListener(new MyOnPageChangeListener());

        viewPager.setInterval(5000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(0);
        viewPager.setScrollDurationFactor(5);

        fabFilter = (FloatingActionButton) view.findViewById(R.id.fab_filter);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FiltersActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(AppConstants.FILTER_MAP, filterTagsMap);
                intent.putExtras(extras);
                startActivityForResult(intent, AppConstants.GET_FILTER_ACTIVITY);
            }
        });

        mActivity = (MainActivity) getActivity();

        setupDiscoverAdapter();
        refreshDateTime();
        String optionSelected = getActivity().getIntent().getStringExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED);
        currentAddressName = (TextView) view.findViewById(R.id.tv_current_address_location);
        circularProgressBarOutlet = (CircularProgressBar) view.findViewById(R.id.circularProgressBarOutlet);
        showProgressBar();
        if (savedInstanceState == null) {
            switch (optionSelected) {
                case AppConstants.CHOOSE_LOCATION_OPTION_CURRENT:
                    mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
                    currentAddressName.setText(mAddressDetailsLocationData.getNeighborhood() + ", " + mAddressDetailsLocationData.getLandmark());
                    fetchOutlets(1);
                    break;
                case AppConstants.CHOOSE_LOCATION_OPTION_SAVED:
                    mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
                    currentAddressName.setText(mAddressDetailsLocationData.getNeighborhood() + ", " + mAddressDetailsLocationData.getLandmark());
                    fetchOutlets(1);
                    break;
                case AppConstants.CHOOSE_LOCATION_OPTION_ADD:
                    mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
                    currentAddressName.setText(mAddressDetailsLocationData.getNeighborhood() + ", " + mAddressDetailsLocationData.getLandmark());
                    fetchOutlets(1);
                    break;
                case AppConstants.CHOOSE_LOCATION_OPTION_SKIPPED:
                    Fragment discoverOutletFragment = (DiscoverOutletFragment) (getActivity().getSupportFragmentManager().getFragments().get(0));
                    locationFetchUtil = new LocationFetchUtil(getActivity(), discoverOutletFragment);
                    locationFetchUtil.requestLocation(true);
                    break;
                default:
                    break;
            }
        } else {
            mAddressDetailsLocationData = sharedPreferenceSingleton.getDeliveryLocation();
            currentAddressName.setText(mAddressDetailsLocationData.getNeighborhood() + ", " + mAddressDetailsLocationData.getLandmark());
            fetchOutlets(1);
        }


        showErrorLayout = (LinearLayout) view.findViewById(R.id.linlay_discover_fragment_error_layout);
        errorDescription = (TextView) view.findViewById(R.id.tv_error_description);
        showAddressLayout = (LinearLayout) view.findViewById(R.id.linlay_display_address);

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
                Intent intent = new Intent(getActivity(), AddressAddNewActivity.class);
                startActivityForResult(intent, AppConstants.EDIT_ADDRESS);
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAddressDetailsLocationData!=null) {
            SharedPreferenceSingleton.getInstance().saveCurrentUsedLocation(mAddressDetailsLocationData);
        }

    }

    public void fetchLocation() {
        locationFetchUtil.requestLocation(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == AppConstants.GET_FILTER_ACTIVITY) {
            if (resultCode == AppConstants.GOT_FILTERS_SUCCESS) {
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

//                Toast.makeText(getActivity(), tags.toString(), Toast.LENGTH_LONG).show();
                if (tags.size() > 0) {

                    ArrayList<Outlet> list = fetchOutletsWithFilters();
                    discoverAdapter.getItems().clear();
                    discoverAdapter.getItems().addAll(list);
                    discoverAdapter.notifyDataSetChanged();
                } else {
                    discoverAdapter.getItems().clear();
                    discoverAdapter.getItems().addAll(fetchedOutlets);
                    discoverAdapter.notifyDataSetChanged();
                }
                hideProgressBar();


            }
        } else if (requestCode == AppConstants.EDIT_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
                AddressDetailsLocationData newAddressLocationData = SharedPreferenceSingleton.getInstance().getDeliveryLocation();
                if (newAddressLocationData != null) {

                    if (newAddressLocationData.getLandmark().equals("Unnamed Address")) {
                        currentAddressName.setText(newAddressLocationData.getLandmark());
                    } else {
                        currentAddressName.setText(newAddressLocationData.getNeighborhood() + ", " + newAddressLocationData.getLandmark());
                    }

                    if (!newAddressLocationData.equals(mAddressDetailsLocationData)) {
                        mAddressDetailsLocationData = newAddressLocationData;
                        fetchOutlets(1);
                    }
                }
            }

        }
    }

    @Override
    public void onReceiveAddress(int resultCode, Address address) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            String mAddressOutput = "";
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                mAddressOutput += address.getAddressLine(i);
                mAddressOutput += ", ";
            }

            mAddressOutput += address.getAddressLine(address.getMaxAddressLineIndex());
            mAddressDetailsLocationData.setAddress(mAddressOutput);
            mAddressDetailsLocationData.setNeighborhood(address.getAddressLine(0));
            mAddressDetailsLocationData.setLandmark(address.getAddressLine(1));
            sharedPreferenceSingleton.saveCurrentUsedLocation(mAddressDetailsLocationData);
            currentAddressName.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1));
            fetchOutlets(1);
        } else {
//            Toast.makeText(getActivity(), "onReceiveAddressError : " + resultCode, Toast.LENGTH_LONG).show();
//            mAddressDetailsLocationData.setAddress("Unnamed Address");
//            mAddressDetailsLocationData.setNeighborhood("Unnamed Address");
//            mAddressDetailsLocationData.setLandmark("Unnamed Address");
//            sharedPreferenceSingleton.saveCurrentUsedLocation(mAddressDetailsLocationData);
//            currentAddressName.setText("Unnamed Address");
//            fetchOutlets(1);
            Toast.makeText(getActivity(), R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), AddressMapActivity.class);
            intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
            startActivity(intent);
            getActivity().finish();
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
//            Toast.makeText(getActivity(), "onReceiveLocationError : " + resultCode, Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            resolveError(resultCode);
        }
    }

    public void resolveError(int resultCode) {
        mAddressDetailsLocationData = sharedPreferenceSingleton.getCurrentUsedLocation();

        if (mAddressDetailsLocationData == null) {
            Intent intent = new Intent(getActivity(), AddressMapActivity.class);
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
            currentAddressName.setText(mAddressDetailsLocationData.getNeighborhood() + ", " + mAddressDetailsLocationData.getLandmark());
            setupDiscoverAdapter();
            refreshDateTime();
            fetchOutlets(1);
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
        discoverAdapter.setmContext(getActivity());
        mRecyclerView.setAdapter(discoverAdapter);
    }

    private void fetchOutlets(final int start) {
        discoverAdapter.getItems().clear();
        discoverAdapter.notifyDataSetChanged();
        noDataHolder.noDataOutlet.setVisibility(View.GONE);
        showProgressBar();
        fabFilter.setVisibility(View.GONE);
//        String latitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, null);
//        String longitude = getPrefs().getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, null);
        String latitude = mAddressDetailsLocationData != null ? mAddressDetailsLocationData.getCoords().getLat() : "28.4733044";
        String longitude = mAddressDetailsLocationData != null ? mAddressDetailsLocationData.getCoords().getLon() : "77.0994511";

        Log.d(getTagName(), "Going to fetch outlets with, start: " + start + ", lat " + latitude + ", long: " + longitude + ", date: " + mDate + ", time: " + mTime);
        fetchingOutlets = true;

        int end = start + AppConstants.DISCOVER_LIST_PAGESIZE - 1;

        HttpService.getInstance().getRecommendedOutlets(getUserToken(), latitude, longitude, mDate, mTime, new Callback<BaseResponse<DiscoverData>>() {
            @Override
            public void success(BaseResponse<DiscoverData> arrayListBaseResponse, Response response) {
                fetchingOutlets = false;

                fetchedOutlets = arrayListBaseResponse.getData().getOutlets();
                String twystBucks = arrayListBaseResponse.getData().getTwystBucks();

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
                        if (start == 1) {//Clear the items in adapter, fresh download
                            Log.d(getTagName(), "clearing outlets on discover screen");
                            discoverAdapter.getItems().clear();
                        }

                        outletsNotFound = false;
                        discoverAdapter.setOutletsNotFound(false);
                        discoverAdapter.getItems().addAll(fetchedOutlets);
                        discoverAdapter.notifyDataSetChanged();
                        LinkedHashSet<String> cuisinesSet = new LinkedHashSet<String>();
                        for (Outlet outlet: fetchedOutlets){
                            cuisinesSet.addAll(outlet.getCuisines());
                        }

                        ArrayList<String> cuisinesList = new ArrayList<String>();
                        for (String cuisine: cuisinesSet){
                            cuisinesList.add(cuisine);
                        }
                        cuisinesSet = null;
                        FilterOptions.updateCuisinesList(cuisinesList);

                        int a =0;
                    }
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

                if (filterTagsMap.get(AppConstants.paymentTag) != null && filterTagsMap.get(AppConstants.paymentTag).length > 0){
                    if (outlet.getPaymentOptions() != null  && !outlet.getPaymentOptions().contains("cod")){
                        qualified = false;
                        continue;
                    }
                }

                if (qualified && filterTagsMap.get(AppConstants.cuisinetag) != null && filterTagsMap.get(AppConstants.cuisinetag).length > 0) {
                    for (long optionPosition : filterTagsMap.get(AppConstants.cuisinetag)) {
                        String option = optionsMap.get(AppConstants.cuisinetag).get((int) optionPosition);
                        if (!(outlet.getCuisines().contains(option))) {
                            qualified = false;
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
        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
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
