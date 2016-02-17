package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.SimpleArrayAdapter;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.LocationDetails.LocationsVerified;
import com.twyst.app.android.model.LocationDetails.LocationsVerify;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.PermissionUtil;
import com.twyst.app.android.util.SharedPreferenceSingleton;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anshul on 1/8/2016.
 */
public class AddressDetailsActivity extends BaseActionActivity implements LocationFetchUtil.LocationFetchResultCodeListener , ActivityCompat.OnRequestPermissionsResultCallback  {
    List<AddressDetailsLocationData> mAddressList = new ArrayList<AddressDetailsLocationData>();
    SimpleArrayAdapter adapter = null;
    private LinearLayout add;

    protected static final String TAG = "AddressDetailsActivity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private static final int REQUEST_LOCATION = 1;

    private Location mLocation;
    private AddressDetailsLocationData mAddressDetailsLocationData;

    private TextView mLocationAddressTextView;
    private ProgressBar mProgressBar;
    private LinearLayout currentAddresslayout;
    private TextView mLocationFetchAgain;
    private TextView mTurnOnGps;

    LocationFetchUtil locationFetchUtil;

    // From Cart
    private String mOutletId;
    private ArrayList<Items> mCartItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);

        setupToolBar();
        setup();
        fetchSavedAddresses();
    }

    private void setupAdapter(ArrayList<LocationsVerified> locationsVerifiedList) {
        // saved address related code
        adapter = new SimpleArrayAdapter(AddressDetailsActivity.this, mAddressList, locationsVerifiedList);
        final ListView listView = (ListView) findViewById(R.id.saved_address_list_view);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferenceSingleton.getInstance().setSaveLocationClicked(true);
                AddressDetailsLocationData addressDetailsLocationData = (AddressDetailsLocationData) listView.getItemAtPosition(position);
                UtilMethods.checkOut(addressDetailsLocationData, mCartItemsList, mOutletId, AddressDetailsActivity.this, true);
            }
        });
    }

    private void setup() {
        // Add new Address related code
        add = (LinearLayout) findViewById(R.id.linlay_add_address_icon);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewAddress();
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
//                    checkCurrentDeliverableAndProceed();
                    UtilMethods.checkOut(mAddressDetailsLocationData, mCartItemsList, mOutletId, AddressDetailsActivity.this, true);
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

    private void addNewAddress() {
        Intent addressDetailsIntent = new Intent(AddressDetailsActivity.this, AddressMapActivity.class);

        Bundle addressDetailsBundle = new Bundle();
        addressDetailsBundle.putString(AppConstants.INTENT_PARAM_OUTLET_ID, mOutletId);
        addressDetailsBundle.putSerializable(AppConstants.INTENT_PARAM_CART_LIST, mCartItemsList);

        addressDetailsIntent.putExtras(addressDetailsBundle);
        startActivity(addressDetailsIntent);
        finish();
    }

    private void fetchSavedAddresses() {
        Bundle bundle = getIntent().getExtras();
//        mOutletId = bundle.getString(AppConstants.INTENT_PARAM_OUTLET_ID);
//        mCartItemsList = (ArrayList<Items>) bundle.getSerializable(AppConstants.INTENT_PARAM_CART_LIST);
        mOutletId = OrderInfoSingleton.getInstance().getOrderSummary().getOutletId();
        mCartItemsList = OrderInfoSingleton.getInstance().getOrderSummary().getmCartItemsList();

        SharedPreferenceSingleton preference = SharedPreferenceSingleton.getInstance();
        mAddressList = preference.getAddresses();
        if (mAddressList != null && mAddressList.size() > 0) {
            ((CardView) findViewById(R.id.cardView_listview)).setVisibility(View.VISIBLE);
            ((CardView) findViewById(R.id.cardView_noAddress)).setVisibility(View.GONE);
            verifyLocationsAPI();

        } else {
            ((CardView) findViewById(R.id.cardView_listview)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.cardView_noAddress)).setVisibility(View.VISIBLE);
        }
    }

    private void verifyLocationsAPI() {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        LocationsVerify locationsVerify = new LocationsVerify(mOutletId, getLocalCoordsList());
        HttpService.getInstance().postLocationsVerify(UtilMethods.getUserToken(AddressDetailsActivity.this), locationsVerify, new Callback<BaseResponse<ArrayList<LocationsVerified>>>() {
            @Override
            public void success(BaseResponse<ArrayList<LocationsVerified>> baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    ArrayList<LocationsVerified> locationsVerifiedList = baseResponse.getData();
                    setupAdapter(locationsVerifiedList);
                } else {
                    Toast.makeText(AddressDetailsActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(AddressDetailsActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    private ArrayList<Coords> getLocalCoordsList() {
        ArrayList<Coords> localCoordsList = new ArrayList<>();
        if (mAddressList != null) {
            for (AddressDetailsLocationData addressDetailsLocationData : mAddressList) {
                localCoordsList.add(addressDetailsLocationData.getCoords());
            }
        }

        return localCoordsList;
    }

    public void fetchCurrentLocation() {
        if (PermissionUtil.getInstance().approveLocation(AddressDetailsActivity.this,false)){
            updateUIWidgets(AppConstants.SHOW_PROGRESS_BAR);
            mLocation = null;
            locationFetchUtil.requestLocation(true);
        } else {
            updateUIWidgets(AppConstants.SHOW_FETCH_LOCATION_AGAIN);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for location permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                fetchCurrentLocation();
            } else {
                Log.i(TAG, "Location permissions were NOT granted.");

                Intent intent = new Intent(AddressDetailsActivity.this, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_LOCATION);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, getResources().getString(R.string.permission_location_rationale));
                startActivity(intent);

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
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                mAddressOutput += address.getAddressLine(i);
                mAddressOutput += ", ";
            }

            mAddressOutput += address.getAddressLine(address.getMaxAddressLineIndex());
            mAddressDetailsLocationData.setAddress(mAddressOutput);
            mAddressDetailsLocationData.setNeighborhood(address.getAddressLine(0));
            mAddressDetailsLocationData.setLandmark(address.getAddressLine(1));
            mLocationAddressTextView.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1));
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
            locationFetchUtil.requestAddress(location, true);
        } else if (resultCode == AppConstants.SHOW_FETCH_LOCATION_AGAIN) {
            updateUIWidgets(resultCode);
        }
    }
}
