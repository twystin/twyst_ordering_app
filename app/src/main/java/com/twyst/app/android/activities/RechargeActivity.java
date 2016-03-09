package com.twyst.app.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.Recharge;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.NumberDatabaseSingleton;
import com.twyst.app.android.util.TwystProgressHUD;

import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 2/25/2016.
 */
public class RechargeActivity extends BaseActionActivity {
    private static final int PICK_CONTACT = 2;

    Spinner mOperatorSpinner, mCircleSpinner;

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
        findViewById(R.id.bRecharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToRecharge();
            }
        });

        EditText etNumber = (EditText) findViewById(R.id.et_number);
        etNumber.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String operator = "";
                        String circle = "";
                        if (s.length() >= 4) {
                            String[] string = NumberDatabaseSingleton.getInstance().getNumberDatabase().
                                    getFilteredNumberMapping(s.toString());
                            operator = string[0];
                            circle = string[1];
                        }
                        mOperatorSpinner.setSelection(getOperatorIndex(operator));
                        mCircleSpinner.setSelection(getCircleIndex(circle));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                }

        );
    }

    private void proceedToRecharge() {
        EditText etNumber = (EditText) findViewById(R.id.et_number);
        if (TextUtils.isEmpty(etNumber.getText().toString())) {
            etNumber.setError("Please enter phone number!");
        } else {
            int selectedOperator = ((OperatorMapping) mOperatorSpinner.getSelectedItem()).getOperatorID();
            int selectedCircle = ((CircleMapping) mCircleSpinner.getSelectedItem()).getCircleCode();

                    Recharge recharge = new Recharge(Long.parseLong(etNumber.getText().toString()), 10, selectedOperator, selectedCircle);
            final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
            HttpService.getInstance().postRecharge(getUserToken(), recharge, new Callback<BaseResponse>() {
                @Override
                public void success(BaseResponse loginDataBaseResponse, Response response) {
                    twystProgressHUD.dismiss();
                    if (loginDataBaseResponse.isResponse()) {
                        Toast.makeText(RechargeActivity.this, "Recharge done successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RechargeActivity.this, loginDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    twystProgressHUD.dismiss();
                    handleRetrofitError(error);
                }
            });
        }
    }

    private int getCircleIndex(String circle) {
        int index = 0;
        if (!TextUtils.isEmpty(circle)) {
            for (int i = 0; i < mCircleSpinner.getCount(); i++) {
                if (((CircleMapping) mCircleSpinner.getItemAtPosition(i)).getSqlTableCircleName().equals(circle)) {
                    index = i;
                }
            }
        }
        return index;
    }

    private int getOperatorIndex(String operator) {
        int index = 0;
        if (!TextUtils.isEmpty(operator)) {
            for (int i = 0; i < mOperatorSpinner.getCount(); i++) {
                if (Arrays.asList(((OperatorMapping) mOperatorSpinner.getItemAtPosition(i)).getSqlTableOperatorNames()).contains(operator)) {
                    index = i;
                }
            }
        }
        return index;
    }

    private void setupSpinners() {
        mOperatorSpinner = (Spinner) findViewById(R.id.spinnerOperatorList);
        mOperatorSpinner.setAdapter(new ArrayAdapter<OperatorMapping>(this, R.layout.custom_spinner, OperatorMapping.values()));

        mCircleSpinner = (Spinner) findViewById(R.id.spinnerCircleList);
        mCircleSpinner.setAdapter(new ArrayAdapter<CircleMapping>(this, R.layout.custom_spinner, CircleMapping.values()));

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
            tabTextView.setText("₹ " + tab.getText());
            tab.setCustomView(relativeLayout);
            tab.select();
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) findViewById(R.id.tvAmount)).setText("₹ " + tab.getText());
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
        DEFAULT(0, "Select Operator", new String[]{""}),
        AIRTEL(1, "Airtel", new String[]{"AIRTEL"}),
        VODAFONE(2, "Vodafone", new String[]{"VODAFONE"}),
        BSNL(3, "BSNL", new String[]{"BSNL"}),
        RCDMA(4, "Reliance CDMA", new String[]{"RELIANCE CDMA"}),
        RGSM(5, "Reliance GSM", new String[]{"RELIANCE GSM"}),
        AIRCEL(6, "Aircel", new String[]{"AIRCEL"}),
        MTNL(7, "MTNL", new String[]{"MTNL DELHI", "MTNL MUMBAI"}),
        IDEA(8, "Idea", new String[]{"IDEA"}),
        TATA_INDICOM(9, "Tata Indicom", new String[]{"TATA INDICOM"}),
        LOOP_MOBILE(10, "Loop Mobile", new String[]{"LOOP MOBILE"}),
        TATA_DOCOMO(11, "Tata Docomo", new String[]{"TATA DOCOMO GSM"}),
        VIRGIN_CDMA(12, "Virgin CDMA", new String[]{""}),
        MTS(13, "MTS", new String[]{"MTS"}),
        VIRGIN_GSM(14, "Virgin GSM", new String[]{"VIRGIN GSM"}),
        STEL(15, "S Tel", new String[]{""}),
        UNINOR(16, "Uninor", new String[]{"TELENOR"});

        private String operatorName;
        private String[] sqlTableOperatorNames;
        private int operatorID;

        private OperatorMapping(int operatorID, String operatorName, String[] sqlTableOperatorNames) {
            this.operatorID = operatorID;
            this.operatorName = operatorName;
            this.sqlTableOperatorNames = sqlTableOperatorNames;
        }

        public String[] getSqlTableOperatorNames() {
            return sqlTableOperatorNames;
        }

        public void setSqlTableOperatorNames(String[] sqlTableOperatorNames) {
            this.sqlTableOperatorNames = sqlTableOperatorNames;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public int getOperatorID() {
            return operatorID;
        }

        public void setOperatorID(int operatorID) {
            this.operatorID = operatorID;
        }

        @Override
        public String toString() {
            return operatorName;
        }
    }

    private enum CircleMapping {
        DEFAULT(0, "Select Circle", ""),
        AP(1, "Andhra Pradesh", "ANDHRA PRADESH"),
        ASSAM(2, "Assam", "ASSAM"),
        BJ(3, "Bihar & Jharkhand", "BIHAR & JHARKHAND"),
        CHENNAI(4, "Chennai", "CHENNAI"),
        NCR(5, "Delhi & NCR", "DELHI & NCR"),
        GUJARAT(6, "Gujarat", "GUJARAT"),
        HARYANA(7, "Haryana", "HARYANA"),
        HP(8, "Himachal Pradesh", "HIMACHAL PRADESH"),
        JK(9, "Jammu & Kashmir", "JAMMU & KASHMIR"),
        KARNATAKA(10, "Karnataka", "KARNATAKA"),
        KERALA(11, "Kerala", "KERALA"),
        KOLKATA(12, "Kolkata", "KOLKATA"),
        MAHARASHTRA(13, "Maharashtra & Goa (except Mumbai)", "MAHARASHTRA & GOA"),
        MP(14, "MP & Chattisgarh", "MP & CHATTISGARH"),
        MUMBAI(15, "Mumbai", "MUMBAI"),
        NORTH_EAST(16, "North East", "NORTH EAST"),
        ORISSA(17, "Orissa", "ODISHA"),
        PUNJAB(18, "Punjab", "PUNJAB"),
        RAJASTHAN(19, "Rajasthan", "RAJASTHAN"),
        TAMILNADU(20, "Tamilnadu", "TAMIL NADU"),
        UP_E(21, "UP(EAST)", "UTTAR PRADESH (E)"),
        UP_W(22, "UP(WEST) & Uttarakhand", "UTTAR PRADESH (W) & UTTARAKHAND"),
        WB(23, "West Bengal", "WEST BENGAL & ANDAMAN NICOBAR"),
        ELSE(51, "All India (except Delhi/Mumbai)", "");

        private String circleName;
        private String sqlTableCircleName;
        private int circleCode;

        private CircleMapping(int circleCode, String circleName, String sqlTableCircleName) {
            this.circleCode = circleCode;
            this.circleName = circleName;
            this.sqlTableCircleName = sqlTableCircleName;
        }

        public String getSqlTableCircleName() {
            return sqlTableCircleName;
        }

        public void setSqlTableCircleName(String sqlTableCircleName) {
            this.sqlTableCircleName = sqlTableCircleName;
        }

        public String getCircleName() {
            return circleName;
        }

        public void setCircleName(String circleName) {
            this.circleName = circleName;
        }

        public int getCircleCode() {
            return circleCode;
        }

        public void setCircleCode(int circleCode) {
            this.circleCode = circleCode;
        }

        @Override
        public String toString() {
            return circleName;
        }
    }
}
