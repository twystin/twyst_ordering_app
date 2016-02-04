package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.ShoppingVouchersAdapter;

import java.util.ArrayList;

public class ShoppingVouchersActivity extends BaseActionActivity {
    ArrayList<Integer> voucherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_vouchers);

        setupToolBar();

        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        voucherList.add(R.drawable.ebay_logo);
        showShoppingVouchers();
    }

    private void showShoppingVouchers() {
        RecyclerView shoppingVouchersRV = (RecyclerView) findViewById(R.id.rv_shopping_vouchers);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ShoppingVouchersActivity.this, LinearLayoutManager.VERTICAL, false);
        shoppingVouchersRV.setLayoutManager(mLayoutManager);

        ShoppingVouchersAdapter mShoppingVouchersAdapter = new ShoppingVouchersAdapter(ShoppingVouchersActivity.this, voucherList);
        shoppingVouchersRV.setAdapter(mShoppingVouchersAdapter);
    }

}
