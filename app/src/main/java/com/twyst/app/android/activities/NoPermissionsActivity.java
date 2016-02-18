package com.twyst.app.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.PermissionUtil;

public class NoPermissionsActivity extends AppCompatActivity {

    private static final int REQUEST_CONTATCS = 0;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_PHONE = 2;
    private static final int REQUEST_SMS = 3;

    private static final int REQUEST_SETTINGS      = 4;
    private TextView tvRetry;
    private int permissionRequested = -1;
    private String rationale = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_permissions);

        setupToolBar();

        tvRetry = (TextView)findViewById(R.id.retry_permission);
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(permissionRequested){
                    case REQUEST_LOCATION:
                        if (PermissionUtil.getInstance().approveLocation(NoPermissionsActivity.this,true)){
                            finish();
                        } else {
                            if (rationale != null){
                                showSettingsAlert(rationale);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        rationale = getIntent().getStringExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE);
        permissionRequested = getIntent().getIntExtra(AppConstants.INTENT_PERMISSION,-1);
        showSettingsAlert(rationale);



    }


    public void showSettingsAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NoPermissionsActivity.this);
        alertDialog.setTitle("Alert");

        alertDialog.setMessage(message);
        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                startActivityForResult (intent, REQUEST_SETTINGS);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_SETTINGS){
            switch(permissionRequested){
                case REQUEST_LOCATION:
                    if (PermissionUtil.getInstance().approveLocation(NoPermissionsActivity.this,true)){
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
