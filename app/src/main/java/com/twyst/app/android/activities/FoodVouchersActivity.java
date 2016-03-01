package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.FoodVouchersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.offer.FoodOffer;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FoodVouchersActivity extends BaseActionActivity {
    private ArrayList<FoodOffer> foodOffersList;
    private TwystProgressHUD twystProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_vouchers);

        setupToolBar();
        showProgressBar();
        fetchFoodOffers();

    }

    private void fetchFoodOffers() {
        HttpService.getInstance().getFoodOffers(UtilMethods.getUserToken(FoodVouchersActivity.this), new Callback<BaseResponse<ArrayList<FoodOffer>>>() {
//        HttpService.getInstance().getFoodOffers("OG_4ixBTbJNNMNKnIoiCf_i5hn7XE8gF", new Callback<BaseResponse<ArrayList<FoodOffer>>>() {

            @Override
            public void success(final BaseResponse<ArrayList<FoodOffer>> foodOffersListBaseResponse, Response response) {

                if (foodOffersListBaseResponse.isResponse()) {
                    foodOffersList = foodOffersListBaseResponse.getData();
                    FoodOffer foodOffer1 = foodOffersList.get(0);
                    String[] timeArray = getTimeArray(OrderTrackingState.getFormattedDate(foodOffer1.getExpiryDate()));

                    int i = 1;
                    if (foodOffersList != null && foodOffersList.size() > 0) {
                        showFoodVouchers(foodOffersList);
                    }
                    hideProgressBar();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                UtilMethods.handleRetrofitError(FoodVouchersActivity.this, error);
                hideProgressBar();
            }
        });
    }

    private String[] getTimeArray(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
        String formattedDate = dateFormat.format(date).toString();
        return formattedDate.split("\\s+");
    }

    private void showProgressBar() {
        if (twystProgressHUD == null) {
            twystProgressHUD = TwystProgressHUD.show(FoodVouchersActivity.this, false, null);
        }
    }

    private void hideProgressBar() {
        if (twystProgressHUD != null) {
            twystProgressHUD.dismiss();
        }
    }


    private void showFoodVouchers(ArrayList<FoodOffer> myDataList) {
        RecyclerView foodVouchersRV = (RecyclerView) findViewById(R.id.rv_food_vouchers);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(FoodVouchersActivity.this, LinearLayoutManager.VERTICAL, false);
        foodVouchersRV.setLayoutManager(mLayoutManager);
        FoodVouchersAdapter mFoodVouchersAdapter = new FoodVouchersAdapter(FoodVouchersActivity.this, myDataList);
        foodVouchersRV.setAdapter(mFoodVouchersAdapter);
    }


}
