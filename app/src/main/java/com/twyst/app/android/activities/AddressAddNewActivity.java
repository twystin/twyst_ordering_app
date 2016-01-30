package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.SharedPreferenceAddress;

/**
 * Created by anshul on 1/8/2016.
 */
public class AddressAddNewActivity extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 2;
    private ImageView homeTag;
    private ImageView workTag;
    private ImageView otherTag;
    private ImageView editNeighborhood;
    private ImageView editLandmark;
    private EditText address;
    private EditText neighborhood;
    private EditText landmark;

    private OrderSummary mOrderSummary;

    private AddressDetailsLocationData mNewAddress = new AddressDetailsLocationData();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add_new);

        Bundle extras = getIntent().getExtras();
        mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);

        homeTag = (ImageView) findViewById(R.id.add_address_home_tag);
        workTag = (ImageView) findViewById(R.id.add_address_work_tag);
        otherTag = (ImageView) findViewById(R.id.add_address_other_tag);
        homeTag.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.home_tag_config));
        homeTag.setSelected(true);
        workTag.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.work_tag_config));
        otherTag.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.other_tag_config));

        address = (EditText) findViewById(R.id.editView_address);
        neighborhood = (EditText) findViewById(R.id.editView_building);
        landmark = (EditText) findViewById(R.id.editView_landmark);
        editNeighborhood = (ImageView) findViewById(R.id.edit_image_neighborhood);
        editLandmark = (ImageView) findViewById(R.id.edit_image_landmark);

        editNeighborhood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                neighborhood.setEnabled(true);
//                neighborhood.setSelected(true);
                neighborhood.setCursorVisible(true);
                neighborhood.requestFocus();
            }
        });

        editLandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                landmark.setEnabled(true);
//                landmark.setSelected(true);
                landmark.setCursorVisible(true);
                landmark.requestFocus();
            }
        });

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


//        Boolean setUpMap = getIntent().getBooleanExtra(AppConstants.MAP_TO_BE_SHOWN, false);
//        if (setUpMap) {
//            Intent intent = new Intent(AddressAddNewActivity.this, AddressMapActivity.class);
//            startActivityForResult(intent, PLACE_PICKER_REQUEST);
//        } else {
//            mNewAddress = (AddressDetailsLocationData) getIntent().getSerializableExtra(AppConstants.DATA_TO_BE_SHOWN);
            mNewAddress = mOrderSummary.getAddressDetailsLocationData();
            setTextLocationFetch(mNewAddress);
            ((LinearLayout) findViewById(R.id.linlay_add_address)).setVisibility(View.VISIBLE);
//        }

        Button bProceed = (Button) findViewById(R.id.proceed_address_new);
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText neighborhood = (EditText) findViewById(R.id.editView_building);
                EditText name = (EditText) findViewById(R.id.editView_name);
                EditText address = (EditText) findViewById(R.id.editView_address);
                EditText landmark = (EditText) findViewById(R.id.editView_landmark);

                if (validateEditText(name) && validateEditText(neighborhood) && validateEditText(address)) {
                    mNewAddress.setNeighborhood(neighborhood.getText().toString());
                    mNewAddress.setName(name.getText().toString());
                    mNewAddress.setAddress(address.getText().toString());
                    mNewAddress.setLandmark(landmark.getText().toString());
                    if (homeTag.isSelected()) {
                        mNewAddress.setTag("Home");
                    } else if (workTag.isSelected()) {
                        mNewAddress.setTag("Work");
                    } else {
                        mNewAddress.setTag(((EditText) findViewById(R.id.editView_other_tag)).getText().toString());
                    }

                    SharedPreferenceAddress preference = new SharedPreferenceAddress();
                    preference.addAddress(AddressAddNewActivity.this, mNewAddress);
                    updateOrderSummaryAndCheckout(mNewAddress);
                } else {
                    validateEditText(neighborhood);
                    validateEditText(address);
                }
            }
        });

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
                Toast.makeText(AddressAddNewActivity.this, locationData.getAddress(), Toast.LENGTH_LONG);
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
        address.setText(locationData.getAddress());
        neighborhood.setText(locationData.getNeighborhood());
        landmark.setText(locationData.getLandmark());
        Toast.makeText(AddressAddNewActivity.this, locationData.getAddress(), Toast.LENGTH_LONG);
    }

}
