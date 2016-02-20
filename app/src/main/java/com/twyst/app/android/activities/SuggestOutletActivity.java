package com.twyst.app.android.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Suggestion;
import com.twyst.app.android.model.SuggestionMeta;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SuggestOutletActivity extends BaseActionActivity {
    private boolean fromDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_outlet);
        fromDrawer = getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, false);
        hideProgressHUDInLayout();

        setupToolBar();

        final EditText outletNameET = (EditText) findViewById(R.id.outletNameET);
        final EditText outletLocET = (EditText) findViewById(R.id.outletLocET);
        final EditText commentEt = (EditText) findViewById(R.id.commentEt);

        findViewById(R.id.suggestOfferBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(outletLocET.getText()) && !TextUtils.isEmpty(outletNameET.getText())) {
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(SuggestOutletActivity.this, false, null);

                    Suggestion suggestion = new Suggestion();
                    SuggestionMeta suggestionMeta = new SuggestionMeta();
                    suggestionMeta.setOutlet(outletNameET.getText().toString());
                    suggestionMeta.setLocation(outletLocET.getText().toString());
                    if (TextUtils.isEmpty(commentEt.getText())) {
                        suggestionMeta.setComment("");
                    } else {
                        suggestionMeta.setComment(commentEt.getText().toString());
                    }

                    suggestion.setSuggestionMeta(suggestionMeta);

                    HttpService.getInstance().postSuggestion(UtilMethods.getUserToken(SuggestOutletActivity.this), suggestion, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse baseResponse, Response response) {

                            if (baseResponse.isResponse()) {
                                Toast.makeText(SuggestOutletActivity.this, "Outlet suggestion sent to Twyst.", Toast.LENGTH_LONG).show();
                                twystProgressHUD.dismiss();
                                finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                UtilMethods.handleRetrofitError(SuggestOutletActivity.this, error);
                            } else {
                                UtilMethods.buildAndShowSnackbarWithMessage(SuggestOutletActivity.this, "Unable to submit outlet!");
                            }
                        }
                    });
                } else if (TextUtils.isEmpty(outletLocET.getText()) && !TextUtils.isEmpty(outletNameET.getText())) {
                    outletLocET.setError("Outlet location required");
                } else if (!TextUtils.isEmpty(outletLocET.getText()) && TextUtils.isEmpty(outletNameET.getText())) {
                    outletNameET.setError("Outlet name required");
                } else {
                    outletNameET.setError("Outlet name required");
                    outletLocET.setError("Outlet location required");
                }

            }
        });

        findViewById(R.id.suggestCancelBtn).setOnClickListener(new View.OnClickListener() {
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
