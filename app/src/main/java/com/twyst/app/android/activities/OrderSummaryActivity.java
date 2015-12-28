package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.SummaryAdapter;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class OrderSummaryActivity extends AppCompatActivity {
    private RecyclerView mSummaryRecyclerView;
    private SummaryAdapter mSummaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        Bundle extras = getIntent().getExtras();
        OrderSummary orderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);

        setupToolBar();
        setupSummaryRecyclerView();
    }

    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");
    }

    private void setupSummaryRecyclerView() {
        mSummaryRecyclerView = (RecyclerView) findViewById(R.id.summaryRecyclerView);
        mSummaryRecyclerView.setHasFixedSize(true);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSummaryRecyclerView.setLayoutManager(mLayoutManager);

        mSummaryAdapter = new SummaryAdapter();
        mSummaryRecyclerView.setAdapter(mSummaryAdapter);


    }
}
