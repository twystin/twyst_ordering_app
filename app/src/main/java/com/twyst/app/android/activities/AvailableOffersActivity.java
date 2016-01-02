package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.AvailableOffersAdapter;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.AppConstants;

import java.util.List;

/**
 * Created by Vipul Sharma on 12/23/2015.
 */
public class AvailableOffersActivity extends BaseActivity {
    private RecyclerView mOfferRecyclerView;
    private OrderSummary mOrderSummary;
    AvailableOffersAdapter mAvailableOffersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);

        setupToolBar();
        setupOfferRecyclerView();

        findViewById(R.id.bSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSummary(-1);
            }
        });
    }

    private void setupOfferRecyclerView() {
        mOfferRecyclerView = (RecyclerView) findViewById(R.id.offerRecyclerView);
        mOfferRecyclerView.setHasFixedSize(true);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOfferRecyclerView.setLayoutManager(mLayoutManager);

        mAvailableOffersAdapter = new AvailableOffersAdapter(AvailableOffersActivity.this, mOrderSummary);
        mOfferRecyclerView.setAdapter(mAvailableOffersAdapter);
        mAvailableOffersAdapter.setOnViewHolderListener(new AvailableOffersAdapter.OnViewHolderListener() {
            @Override
            public void onItemClicked(int position) {
                mAvailableOffersAdapter.setSelectedPosition(position);
                mAvailableOffersAdapter.notifyDataSetChanged();
                findViewById(R.id.bApply).setBackground(getResources().getDrawable(R.drawable.button_red_default));
                findViewById(R.id.bApply).setClickable(true);
                findViewById(R.id.bApply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        applyOffer();
                    }
                });
            }
        });
    }


    private void applyOffer() {
        //if any offer is selected
        if (mAvailableOffersAdapter.getSelectedPosition() >= 0) {
            int freeItemIndex = mOrderSummary.getOfferOrderList().get(mAvailableOffersAdapter.getSelectedPosition()).getFreeItemIndex();
            mOrderSummary.setOfferUsedID(mOrderSummary.getOfferOrderList().get(mAvailableOffersAdapter.getSelectedPosition()).getId());
            goToSummary(freeItemIndex);

            // Apply selected offer
            
        } else {
            Toast.makeText(AvailableOffersActivity.this, "Please select a offer!", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToSummary(int freeItemIndex) {
        Bundle orderSummaryData = new Bundle();
        orderSummaryData.putSerializable(AppConstants.INTENT_ORDER_SUMMARY, mOrderSummary);
        orderSummaryData.putInt(AppConstants.INTENT_FREE_ITEM_INDEX, freeItemIndex);

        Intent checkOutIntent = new Intent(AvailableOffersActivity.this, OrderSummaryActivity.class);
        checkOutIntent.putExtras(orderSummaryData);
        startActivity(checkOutIntent);
    }

    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Offers Available");
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
