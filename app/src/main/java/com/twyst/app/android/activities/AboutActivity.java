package com.twyst.app.android.activities;

import android.os.Bundle;

import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 2/11/2016.
 */
public class AboutActivity extends BaseActionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setupAsChild=true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupToolBar();
    }
}
