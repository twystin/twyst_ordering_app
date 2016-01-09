package com.twyst.app.android.activities;

import android.app.Activity;
import android.os.Bundle;

import com.twyst.app.android.R;

/**
 * Created by Vipul Sharma on 1/9/2016.
 */
public class UserVerificationActivity extends Activity {

    private String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_verification);
    }
}
