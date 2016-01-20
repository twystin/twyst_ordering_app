package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.OrderHistoryAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OrderHistory;
import com.twyst.app.android.service.HttpService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrderHistoryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setupOrderHistoryLocally();
        fetchOrderHistory();

    }

    private void setupOrderHistoryLocally() {
        String historyString = getOrderHistoryString();
        Gson gson = new Gson();
        OrderHistory[] orderHistoriesArray = gson.fromJson(historyString, OrderHistory[].class);
        List<OrderHistory> orderHistoryList = Arrays.asList(orderHistoriesArray);


        RecyclerView myOrdersRecyclerView = (RecyclerView) findViewById(R.id.my_orders_recyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        myOrdersRecyclerView.setLayoutManager(mLayoutManager);
        myOrdersRecyclerView.setHasFixedSize(true);

        OrderHistoryAdapter mAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this,orderHistoryList);
        myOrdersRecyclerView.setAdapter(mAdapter);

    }

    private void fetchOrderHistory() {
        HttpService.getInstance().getOrderHistory(getUserToken(), new Callback<BaseResponse<ArrayList<OrderHistory>>>() {
            @Override
            public void success(BaseResponse<ArrayList<OrderHistory>> orderHistoryBaseResponse, Response response) {
                if (orderHistoryBaseResponse.isResponse()) {

                    ArrayList<OrderHistory> orderHistoryList = orderHistoryBaseResponse.getData();

                    RecyclerView myOrdersRecyclerView = (RecyclerView) findViewById(R.id.my_orders_recyclerView);

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
                    myOrdersRecyclerView.setLayoutManager(mLayoutManager);
                    myOrdersRecyclerView.setHasFixedSize(true);

                    OrderHistoryAdapter mAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this,orderHistoryList);
                    myOrdersRecyclerView.setAdapter(mAdapter);

                } else {
                    Toast.makeText(OrderHistoryActivity.this, orderHistoryBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                hideProgressHUDInLayout();
                hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                hideSnackbar();
                handleRetrofitError(error);
            }
        });

    }

    @Override
    protected String getTagName() {
        return OrderHistoryActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_history;
    }

    private ArrayList<OrderHistory> getData(){
        ArrayList<OrderHistory> orderHistory = new ArrayList<>();

        return orderHistory;
    }

    public String getOrderHistoryString() {
        String dataFromServer = "[\n" +
                "{\n" +
                "_id: \"5679087fb87d2a6f819805b2\",\n" +
                "item_name: \"Egg & Cheese\",\n" +
                "item_quantity: 1,\n" +
                "item_cost: \"121\",\n" +
                "option: {\n" +
                "option_value: \"11 inch\",\n" +
                "option_cost: 408,\n" +
                "_id: \"5679087fb87d2a6f819805bc\",\n" +
                "is_vegetarian: false,\n" +
                "addons: [\n" +
                "{\n" +
                "addon_title: \"Addons\",\n" +
                "_id: \"5679087fb87d2a6f819805c0\",\n" +
                "addon_set: [\n" +
                "{\n" +
                "addon_cost: 116,\n" +
                "addon_value: \"Extra Sea Food & Meat\",\n" +
                "_id: \"5679087fb87d2a6f819805c1\",\n" +
                "is_vegetarian: false,\n" +
                "is_available: true\n" +
                "},\n" +
                "{\n" +
                "addon_cost: 63,\n" +
                "addon_value: \"Extra Chicken Toppings\",\n" +
                "_id: \"5679087fb87d2a6f819805c2\",\n" +
                "is_vegetarian: false,\n" +
                "is_available: true\n" +
                "},\n" +
                "{\n" +
                "addon_cost: 42,\n" +
                "addon_value: \"Extra Vegetable Toppings\",\n" +
                "_id: \"5679087fb87d2a6f819805c3\",\n" +
                "is_vegetarian: true,\n" +
                "is_available: true\n" +
                "},\n" +
                "{\n" +
                "addon_cost: 63,\n" +
                "addon_value: \"Extra Cheese\",\n" +
                "_id: \"5679087fb87d2a6f819805c4\",\n" +
                "is_vegetarian: true,\n" +
                "is_available: true\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "],\n" +
                "sub_options: [\n" +
                "{\n" +
                "_id: \"5679087fb87d2a6f819805bd\",\n" +
                "sub_option_title: \"Crust\",\n" +
                "sub_option_set: [\n" +
                "{\n" +
                "_id: \"5679087fb87d2a6f819805be\",\n" +
                "sub_option_cost: 0,\n" +
                "sub_option_value: \"Original Crust\",\n" +
                "is_vegetarian: true,\n" +
                "is_available: true\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "],\n" +
                "option_is_addon: false\n" +
                "},\n" +
                "item_tags: [\n" +
                "\"starter\",\n" +
                "\"pizza\"\n" +
                "]\n" +
                "}\n" +
                "],\n" +
                "address: {\n" +
                "coords: { }\n" +
                "},\n" +
                "is_favourite: false,\n" +
                "order_cost: 740.6649,\n" +
                "cashback: 0,\n" +
                "order_date: \"2016-01-18T14:41:50.482Z\",\n" +
                "order_status: \"pending\"\n" +
                "},\n" +
                "{\n" +
                "outlet_name: \"Beyond Breads\",\n" +
                "items: [\n" +
                "{\n" +
                "_id: \"5680efc0e5b619416c628899\",\n" +
                "item_name: \"Choco Mocha Cake\",\n" +
                "item_quantity: 1,\n" +
                "item_cost: \"130\",\n" +
                "option: {\n" +
                "option_cost: 669,\n" +
                "option_value: \"One Kg\",\n" +
                "_id: \"5680f18ce5b619416c6288a9\",\n" +
                "is_vegetarian: true,\n" +
                "addons: [ ],\n" +
                "sub_options: [ ],\n" +
                "option_is_addon: false\n" +
                "},\n" +
                "item_tags: [\n" +
                "\"coffee cakes\"\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "_id: \"5680f18ce5b619416c6288a5\",\n" +
                "item_name: \"Coffee Crunch Cake\",\n" +
                "item_quantity: 1,\n" +
                "item_cost: \"130\",\n" +
                "option: {\n" +
                "option_cost: 645,\n" +
                "option_value: \"One Kg\",\n" +
                "_id: \"5680f18ce5b619416c6288a6\",\n" +
                "is_vegetarian: true,\n" +
                "addons: [ ],\n" +
                "sub_options: [ ],\n" +
                "option_is_addon: false\n" +
                "},\n" +
                "item_tags: [\n" +
                "\"coffee cakes\"\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "_id: \"5680f18ce5b619416c6288a1\",\n" +
                "item_name: \"Tiramisu Cake\",\n" +
                "item_quantity: 1,\n" +
                "item_cost: \"130\",\n" +
                "option: {\n" +
                "option_cost: 130,\n" +
                "option_value: \"Two Pieces\",\n" +
                "_id: \"5680f18ce5b619416c6288a4\",\n" +
                "is_vegetarian: true,\n" +
                "addons: [ ],\n" +
                "sub_options: [ ],\n" +
                "option_is_addon: false\n" +
                "},\n" +
                "item_tags: [\n" +
                "\"coffee cakes\"\n" +
                "]\n" +
                "}\n" +
                "],\n" +
                "address: {\n" +
                "coords: { }\n" +
                "},\n" +
                "is_favourite: false,\n" +
                "order_cost: null,\n" +
                "cashback: 0,\n" +
                "order_date: \"2016-01-20T05:50:19.543Z\",\n" +
                "order_status: \"pending\"\n" +
                "}\n" +
                "]";
        return dataFromServer;
    }

}


