package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.OrderHistoryAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OrderHistory;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrderHistoryActivity extends BaseActionActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView myOrdersRecyclerView;
    private OrderHistoryAdapter mOrderHistoryAdapter;
    private boolean mFetchingOrderHistory;
    TwystProgressHUD twystProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        setupToolBar();

        myOrdersRecyclerView = (RecyclerView) findViewById(R.id.my_orders_recyclerView);
        myOrdersRecyclerView.setHasFixedSize(true);
        myOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        setupSwipeRefresh();
        setupOrderHistoryAdapter();
        twystProgressHUD = TwystProgressHUD.show(this, false, null);
        fetchOrderHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOrderHistoryAdapter != null) {
            refreshOrderHistoryBackground();
        }
    }

    private void refreshOrderHistoryBackground() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (!mFetchingOrderHistory) {
            fetchOrderHistory();
        }
    }

    private void updateList(ArrayList<OrderHistory> orderHistoryList) {
        findViewById(R.id.no_data_reorder).setVisibility(View.GONE);
        if (orderHistoryList != null && orderHistoryList.size() > 0) {
            mOrderHistoryAdapter.setOrderHistoryList(orderHistoryList);
            mOrderHistoryAdapter.notifyDataSetChanged();
        } else {
            findViewById(R.id.no_data_reorder).setVisibility(View.VISIBLE);
            ImageView iv = (ImageView) findViewById(R.id.iv_no_data);
            iv.setImageResource(R.drawable.no_data_order);
            TextView tv = (TextView) findViewById(R.id.tv_no_data);
            tv.setText(getResources().getString(R.string.no_data_reorder));
        }
    }

    private void setupOrderHistoryAdapter() {
        mOrderHistoryAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this);
        myOrdersRecyclerView.setAdapter(mOrderHistoryAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.button_orange);
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshOrderHistoryBackground();
            }
        });
    }

    private void fetchOrderHistory() {
        mFetchingOrderHistory = true;
        mSwipeRefreshLayout.setEnabled(false);
        HttpService.getInstance().getOrderHistory(UtilMethods.getUserToken(OrderHistoryActivity.this), new Callback<BaseResponse<ArrayList<OrderHistory>>>() {
            @Override
            public void success(BaseResponse<ArrayList<OrderHistory>> orderHistoryBaseResponse, Response response) {
                if (orderHistoryBaseResponse.isResponse()) {
                    updateList(orderHistoryBaseResponse.getData());
                } else {
                    Toast.makeText(OrderHistoryActivity.this, orderHistoryBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                twystProgressHUD.dismiss();
                hideSnackbar();
                mFetchingOrderHistory = false;
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                hideSnackbar();
                handleRetrofitError(error);
                mFetchingOrderHistory = false;
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(true);
            }
        });
    }

}


