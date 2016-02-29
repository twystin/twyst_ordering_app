package com.twyst.app.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
        Spinner mySpinner = (Spinner) findViewById(R.id.spinnerOperatorList);
        mySpinner.setAdapter(new ArrayAdapter<OperatorMapping>(this, R.layout.custom_spinner, OperatorMapping.values()));
        String selected = ((OperatorMapping) mySpinner.getSelectedItem()).getOperatorID();
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
        AIRCEL("6", "Aircel");

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
        GUJARAT("6", "Gujarat");

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
