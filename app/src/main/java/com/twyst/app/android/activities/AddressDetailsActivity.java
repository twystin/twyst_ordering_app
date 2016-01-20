package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.SimpleArrayAdapter;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.SharedPreferenceAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/8/2016.
 */
public class AddressDetailsActivity extends AppCompatActivity implements LocationFetchUtil.LocationFetchResultCodeListener {

    List<AddressDetailsLocationData> addressList = new ArrayList<AddressDetailsLocationData>();
    SimpleArrayAdapter adapter = null;
    private LinearLayout add;

    protected static final String TAG = "MainActivity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private Location mLocation;
    private AddressDetailsLocationData mAddressDetailsLocationData;

    private TextView mLocationAddressTextView;
    private ProgressBar mProgressBar;
    private LinearLayout currentAddresslayout;
    private TextView mLocationFetchAgain;
    private TextView mTurnOnGps;

    LocationFetchUtil locationFetchUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);

        // saved address related code
        adapter = new SimpleArrayAdapter(AddressDetailsActivity.this,addressList);
        ListView listView = (ListView) findViewById(R.id.saved_address_list_view);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
            }
        });
        if (addressList == null || addressList.size() == 0) {
            ((CardView) findViewById(R.id.cardView_listview)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.cardView_noAddress)).setVisibility(View.VISIBLE);
        }


        // Add new Address related code
        add = (LinearLayout) findViewById(R.id.linlay_add_address_icon);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressDetailsActivity.this, AddressAddNewActivity.class);
                intent.putExtra(AppConstants.MAP_TO_BE_SHOWN, true);
                startActivity(intent);

            }
        });

        // fetch current location related code
        mLocationAddressTextView = (TextView) findViewById(R.id.tv_current_address);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        currentAddresslayout = (LinearLayout) findViewById(R.id.linlay_current_location);
        mTurnOnGps = (TextView) findViewById(R.id.tv_turn_on_gps);
        mLocationFetchAgain = (TextView) findViewById(R.id.tv_fetch_location_again);
        ((ImageView) findViewById(R.id.radio_current_loc)).setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.radio_check_config));

        locationFetchUtil = new LocationFetchUtil(this);
        fetchCurrentLocation();

        currentAddresslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((mLocationAddressTextView.getText().toString()).equals("unavailable!"))) {
                    ((ImageView) findViewById(R.id.radio_current_loc)).setSelected(true);
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent(AddressDetailsActivity.this, AddressAddNewActivity.class);
                    intent.putExtra(AppConstants.MAP_TO_BE_SHOWN, false);
                    intent.putExtra(AppConstants.DATA_TO_BE_SHOWN, mAddressDetailsLocationData);
                    startActivity(intent);
                }

            }
        });

        mTurnOnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentLocation();
            }
        });

        mLocationFetchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentLocation();
            }
        });
    }

    public void fetchCurrentLocation() {
        updateUIWidgets(AppConstants.SHOW_PROGRESS_BAR);
        mLocation = null;
        locationFetchUtil.requestLocation(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferenceAddress preference = new SharedPreferenceAddress();
        addressList = preference.getAddresses(AddressDetailsActivity.this);
        if (addressList != null && addressList.size() > 0) {
            adapter.clear();
            adapter.addAll(addressList);
            adapter.notifyDataSetChanged();
            ((CardView) findViewById(R.id.cardView_listview)).setVisibility(View.VISIBLE);
            ((CardView) findViewById(R.id.cardView_noAddress)).setVisibility(View.GONE);
        }
    }

    protected String getTagName() {
        return AddressDetailsActivity.class.getSimpleName();
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
                    updateUIWidgets(AppConstants.SHOW_TURN_ON_GPS);
                    break;
            }
        }
    }

    private void updateUIWidgets(int resultCode) {

        switch (resultCode) {
            case AppConstants.SHOW_PROGRESS_BAR:
                mProgressBar.setVisibility(View.VISIBLE);
                currentAddresslayout.setVisibility(View.GONE);
                mTurnOnGps.setVisibility(View.GONE);
                mLocationFetchAgain.setVisibility(View.GONE);
                break;
            case AppConstants.SHOW_CURRENT_LOCATION:
                mProgressBar.setVisibility(View.GONE);
                currentAddresslayout.setVisibility(View.VISIBLE);
                mTurnOnGps.setVisibility(View.GONE);
                mLocationFetchAgain.setVisibility(View.GONE);
                break;
            case AppConstants.SHOW_TURN_ON_GPS:
                mProgressBar.setVisibility(View.GONE);
                currentAddresslayout.setVisibility(View.GONE);
                mTurnOnGps.setVisibility(View.VISIBLE);
                mLocationFetchAgain.setVisibility(View.GONE);
                break;
            case AppConstants.SHOW_FETCH_LOCATION_AGAIN:
                mProgressBar.setVisibility(View.GONE);
                currentAddresslayout.setVisibility(View.GONE);
                mTurnOnGps.setVisibility(View.GONE);
                mLocationFetchAgain.setVisibility(View.VISIBLE);
                break;
        }
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
            mLocationAddressTextView.setText(address.getAddressLine(0)+ ", " + address.getAddressLine(1));
        }
        updateUIWidgets(resultCode);
    }

    @Override
    public void onReceiveLocation(int resultCode, Location location) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            mLocation = location;
            mAddressDetailsLocationData = new AddressDetailsLocationData();
            Coords coords = new Coords(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
            mAddressDetailsLocationData.setCoords(coords);
            locationFetchUtil.requestAddress(location,true);
        } else if (resultCode == AppConstants.SHOW_FETCH_LOCATION_AGAIN) {
            updateUIWidgets(resultCode);
        }
    }
}
