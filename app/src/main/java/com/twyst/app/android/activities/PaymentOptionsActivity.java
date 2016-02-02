package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobikwik.sdk.MobikwikSDK;
import com.mobikwik.sdk.lib.MKTransactionResponse;
import com.mobikwik.sdk.lib.Transaction;
import com.mobikwik.sdk.lib.TransactionConfiguration;
import com.mobikwik.sdk.lib.User;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.PaymentData;
import com.twyst.app.android.model.order.OrderCheckOutResponse;
import com.twyst.app.android.model.order.OrderConfirmedCOD;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.SharedPreferenceAddress;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentOptionsActivity extends AppCompatActivity {
    private List<PaymentData> mPaymentDataList = new ArrayList<PaymentData>();
    private static final int PAYMENT_REQ_CODE = 0;

    private OrderCheckOutResponse mOrderCheckoutResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupToolBar();

        Intent intent = getIntent();
        mOrderCheckoutResponse = (OrderCheckOutResponse) intent.getSerializableExtra(AppConstants.INTENT_ORDER_CHECKOUT_RESPONSE);

        PaymentData pd1 = new PaymentData();
        PaymentData pd2 = new PaymentData();
        pd1.setPaymentMode("Online Payment");
        pd1.setCashBackPercent(15);
        pd2.setPaymentMode("Cash On Delivery");
        pd2.setCashBackPercent(5);
        mPaymentDataList.add(pd1);
        mPaymentDataList.add(pd2);

        final PaymentArrayAdapter pdAdapter = new PaymentArrayAdapter();
        final Button proceed = (Button) findViewById(R.id.bProceed);
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
                        //Online Payment
                        goToPayment();
                        break;
                    case 1:
                        //COD
                        cod();
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
                    Toast.makeText(PaymentOptionsActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    Intent orderTrackingIntent = new Intent(PaymentOptionsActivity.this, OrderTrackingActivity.class);
                    orderTrackingIntent.putExtra(AppConstants.INTENT_ORDER_ID, mOrderCheckoutResponse.getOrderID());
                    orderTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(orderTrackingIntent);
                    finish();
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

    private void goToPayment() {
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
        Transaction newTransaction = Transaction.Factory.newTransaction(usr, mOrderCheckoutResponse.getOrderNumber(), String.valueOf("1"));

        Intent mobikwikIntent = new Intent(this, MobikwikSDK.class);
        mobikwikIntent.putExtra(MobikwikSDK.EXTRA_TRANSACTION_CONFIG, config);
        mobikwikIntent.putExtra(MobikwikSDK.EXTRA_TRANSACTION, newTransaction);
        startActivityForResult(mobikwikIntent, PAYMENT_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQ_CODE) {
            if (data != null) {
                MKTransactionResponse response = (MKTransactionResponse)
                        data.getSerializableExtra(MobikwikSDK.EXTRA_TRANSACTION_RESPONSE);
                Toast.makeText(PaymentOptionsActivity.this, response.statusMessage, Toast.LENGTH_SHORT).show();
                Log.d("PaymentOptionsActivity", response.statusMessage);
                Log.d("PaymentOptionsActivity", response.statusCode);
            }
        }
    }

    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

            pdholder.checkedbox.setSelected(selectedPosition == position);

            pdholder.paymentmode.setText(mPaymentDataList.get(position).getPaymentMode());
            pdholder.cashbackamount.setText(String.format("%d%%", mPaymentDataList.get(position).getCashBackPercent()));
            if (mPaymentDataList.get(position).getCashBackPercent() == 0) {
                pdholder.cashbackIcon.setVisibility(View.INVISIBLE);
            }

            return row;
        }
    }

    static class PaymentDataHolder {
        private TextView paymentmode;
        private TextView cashbackamount;
        private ImageView checkedbox;
        private RelativeLayout cashbackIcon;

        PaymentDataHolder(View view) {
            paymentmode = (TextView) view.findViewById(R.id.tv_payment_option_name);
            cashbackamount = (TextView) view.findViewById(R.id.tv_cashback_percent);
            checkedbox = (ImageView) view.findViewById(R.id.iv_rb_payment_select);
            cashbackIcon = (RelativeLayout) view.findViewById(R.id.rl_cashback_icon);
        }
    }
}


