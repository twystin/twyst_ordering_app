package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.util.Util;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Recharge;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.NumberDatabaseSingleton;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.Utils;

import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 2/25/2016.
 */
public class RechargeActivity extends BaseActionActivity {
    private static final int PICK_CONTACT = 2;

    private Spinner mOperatorSpinner, mCircleSpinner;
    private EditText etNumber, etAmount;

    private static int FACTOR = HttpService.getInstance().getSharedPreferences().getInt(AppConstants.PREFERENCE_RECHARGE_HANDLING_FEE_FACTOR, AppConstants.RECHARGE_HANDLING_FEE_FACTOR);
    private static int MIN = HttpService.getInstance().getSharedPreferences().getInt(AppConstants.PREFERENCE_RECHARGE_MIN_HANDLING_FEE, AppConstants.RECHARGE_MIN_HANDLING_FEE);

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
        setupEditTextNumber();
        setupEditTextAmount();
        findViewById(R.id.bRecharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToRecharge();
            }
        });

        findViewById(R.id.ll_twyst_cash_launcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twystCashIntent = new Intent(RechargeActivity.this, TwystCashHistoryActivity.class);
                startActivity(twystCashIntent);
            }
        });
        updateTwystCash();
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

    private void updateTwystCash() {
        if (Utils.getTwystCash() != -1) {
            ((TextView) findViewById(R.id.tv_my_twyst_cash)).setText(String.valueOf(Utils.getTwystCash()));
        }
    }

    private void setupEditTextNumber() {
        etNumber = (EditText) findViewById(R.id.et_number);
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

        etNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.llNumber).setBackgroundResource(R.drawable.underline_selected);
                } else {
                    findViewById(R.id.llNumber).setBackgroundResource(R.drawable.underline_unselected);
                }
            }
        });
    }

    private void setupEditTextAmount() {
        etAmount = (EditText) findViewById(R.id.et_amount);
        etAmount.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) {
                            findViewById(R.id.llCashUsage).setVisibility(View.VISIBLE);
                            updateUsageTexts(s.toString());
                        } else {
                            findViewById(R.id.llCashUsage).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                }
        );
    }

    private void updateUsageTexts(String amount) {
        final TextView tvUsageHandling = (TextView) findViewById(R.id.tvUsageHandling);
        final TextView tvTotal = (TextView) findViewById(R.id.tvTotal);

        int handlingCost = getHandlingCost(amount);
        tvUsageHandling.setText(String.valueOf(handlingCost));
        tvTotal.setText(String.valueOf(Integer.parseInt(amount) + handlingCost));

        tvUsageHandling.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.twyst_cash_icon);
                        int height = tvUsageHandling.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, ((220 * height) / 311), height);
                        tvUsageHandling.setCompoundDrawables(null, null, img, null);
                        tvUsageHandling.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        tvTotal.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.twyst_cash_icon);
                        int height = tvTotal.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, ((220 * height) / 311), height);
                        tvTotal.setCompoundDrawables(null, null, img, null);
                        tvTotal.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
    }

    private int getHandlingCost(String amount) {
        return Math.max(FACTOR, (int) Math.round((double) (MIN) / 100 * Double.parseDouble(amount)));
    }

    private void proceedToRecharge() {
        etNumber.setError(null);
        etAmount.setError(null);
        TextView errorText = (TextView) mOperatorSpinner.getSelectedView();
        errorText.setError(null);
        errorText = (TextView) mCircleSpinner.getSelectedView();
        errorText.setError(null);

        if (TextUtils.isEmpty(etNumber.getText().toString())) {
            etNumber.setError("Please enter phone number!");
            return;
        }
        if (etNumber.getText().toString().length() < 10) {
            etNumber.setError("Please enter a valid phone number!");
            return;
        }

        int selectedOperator = ((OperatorMapping) mOperatorSpinner.getSelectedItem()).getOperatorID();
        if (selectedOperator == 0) {
            TextView errorText1 = (TextView) mOperatorSpinner.getSelectedView();
            errorText1.setError("Please select an operator");
            return;
        }
        int selectedCircle = ((CircleMapping) mCircleSpinner.getSelectedItem()).getCircleCode();
        if (selectedCircle == 0) {
            TextView errorText1 = (TextView) mCircleSpinner.getSelectedView();
            errorText1.setError("Please select a circle");
            return;
        }

        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            etAmount.setError("Please enter an amount!");
            return;
        }

        Recharge recharge = new Recharge(Long.parseLong(etNumber.getText().toString()), Integer.parseInt(etAmount.getText().toString()), selectedOperator, selectedCircle);
        RadioButton rbPrepaid = (RadioButton) findViewById(R.id.rb_prepaid);
        RadioButton rbPostpaid = (RadioButton) findViewById(R.id.rb_postpaid);

        if (rbPrepaid.isChecked()) {
            recharge.setConntype(null);
        }
        if (rbPostpaid.isChecked()) {
            recharge.setConntype(Recharge.POSTPAID);
        }

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        HttpService.getInstance().postRecharge(getUserToken(), recharge, new Callback<BaseResponse<Integer>>() {
            @Override
            public void success(BaseResponse<Integer> loginDataBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (loginDataBaseResponse.isResponse()) {
                    if (loginDataBaseResponse.getData() != null) {
                        int twystCash = loginDataBaseResponse.getData();
                        Utils.setTwystCash(twystCash);
                        updateTwystCash();
                    }
                    Toast.makeText(RechargeActivity.this, "Recharge done successfully!", Toast.LENGTH_SHORT).show();
                    finish();
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
        mOperatorSpinner.setAdapter(new RechargeSpinnerAdapter(this, R.layout.custom_spinner, OperatorMapping.values()));
        mOperatorSpinner.setPrompt(OperatorMapping.DEFAULT.toString()); // will work only if spinnerMode is dialog

        mCircleSpinner = (Spinner) findViewById(R.id.spinnerCircleList);
        mCircleSpinner.setAdapter(new RechargeSpinnerAdapter(this, R.layout.custom_spinner, CircleMapping.values()));
        mCircleSpinner.setPrompt(CircleMapping.DEFAULT.toString());
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

    public class RechargeSpinnerAdapter<T> extends ArrayAdapter<T> {
        final private Context mContext;
        final private T[] mValues;
        final private LayoutInflater mInflater;

        public RechargeSpinnerAdapter(Context context, int layoutResourceId, T[] values) {
            super(context, layoutResourceId, values);
            this.mContext = context;
            this.mValues = values;
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = mInflater.inflate(R.layout.custom_spinner, parent,
                    false);
            Object object = mValues[position];
            TextView buttonShowDropDown = (TextView) row.findViewById(R.id.buttonShowDropDown);
            buttonShowDropDown.setText(object.toString());
            if (position == 0) {
                buttonShowDropDown.setTextColor(mContext.getResources().getColor(R.color.verify_half_white));
            }
            return row;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View row = mInflater.inflate(R.layout.custom_spinner_dropdown, parent,
                    false);
            super.getDropDownView(position, row, parent);
            Object object = mValues[position];
            TextView buttonShowDropDown = (TextView) row.findViewById(R.id.buttonShowDropDown);
            buttonShowDropDown.setText(object.toString());

            if (position == 0) {
                buttonShowDropDown.setHeight(0);
            }
            return row;
        }

    }

}
