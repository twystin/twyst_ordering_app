package com.twyst.app.android.activities;

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
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

public class OrderTrackingActivity extends AppCompatActivity {
    static int count = 1;
    private ListView trackOrderStatesListview;
    private ArrayList<OrderTrackingState> mTrackOrderStatesList;
    private TrackOrderStatesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        String orderID = getIntent().getExtras().getString(AppConstants.INTENT_ORDER_ID,"");
        mTrackOrderStatesList = OrderTrackingState.getInitialList(orderID, OrderTrackingActivity.this);

        setupToolBar();
        trackOrderStatesListview = (ListView) findViewById(R.id.listview_track_order_states);
        mAdapter = new TrackOrderStatesAdapter();
        trackOrderStatesListview.setAdapter(mAdapter);

//        handler.postDelayed(runnable, 5000);
    }

//    final Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (count < 6) {
//                OrderTrackingState newState = new OrderTrackingState();
//                newState.setOrderState(count);
//                newState.setTime("12:30");
//                newState.setAmpm("AM");
//                newState.setIsCurrent(true);
//                trackOrderStatesList.add(0, newState);
//
//                for (int j = 1;j<count;j++){
//                    trackOrderStatesList.get(j).setIsCurrent(false);
//                }
//                count++;
//                mAdapter.notifyDataSetChanged();
//                handler.postDelayed(runnable, 5000);
//            } else {
//                handler.removeCallbacks(runnable);
//            }
//        }
//    };

    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
            switch (orderstate.getOrderState()) {

                case OrderTrackingState.STATE_PLACED:
                    viewholder.message.setText(orderstate.getMessage());
                    viewholder.trackOrderStateTime.setText(orderstate.getTime());
                    viewholder.trackOrderStateAmOrPm.setText(orderstate.getAmpm());
                    if (orderstate.isCurrent()) {
                        viewholder.tvClickForFailure.setVisibility(View.VISIBLE);
                        viewholder.tvClickForFailure.setText(getResources().getString(R.string.order_placed_cancel_message));
                    }
                    break;

                case OrderTrackingState.STATE_ACCEPTED:
                    viewholder.message.setText(getResources().getString(R.string.order_accepted_message));
                    viewholder.trackOrderStateTime.setText(orderstate.getTime());
                    viewholder.trackOrderStateAmOrPm.setText(orderstate.getAmpm());
                    if (orderstate.isCurrent()) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setBackgroundColor(getResources().getColor(R.color.background_green));
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_accepted_contact_outlet_message));
                    }
                    break;

                case OrderTrackingState.STATE_DISPATCHED:
                    viewholder.message.setText(getResources().getString(R.string.order_dispatched_message));
                    viewholder.trackOrderStateTime.setText(orderstate.getTime());
                    viewholder.trackOrderStateAmOrPm.setText(orderstate.getAmpm());
                    if (orderstate.isCurrent()) {
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_dispatched_message_success));
                    }
                    break;

                case OrderTrackingState.STATE_ASSUMED_DELIVERED:
                    viewholder.message.setText(getResources().getString(R.string.order_assumed_delivery_message));
                    viewholder.trackOrderStateTime.setText(orderstate.getTime());
                    viewholder.trackOrderStateAmOrPm.setText(orderstate.getAmpm());
                    if (orderstate.isCurrent()) {
                        viewholder.tvClickForFailure.setVisibility(View.VISIBLE);
                        viewholder.tvClickForFailure.setText(getResources().getString(R.string.order_assumed_delivery_message_failure));
                        viewholder.tvClickForSuccess.setVisibility(View.VISIBLE);
                        viewholder.tvClickForSuccess.setText(getResources().getString(R.string.order_assumed_delivery_message_success));
                    }
                    break;

                case OrderTrackingState.STATE_NOT_DELIVERED:
                    viewholder.message.setText(getResources().getString(R.string.order_not_delivered_message));
                    viewholder.trackOrderStateTime.setText(orderstate.getTime());
                    viewholder.trackOrderStateAmOrPm.setText(orderstate.getAmpm());
                    if (orderstate.isCurrent()) {
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