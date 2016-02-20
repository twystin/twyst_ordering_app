package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.VoucherDetailsAdapter;

import java.util.ArrayList;

public class VoucherDetailsActivity extends BaseActionActivity {
    ArrayList<MyData> myDataList = new ArrayList<MyData>();
    MyData data1 = new MyData("250", "75");
    MyData data2 = new MyData("200", "175");
    MyData data3 = new MyData("300", "200");
    MyData data4 = new MyData("550", "125");
    MyData data5 = new MyData("250", "135");
    MyData data6 = new MyData("250", "135");
    MyData data7 = new MyData("250", "135");
    MyData data8 = new MyData("250", "135");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_details);

        setupToolBar();

        myDataList.add(data1);
        myDataList.add(data2);
        myDataList.add(data3);
        myDataList.add(data4);
        myDataList.add(data5);
        myDataList.add(data6);
        myDataList.add(data7);
        myDataList.add(data8);
        showVouchers(myDataList);
    }

    private void showVouchers(ArrayList<MyData> list) {
        RecyclerView vouchersRV = (RecyclerView) findViewById(R.id.rv_voucher_details);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(VoucherDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        vouchersRV.setLayoutManager(mLayoutManager);

        VoucherDetailsAdapter mVoucherDetailsAdapter = new VoucherDetailsAdapter(VoucherDetailsActivity.this, list);
        vouchersRV.setAdapter(mVoucherDetailsAdapter);
    }


    public class MyData {
        private String amount1;
        private String amount2;

        public MyData(String amount1, String amount2) {
            this.amount1 = amount1;
            this.amount2 = amount2;
        }

        public String getAmount1() {
            return amount1;
        }

        public void setAmount1(String amount1) {
            this.amount1 = amount1;
        }

        public String getAmount2() {
            return amount2;
        }

        public void setAmount2(String amount2) {
            this.amount2 = amount2;
        }
    }

}
