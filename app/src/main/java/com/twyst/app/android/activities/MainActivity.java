package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DiscoverPagerAdapter;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.SharedPreferenceAddress;

/**
 * Created by Anshul on 1/14/2016.
 */
public class MainActivity extends BaseActivity implements LocationFetchUtil.LocationFetchResultCodeListener {

    protected boolean search;
    SharedPreferenceAddress sharedPreferenceAddress = new SharedPreferenceAddress();
    private LocationFetchUtil locationFetchUtil;
    private AddressDetailsLocationData mAddressDetailsLocationData;
    private Location mLocation;
    private boolean currentLocationRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String optionSelected = getIntent().getStringExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED);
        switch (optionSelected){
            case AppConstants.CHOOSE_LOCATION_OPTION_CURRENT:
                locationFetchUtil = new LocationFetchUtil(MainActivity.this);
                currentLocationRequested = true;
                locationFetchUtil.requestLocation();
                break;
            case AppConstants.CHOOSE_LOCATION_OPTION_SAVED:
                mAddressDetailsLocationData = sharedPreferenceAddress.getCurrentUsedLocation(MainActivity.this);
                break;
            case AppConstants.CHOOSE_LOCATION_OPTION_ADD:
                mAddressDetailsLocationData = sharedPreferenceAddress.getCurrentUsedLocation(MainActivity.this);
                break;
            default:
                break;
        }

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Order"));
        tabLayout.addTab(tabLayout.newTab().setText("Redeem"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final DiscoverPagerAdapter adapter = new DiscoverPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public void onReceiveAddress(int resultCode, Address address) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            String mAddressOutput = "";
            for (int i = 0; i < address.getMaxAddressLineIndex();i++)
            {
                mAddressOutput +=  address.getAddressLine(i);
                mAddressOutput += ", ";
            }

            mAddressOutput += address.getAddressLine(address.getMaxAddressLineIndex());
            mAddressDetailsLocationData.setAddress(mAddressOutput);
            mAddressDetailsLocationData.setNeighborhood(address.getAddressLine(0));
            mAddressDetailsLocationData.setLandmark(address.getAddressLine(1));
            sharedPreferenceAddress.saveCurrentUsedLocation(MainActivity.this, mAddressDetailsLocationData);
            sharedPreferenceAddress.saveLastUsedLocation(MainActivity.this, mAddressDetailsLocationData);
        } else {
            Toast.makeText(MainActivity.this, "onReceiveAddressError : " + resultCode, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onReceiveLocation(int resultCode, Location location) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            mLocation = location;
            mAddressDetailsLocationData = new AddressDetailsLocationData();
            Coords coords = new Coords(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
            mAddressDetailsLocationData.setCoords(coords);
            locationFetchUtil.requestAddress(location);
        } else if (resultCode == AppConstants.SHOW_FETCH_LOCATION_AGAIN) {
            Toast.makeText(MainActivity.this,"onReceiveLocationError : " + resultCode,Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    fetchCurrentLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
//                    updateUIWidgets(AppConstants.SHOW_TURN_ON_GPS);
                    break;
            }
        }
    }



    public void fetchCurrentLocation() {
//        updateUIWidgets(AppConstants.SHOW_PROGRESS_BAR);
        mLocation = null;
        locationFetchUtil.requestLocation();
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            MainActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    protected String getTagName() {
        return DiscoverActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private String createJsonObject() {
        return "[{\"_id\":\"5602e407d7ffbaef7ac31701\",\"name\":\"The Club Chemistry\",\"city\":\"Gurgaon\",\"address\":\"Ground Floor , Grand Mall,MG Road Gurgaon\",\"locality_1\":\"Grand Mall\",\"locality_2\":\"MG Road\",\"lat\":28.480004,\"long\":77.090141,\"distance\":1.2,\"open\":true,\"phone\":\"9999039301\",\"rating\":\"3.0\",\"offers\":[{\"_id\":\"560f6c55c2b4deab91b06eb7\",\"header\":\"Unlimited\",\"line1\":\"IMFL + 2 Starters\",\"line2\":\"at Rs. 1499\",\"description\":\"\",\"terms\":\"Couples Only \\nValid for 2.5 Hours\",\"type\":\"offer\",\"meta\":{\"conditions\":\"1499\",\"item\":\"IMFL + 2 Starters\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c56c2b4deab91b06eb8\",\"header\":\"Unlimited\",\"line1\":\"IMFL\",\"line2\":\"at Rs. 899\",\"description\":\"\",\"terms\":\"Valid Per Person\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"conditions\":\"899\",\"item\":\"IMFL\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c56c2b4deab91b06eb9\",\"header\":\"Unlimited\",\"line1\":\"IMFL\",\"line2\":\"at Rs. 3999\",\"description\":\"\",\"terms\":\"Valid for a Table of Maximum 5 People\\nValid for 2.5 Hours\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"conditions\":\"3999\",\"item\":\"IMFL\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c56c2b4deab91b06eba\",\"header\":\"Combo\",\"line1\":\"8 IMFL/Domestic + 2 Starters\",\"line2\":\"for Rs. 899\",\"description\":\"\",\"terms\":\"Taxes Extra\",\"type\":\"offer\",\"meta\":{\"_for\":899,\"items\":\"8 IMFL/Domestic + 2 Starters\",\"reward_type\":\"combo\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c57c2b4deab91b06ebb\",\"header\":\"Combo\",\"line1\":\"2 Starters + 2 Main Course + 6 IMFL\",\"line2\":\"for Rs. 1099\",\"description\":\"\",\"terms\":\"Taxes Extra\",\"type\":\"offer\",\"meta\":{\"_for\":1099,\"items\":\"2 Starters + 2 Main Course + 6 IMFL\",\"reward_type\":\"combo\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c57c2b4deab91b06ebc\",\"header\":\"Unlimited\",\"line1\":\"IMFL +  Starters ( Any 3 Veg/Chicken)\",\"line2\":\"at Rs. 1599\",\"description\":\"\",\"terms\":\"Taxes Extra\\nCouples Only\\nValid for 2.5 Hours\",\"type\":\"offer\",\"meta\":{\"conditions\":\"1599\",\"item\":\"IMFL +  Starters ( Any 3 Veg/Chicken)\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100}],\"following\":false,\"logo\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602e407d7ffbaef7ac31701/logo\",\"background\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602e407d7ffbaef7ac31701/background\",\"open_next\":{\"time\":\"1:00 PM\",\"day\":\"Tomorrow\"}},{\"_id\":\"5602f1add7ffbaef7ac31746\",\"name\":\"S Bar Club and Lounge\",\"city\":\"Gurgaon\",\"address\":\"Lower Ground Floor, 1,2,3,4 . Grand Mall ,MG Road ,Gurgaon\",\"locality_1\":\"Grand Mall\",\"locality_2\":\"MG Road\",\"lat\":28.480004,\"long\":77.090141,\"distance\":1.2,\"open\":true,\"phone\":\"01244108006\",\"rating\":\"4.0\",\"offers\":[{\"_id\":\"560f6c57c2b4deab91b06ebd\",\"header\":\"Combo\",\"line1\":\"4 Domestic Beer/IMFL(30ML) + 2 Starters (Veg+Chicken)\",\"line2\":\"for Rs. 799\",\"description\":\"\",\"terms\":\"Valid For 2\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"combo\",\"items\":\"4 Domestic Beer/IMFL(30ML) + 2 Starters (Veg+Chicken)\",\"_for\":799},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":1,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c57c2b4deab91b06ebe\",\"header\":\"Combo\",\"line1\":\"8 Domestic Beer/IMFL (30ML) + 2 Starters (Veg/Chicken)\",\"line2\":\"for Rs. 1099\",\"description\":\"\",\"terms\":\"Taxes Extra\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"combo\",\"items\":\"8 Domestic Beer/IMFL (30ML) + 2 Starters (Veg/Chicken)\",\"_for\":1099},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c58c2b4deab91b06ebf\",\"header\":\"Unlimited\",\"line1\":\"IMFL + 2 Starters (Veg/Chicken)\",\"line2\":\"at Rs. 1499\",\"description\":\"\",\"terms\":\"Taxes Extra\\nCouples Only\\n3 Hours of Service\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"unlimited\",\"item\":\"IMFL + 2 Starters (Veg/Chicken)\",\"conditions\":\"1499\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c58c2b4deab91b06ec0\",\"header\":\"Combo\",\"line1\":\"2 Domestic Beer/IMFL(30 ML) + 1 Starter\",\"line2\":\"for Rs. 349\",\"description\":\"\",\"terms\":\"For 1 Person\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"combo\",\"items\":\"2 Domestic Beer/IMFL(30 ML) + 1 Starter\",\"_for\":349},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c58c2b4deab91b06ec1\",\"header\":\"Unlimited\",\"line1\":\"2 Domestic Beer/ IMFL(30 ML) + 3 Starters(Veg/NVeg)\",\"line2\":\"at Rs. 899/999\",\"description\":\"\",\"terms\":\"Taxes Extra\\nApplicable Per Person\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"unlimited\",\"item\":\"2 Domestic Beer/ IMFL(30 ML) + 3 Starters(Veg/NVeg)\",\"conditions\":\"899/999\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100}],\"following\":false,\"logo\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602f1add7ffbaef7ac31746/logo\",\"background\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602f1add7ffbaef7ac31746/background\",\"open_next\":{\"time\":\"12:00 PM\",\"day\":\"Tomorrow\"}}]";
    }



    public String getUserToken() {
//        return "us5lxmyPyqnA4Ow20GmbhG362ZuMS4qB";
        return "vH8quBjd1C-2lgZBcFcjrUjQMMbnInLQ";

//        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }

}
