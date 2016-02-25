package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobikwik.sdk.MobikwikSDK;
import com.mobikwik.sdk.lib.MKTransactionResponse;
import com.mobikwik.sdk.lib.SDKErrorCodes;
import com.mobikwik.sdk.lib.Transaction;
import com.mobikwik.sdk.lib.TransactionConfiguration;
import com.mobikwik.sdk.lib.User;
import com.mobikwik.sdk.lib.payinstrument.PaymentInstrumentType;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.PaymentData;
import com.twyst.app.android.model.order.OrderCheckOutResponse;
import com.twyst.app.android.model.order.OrderConfirmedCOD;
import com.twyst.app.android.model.order.OrderInfoLocal;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentOptionsActivity extends BaseActionActivity {
    private List<PaymentData> mPaymentDataList = new ArrayList<PaymentData>();
    private static final int PAYMENT_REQ_CODE = 0;

    private String mPaymentType;

    private OrderCheckOutResponse mOrderCheckoutResponse;
    private static final String PAYMENT_MODE_COD = "Cash On Delivery";
    private static final String PAYMENT_MODE_BANKING = "Net Banking";
    private static final String PAYMENT_MODE_MK_WALLET = "Mobikwik Wallet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupToolBar();

        Intent intent = getIntent();
        mOrderCheckoutResponse = (OrderCheckOutResponse) intent.getSerializableExtra(AppConstants.INTENT_ORDER_CHECKOUT_RESPONSE);

        PaymentData pd1 = new PaymentData();
        PaymentData pd2 = new PaymentData();
        PaymentData pd3 = new PaymentData();

        // Three modes : COD, Net Banking, Mobikwik Wallet
        pd1.setPaymentMode(PAYMENT_MODE_COD);
        pd1.setCashBackPercent(mOrderCheckoutResponse.getCod_cashback_percent());
        pd1.setCashBackAmount(mOrderCheckoutResponse.getCod_cashback());
        pd2.setPaymentMode(PAYMENT_MODE_BANKING);
        pd2.setCashBackPercent(mOrderCheckoutResponse.getInapp_cashback_percent());
        pd2.setCashBackAmount(mOrderCheckoutResponse.getInapp_cashback());
        pd3.setPaymentMode(PAYMENT_MODE_MK_WALLET);
        pd3.setCashBackPercent(mOrderCheckoutResponse.getInapp_cashback_percent());
        pd3.setCashBackAmount(mOrderCheckoutResponse.getInapp_cashback());

        mPaymentDataList.add(pd1);
        mPaymentDataList.add(pd2);
        mPaymentDataList.add(pd3);

        final PaymentArrayAdapter pdAdapter = new PaymentArrayAdapter();
        final View proceed = (View) findViewById(R.id.bProceed);
        ListView listView = (ListView) findViewById(R.id.lv_payment_options);
        listView.setAdapter(pdAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pdAdapter.setSelectedPosition(position);
                pdAdapter.notifyDataSetChanged();
                proceed.setEnabled(true);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (pdAdapter.getSelectedPosition()) {
                    case 0:
                        //COD
                        cod();
                        break;
                    case 1:
                        //Net Banking
                        goToPayment(PaymentInstrumentType.NETBANKING);
                        break;
                    case 2:
                        //Mobikwik Wallet
                        goToPayment(PaymentInstrumentType.MK_WALLET);
                        break;

                }
            }
        });
    }

    private void cod() {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        OrderConfirmedCOD orderConfirmedCOD = new OrderConfirmedCOD(mOrderCheckoutResponse.getOrderNumber(), mOrderCheckoutResponse.getOutletID());
        HttpService.getInstance().postOrderConfirmCOD(UtilMethods.getUserToken(PaymentOptionsActivity.this), orderConfirmedCOD, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    gotoOrderTracking();
                } else {
                    Toast.makeText(PaymentOptionsActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(PaymentOptionsActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    private void gotoOrderTracking() {
        Toast.makeText(PaymentOptionsActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
        Intent orderTrackingIntent = new Intent(PaymentOptionsActivity.this, OrderTrackingActivity.class);

        OrderInfoLocal.saveLocalList(mOrderCheckoutResponse.getOrderID(),
                (OrderInfoLocal) getIntent().getSerializableExtra(AppConstants.INTENT_ORDER_INFO_LOCAL), PaymentOptionsActivity.this);
        orderTrackingIntent.putExtra(AppConstants.INTENT_ORDER_ID, mOrderCheckoutResponse.getOrderID());
        orderTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(orderTrackingIntent);
        finish();
    }

    public String getPaymentType() {
        return mPaymentType;
    }

    public void setPaymentType(String paymentType) {
        this.mPaymentType = paymentType;
    }

    private void goToPayment(PaymentInstrumentType paymentType) {
        setPaymentType(paymentType.getType());
        OrderInfoLocal.saveLocalList(mOrderCheckoutResponse.getOrderID(),
                (OrderInfoLocal) getIntent().getSerializableExtra(AppConstants.INTENT_ORDER_INFO_LOCAL), PaymentOptionsActivity.this);

        TransactionConfiguration config = new TransactionConfiguration();
        config.setDebitWallet(true);
        config.setPgResponseUrl(AppConstants.HOST + "/api/v4/zaakpay/response"); //You need to replace this string with the path of the page hosted on your server
        config.setChecksumUrl(AppConstants.HOST + "/api/v4/calculate/checksum"); //You need to replace this string with the path of the page hosted on your server
        config.setMerchantName("Twyst");
        config.setMbkId("MBK2136"); //Your MobiKwik Merchant Identifier
        config.setMode("1"); //Mode is 0 for test environment, 1 for Live

        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String number = sharedPreferences.getString(AppConstants.PREFERENCE_USER_PHONE, "");
        String emailID = sharedPreferences.getString(AppConstants.PREFERENCE_USER_EMAIL, "");

        User usr = new User(emailID, number);
//        Transaction newTransaction = Transaction.Factory.newTransaction(usr, mOrderCheckoutResponse.getOrderNumber(), String.valueOf("1"), paymentType);
        Transaction newTransaction = Transaction.Factory.newTransaction(usr, mOrderCheckoutResponse.getOrderNumber(), String.valueOf(mOrderCheckoutResponse.getActualAmountPaid()), paymentType);

        Intent mobikwikIntent = new Intent(this, MobikwikSDK.class);
        mobikwikIntent.putExtra(MobikwikSDK.EXTRA_TRANSACTION_CONFIG, config);
        mobikwikIntent.putExtra(MobikwikSDK.EXTRA_TRANSACTION, newTransaction);
        startActivityForResult(mobikwikIntent, PAYMENT_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQ_CODE) {
            String successCode = "";
            if (getPaymentType().equals(PaymentInstrumentType.NETBANKING.getType())) {
                successCode = "1";
            } else if (getPaymentType().equals(PaymentInstrumentType.MK_WALLET.getType())) {
                successCode = SDKErrorCodes.SUCCESS.getErrorCode(); //"0"
            }
//        Allowed Transaction are :
//        PaymentInstrumentType.CREDIT_CARD
//        PaymentInstrumentType.DEBIT_CARD
//        PaymentInstrumentType.NETBANKING
//        PaymentInstrumentType.MK_WALLET

            if (data != null) {
                MKTransactionResponse response = (MKTransactionResponse)
                        data.getSerializableExtra(MobikwikSDK.EXTRA_TRANSACTION_RESPONSE);
                if (response.statusCode.equals(successCode)) {
                    gotoOrderTracking();
                } else {
                    Toast.makeText(PaymentOptionsActivity.this, response.statusMessage, Toast.LENGTH_SHORT).show();
                }
                Log.d("PaymentOptionsActivity", response.statusMessage);
                Log.d("PaymentOptionsActivity", response.statusCode);
            }
        }
    }

    public class PaymentArrayAdapter extends ArrayAdapter<PaymentData> {
        int selectedPosition = -1;

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
        }

        PaymentArrayAdapter() {
            super(PaymentOptionsActivity.this, R.layout.payment_option_row, mPaymentDataList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            PaymentDataHolder pdholder;

            if (row == null) {
                // Since view is empty, we should inflate the view
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.payment_option_row, parent, false);
                pdholder = new PaymentDataHolder(row);
                row.setTag(pdholder);
            } else {
                pdholder = (PaymentDataHolder) row.getTag();
            }

//            if (position == 1 || position == 2) {//  Disable Net Banking, MK Wallet as of now
//                row.setAlpha(.5f);
//                row.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return true;
//                    }
//                });
//            }

            pdholder.checkedbox.setSelected(selectedPosition == position);
            pdholder.paymentmode.setText(mPaymentDataList.get(position).getPaymentMode());

            if (mPaymentDataList.get(position).getCashBackPercent() == 0) {
                pdholder.cashbackIcon.setVisibility(View.INVISIBLE);
            } else {
                pdholder.cashbackIcon.setVisibility(View.VISIBLE);
                double cashbackpercent = mPaymentDataList.get(position).getCashBackPercent();
                String cashbackpercent_str = "";
                // if the decimal value is too small, don't display it.
                if (cashbackpercent - (int) cashbackpercent < 0.001) {
                    cashbackpercent_str = String.format("%d%%", (int) cashbackpercent);
                } else {
                    cashbackpercent_str = String.format("%.1f%%", cashbackpercent);
                }
                pdholder.cashBackPercent.setText(cashbackpercent_str);
                pdholder.cashBackAmount.setText(String.format("(%d Twyst Bucks)", mPaymentDataList.get(position).getCashBackAmount()));
            }
            return row;
        }
    }

    static class PaymentDataHolder {
        private TextView paymentmode;
        private TextView cashBackPercent;
        private ImageView checkedbox;
        private LinearLayout cashbackIcon;
        private TextView cashBackAmount;

        PaymentDataHolder(View view) {
            paymentmode = (TextView) view.findViewById(R.id.tv_payment_option_name);
            cashBackPercent = (TextView) view.findViewById(R.id.tv_cashback_percent);
            cashBackAmount = (TextView) view.findViewById(R.id.tv_cashback_amount);
            checkedbox = (ImageView) view.findViewById(R.id.iv_rb_payment_select);
            cashbackIcon = (LinearLayout) view.findViewById(R.id.ll_cashback_info);
        }
    }
}


