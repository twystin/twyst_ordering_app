package com.twyst.app.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 2/25/2016.
 */
public class RechargeActivity extends BaseActionActivity {
    private static final int PICK_CONTACT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        setupToolBar();

        findViewById(R.id.ibContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContacts();
            }
        });

        setupSpinners();
    }

    private void setupSpinners() {
        Spinner operatorSpinner = (Spinner) findViewById(R.id.spinnerOperatorList);
        operatorSpinner.setAdapter(new ArrayAdapter<OperatorMapping>(this, R.layout.custom_spinner, OperatorMapping.values()));
        String selectedOperator = ((OperatorMapping) operatorSpinner.getSelectedItem()).getOperatorID();

        Spinner circleSpinner = (Spinner) findViewById(R.id.spinnerCircleList);
        circleSpinner.setAdapter(new ArrayAdapter<CircleMapping>(this, R.layout.custom_spinner, CircleMapping.values()));
        String selectedCircle = ((CircleMapping) circleSpinner.getSelectedItem()).getCircleCode();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.slidingTabsAmount);
        tabLayout.addTab(tabLayout.newTab().setText("99"));
        tabLayout.addTab(tabLayout.newTab().setText("199"));
        tabLayout.addTab(tabLayout.newTab().setText("299"));
        tabLayout.addTab(tabLayout.newTab().setText("399"));
        tabLayout.addTab(tabLayout.newTab().setText("499"));
        tabLayout.addTab(tabLayout.newTab().setText("599"));
        tabLayout.addTab(tabLayout.newTab().setText("699"));

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(this).inflate(R.layout.amount_custom_tab_layout, tabLayout, false);

            TextView tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
            tabTextView.setText(tab.getText());
            tab.setCustomView(relativeLayout);
            tab.select();
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) findViewById(R.id.tvAmount)).setText(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void openContacts() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    // Get the URI that points to the selected contact
                    Uri contactUri = data.getData();
                    // We only need the NUMBER column, because there will be only one row in the result
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                    // Perform the query on the contact to get the NUMBER column
                    // We don't need a selection or sort order (there's only one result for the given URI)
                    // CAUTION: The query() method should be called from a separate thread to avoid blocking
                    // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                    // Consider using CursorLoader to perform the query.
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();

                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    updateContact(number);
                }
                break;
        }
    }

    private void updateContact(String number) {
        String formattedNumber = getFormattedNumber(number);
        if (formattedNumber.length() == 10) {
            EditText etNumber = (EditText) findViewById(R.id.et_number);
            etNumber.setText(formattedNumber);
        } else {
            Toast.makeText(RechargeActivity.this, "Could not read phone number!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFormattedNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            number = number.replaceAll("[^0-9]", "");
            if (number.length() == 10) {
                return number;
            } else if (number.length() == 11 && number.substring(0, 1).equals("0")) {
                return number.substring(1);
            } else if (number.length() == 12 && number.substring(0, 2).equals("91")) {
                return number.substring(2);
            }
        }
        return "";
    }

    private enum OperatorMapping {
        AIRTEL("1", "Airtel"),
        VODAFONE("2", "Vodafone"),
        BSNL("3", "BSNL"),
        RCDMA("4", "Reliance CDMA"),
        RGSM("5", "Reliance GSM"),
        AIRCEL("6", "Aircel"),
        MTNL("7", "MTNL"),
        IDEA("8", "Idea"),
        TATA_INDICOM("9", "Tata Indicom"),
        LOOP_MOBILE("10", "Loop Mobile"),
        TATA_DOCOMO("11", "Tata Docomo"),
        VIRGIN_CDMA("12", "Virgin CDMA"),
        MTS("13", "MTS"),
        VIRGIN_GSM("14", "Virgin GSM"),
        STEL("15", "S Tel"),
        UNINOR("16", "Uninor");

        private String operatorName;
        private String operatorID;

        private OperatorMapping(String operatorID, String operatorName) {
            this.operatorID = operatorID;
            this.operatorName = operatorName;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public String getOperatorID() {
            return operatorID;
        }

        public void setOperatorID(String operatorID) {
            this.operatorID = operatorID;
        }

        @Override
        public String toString() {
            return operatorName;
        }
    }

    private enum CircleMapping {
        AP("1", "Andhra Pradesh"),
        ASSAM("2", "Assam"),
        BJ("3", "Bihar & Jharkhand"),
        CHENNAI("4", "Chennai"),
        NCR("5", "Delhi & NCR"),
        GUJARAT("6", "Gujarat"),
        HARYANA("7", "Haryana"),
        HP("8", "Himachal Pradesh"),
        JK("9", "Jammu & Kashmir"),
        KARNATAKA("10", "Karnataka"),
        KERALA("11", "Kerala"),
        KOLKATA("12", "Kolkata"),
        MAHARASHTRA("13", "Maharashtra & Goa (except Mumbai)"),
        MP("14", "MP & Chattisgarh"),
        MUMBAI("15", "Mumbai"),
        NORTH_EAST("16", "North East"),
        ORISSA("17", "Orissa"),
        PUNJAB("18", "Punjab"),
        RAJASTHAN("19", "Rajasthan"),
        TAMILNADU("20", "Tamilnadu"),
        UP_E("21", "UP(EAST)"),
        UP_W("22", "UP(WEST) & Uttarakhand"),
        WB("23", "West Bengal"),
        ELSE("51", "All India (except Delhi/Mumbai)");

        private String circleName;
        private String circleCode;

        private CircleMapping(String circleCode, String circleName) {
            this.circleCode = circleCode;
            this.circleName = circleName;
        }

        public String getCircleName() {
            return circleName;
        }

        public void setCircleName(String circleName) {
            this.circleName = circleName;
        }

        public String getCircleCode() {
            return circleCode;
        }

        public void setCircleCode(String circleCode) {
            this.circleCode = circleCode;
        }

        @Override
        public String toString() {
            return circleName;
        }
    }
}
