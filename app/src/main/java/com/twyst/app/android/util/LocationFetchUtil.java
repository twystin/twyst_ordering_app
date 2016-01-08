package com.twyst.app.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.twyst.app.android.R;
import com.twyst.app.android.service.FetchAddressIntentService;

/**
 * Created by anshul on 1/6/2016.
 */
public class LocationFetchUtil implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private final LocationFetchResultCodeListener locationFetchResultCodeListener;

    protected static final String TAG = "LocationFetchUtil";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int GPS_TRY_AGAIN_TIME = 2500;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    protected boolean mAddressRequested;
    protected boolean mLocationRequested;
    protected String mAddressOutput;

    static private short no_of_trials = 0;


    public LocationFetchUtil(Context context) {
        mContext = context;
        mAddressRequested = false;
        mLocationRequested = false;
        locationFetchResultCodeListener = (LocationFetchResultCodeListener) mContext;
    }

    public void requestAddress(Location lastLocation) {
        mAddressRequested = true;
        mAddressOutput = "";
        mResultReceiver = new AddressResultReceiver(new Handler());
        mLastLocation = lastLocation;
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        fetchAddress();
    }

    public void requestLocation() {

        mLocationRequested = true;
        mLastLocation = null;
        buildGoogleApiClient();
        createLocationRequest();
        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest);
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(getTagName(), "All location settings are satisfied.");
                        if (mLocationRequested) {
                            fetchLocation(true);
                        } else if (mAddressRequested) {
                            fetchAddress();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(getTagName(), "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult((Activity) mContext, AppConstants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(getTagName(), "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(getTagName(), "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        locationFetchResultCodeListener.onReceiveLocation(3, mLastLocation);
                        break;
                }
            }
        });

    }

    public void fetchLocation(final boolean tryAgain) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLocationRequested = false;
            locationFetchResultCodeListener.onReceiveLocation(2, mLastLocation);
        } else {
            if (tryAgain) {
                // Execute some code after 2 seconds have passed
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        fetchLocation(false);
                    }
                }, GPS_TRY_AGAIN_TIME);
            } else {
                mLocationRequested = false;
                locationFetchResultCodeListener.onReceiveLocation(4, mLastLocation);
            }
        }
    }

    public void fetchAddress() {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(mContext, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            } else {
                startIntentService();
            }
        }
    }

    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppConstants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        mContext.startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppConstants.RESULT_DATA_KEY);
            if (mAddressOutput.equals("Sorry, the service is not available")) {

                if (++no_of_trials < 1) {
                    fetchAddress();
                } else {
                    no_of_trials = 0;
                    mAddressRequested = false;
                    locationFetchResultCodeListener.onReceiveAddress(4, mAddressOutput);
                }
            } else {

                mAddressRequested = false;
                locationFetchResultCodeListener.onReceiveAddress(2, mAddressOutput);
            }

            // Show a toast message if an address was found.
            if (resultCode == AppConstants.SUCCESS_RESULT) {
                showToast(mContext.getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.

        }
    }


    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    protected String getTagName() {
        return "LocationFetchUtil";
    }

    public interface LocationFetchResultCodeListener {
        void onReceiveAddress(int resultCode, String addressOutput);

        void onReceiveLocation(int resultCode, Location location);
    }


}
