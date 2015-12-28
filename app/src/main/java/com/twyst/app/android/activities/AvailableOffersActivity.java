package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.AvailableOffersAdapter;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by Vipul Sharma on 12/23/2015.
 */
public class AvailableOffersActivity extends BaseActivity {
    private RecyclerView mOfferRecyclerView;
    private OrderSummary mOrderSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);

        setupOfferRecyclerView();
    }

    private void setupOfferRecyclerView() {
        mOfferRecyclerView = (RecyclerView) findViewById(R.id.offerRecyclerView);
        mOfferRecyclerView.setHasFixedSize(true);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOfferRecyclerView.setLayoutManager(mLayoutManager);

        AvailableOffersAdapter availableOffersAdapter = new AvailableOffersAdapter(AvailableOffersActivity.this, mOrderSummary.getOfferOrderList());
        mOfferRecyclerView.setAdapter(availableOffersAdapter);
    }

    @Override
    protected String getTagName() {
        return null;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_available_offers;
    }
}
