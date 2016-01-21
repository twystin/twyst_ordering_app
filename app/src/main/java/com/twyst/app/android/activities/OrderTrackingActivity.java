package com.twyst.app.android.activities;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;

public class OrderTrackingActivity extends AppCompatActivity {

    final private int ORDER_SENT = 0;
    final private int ORDER_ACCEPTED = 1;
    final private int ORDER_DISPATCHED = 2;
    final private int ORDER_DELIVERED = 3;

    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);
        setupToolBar();
        ImageView circle1 = (ImageView) findViewById(R.id.order_tracking_circle1);
        ((GradientDrawable)circle1.getDrawable()).setColor(getResources().getColor(R.color.background_green));

        // To be removed later:
        TextView refresh = (TextView) findViewById(R.id.tvRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status += 1;
                updateOrderTrack(status);
            }
        });

    }

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

    private void updateOrderTrack (int Status){

        LinearLayout ll1 = (LinearLayout) findViewById(R.id.track_status_sent_card);
        RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.track_status_accepted_card);
        switch (Status){
            case ORDER_ACCEPTED :
                ImageView circle2 = (ImageView) findViewById(R.id.order_tracking_circle2);
                ((GradientDrawable)circle2.getDrawable()).setColor(getResources().getColor(R.color.background_green));
                ImageView rect1 = (ImageView) findViewById(R.id.order_tracking_rect1);
                ((GradientDrawable)rect1.getDrawable()).setColor(getResources().getColor(R.color.background_green));
                ImageView dialogbox2 = (ImageView) findViewById(R.id.dialogbox2);
                dialogbox2.setImageDrawable(getResources().getDrawable(R.drawable.dialoguebox));
                ll1.setVisibility(View.GONE);
                rl1.setVisibility(View.VISIBLE);
                break;
            case ORDER_DISPATCHED :
                ImageView circle3 = (ImageView) findViewById(R.id.order_tracking_circle3);
                ((GradientDrawable) circle3.getDrawable()).setColor(getResources().getColor(R.color.background_green));
                ImageView rect2 = (ImageView) findViewById(R.id.order_tracking_rect2);
                ((GradientDrawable)rect2.getDrawable()).setColor(getResources().getColor(R.color.background_green));
                ImageView dialogbox3 = (ImageView) findViewById(R.id.dialogbox3);
                dialogbox3.setImageDrawable(getResources().getDrawable(R.drawable.dialoguebox));
                rl1.setVisibility(View.GONE);
                break;

            case ORDER_DELIVERED :
                ImageView circle4 = (ImageView) findViewById(R.id.order_tracking_circle4);
                ((GradientDrawable)circle4.getDrawable()).setColor(getResources().getColor(R.color.background_green));
                ImageView rect3 = (ImageView) findViewById(R.id.order_tracking_rect3);
                ((GradientDrawable)rect3.getDrawable()).setColor(getResources().getColor(R.color.background_green));
                ImageView dialogbox4 = (ImageView) findViewById(R.id.dialogbox4);
                dialogbox4.setImageDrawable(getResources().getDrawable(R.drawable.dialoguebox));
                break;
            // To be Removed Later
            default:
                Toast.makeText(this, "Enough", Toast.LENGTH_SHORT).show();
        }
    }
}