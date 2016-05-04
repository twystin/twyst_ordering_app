package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.FoodVouchersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.offer.FoodOffer;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FoodVouchersActivity extends BaseActionActivity {
    private ArrayList<FoodOffer> foodOffersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_vouchers);

        setupToolBar();
        fetchFoodOffers();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.sentEventTracking(this, AppConstants.EVENT_FOOD_OFFERS_VIEW);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    private void fetchFoodOffers() {
        HttpService.getInstance().getFoodOffers(UtilMethods.getUserToken(FoodVouchersActivity.this), new Callback<BaseResponse<ArrayList<FoodOffer>>>() {
            @Override
            public void success(final BaseResponse<ArrayList<FoodOffer>> foodOffersListBaseResponse, Response response) {
                if (foodOffersListBaseResponse.isResponse()) {
                    foodOffersList = foodOffersListBaseResponse.getData();
                    int i = 1;
                    if (foodOffersList != null && foodOffersList.size() > 0) {
                        showFoodVouchers(foodOffersList);
                    }
                    hideProgressHUDInLayout();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                UtilMethods.handleRetrofitError(FoodVouchersActivity.this, error);
                hideProgressHUDInLayout();
            }
        });
    }

    private void showFoodVouchers(ArrayList<FoodOffer> myDataList) {
        RecyclerView foodVouchersRV = (RecyclerView) findViewById(R.id.rv_food_vouchers);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(FoodVouchersActivity.this, LinearLayoutManager.VERTICAL, false);
        foodVouchersRV.setLayoutManager(mLayoutManager);
        FoodVouchersAdapter mFoodVouchersAdapter = new FoodVouchersAdapter(FoodVouchersActivity.this, myDataList);
        foodVouchersRV.setAdapter(mFoodVouchersAdapter);
    }


}
