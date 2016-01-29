package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.LocationDetails.LocationsVerified;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/20/2016.
 */
public class SimpleArrayAdapter extends ArrayAdapter<AddressDetailsLocationData> {
    private Context mContext;
    private List<AddressDetailsLocationData> addressList;
    private List<LocationsVerified> locationsVerifiedList;

    public SimpleArrayAdapter(Context context, List<AddressDetailsLocationData> addressList, List<LocationsVerified> locationsVerifiedList) {
        super(context, R.layout.list_choose_location_saved_row, addressList);
        mContext = context;
        this.addressList = addressList;
        this.locationsVerifiedList = locationsVerifiedList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AddressHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.saved_address_row, parent, false);
            holder = new AddressHolder(row);
            row.setTag(holder);
        } else {
            holder = (AddressHolder) row.getTag();
        }

        holder.getRadioButton().setImageDrawable(mContext.getResources().getDrawable(R.drawable.radio_check_config));

        holder.populateFrom(addressList.get(position));

        if (locationsVerifiedList != null && !locationsVerifiedList.get(position).isDeliverable()) {
            row.setAlpha(.5f);
            row.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        return row;
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
