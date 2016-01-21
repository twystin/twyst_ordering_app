package com.twyst.app.android.activities;

import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.twyst.app.android.R;
import com.twyst.app.android.model.PaymentData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class PaymentOptionsActivity extends AppCompatActivity {

    private List<PaymentData> mPaymentDataList = new ArrayList<PaymentData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupToolBar();

        PaymentData pd1 = new PaymentData();
        PaymentData pd2 = new PaymentData();
        pd1.setPaymentMode("Online Payment");
        pd1.setCashBackPercent("15%");
        pd2.setPaymentMode("Cash On Delivery");
        pd2.setCashBackPercent("5%");
        mPaymentDataList.add(pd1);
        mPaymentDataList.add(pd2);

        final PaymentArrayAdapter pdAdapter = new PaymentArrayAdapter();
        final TextView proceed = (TextView) findViewById(R.id.tvProceed);
        ListView listView = (ListView) findViewById(R.id.lv_payment_options);
        listView.setAdapter(pdAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pdAdapter.setSelectedPosition(position);
                pdAdapter.notifyDataSetChanged();
                proceed.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        });

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
                row = inflater.inflate(R.layout.payment_option_row, parent,false);
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


