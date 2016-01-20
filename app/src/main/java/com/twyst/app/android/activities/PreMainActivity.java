package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.SharedPreferenceAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/18/2016.
 */
public class PreMainActivity extends AppCompatActivity {
    List<AddressDetailsLocationData> addressList = new ArrayList<AddressDetailsLocationData>();
    SimpleArrayAdapter adapter = null;
    private ListView listViewSavedLocations;
    private boolean isSaveLocationClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);

        LinearLayout linLayCurrentLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_current);
        LinearLayout linlaySavedLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_saved);
        LinearLayout linlayAdNewLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_add_new);

        linLayCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_CURRENT);
                startActivity(intent);
            }
        });

        listViewSavedLocations = (ListView) findViewById(R.id.lv_saved_locations);
        listViewSavedLocations.setAdapter(adapter);

        SharedPreferenceAddress preference = new SharedPreferenceAddress();
        addressList = preference.getAddresses(PreMainActivity.this);
        if (addressList != null && addressList.size() > 0) {

            linlaySavedLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSaveLocationClicked) {
                        isSaveLocationClicked = true;
                        adapter = new SimpleArrayAdapter();
                        listViewSavedLocations = (ListView) findViewById(R.id.lv_saved_locations);
                        listViewSavedLocations.setAdapter(adapter);
                        listViewSavedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                AddressDetailsLocationData chosenLocation = addressList.get(position);
                                SharedPreferenceAddress sharedPreferenceAddress = new SharedPreferenceAddress();
                                sharedPreferenceAddress.saveCurrentUsedLocation(PreMainActivity.this, chosenLocation);
                                sharedPreferenceAddress.saveLastUsedLocation(PreMainActivity.this, chosenLocation);
                                Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
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
                Intent intent = new Intent(PreMainActivity.this, AddressMapActivity.class);
                intent.putExtra("Choose activity directed to map", true);
                startActivity(intent);
            }
        });
    }

    class SimpleArrayAdapter extends ArrayAdapter<AddressDetailsLocationData> {

        SimpleArrayAdapter() {
            super(PreMainActivity.this, R.layout.list_choose_location_saved_row, addressList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            AddressHolder holder;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.saved_address_row, parent, false);
                holder = new AddressHolder(row);
                row.setTag(holder);
            } else {
                holder = (AddressHolder) row.getTag();
            }

            holder.getRadioButton().setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.radio_check_config));

            holder.populateFrom(addressList.get(position));
            return row;
        }
    }

    static class AddressHolder {
        private ImageView radioButton;
        private TextView name;
        private TextView address;
        private ImageView tagImage;
        private TextView tagName;

        AddressHolder(View row) {
            radioButton = (ImageView) row.findViewById(R.id.radio_saved_address);
            name = (TextView) row.findViewById(R.id.tv_name);
            address = (TextView) row.findViewById(R.id.tv_address);
            tagImage = (ImageView) row.findViewById(R.id.image_tag);
            tagName = (TextView) row.findViewById(R.id.tv_tag_name);
        }

        public void populateFrom(AddressDetailsLocationData addr) {

            name.setText(addr.getName());
            address.setText(addr.getAddress());

            switch (addr.getTag()) {
                case "home":
                    tagImage.setImageResource(R.drawable.address_home_enabled);
                    break;
                case "work":
                    tagImage.setImageResource(R.drawable.address_work_enabled);
                    break;
                default:
                    tagImage.setImageResource(R.drawable.address_location_enabled);
            }

            tagName.setText(addr.getTag());
        }

        public ImageView getRadioButton() {
            return radioButton;
        }

        public ImageView getDelete() {
            return tagImage;
        }
    }
}
