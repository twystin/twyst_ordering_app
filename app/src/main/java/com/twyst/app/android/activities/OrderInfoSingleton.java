package com.twyst.app.android.activities;

import com.twyst.app.android.model.order.OrderSummary;

/**
 * Created by anshul on 2/8/2016.
 */
public class OrderInfoSingleton {
    private static OrderInfoSingleton instance = null;

    private OrderSummary orderSummary = null;


    private OrderInfoSingleton(){
    }

    public static OrderInfoSingleton getInstance(){
        if (instance == null){
            return instance = new OrderInfoSingleton();
        } else {
            return instance;
        }
    }

    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }
}
