package com.twyst.app.android.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twyst.app.android.R;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Vipul Sharma on 2/2/2016.
 */
public class BaseActionActivity extends AppCompatActivity {

    public void setupToolBar() {
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

    public void hideProgressHUDInLayout() {
        CircularProgressBar circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }

}
