package com.twyst.app.android.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.AvailableOffersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 12/23/2015.
 */
public class AvailableOffersActivity extends BaseActionActivity {
    private ListView mOfferRecyclerView;
    private OrderSummary mOrderSummary;
    AvailableOffersAdapter mAvailableOffersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_offers);

        Bundle extras = getIntent().getExtras();
        mOrderSummary = (OrderSummary) extras.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);

        setupToolBar();
        setupOfferRecyclerView();
        findViewById(R.id.bApply).setEnabled(false);
        findViewById(R.id.bSkip).setEnabled(true);
        findViewById(R.id.bSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilMethods.goToSummary(AvailableOffersActivity.this, -1, mOrderSummary);
            }
        });
    }

    private void setupOfferRecyclerView() {
        mOfferRecyclerView = (ListView) findViewById(R.id.offerRecyclerView);

        mAvailableOffersAdapter = new AvailableOffersAdapter(AvailableOffersActivity.this, mOrderSummary);
        mOfferRecyclerView.setAdapter(mAvailableOffersAdapter);
        mAvailableOffersAdapter.setOnViewHolderListener(new AvailableOffersAdapter.OnViewHolderListener() {
            @Override
            public void onItemClicked(int position) {
                if (mAvailableOffersAdapter.getSelectedPosition() == position) {
                    //Already selected, deselect current
                    mAvailableOffersAdapter.setSelectedPosition(-1);

                    findViewById(R.id.bSkip).setEnabled(true);
                    findViewById(R.id.bSkip).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UtilMethods.goToSummary(AvailableOffersActivity.this, -1, mOrderSummary);
                        }
                    });
                    findViewById(R.id.bApply).setEnabled(false);
                } else {
                    //Select current
                    mAvailableOffersAdapter.setSelectedPosition(position);

                    findViewById(R.id.bApply).setEnabled(true);
                    findViewById(R.id.bApply).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyOffer();
                        }
                    });
                    findViewById(R.id.bSkip).setEnabled(false);
                }
                mAvailableOffersAdapter.notifyDataSetChanged();
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
            HttpService.getInstance().postOfferApply(UtilMethods.getUserToken(AvailableOffersActivity.this), mOrderSummary, new Callback<BaseResponse<OrderSummary>>() {
                @Override
                public void success(BaseResponse<OrderSummary> orderSummaryBaseResponse, Response response) {
                    if (orderSummaryBaseResponse.isResponse()) {
                        OrderSummary returnOrderSummary = orderSummaryBaseResponse.getData();

                        returnOrderSummary.setmCartItemsList(mOrderSummary.getmCartItemsList());
                        returnOrderSummary.setOutletId(mOrderSummary.getOutletId());
                        returnOrderSummary.setAddressDetailsLocationData(mOrderSummary.getAddressDetailsLocationData());

                        UtilMethods.goToSummary(AvailableOffersActivity.this, freeItemIndex, returnOrderSummary);
                    } else {
                        Toast.makeText(AvailableOffersActivity.this, orderSummaryBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    twystProgressHUD.dismiss();
                    UtilMethods.hideSnackbar();
                }

                @Override
                public void failure(RetrofitError error) {
                    twystProgressHUD.dismiss();
                    UtilMethods.handleRetrofitError(AvailableOffersActivity.this, error);
                    UtilMethods.hideSnackbar();
                }
            });

        } else {
            Toast.makeText(AvailableOffersActivity.this, "Please select a offer!", Toast.LENGTH_SHORT).show();
        }
    }
}
