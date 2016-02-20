package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.FoodVouchersAdapter;

import java.util.ArrayList;

public class FoodVouchersActivity extends BaseActionActivity {
    ArrayList<MyData> myDataList = new ArrayList<MyData>();
    MyData data1 = new MyData("La Pino'z Pizza", "75");
    MyData data2 = new MyData("Biryani Blues", "175");
    MyData data3 = new MyData("Beyond Breads", "200");
    MyData data4 = new MyData("Chaayos", "125");
    MyData data5 = new MyData("New York Slice", "135");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_vouchers);

        setupToolBar();

        myDataList.add(data1);
        myDataList.add(data2);
        myDataList.add(data3);
        myDataList.add(data4);
        myDataList.add(data5);
        showFoodVouchers(myDataList);
    }

    private void showFoodVouchers(ArrayList<MyData> myDataList) {
        RecyclerView foodVouchersRV = (RecyclerView) findViewById(R.id.rv_food_vouchers);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(FoodVouchersActivity.this, LinearLayoutManager.VERTICAL, false);
        foodVouchersRV.setLayoutManager(mLayoutManager);
        FoodVouchersAdapter mFoodVouchersAdapter = new FoodVouchersAdapter(FoodVouchersActivity.this, myDataList);
        foodVouchersRV.setAdapter(mFoodVouchersAdapter);
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
