package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.SharedPreferenceAddress;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;

/**
 * Created by anshul on 1/8/2016.
 */
public class AddressMapActivity extends FragmentActivity implements LocationFetchUtil.LocationFetchResultCodeListener, OnMapReadyCallback {
    private GoogleMap mMap;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected Location mLastLocation;

    protected boolean mAddressRequested;
    protected boolean mLocationRequested;
    protected String mAddressOutput;
    private Address mAddress;

    private TwystProgressHUD twystProgressHUD;
    RelativeLayout mMapLayout;
    ImageButton mFetchAddressButton;

    private AddressDetailsLocationData locationData = null;
    private LatLng currentPosition = new LatLng(28.49, 77.09);
    private LatLng currentLocation;
    private LocationFetchUtil locationFetchUtil;
    private TextView errorMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map);

        mMapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        mFetchAddressButton = (ImageButton) findViewById(R.id.fetch_address_button);
        errorMessage = (TextView) findViewById(R.id.tv_map_error_message);

        locationData = new AddressDetailsLocationData();

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mLocationRequested = false;
        mAddressOutput = "";
        locationFetchUtil = new LocationFetchUtil(this);

        setUpMapIfNeeded();
        fetchLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setMarkerOnMap(latLng, "", false);
                Coords coords = new Coords(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
                locationData.setCoords(coords);
                if (mLastLocation != null) {
                    mLastLocation.setLongitude(latLng.longitude);
                    mLastLocation.setLatitude(latLng.latitude);
                }
            }
        });

        mFetchAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentLocation();
            }
        });

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressRequested = true;
                twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
                locationFetchUtil.requestAddress(mLastLocation, false);
            }
        });

        errorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationRequested = true;
                errorMessage.setVisibility(View.GONE);
                twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
                locationFetchUtil.requestLocation(true);
            }
        });

    }

    public void fetchLocation() {
        twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
        mLocationRequested = true;
        locationFetchUtil.requestLocation(false);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mMap = mapFragment.getMap();
        }
    }

    private void setUpMap() {
        if (mMap != null) {
            if (currentPosition != null) {
                twystProgressHUD.dismiss();
                setMarkerOnMap(currentPosition, "Selected location", true);
            }
        }
    }

    private void showCurrentLocation() {
        if (mMap != null) {
            if (currentLocation != null) {
                twystProgressHUD.dismiss();
                setMarkerOnMap(currentLocation, "Current location", true);
            }
        }
    }

    protected String getTagName() {
        return AddressMapActivity.class.getSimpleName();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);
        if (twystProgressHUD != null) {
            twystProgressHUD.dismiss();
        }
        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    if (mLocationRequested) {
                        fetchLocation();
                    } else if (mAddressRequested) {
                        locationFetchUtil.requestAddress(mLastLocation, false);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
                    mAddressRequested = false;
                    mLocationRequested = false;
                    errorMessage.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (currentPosition != null) {
            mMap = googleMap;
            setMarkerOnMap(currentPosition, "Current location", true);
        }
    }

    private void setMarkerOnMap(LatLng latLng, String addressTitle, boolean isAnimate) {
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(latLng);
        markerOptions.title(addressTitle);

        mMap.clear();
        mMap.addMarker(markerOptions);
        if (isAnimate) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            CameraPosition myPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(17).bearing(90).tilt(30).build();
            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(myPosition));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onReceiveAddress(int resultCode, Address address) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            mAddressRequested = false;
            twystProgressHUD.dismiss();
            mAddress = address;
            mAddressOutput = "";
            for (int i = 0; i < mAddress.getMaxAddressLineIndex(); i++) {
                mAddressOutput += mAddress.getAddressLine(i);
                mAddressOutput += ", ";
            }

            mAddressOutput += mAddress.getAddressLine(mAddress.getMaxAddressLineIndex());
            locationData.setAddress(mAddressOutput);

            locationData.setNeighborhood(mAddress.getAddressLine(0));
            locationData.setLandmark(mAddress.getAddressLine(1));

            Bundle info = new Bundle();
            info.putSerializable("locationData", locationData);

            if (getIntent().getBooleanExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, false)) {
                Intent intent = new Intent(AddressMapActivity.this, MainActivity.class);
                SharedPreferenceAddress sharedPreferenceAddress = new SharedPreferenceAddress();
                sharedPreferenceAddress.saveCurrentUsedLocation(AddressMapActivity.this, locationData);
                sharedPreferenceAddress.saveLastUsedLocation(AddressMapActivity.this, locationData);
                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_ADD);
                startActivity(intent);
                finish();
            } else {
                // in Payment flow
                Bundle bundle = getIntent().getExtras();
                String outletId = bundle.getString(AppConstants.INTENT_PARAM_OUTLET_ID);
                ArrayList<Items> cartItemsList = (ArrayList<Items>) bundle.getSerializable(AppConstants.INTENT_PARAM_CART_LIST);

                UtilMethods.checkOut(true, locationData, cartItemsList, outletId, AddressMapActivity.this);
//                Intent intent = new Intent();
//                intent.putExtras(info);
//                setResult(RESULT_OK, intent);
//                finish();
            }
        } else {
            // ideally only error should be AppConstants.SHOW_FETCH_LOCATION_AGAIN
            twystProgressHUD.dismiss();
            showSettingsAlert();

        }
    }

    @Override
    public void onReceiveLocation(int resultCode, Location location) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {

            mLocationRequested = false;
            mLastLocation = location;
            locationData = new AddressDetailsLocationData();
            Coords coords = new Coords(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
            locationData.setCoords(coords);
            Log.d("Debug", "mlastLocation : " + mLastLocation.getLatitude() + mLastLocation.getLongitude());
            currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mFetchAddressButton.setClickable(true);
            currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d("Debug", "" + currentPosition.toString() + currentPosition.latitude + currentPosition.longitude);
            errorMessage.setVisibility(View.GONE);
        } else {
            mFetchAddressButton.setClickable(false);
            SharedPreferenceAddress sharedPreference = new SharedPreferenceAddress();
            locationData = sharedPreference.getLastUsedLocation(AddressMapActivity.this);
            mLastLocation = new Location("default");

            if (locationData != null) {
                currentPosition = new LatLng(Double.parseDouble(locationData.getCoords().getLat()), Double.parseDouble(locationData.getCoords().getLon()));
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("CLick to turn On GPS, currently showing last known!");
                mLastLocation.setLatitude(Double.parseDouble(locationData.getCoords().getLat()));
                mLastLocation.setLongitude(Double.parseDouble(locationData.getCoords().getLon()));
            } else {
                locationData = new AddressDetailsLocationData();
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Click to turn On GPS, as none Saved Address, Currently showing Default!");
                mLastLocation.setLatitude(currentPosition.latitude);
                mLastLocation.setLongitude(currentPosition.longitude);
                locationData.setCoords(new Coords(String.valueOf(currentPosition.latitude), String.valueOf(currentPosition.longitude)));
            }
        }
        setUpMap();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddressMapActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Alert");

        // Setting Dialog Message
        alertDialog.setMessage("Sorry Service was not available, try again?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mAddressRequested = true;
                twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
                locationFetchUtil.requestAddress(mLastLocation, false);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mAddressRequested = false;
                twystProgressHUD.dismiss();
                finish();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
