package com.twyst.app.android.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.WriteMeta;
import com.twyst.app.android.model.WriteToUs;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WriteToUsActivity extends BaseActionActivity {
    private boolean fromDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setupAsChild = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_to_us);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        setupToolBar();
        hideProgressHUDInLayout();
        final EditText commentEt = (EditText) findViewById(R.id.commentEt);

        findViewById(R.id.writeToUsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(WriteToUsActivity.this, false, null);
                if (TextUtils.isEmpty(commentEt.getText())) {
                    Toast.makeText(WriteToUsActivity.this, "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
                    twystProgressHUD.dismiss();
                    commentEt.setError("Comments required");
                    twystProgressHUD.dismiss();
                } else {
                    WriteToUs writeToUs = new WriteToUs();
                    WriteMeta writeMeta = new WriteMeta();
                    writeMeta.setComments(commentEt.getText().toString());
                    writeToUs.setWriteMeta(writeMeta);
                    HttpService.getInstance().writeToUs(UtilMethods.getUserToken(WriteToUsActivity.this), writeToUs, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {
                            Toast.makeText(WriteToUsActivity.this, "Your comment has been sent to Twyst.", Toast.LENGTH_SHORT).show();
                            twystProgressHUD.dismiss();
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            UtilMethods.handleRetrofitError(WriteToUsActivity.this, error);
                        }
                    });
                }

            }
        });

        findViewById(R.id.writeCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
}
