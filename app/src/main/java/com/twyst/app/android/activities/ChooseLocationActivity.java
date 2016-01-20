package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.SimpleArrayAdapter;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.SharedPreferenceAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/18/2016.
 */
public class ChooseLocationActivity extends AppCompatActivity {

    private List<AddressDetailsLocationData> addressList = new ArrayList<AddressDetailsLocationData>();
    private SimpleArrayAdapter adapter = null;
    private ListView listViewSavedLocations;
    private boolean isSaveLocationClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        LinearLayout linLayCurrentLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_current);
        LinearLayout linlaySavedLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_saved);
        LinearLayout linlayAdNewLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_add_new);

        linLayCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLocationActivity.this, MainActivity.class);
                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_CURRENT);
                startActivity(intent);
            }
        });

        listViewSavedLocations = (ListView) findViewById(R.id.lv_saved_locations);
        listViewSavedLocations.setAdapter(adapter);

        SharedPreferenceAddress preference = new SharedPreferenceAddress();
        addressList = preference.getAddresses(ChooseLocationActivity.this);
        if (addressList != null && addressList.size() > 0) {
            linlaySavedLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSaveLocationClicked) {
                        isSaveLocationClicked = true;
                        adapter = new SimpleArrayAdapter(ChooseLocationActivity.this, addressList);
                        listViewSavedLocations = (ListView) findViewById(R.id.lv_saved_locations);
                        listViewSavedLocations.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        listViewSavedLocations.setAdapter(adapter);
                        listViewSavedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                AddressDetailsLocationData chosenLocation = addressList.get(position);
                                SharedPreferenceAddress sharedPreferenceAddress = new SharedPreferenceAddress();
                                sharedPreferenceAddress.saveCurrentUsedLocation(ChooseLocationActivity.this, chosenLocation);
                                sharedPreferenceAddress.saveLastUsedLocation(ChooseLocationActivity.this, chosenLocation);
                                Intent intent = new Intent(ChooseLocationActivity.this, MainActivity.class);
                                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_SAVED);
                                startActivity(intent);
                            }
                        });
                        listViewSavedLocations.setVisibility(View.VISIBLE);
                    } else {
                        isSaveLocationClicked = false;
                        listViewSavedLocations.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            linlaySavedLocation.setEnabled(false);
            linlaySavedLocation.setClickable(false);
        }

        linlayAdNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLocationActivity.this, AddressMapActivity.class);
                intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
                startActivity(intent);
            }
        });
    }
}
