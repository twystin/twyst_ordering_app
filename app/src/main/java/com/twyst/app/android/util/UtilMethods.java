package com.twyst.app.android.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.twyst.app.android.activities.AddressAddNewActivity;
import com.twyst.app.android.activities.AvailableOffersActivity;
import com.twyst.app.android.activities.OrderSummaryActivity;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 1/30/2016.
 */
public class UtilMethods {
    public static void checkOut(final boolean isLocationInputRequired,final AddressDetailsLocationData addressDetailsLocationData, final ArrayList<Items> mCartItemsList, String mOutletId, final Activity activity) {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(activity, false, null);
        final OrderSummary orderSummary = new OrderSummary(mCartItemsList, mOutletId, addressDetailsLocationData.getCoords());
        HttpService.getInstance().postOrderVerify(getUserToken(), orderSummary, new Callback<BaseResponse<OrderSummary>>() {
            @Override
            public void success(BaseResponse<OrderSummary> orderSummaryBaseResponse, Response response) {
                if (orderSummaryBaseResponse.isResponse()) {
                    OrderSummary returnOrderSummary = orderSummaryBaseResponse.getData();
                    Intent checkOutIntent;
                    returnOrderSummary.setmCartItemsList(mCartItemsList);
                    returnOrderSummary.setOutletId(orderSummary.getOutletId());
                    returnOrderSummary.setAddressDetailsLocationData(addressDetailsLocationData);

                    if (isLocationInputRequired){
                        checkOutIntent = new Intent(activity, AddressAddNewActivity.class);
                    }else{
                        if (returnOrderSummary.getOfferOrderList().size() > 0) {
                            checkOutIntent = new Intent(activity, AvailableOffersActivity.class);
                        } else {
                            checkOutIntent = new Intent(activity, OrderSummaryActivity.class);
                        }
                    }

                    Bundle orderSummaryData = new Bundle();
                    orderSummaryData.putSerializable(AppConstants.INTENT_ORDER_SUMMARY, returnOrderSummary);
                    checkOutIntent.putExtras(orderSummaryData);
                    activity.startActivity(checkOutIntent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, orderSummaryBaseResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

                twystProgressHUD.dismiss();
                hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(activity, error);
                hideSnackbar();
            }
        });
    }

    public static void handleRetrofitError(Activity activity, RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage(activity, "No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage(activity, "An unexpected error has occurred.");
        }
//        Log.e(getTagName(), "failure", error);
    }

    public static void buildAndShowSnackbarWithMessage(final Activity activity, String msg) {
        final Snackbar snackbar = Snackbar.with(activity.getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
                        //.color(getResources().getColor(android.R.color.black))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Intent intent = activity.getIntent();
                        activity.overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.finish();
                        activity.overridePendingTransition(0, 0);
                        activity.startActivity(intent);
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(activity, snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected static void showSnackbar(Activity activity, Snackbar snackbar) {
        SnackbarManager.show(snackbar, activity);
    }

    public static void hideSnackbar() {
        SnackbarManager.dismiss();
    }

    public static String getUserToken() {
        return AppConstants.USER_TOKEN_HARDCODED;
//        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

    }

    public static void goToSummary(Activity activity, int freeItemIndex, OrderSummary orderSummary) {
        Bundle orderSummaryData = new Bundle();
        orderSummaryData.putSerializable(AppConstants.INTENT_ORDER_SUMMARY, orderSummary);
        orderSummaryData.putInt(AppConstants.INTENT_FREE_ITEM_INDEX, freeItemIndex);

        Intent checkOutIntent = new Intent(activity, OrderSummaryActivity.class);
        checkOutIntent.putExtras(orderSummaryData);
        activity.startActivity(checkOutIntent);
    }
}
