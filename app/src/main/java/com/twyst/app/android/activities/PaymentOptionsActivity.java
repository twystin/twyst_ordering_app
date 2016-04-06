package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.appsflyer.AppsFlyerLib;
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

    private boolean isCODAvailable, isOnlineAvailable;
    private String mPaymentType;

    private OrderCheckOutResponse mOrderCheckoutResponse;
    private static final String PAYMENT_MODE_COD = "Cash On Delivery";
    private static final String PAYMENT_MODE_BANKING = "Net Banking";
    private static final String PAYMENT_MODE_MK_WALLET = "Mobikwik Wallet";
    private static final String PAYMENT_MODE_CREDIT_CARD = "Credit Card";
    private static final String PAYMENT_MODE_DEBIT_CARD = "Debit Card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupToolBar();

        Intent intent = getIntent();
        mOrderCheckoutResponse = (OrderCheckOutResponse) intent.getSerializableExtra(AppConstants.INTENT_ORDER_CHECKOUT_RESPONSE);
        isCODAvailable = intent.getBooleanExtra(AppConstants.INTENT_PAYMENT_OPTION_IS_COD, false);
        isOnlineAvailable = intent.getBooleanExtra(AppConstants.INTENT_PAYMENT_OPTION_IS_ONLINE, false);

        PaymentData pd1 = new PaymentData();
        PaymentData pd2 = new PaymentData();
        PaymentData pd3 = new PaymentData();
        PaymentData pd4 = new PaymentData();
        PaymentData pd5 = new PaymentData();

        // Three modes : COD, Mobikwik Wallet, Net Banking, Credit Card, Debit Card
        pd1.setPaymentMode(PAYMENT_MODE_COD);
        pd1.setCashBackPercent(mOrderCheckoutResponse.getCod_cashback_percent());
        pd1.setCashBackAmount(mOrderCheckoutResponse.getCod_cashback());
        pd2.setPaymentMode(PAYMENT_MODE_MK_WALLET);
        pd2.setPromoCode(mOrderCheckoutResponse.getMobikwikCashback());
        pd2.setCashBackPercent(mOrderCheckoutResponse.getInapp_cashback_percent());
        pd2.setCashBackAmount(mOrderCheckoutResponse.getInapp_cashback());
        pd3.setPaymentMode(PAYMENT_MODE_BANKING);
        pd3.setCashBackPercent(mOrderCheckoutResponse.getInapp_cashback_percent());
        pd3.setCashBackAmount(mOrderCheckoutResponse.getInapp_cashback());
        pd4.setPaymentMode(PAYMENT_MODE_CREDIT_CARD);
        pd4.setCashBackPercent(mOrderCheckoutResponse.getInapp_cashback_percent());
        pd4.setCashBackAmount(mOrderCheckoutResponse.getInapp_cashback());
        pd5.setPaymentMode(PAYMENT_MODE_DEBIT_CARD);
        pd5.setCashBackPercent(mOrderCheckoutResponse.getInapp_cashback_percent());
        pd5.setCashBackAmount(mOrderCheckoutResponse.getInapp_cashback());

        mPaymentDataList.add(pd2);
        mPaymentDataList.add(pd3);
        mPaymentDataList.add(pd4);
        mPaymentDataList.add(pd5);

        mPaymentDataList.add(pd1); //COD

        final PaymentArrayAdapter pdAdapter = new PaymentArrayAdapter();
        final View proceed = (View) findViewById(R.id.proceed_ok);
        proceed.setEnabled(false);
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
                switch (mPaymentDataList.get(pdAdapter.getSelectedPosition()).getPaymentMode()) {
                    case PAYMENT_MODE_COD:
                        //COD
                        cod();
                        break;
                    case PAYMENT_MODE_MK_WALLET:
                        //Mobikwik Wallet
                        goToPayment(PaymentInstrumentType.MK_WALLET);
                        break;
                    case PAYMENT_MODE_BANKING:
                        //Net Banking
                        goToPayment(PaymentInstrumentType.NETBANKING);
                        break;
                    case PAYMENT_MODE_CREDIT_CARD:
                        //Credit Card
                        goToPayment(PaymentInstrumentType.CREDIT_CARD);
                        break;
                    case PAYMENT_MODE_DEBIT_CARD:
                        //Debit Card
                        goToPayment(PaymentInstrumentType.DEBIT_CARD);
                        break;

                }
            }
        });
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

        if (TextUtils.isEmpty(emailID)) {
            Toast.makeText(PaymentOptionsActivity.this, "EmailID not found. Could not proceed", Toast.LENGTH_LONG).show();
            return;
        }

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
            if (getPaymentType().equals(PaymentInstrumentType.NETBANKING.getType()) ||
                    getPaymentType().equals(PaymentInstrumentType.CREDIT_CARD.getType()) ||
                    getPaymentType().equals(PaymentInstrumentType.DEBIT_CARD.getType())) {
                successCode = "1";
            } else if (getPaymentType().equals(PaymentInstrumentType.MK_WALLET.getType())) {
                successCode = SDKErrorCodes.SUCCESS.getErrorCode(); //"0"
            }

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

            if (!isCODAvailable && mPaymentDataList.get(position).getPaymentMode().equals(PAYMENT_MODE_COD)) {
//                row.setAlpha(.5f);
//                row.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return true;
//                    }
//                });
                row.setVisibility(View.GONE);
            }


            if (!isOnlineAvailable && !mPaymentDataList.get(position).getPaymentMode().equals(PAYMENT_MODE_COD)) {
                row.setVisibility(View.GONE);
            }

            pdholder.checkedbox.setSelected(selectedPosition == position);
            pdholder.paymentmode.setText(mPaymentDataList.get(position).getPaymentMode());

            if (mPaymentDataList.get(position).getPromoCode() != null) {
                pdholder.tvPromoCode.setText(mPaymentDataList.get(position).getPromoCode());
            }

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
                pdholder.cashBackAmount.setText(String.format("(%d Twyst Cash)", mPaymentDataList.get(position).getCashBackAmount()));
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
        private TextView tvPromoCode;

        PaymentDataHolder(View view) {
            paymentmode = (TextView) view.findViewById(R.id.tv_payment_option_name);
            cashBackPercent = (TextView) view.findViewById(R.id.tv_cashback_percent);
            cashBackAmount = (TextView) view.findViewById(R.id.tv_cashback_amount);
            checkedbox = (ImageView) view.findViewById(R.id.iv_rb_payment_select);
            cashbackIcon = (LinearLayout) view.findViewById(R.id.ll_cashback_info);
            tvPromoCode = (TextView) view.findViewById(R.id.tv_promo_code);
        }
    }
}


