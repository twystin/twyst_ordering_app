package com.twyst.app.android.activities;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);

        setupToolBar();
        setupOfferRecyclerView();

        findViewById(R.id.bApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AvailableOffersActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOfferRecyclerView() {
        mOfferRecyclerView = (RecyclerView) findViewById(R.id.offerRecyclerView);
        mOfferRecyclerView.setHasFixedSize(true);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOfferRecyclerView.setLayoutManager(mLayoutManager);

        final AvailableOffersAdapter availableOffersAdapter = new AvailableOffersAdapter(AvailableOffersActivity.this, mOrderSummary);
        mOfferRecyclerView.setAdapter(availableOffersAdapter);
        availableOffersAdapter.setOnViewHolderListener(new AvailableOffersAdapter.OnViewHolderListener() {
            @Override
            public void onItemClicked(int position) {
                availableOffersAdapter.setSelectedPosition(position);
                availableOffersAdapter.notifyDataSetChanged();
                findViewById(R.id.bApply).setBackground(getResources().getDrawable(R.drawable.button_red_default));
                findViewById(R.id.bApply).setClickable(true);
            }
        });
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
