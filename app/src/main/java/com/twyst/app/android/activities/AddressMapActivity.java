package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.twyst.app.android.util.PermissionUtil;
import com.twyst.app.android.util.SharedPreferenceSingleton;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;

/**
 * Created by anshul on 1/8/2016.
 */
public class AddressMapActivity extends FragmentActivity implements LocationFetchUtil.LocationFetchResultCodeListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mMap;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected Location mLastLocation;

    private static final int REQUEST_LOCATION = 1;
    protected static final String TAG = "AddressMapActivity";

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

                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    locationFetchUtil.requestAddress(mLastLocation, false);
                } else {

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddressMapActivity.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(R.string.internet_connection_unavailable);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    if (twystProgressHUD != null) {
                        twystProgressHUD.dismiss();
                    }
                    mAddressRequested = false;
                    alertDialog.show();
                }
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
        if (PermissionUtil.getInstance().approveLocation(AddressMapActivity.this, false)) {
            twystProgressHUD = TwystProgressHUD.show(AddressMapActivity.this, false, null);
            mLocationRequested = true;
            locationFetchUtil.requestLocation(false);
        } else {

            mFetchAddressButton.setClickable(false);
            SharedPreferenceSingleton sharedPreference = SharedPreferenceSingleton.getInstance();
            locationData = sharedPreference.getCurrentUsedLocation();
            mLastLocation = new Location("default");

            if (locationData != null) {
                currentPosition = new LatLng(Double.parseDouble(locationData.getCoords().getLat()), Double.parseDouble(locationData.getCoords().getLon()));
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Location permissions not provided!");
                mLastLocation.setLatitude(Double.parseDouble(locationData.getCoords().getLat()));
                mLastLocation.setLongitude(Double.parseDouble(locationData.getCoords().getLon()));
            } else {
                locationData = new AddressDetailsLocationData();
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Location permissions not provided!!");
                mLastLocation.setLatitude(currentPosition.latitude);
                mLastLocation.setLongitude(currentPosition.longitude);
                locationData.setCoords(new Coords(String.valueOf(currentPosition.latitude), String.valueOf(currentPosition.longitude)));
            }
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for location permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                fetchLocation();
            } else {
                Log.i(TAG, "Location permissions were NOT granted.");

                Intent intent = new Intent(AddressMapActivity.this, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_LOCATION);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, getResources().getString(R.string.permission_location_rationale));
                startActivity(intent);

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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferenceSingleton sharedPreferenceSingleton = SharedPreferenceSingleton.getInstance();
                sharedPreferenceSingleton.saveCurrentUsedLocation(locationData);
                sharedPreferenceSingleton.setSaveLocationClicked(false);
                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_ADD);
                startActivity(intent);
                finish();
            } else {

                if (SharedPreferenceSingleton.getInstance().isPassedCartCheckoutStage()) {
                    // in Payment flow
                    Bundle bundle = getIntent().getExtras();
                    String outletId = bundle.getString(AppConstants.INTENT_PARAM_OUTLET_ID);
                    String phone = bundle.getString(AppConstants.INTENT_PARAM_PHONE);
                    ArrayList<Items> cartItemsList = (ArrayList<Items>) bundle.getSerializable(AppConstants.INTENT_PARAM_CART_LIST);
                    SharedPreferenceSingleton.getInstance().saveCurrentUsedLocation(locationData);
                    SharedPreferenceSingleton.getInstance().setSaveLocationClicked(false);
                    UtilMethods.checkOut(locationData, cartItemsList, outletId, phone, AddressMapActivity.this, true);
                } else {
                    Intent intent = new Intent(AddressMapActivity.this, AddressAddNewActivity.class);
                    SharedPreferenceSingleton.getInstance().setSaveLocationClicked(false);
                    SharedPreferenceSingleton.getInstance().saveCurrentUsedLocation(locationData);
                    startActivity(intent);
                    finish();
                }

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
            SharedPreferenceSingleton sharedPreference = SharedPreferenceSingleton.getInstance();
            locationData = sharedPreference.getCurrentUsedLocation();
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
        alertDialog.setMessage(R.string.couldnot_fetch_addressName);

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
