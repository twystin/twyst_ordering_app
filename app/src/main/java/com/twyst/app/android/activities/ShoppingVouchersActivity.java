package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.ShoppingVouchersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Cashback;
import com.twyst.app.android.service.HttpService;

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
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                hideSnackbar(); // why?
                handleRetrofitError(error);
            }
        });
    }
}
