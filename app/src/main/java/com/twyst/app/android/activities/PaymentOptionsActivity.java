package com.twyst.app.android.activities;

import android.content.Intent;
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
import android.widget.TextView;

import com.mobikwik.sdk.MobikwikSDK;
import com.mobikwik.sdk.lib.MKTransactionResponse;
import com.mobikwik.sdk.lib.Transaction;
import com.mobikwik.sdk.lib.TransactionConfiguration;
import com.mobikwik.sdk.lib.User;
import com.twyst.app.android.R;
import com.twyst.app.android.model.PaymentData;
import com.twyst.app.android.util.AppConstants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class PaymentOptionsActivity extends AppCompatActivity {

    private List<PaymentData> mPaymentDataList = new ArrayList<PaymentData>();
    private static final int PAYMENT_REQ_CODE = 0;

    private String mOrderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupToolBar();

        Intent intent = getIntent();
        mOrderNumber = intent.getStringExtra(AppConstants.INTENT_ORDER_NUMBER);

        PaymentData pd1 = new PaymentData();
        PaymentData pd2 = new PaymentData();
        pd1.setPaymentMode("Online Payment");
        pd1.setCashBackPercent("15%");
        pd2.setPaymentMode("Cash On Delivery");
        pd2.setCashBackPercent("5%");
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

        User usr = new User("vipul.sharma2008@gmail.com", "9891240762");
        Transaction newTransaction = Transaction.Factory.newTransaction(usr, mOrderNumber, String.valueOf("1"));

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
                Log.d("PaumentOptionsActivity", response.statusMessage);
                Log.d("PaumentOptionsActivity", response.statusCode);
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

            pdholder.checkedbox.setImageResource(R.drawable.radio_check_config);
            pdholder.checkedbox.setSelected(selectedPosition == position);

            pdholder.paymentmode.setText(mPaymentDataList.get(position).getPaymentMode());
            pdholder.cashbackamount.setText(mPaymentDataList.get(position).getCashBackPercent());

            return row;
        }
    }

    static class PaymentDataHolder {
        private TextView paymentmode;
        private TextView cashbackamount;
        private ImageView checkedbox;

        PaymentDataHolder(View view) {
            paymentmode = (TextView) view.findViewById(R.id.tv_payment_option_name);
            cashbackamount = (TextView) view.findViewById(R.id.tv_cashback_percent);
            checkedbox = (ImageView) view.findViewById(R.id.iv_rb_payment_select);
        }
    }
}


