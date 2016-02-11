package com.twyst.app.android.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;

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

        displayAppVersion();

        findViewById(R.id.tvTermsOfUse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openURL(getTermsURL());
            }
        });

        findViewById(R.id.tvPrivacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openURL(getPrivayPolicyURL());
            }
        });
    }

    private String getTermsURL() {
        return "http://docs.google.com/gview?embedded=true&url=" + AppConstants.HOST + "/terms_of_use.pdf";
    }

    private String getPrivayPolicyURL() {
        return "http://docs.google.com/gview?embedded=true&url=" + AppConstants.HOST + "/privacy_policy.pdf";
    }

    private void openURL(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void displayAppVersion() {
        TextView versionApp = (TextView) findViewById(R.id.tvAppVersion);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = "";
        if (pInfo != null && pInfo.versionName != null) {
            version = "v" + pInfo.versionName + " Beta";
        }
        versionApp.setText(version);
    }
}
