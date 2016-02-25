package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.ShoppingVoucherListAdapter;
import com.twyst.app.android.model.Cashback;
import com.twyst.app.android.util.AppConstants;

public class ShoppingVoucherListActivity extends BaseActionActivity {

    private Cashback offerSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_voucher_list);

        setupToolBar();
        Bundle bundle = getIntent().getExtras();
        offerSource = (Cashback) bundle.getSerializable(AppConstants.BUNDLE_CASHBACK_OFFERS);

        showVouchers(offerSource);
    }

    private void showVouchers(Cashback cashback) {
        RecyclerView vouchersRV = (RecyclerView) findViewById(R.id.rv_voucher_details);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ShoppingVoucherListActivity.this, LinearLayoutManager.VERTICAL, false);
        vouchersRV.setLayoutManager(mLayoutManager);

        ShoppingVoucherListAdapter mShoppingVoucherListAdapter = new ShoppingVoucherListAdapter(ShoppingVoucherListActivity.this, cashback.getCashbackOffers());
        vouchersRV.setAdapter(mShoppingVoucherListAdapter);
    }
}
