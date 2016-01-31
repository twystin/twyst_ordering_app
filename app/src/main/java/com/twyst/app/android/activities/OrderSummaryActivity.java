package com.twyst.app.android.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.mobikwik.sdk.MobikwikSDK;
import com.mobikwik.sdk.lib.MKTransactionResponse;
import com.mobikwik.sdk.lib.Transaction;
import com.mobikwik.sdk.lib.TransactionConfiguration;
import com.mobikwik.sdk.lib.User;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.SummaryAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.LocationDetails.LocationsVerified;
import com.twyst.app.android.model.order.OrderCheckOut;
import com.twyst.app.android.model.order.OrderCheckOutResponse;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class OrderSummaryActivity extends AppCompatActivity {
    private RecyclerView mSummaryRecyclerView;
    private SummaryAdapter mSummaryAdapter;
    OrderSummary mOrderSummary;
    private int mFreeItemIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        Bundle extras = getIntent().getExtras();
        mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);
        mFreeItemIndex = extras.getInt(AppConstants.INTENT_FREE_ITEM_INDEX, -1);

        setupToolBar();
        setupSummaryRecyclerView();

        final TextView tvNext = (TextView) findViewById(R.id.tvNext);
        tvNext.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.checkout_arrow);
                        int height = tvNext.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        tvNext.setCompoundDrawables(null, null, img, null);
                        tvNext.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        findViewById(R.id.bNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentOptions();
            }
        });
    }

    private void showPaymentOptions() {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        OrderCheckOut orderCheckOut = new OrderCheckOut(mOrderSummary.getOrderNumber(), mOrderSummary.getOutletId(), mOrderSummary.getAddressDetailsLocationData());
        HttpService.getInstance().postOrderCheckOut(UtilMethods.getUserToken(OrderSummaryActivity.this), orderCheckOut, new Callback<BaseResponse<OrderCheckOutResponse>>() {
            @Override
            public void success(BaseResponse<OrderCheckOutResponse> baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    OrderCheckOutResponse orderCheckOutResponse = baseResponse.getData();
                    Intent paymentOptionsIntent = new Intent(OrderSummaryActivity.this, PaymentOptionsActivity.class);
                    paymentOptionsIntent.putExtra(AppConstants.INTENT_ORDER_CHECKOUT_RESPONSE, orderCheckOutResponse);
                    startActivity(paymentOptionsIntent);
                } else {
                    Toast.makeText(OrderSummaryActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }


            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(OrderSummaryActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
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

        mSummaryAdapter = new SummaryAdapter(OrderSummaryActivity.this, mOrderSummary, mFreeItemIndex);
        mSummaryRecyclerView.setAdapter(mSummaryAdapter);


    }
}
