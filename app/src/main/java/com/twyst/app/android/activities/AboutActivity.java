package com.twyst.app.android.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Vipul Sharma on 2/11/2016.
 */
public class AboutActivity extends BaseActionActivity {
    @Bind(R.id.tvAppVersion) TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setupAsChild=true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setupToolBar();
        displayAppVersion();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
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
        tvAppVersion.setText(version);
    }

    @OnClick({R.id.tvTermsOfUse, R.id.tvPrivacyPolicy})
    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTermsOfUse:
                openURL(getTermsURL());
                break;
            case R.id.tvPrivacyPolicy:
                openURL(getPrivayPolicyURL());
                break;
        }
    }
}
