package com.twyst.app.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.TwystApplication;
import com.twyst.app.android.adapters.SummaryAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.model.OrderUpdate;
import com.twyst.app.android.model.order.CancelOrder;
import com.twyst.app.android.model.order.OrderInfoLocal;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.PermissionUtil;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrderTrackingActivity extends BaseActionActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ListView trackOrderStatesListview;
    private ArrayList<OrderTrackingState> mTrackOrderStatesList;
    private TrackOrderStatesAdapter mAdapter;
    private String mOrderID;
    protected TwystApplication twystApplication;
    private boolean ivArrowExpanded;
    private boolean isOrderDeliverySuccessInProgress;

    private static final int REQUEST_PHONE = 2;
    private final String TAG = "OrderTrackingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        processExtraData();

        setupToolBar();
        trackOrderStatesListview = (ListView) findViewById(R.id.listview_track_order_states);
        mAdapter = new TrackOrderStatesAdapter();
        trackOrderStatesListview.setAdapter(mAdapter);
        twystApplication = (TwystApplication) this.getApplicationContext();
    }

    private void processExtraData() {
        mOrderID = getIntent().getExtras().getString(AppConstants.INTENT_ORDER_ID, "");
        refreshList();

        OrderInfoLocal orderInfoLocal = OrderInfoLocal.getLocalList(mOrderID, OrderTrackingActivity.this);
        if (orderInfoLocal == null) {// No  local data
            findViewById(R.id.iv_arrow).setVisibility(View.GONE);
            if (TextUtils.isEmpty(getIntent().getExtras().getString(AppConstants.INTENT_ORDER_NUMBER, ""))) {
                findViewById(R.id.order_number_layout).setVisibility(View.GONE);
            } else {
                showOrderNumber(getIntent().getExtras().getString(AppConstants.INTENT_ORDER_NUMBER, ""));
            }
        } else {
            showOrderNumber(orderInfoLocal.getOrderNumber());
            setupSummaryRecyclerView(orderInfoLocal);
            findViewById(R.id.iv_arrow).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_arrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ivArrowExpanded) {
                        findViewById(R.id.summaryRecyclerView).setVisibility(View.GONE);
                        ivArrowExpanded = false;
                        ((ImageView) findViewById(R.id.iv_arrow)).setImageResource(R.drawable.collapsed);
                    } else {
                        findViewById(R.id.summaryRecyclerView).setVisibility(View.VISIBLE);
                        ivArrowExpanded = true;
                        ((ImageView) findViewById(R.id.iv_arrow)).setImageResource(R.drawable.expanded);
                    }
                }
            });
        }

    }

    private void showOrderNumber(String orderNumber) {
        findViewById(R.id.order_number_layout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_order_number)).setText(orderNumber);
    }


    private void setupSummaryRecyclerView(OrderInfoLocal orderInfoLocal) {
        RecyclerView mSummaryRecyclerView = (RecyclerView) findViewById(R.id.summaryRecyclerView);
        mSummaryRecyclerView.setHasFixedSize(true);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSummaryRecyclerView.setLayoutManager(mLayoutManager);

        SummaryAdapter mSummaryAdapter = new SummaryAdapter(OrderTrackingActivity.this, orderInfoLocal.getOrderSummary(), orderInfoLocal.getFreeItemIndex());
        mSummaryRecyclerView.setAdapter(mSummaryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        twystApplication.setCurrentActivity(this);
        refreshList();
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void clearReferences() {
        Activity currActivity = twystApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            twystApplication.setCurrentActivity(null);
    }

    //Also called by when received notification
    public void refreshListServer(final String orderIDServer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (orderIDServer.equals(mOrderID)) {
                    refreshList();
                }
            }
        });
    }

    private void refreshList() {
        mTrackOrderStatesList = OrderTrackingState.getInitialList(mOrderID, OrderTrackingActivity.this);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    class TrackOrderStatesAdapter extends ArrayAdapter<OrderTrackingState> {
        TrackOrderStatesAdapter() {
            super(OrderTrackingActivity.this, R.layout.order_tracking_state_row, mTrackOrderStatesList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.order_tracking_state_row, parent, false);
            TrackOrderStateViewholder viewholder = new TrackOrderStateViewholder(row);

            OrderTrackingState orderstate = mTrackOrderStatesList.get(position);

            viewholder.message.setText(orderstate.getMessage());
            viewholder.trackOrderStateTime.setText(orderstate.getTime());
            viewholder.trackOrderStateAmOrPm.setText(orderstate.getAmpm());

            boolean isCurrent = (position == 0);

            switch (orderstate.getOrderState()) {
                case OrderTrackingState.STATE_PLACED:
                    if (isCurrent) {
                        viewholder.tvClickForFailure.setVisibility(View.VISIBLE);
                        viewholder.tvClickForFailure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelOrderDialogShow();
                            }
                        });
                        viewholder.tvClickForFailure.setText(getResources().getString(R.string.order_placed_cancel_message));
                    }
                    break;

                case OrderTrackingState.STATE_ACCEPTED:
                    if (isCurrent) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setBackgroundColor(getResources().getColor(R.color.background_green));
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_accepted_contact_outlet_message));
//                        showDialogCall("8505850504");
                    }
                    break;

                case OrderTrackingState.STATE_DISPATCHED:
                    if (isCurrent) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_dispatched_message_success));
                        viewholder.tvClickForSuccess.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderDelivered(true);
                            }
                        });
                    }
                    break;

                case OrderTrackingState.STATE_ASSUMED_DELIVERED:
                    if (isCurrent) {
                        viewholder.tvClickForFailure.setVisibility(View.VISIBLE);
                        viewholder.tvClickForFailure.setText(getResources().getString(R.string.order_assumed_delivery_message_failure));
                        viewholder.tvClickForFailure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderDelivered(false);
                            }
                        });
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_assumed_delivery_message_success));
                        viewholder.tvClickForSuccess.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderDelivered(true);
                            }
                        });
                    }
                    break;

                case OrderTrackingState.STATE_NOT_DELIVERED:
                    if (isCurrent) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_not_delivered_message_success));
                        viewholder.tvClickForSuccess.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderDelivered(true);
                            }
                        });
                    }
                    break;

                case OrderTrackingState.STATE_DELIVERED:
                    if (!isOrderDeliverySuccessInProgress) {
                        orderDeliveredSuccess();
                    }
                    break;

                default:
                    break;
            }

            return row;
        }

        private void showDialogCall(final String phNo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderTrackingActivity.this);
            final View dialogView = LayoutInflater.from(OrderTrackingActivity.this).inflate(R.layout.dialog_call_outlet, null);

            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            TextView bYES = (TextView) dialogView.findViewById(R.id.bYES);
            TextView bCANCEL = (TextView) dialogView.findViewById(R.id.bCANCEL);

            bCANCEL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            bYES.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PermissionUtil.getInstance().approvePhone(OrderTrackingActivity.this, false)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(phNo));
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                }
            });

        }

        @Override
        public int getCount() {
            return mTrackOrderStatesList.size();
        }
    }

    private void orderDeliveredSuccess() {
        isOrderDeliverySuccessInProgress = true;
        if (HttpService.getInstance().getSharedPreferences().getBoolean(mOrderID + AppConstants.INTENT_ORDER_FEEDBACK, false)) {
            Toast.makeText(OrderTrackingActivity.this, "Order delivered successfully! Please rate the order and claim your cashback!", Toast.LENGTH_LONG).show();
            HttpService.getInstance().getSharedPreferences().edit().putString(AppConstants.INTENT_ORDER_ID_FEEDBACK, mOrderID).apply();
            Intent feedbackIntent = new Intent(OrderTrackingActivity.this, FeedbackActivity.class);
            startActivity(feedbackIntent);
            finish();
        }
    }

    private void orderDelivered(final boolean isDelivered) {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        OrderUpdate orderUpdate = new OrderUpdate(mOrderID, OrderUpdate.DELIVERY_STATUS, isDelivered);

        HttpService.getInstance().putOrderUpdate(UtilMethods.getUserToken(OrderTrackingActivity.this), orderUpdate, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    if (isDelivered) {
                        OrderTrackingState.addToListLocally(mOrderID, OrderTrackingState.getDeliveredOrderTrackingState(OrderTrackingActivity.this), OrderTrackingActivity.this);
                    } else {
                        OrderTrackingState.addToListLocally(mOrderID, OrderTrackingState.getNotDeliveredOrderTrackingState(OrderTrackingActivity.this), OrderTrackingActivity.this);
                    }
                    refreshList();
                } else {
                    Toast.makeText(OrderTrackingActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(OrderTrackingActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    private void cancelOrderDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_cancel_reason, null);

        final View fOK = dialogView.findViewById(R.id.fOK);
        final EditText etReason = (EditText) dialogView.findViewById(R.id.et_reason);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        fOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etReason.getText().toString())) {
                    etReason.setError("Please mention reason for cancellation!");
                } else {
                    cancelOrder(etReason.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialogView.findViewById(R.id.fCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void cancelOrder(String reasonToCancel) {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        CancelOrder cancelOrder = new CancelOrder(mOrderID, reasonToCancel);

        HttpService.getInstance().postOrderCancel(UtilMethods.getUserToken(OrderTrackingActivity.this), cancelOrder, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    OrderTrackingState.addToListLocally(mOrderID, OrderTrackingState.getCancelledOrderTrackingState(OrderTrackingActivity.this), OrderTrackingActivity.this);
                    refreshList();
                } else {
                    Toast.makeText(OrderTrackingActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(OrderTrackingActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_ORDER_HISTORY, false)) {
            Intent intent = new Intent(OrderTrackingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_CURRENT);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    static class TrackOrderStateViewholder {
        private TextView message;
        private TextView trackOrderStateTime;
        private TextView trackOrderStateAmOrPm;
        private TextView tvClickForFailure;
        private TextView tvClickForSuccess;

        TrackOrderStateViewholder(View view) {
            message = (TextView) view.findViewById(R.id.tv_message);
            trackOrderStateTime = (TextView) view.findViewById(R.id.tv_track_order_state_time);
            trackOrderStateAmOrPm = (TextView) view.findViewById(R.id.tv_track_order_state_time_amOrPm);
            tvClickForFailure = (TextView) view.findViewById(R.id.tv_click_for_failure);
            tvClickForSuccess = (TextView) view.findViewById(R.id.tv_click_for_success);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PHONE) {
            Log.i(TAG, "Received response for phone permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
//                findViewById(R.id.bYES).performClick();
                // todo
            } else {
                Log.i(TAG, "Phone permissions were NOT granted.");
                Intent intent = new Intent(OrderTrackingActivity.this, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_PHONE);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, R.string.permission_phone_rationale);
                startActivity(intent);
            }

        }
    }


}