package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.TwystApplication;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

public class OrderTrackingActivity extends BaseActionActivity {
    private ListView trackOrderStatesListview;
    private ArrayList<OrderTrackingState> mTrackOrderStatesList;
    private TrackOrderStatesAdapter mAdapter;
    String mOrderID;
    protected TwystApplication twystApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        mOrderID = getIntent().getExtras().getString(AppConstants.INTENT_ORDER_ID, "");
        mTrackOrderStatesList = OrderTrackingState.getInitialList(mOrderID, OrderTrackingActivity.this);

        setupToolBar();
        trackOrderStatesListview = (ListView) findViewById(R.id.listview_track_order_states);
        mAdapter = new TrackOrderStatesAdapter();
        trackOrderStatesListview.setAdapter(mAdapter);
        twystApplication = (TwystApplication) this.getApplicationContext();
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

    private void clearReferences() {
        Activity currActivity = twystApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            twystApplication.setCurrentActivity(null);
    }

    //Also called by when received notification
    public void refreshList() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrackOrderStatesList = OrderTrackingState.getInitialList(mOrderID, OrderTrackingActivity.this);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

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

            boolean isCurrent = (position + 1 == mTrackOrderStatesList.size());

            switch (orderstate.getOrderState()) {
                case OrderTrackingState.STATE_PLACED:
                    if (isCurrent) {
                        viewholder.tvClickForFailure.setVisibility(View.VISIBLE);
                        viewholder.tvClickForFailure.setText(getResources().getString(R.string.order_placed_cancel_message));
                    }
                    break;

                case OrderTrackingState.STATE_ACCEPTED:
                    if (isCurrent) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setBackgroundColor(getResources().getColor(R.color.background_green));
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_accepted_contact_outlet_message));
                    }
                    break;

                case OrderTrackingState.STATE_DISPATCHED:
                    if (isCurrent) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_dispatched_message_success));
                    }
                    break;

                case OrderTrackingState.STATE_ASSUMED_DELIVERED:
                    if (isCurrent) {
                        viewholder.tvClickForFailure.setVisibility(View.VISIBLE);
                        viewholder.tvClickForFailure.setText(getResources().getString(R.string.order_assumed_delivery_message_failure));
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_assumed_delivery_message_success));
                    }
                    break;

                case OrderTrackingState.STATE_NOT_DELIVERED:
                    if (isCurrent) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_not_delivered_message_success));
                    }
                    break;

                default:
                    break;
            }

            return row;
        }
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

}