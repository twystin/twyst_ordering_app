package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.ShoppingVouchersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Cashback;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShoppingVouchersActivity extends BaseActionActivity {
    ArrayList<Cashback> voucherList = new ArrayList<>();
    ShoppingVouchersAdapter mShoppingVouchersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_vouchers);
        setupToolBar();
        getAllOffers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.sentEventTracking(this, AppConstants.EVENT_SHOPPING_VOUCHERS_VIEW);
    }


    private void getAllOffers() {
        HttpService.getInstance().getCashbackOffers(getUserToken(), new Callback<BaseResponse<ArrayList<Cashback>>>() {
            @Override
            public void success(BaseResponse<ArrayList<Cashback>> cashbackBaseResponse, Response response) {
                if (cashbackBaseResponse.isResponse()) {
                    RecyclerView shoppingVouchersRV = (RecyclerView) findViewById(R.id.rv_shopping_vouchers);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(ShoppingVouchersActivity.this, LinearLayoutManager.VERTICAL, false);
                    shoppingVouchersRV.setLayoutManager(mLayoutManager);
                    voucherList = cashbackBaseResponse.getData();
                    mShoppingVouchersAdapter = new ShoppingVouchersAdapter(ShoppingVouchersActivity.this, voucherList);
                    shoppingVouchersRV.setAdapter(mShoppingVouchersAdapter);
                } else {
                    Toast.makeText(ShoppingVouchersActivity.this, cashbackBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                hideSnackbar(); // why?
                hideProgressHUDInLayout();
                showNoDataLayout();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                hideSnackbar(); // why?
                handleRetrofitError(error);
            }
        });
    }

    private void showNoDataLayout() {
        findViewById(R.id.no_shopping_vouchers).setVisibility(View.GONE);
        if (voucherList.size() == 0) {
            findViewById(R.id.no_shopping_vouchers).setVisibility(View.VISIBLE);
            ImageView iv_no_data = (ImageView) findViewById(R.id.iv_no_data);
            iv_no_data.setImageResource(R.drawable.redeem_shopping);
            TextView tv_no_data = (TextView) findViewById(R.id.tv_no_data);
            tv_no_data.setText(getResources().getString(R.string.no_data_vouchers));
        }
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
}
