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
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
                UtilMethods.goToSummary(AvailableOffersActivity.this, -1, mOrderSummary);
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
            final int freeItemIndex = mOrderSummary.getOfferOrderList().get(mAvailableOffersAdapter.getSelectedPosition()).getFreeItemIndex();
            mOrderSummary.setSelectedOfferID(mOrderSummary.getOfferOrderList().get(mAvailableOffersAdapter.getSelectedPosition()).getId());

            // Apply selected offer
            final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
            HttpService.getInstance().postOfferApply(getUserToken(), mOrderSummary, new Callback<BaseResponse<OrderSummary>>() {
                @Override
                public void success(BaseResponse<OrderSummary> orderSummaryBaseResponse, Response response) {
                    if (orderSummaryBaseResponse.isResponse()) {
                        OrderSummary returnOrderSummary = orderSummaryBaseResponse.getData();

                        returnOrderSummary.setmCartItemsList(mOrderSummary.getmCartItemsList());
                        returnOrderSummary.setOutletId(mOrderSummary.getOutletId());

                        UtilMethods.goToSummary(AvailableOffersActivity.this,freeItemIndex, returnOrderSummary);
                    } else {
                        Toast.makeText(AvailableOffersActivity.this, orderSummaryBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    twystProgressHUD.dismiss();
                    hideSnackbar();
                }

                @Override
                public void failure(RetrofitError error) {
                    twystProgressHUD.dismiss();
                    handleRetrofitError(error);
                    hideSnackbar();
                }
            });

        } else {
            Toast.makeText(AvailableOffersActivity.this, "Please select a offer!", Toast.LENGTH_SHORT).show();
        }
    }

//    private void goToSummary(int freeItemIndex, OrderSummary orderSummary) {
//        Bundle orderSummaryData = new Bundle();
//        orderSummaryData.putSerializable(AppConstants.INTENT_ORDER_SUMMARY, orderSummary);
//        orderSummaryData.putInt(AppConstants.INTENT_FREE_ITEM_INDEX, freeItemIndex);
//
//        Intent checkOutIntent = new Intent(AvailableOffersActivity.this, OrderSummaryActivity.class);
//        checkOutIntent.putExtras(orderSummaryData);
//        startActivity(checkOutIntent);
//    }

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
