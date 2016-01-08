package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.SharedPreferenceAddress;
import com.twyst.app.android.util.TwystProgressHUD;

import java.util.List;

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

    private TwystProgressHUD twystProgressHUD;
    RelativeLayout mMapLayout;
    ImageButton mFetchAddressButton;

    private AddressDetailsLocationData locationData = null;
    private LatLng currentPosition = null;
    private LocationFetchUtil locationFetchUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map);

        mMapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        mFetchAddressButton = (ImageButton) findViewById(R.id.fetch_address_button);

        locationData = new AddressDetailsLocationData();
        currentPosition = new LatLng(28.49, 77.09);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mLocationRequested = false;
//        showProgressBar();
        mAddressOutput = "";
//        updateValuesFromBundle(savedInstanceState);

        setUpMapIfNeeded();
        locationFetchUtil = new LocationFetchUtil(this);
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
                setUpMap();
            }
        });

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAddressRequested = true;
//                showProgressBar();
                twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
                locationFetchUtil.requestAddress(mLastLocation);
            }
        });

    }

    public void fetchLocation() {
        mLocationRequested = true;
        locationFetchUtil.requestLocation();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
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
                setMarkerOnMap(currentPosition, "Current location", true);
            }
        }
    }

    protected String getTagName() {
        return AddressMapActivity.class.getSimpleName();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    if (mLocationRequested) {
                        fetchLocation();
                    } else if (mAddressRequested) {
                        locationFetchUtil.requestAddress(mLastLocation);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
//                    updateUIWidgets(AppConstants.SHOW_TURN_ON_GPS);
                    mAddressRequested = false;
                    mLocationRequested = false;
                    finish();
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
    public void onReceiveAddress(int resultCode, String addressOutput) {
        if (resultCode == 2) {
            mAddressRequested = false;
            twystProgressHUD.dismiss();
            locationData.setAddress(addressOutput);
            Bundle info = new Bundle();
            info.putSerializable("locationData", locationData);
            Intent intent = new Intent();
            intent.putExtras(info);
            setResult(RESULT_OK, intent);
            finish();
        } else if (resultCode == 4) {
            twystProgressHUD.dismiss();
            showSettingsAlert();
        }
    }

    @Override
    public void onReceiveLocation(int resultCode, Location location) {
        if (resultCode == 2) {
            mLocationRequested = false;
            mLastLocation = location;
            locationData = new AddressDetailsLocationData();
            Coords coords = new Coords(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
            locationData.setCoords(coords);

            Log.d("Anshul", "mlastLocation : " + mLastLocation.getLatitude() + mLastLocation.getLongitude());
            currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d("Anshul", "" + currentPosition.toString() + currentPosition.latitude + currentPosition.longitude);
            setUpMap();

        } else {
            SharedPreferenceAddress sharedPreference = new SharedPreferenceAddress();
            List<AddressDetailsLocationData> lastSavedLocations = sharedPreference.getAddresses(AddressMapActivity.this);
            if (lastSavedLocations != null && lastSavedLocations.size() > 0) {
                locationData = lastSavedLocations.get(lastSavedLocations.size() - 1);
                currentPosition = new LatLng(Double.parseDouble(locationData.getCoords().getLat()), Double.parseDouble(locationData.getCoords().getLon()));
                setUpMap();
            }

//            Toast.makeText(this,"(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_LONG);
        }
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
//                showProgressBar();
                twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
                locationFetchUtil.requestAddress(mLastLocation);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mAddressRequested = false;
//                showProgressBar();
                twystProgressHUD.dismiss();
                finish();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
