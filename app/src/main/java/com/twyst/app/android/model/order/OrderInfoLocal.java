package com.twyst.app.android.model.order;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.twyst.app.android.activities.OrderTrackingActivity;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.util.AppConstants;

import java.io.Serializable;

/**
 * Created by Vipul Sharma on 2/9/2016.
 */
public class OrderInfoLocal implements Serializable {
    private static final String ORDER_ID_SUFFIX = "local";

    public OrderInfoLocal(String orderNumber, OrderSummary orderSummary, int freeItemIndex) {
        this.orderNumber = orderNumber;
        this.orderSummary = orderSummary;
        this.freeItemIndex = freeItemIndex;
    }

    private String orderNumber;
    private OrderSummary orderSummary;
    private int freeItemIndex;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }

    public int getFreeItemIndex() {
        return freeItemIndex;
    }

    public void setFreeItemIndex(int freeItemIndex) {
        this.freeItemIndex = freeItemIndex;
    }

    public static void saveLocalList(String orderID, OrderInfoLocal orderInfoLocal, Activity activity) {
        Gson gson = new Gson();
        String json = gson.toJson(orderInfoLocal);

        SharedPreferences.Editor sharedPreferences = activity.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(orderID + ORDER_ID_SUFFIX, json);
        sharedPreferences.apply();
    }

    public static OrderInfoLocal getLocalList(String orderID, Activity activity) {
        String orderInfoLocalString = activity.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(orderID + ORDER_ID_SUFFIX, "");

        if (TextUtils.isEmpty(orderInfoLocalString)) {
            return null;
        } else {
            Gson gson = new Gson();
            OrderInfoLocal orderInfoLocal = gson.fromJson(orderInfoLocalString, OrderInfoLocal.class);
            return orderInfoLocal;
        }
    }
}
