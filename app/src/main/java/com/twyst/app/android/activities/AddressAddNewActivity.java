package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.SharedPreferenceSingleton;

import java.util.ArrayList;

/**
 * Created by anshul on 1/8/2016.
 */
public class AddressAddNewActivity extends BaseActionActivity implements OnMapReadyCallback {
    private static final int PLACE_PICKER_REQUEST = 2;
    private ImageView homeTag;
    private ImageView workTag;
    private ImageView otherTag;
    private EditText address;
    private EditText line1;
    private EditText line2;
    private EditText landmark;
    private GoogleMap mMap;
    private ImageView chosenLocationButton;
    private LatLng currentPosition = null;
    private TextView tvProceed;
    private FrameLayout flProceed;
    private TextView tvAddressDetected;
    private LinearLayout linlayFullAddress;

    private OrderSummary mOrderSummary;
    private AddressDetailsLocationData mNewAddress = new AddressDetailsLocationData();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add_new);

        homeTag = (ImageView) findViewById(R.id.add_address_home_tag);
        workTag = (ImageView) findViewById(R.id.add_address_work_tag);
        otherTag = (ImageView) findViewById(R.id.add_address_other_tag);
        homeTag.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.home_tag_config));
//        homeTag.setSelected(true);
        workTag.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.work_tag_config));
        otherTag.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.other_tag_config));

        address = (EditText) findViewById(R.id.editView_address);
        line1 = (EditText) findViewById(R.id.editView_line1);
        line2 = (EditText) findViewById(R.id.editView_line2);
        landmark = (EditText) findViewById(R.id.editView_landmark);

        //Setting text
        ((TextView) findViewById(R.id.tv_tag_home)).setText(AddressDetailsLocationData.TAG_HOME);
        ((TextView) findViewById(R.id.tv_tag_work)).setText(AddressDetailsLocationData.TAG_WORK);
        ((TextView) findViewById(R.id.tv_tag_other)).setText(AddressDetailsLocationData.TAG_OTHER);


        homeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeTag.setSelected(true);
                workTag.setSelected(false);
                otherTag.setSelected(false);
                findViewById(R.id.editView_other_tag).setVisibility(EditText.GONE);
            }
        });

        otherTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeTag.setSelected(false);
                workTag.setSelected(false);
                otherTag.setSelected(true);

                EditText otherTagEdittext = (EditText) findViewById(R.id.editView_other_tag);
                otherTagEdittext.setVisibility(EditText.VISIBLE);
                otherTagEdittext.setCursorVisible(true);
                otherTagEdittext.requestFocus();
            }
        });

        workTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeTag.setSelected(false);
                workTag.setSelected(true);
                otherTag.setSelected(false);
                findViewById(R.id.editView_other_tag).setVisibility(EditText.GONE);
            }
        });

        tvProceed = (TextView) findViewById(R.id.tvProceedOk);
        flProceed = (FrameLayout) findViewById(R.id.proceed_ok);

        tvAddressDetected = (TextView) findViewById(R.id.tv_detetcted_address);
        linlayFullAddress = (LinearLayout) findViewById(R.id.linlay_full_address);
        if (SharedPreferenceSingleton.getInstance().isPassedCartCheckoutStage()) {
            tvProceed.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Drawable img = getResources().getDrawable(
                                    R.drawable.checkout_arrow);
                            int height = tvProceed.getMeasuredHeight() * 2 / 3;
                            img.setBounds(0, 0, height, height);
                            tvProceed.setCompoundDrawables(null, null, img, null);
                            tvProceed.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    });
        }


        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);
            mNewAddress = mOrderSummary.getAddressDetailsLocationData();
            setTextLocationFetch(mNewAddress);
            tvProceed.setText("PROCEED");
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvProceed.getLayoutParams();
            params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            tvProceed.setLayoutParams(params);
        } else {
            mNewAddress = SharedPreferenceSingleton.getInstance().getDeliveryLocation();
            setTextLocationFetch(mNewAddress);
            tvProceed.setText("OK");
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvProceed.getLayoutParams();
            params.gravity = Gravity.CENTER;
            tvProceed.setLayoutParams(params);
        }

        setupToolBar();
        if (mNewAddress != null && mNewAddress.getTag() != null) {
//            tvAddressDetected.setText("Saved Address");
            linlayFullAddress.setVisibility(View.GONE);
        } else {
            tvAddressDetected.setText("Auto Detected Address");
        }

        if (mNewAddress != null && mNewAddress.getTag() != null) {
            switch (mNewAddress.getTag()) {
                case AddressDetailsLocationData.TAG_HOME:
                    homeTag.setSelected(true);
                    break;
                case AddressDetailsLocationData.TAG_WORK:
                    workTag.setSelected(true);
                    break;
                default:
                    otherTag.setSelected(true);
                    break;
            }
        }


        flProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extras != null) {
                    clickProceedFunc();
                } else {
                    clickOkFunc();
                }
            }
        });

        // setting up map
        chosenLocationButton = (ImageView) findViewById(R.id.chosen_location_button);
        if (mNewAddress != null) {
            currentPosition = new LatLng(Double.parseDouble(mNewAddress.getCoords().getLat()), Double.parseDouble(mNewAddress.getCoords().getLon()));
        }
        setUpMapIfNeeded();
        if (currentPosition != null) {
            chosenLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMarkerOnMap(currentPosition, "Current location", true);
                }
            });
        }

        ((LinearLayout) findViewById(R.id.linlay_add_address)).setVisibility(View.VISIBLE);

        findViewById(R.id.tvChangeLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreferenceSingleton.getInstance().isPassedCartCheckoutStage()) {
                    Intent intent = new Intent(AddressAddNewActivity.this, AddressDetailsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(AddressAddNewActivity.this, AddressDetailsActivity.class);
                    startActivity(intent);
                }
            }
        });

        // if change is clicked from MainActivity when save location was previously selected
        if (extras == null) {
            mNewAddress = SharedPreferenceSingleton.getInstance().getDeliveryLocation();
            if (mNewAddress != null && mNewAddress.getTag() != null) {
                Intent intent = new Intent(AddressAddNewActivity.this, AddressDetailsActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    private void clickProceedFunc() {
        EditText address = (EditText) findViewById(R.id.editView_address);
        EditText line1 = (EditText) findViewById(R.id.editView_line1);
        EditText line2 = (EditText) findViewById(R.id.editView_line2);
        EditText landmark = (EditText) findViewById(R.id.editView_landmark);

        if (validateEditText(line1) && validateEditText(line2)) {
            mNewAddress.setLine1(line1.getText().toString());
            mNewAddress.setLine2(line2.getText().toString());
            mNewAddress.setLandmark(landmark.getText().toString());
            if (homeTag.isSelected()) {
                mNewAddress.setTag(AddressDetailsLocationData.TAG_HOME);
            } else if (workTag.isSelected()) {
                mNewAddress.setTag(AddressDetailsLocationData.TAG_WORK);
            } else if (otherTag.isSelected()) {
                mNewAddress.setTag(((EditText) findViewById(R.id.editView_other_tag)).getText().toString());
            }

            SharedPreferenceSingleton preference = SharedPreferenceSingleton.getInstance();
            preference.saveCurrentUsedLocation(mNewAddress);

            if (mNewAddress.getTag() != null) {
                boolean isLocationAlreadyExist = false;
                ArrayList<AddressDetailsLocationData> savedAddressList = preference.getAddresses();
                if (savedAddressList != null && savedAddressList.size() > 0) {
                    for (AddressDetailsLocationData savedLocation : preference.getAddresses()) {
                        if (savedLocation.equals(mNewAddress)) {
                            isLocationAlreadyExist = true;
                            break;
                        }
                    }
                }

                if (!isLocationAlreadyExist) {
                    preference.addAddress(mNewAddress);
                }
            }
            updateOrderSummaryAndCheckout(mNewAddress);
        } else {
            validateEditText(line1);
            validateEditText(line2);
        }
    }

    private void clickOkFunc() {
        EditText address = (EditText) findViewById(R.id.editView_address);
        EditText line1 = (EditText) findViewById(R.id.editView_line1);
        EditText line2 = (EditText) findViewById(R.id.editView_line2);
        EditText landmark = (EditText) findViewById(R.id.editView_landmark);

        if (validateEditText(line2) && validateEditText(line1)) {
            mNewAddress.setLine1(line1.getText().toString());
            mNewAddress.setLine2(line2.getText().toString());
            mNewAddress.setLandmark(landmark.getText().toString());
            if (homeTag.isSelected()) {
                mNewAddress.setTag(AddressDetailsLocationData.TAG_HOME);
            } else if (workTag.isSelected()) {
                mNewAddress.setTag(AddressDetailsLocationData.TAG_WORK);
            } else if (otherTag.isSelected()) {
                mNewAddress.setTag(((EditText) findViewById(R.id.editView_other_tag)).getText().toString());
            }

            SharedPreferenceSingleton preference = SharedPreferenceSingleton.getInstance();
            preference.saveCurrentUsedLocation(mNewAddress);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        } else {
            validateEditText(line1);
            validateEditText(line2);
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

    @Override
    public void setupToolBar() {
        super.setupToolBar();
        if (mNewAddress != null && mNewAddress.getTag() != null) {
            this.setTitle("Confirm Address");
        } else {
            this.setTitle("Edit Address");
        }
    }

    private void updateOrderSummaryAndCheckout(AddressDetailsLocationData mNewAddress) {
        mOrderSummary.setAddressDetailsLocationData(mNewAddress);
        checkOut();
    }

    private void checkOut() {
        Intent checkOutIntent;
        if (mOrderSummary.getOfferOrderList().size() > 0) {
            checkOutIntent = new Intent(AddressAddNewActivity.this, AvailableOffersActivity.class);
        } else {
            checkOutIntent = new Intent(AddressAddNewActivity.this, OrderSummaryActivity.class);
        }

        Bundle orderSummaryData = new Bundle();
        orderSummaryData.putSerializable(AppConstants.INTENT_ORDER_SUMMARY, mOrderSummary);
        checkOutIntent.putExtras(orderSummaryData);
        startActivity(checkOutIntent);
        finish();
    }

    public boolean validateEditText(EditText editText) {
        if ((editText.getText().toString()).matches("")) {
            editText.setError(editText.getHint().toString() + " is required!");
            return false;
        }
        return true;
    }

    protected String getTagName() {
        return AddressAddNewActivity.class.getSimpleName();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);
        ((LinearLayout) findViewById(R.id.linlay_add_address)).setVisibility(View.VISIBLE);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                AddressDetailsLocationData locationData = (AddressDetailsLocationData) extras.getSerializable("locationData");
                if (locationData != null) {
                    mNewAddress = locationData;
                    setTextLocationFetch(locationData);
                }
            } else {
                finish();
            }
        }
    }

    private void setTextLocationFetch(AddressDetailsLocationData locationData) {
        if (!TextUtils.isEmpty(locationData.getLandmark())) {
            address.setText(locationData.getLine1() + ", " + locationData.getLine2() + ", " + locationData.getLandmark() + ", " +
                    locationData.getCity() + ", " + locationData.getState());
        } else {
            address.setText(locationData.getLine1() + ", " + locationData.getLine2() + ", " +
                    locationData.getCity() + ", " + locationData.getState());
        }
        line1.setText(locationData.getLine1());
        line2.setText(locationData.getLine2());
        if (!TextUtils.isEmpty(locationData.getLandmark())) {
            landmark.setText(locationData.getLandmark());
        } else {
            landmark.setText("");
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        func();
    }

    private void func() {
        AddressDetailsLocationData selectedLocationOnNewIntent = SharedPreferenceSingleton.getInstance().getDeliveryLocation();

        if (selectedLocationOnNewIntent != null && selectedLocationOnNewIntent.getTag() != null) {
            SharedPreferenceSingleton preference = SharedPreferenceSingleton.getInstance();
            preference.saveCurrentUsedLocation(selectedLocationOnNewIntent);
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        if (!selectedLocationOnNewIntent.equals(mNewAddress)) {
            mNewAddress = selectedLocationOnNewIntent;


            if (mNewAddress != null) {
                setTextLocationFetch(mNewAddress);
                homeTag.setSelected(false);
                workTag.setSelected(false);
                otherTag.setSelected(false);
                if (mNewAddress.getTag() != null) {
                    this.setTitle("Confirm Address");
                    tvAddressDetected.setText("Saved Address");
                    switch (mNewAddress.getTag()) {
                        case AddressDetailsLocationData.TAG_HOME:
                            homeTag.setSelected(true);
                            break;
                        case AddressDetailsLocationData.TAG_WORK:
                            workTag.setSelected(true);
                            break;
                        default:
                            otherTag.setSelected(true);
                            break;
                    }
                } else {
                    tvAddressDetected.setText("Auto Detected Address");
                    this.setTitle("Edit Address");

                }
                currentPosition = new LatLng(Double.parseDouble(mNewAddress.getCoords().getLat()), Double.parseDouble(mNewAddress.getCoords().getLon()));
                setMarkerOnMap(currentPosition, "Current location", true);
            }

        }
    }
}
