package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobikwik.sdk.lib.User;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 1/9/2016.
 */
public class UserVerificationActivity extends Activity {
    // Verify Number
    ImageView ivCorrectSymbol;
    TextView tvVerifyNumberHint;
    EditText etPhonePre;
    EditText etPhoneCodeInput;
    View verifyNumberGo;
    TextView tvVerifyNumberGoText;
    CircularProgressBar verifyNumberProgressBar;
    TextView tvVerifyNumberLowerHint;
    TextView tvVerifyNumberResendManually;

    private SharedPreferences.Editor sharedPreferences;
    private String otpCodeReaded;

    private String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);

        setupVerifyNumber();
    }

    private void setupVerifyNumber() {
        ivCorrectSymbol = (ImageView) findViewById(R.id.verify_number_correct_symbol);
        tvVerifyNumberHint = (TextView) findViewById(R.id.verify_number_hint);
        etPhonePre = (EditText) findViewById(R.id.verify_number_phone_pre);
        etPhoneCodeInput = (EditText) findViewById(R.id.verify_number_phone_code);
        verifyNumberGo = (View) findViewById(R.id.verify_number_go);
        tvVerifyNumberGoText = (TextView) findViewById(R.id.verify_number_go_text);
        verifyNumberProgressBar = (CircularProgressBar) findViewById(R.id.verify_number_progress_bar);
        tvVerifyNumberLowerHint = (TextView) findViewById(R.id.verify_number_lower_hint);
        tvVerifyNumberResendManually = (TextView) findViewById(R.id.verify_number_resend_enter_manually);

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();

        verifyNumberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyNumberGoClicked(v);
            }
        });

    }

    private void verifyNumberGoClicked(View view) {
        //Case: Fetch OTP
        if (TextUtils.isEmpty(etPhoneCodeInput.getText()) || etPhoneCodeInput.getText().toString().trim().length() < 10) {
            etPhoneCodeInput.setError("Please enter valid mobile number");

        } else {
            etPhoneCodeInput.setError(null);
            verifyNumberGo.setEnabled(false);
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            verifyNumberProgressBar.setVisibility(View.VISIBLE);
            tvVerifyNumberGoText.setBackground(null);
            HttpService.getInstance().getMobileAuthCode(etPhoneCodeInput.getText().toString(), new Callback<BaseResponse<OTPCode>>() {
                @Override
                public void success(final BaseResponse<OTPCode> twystResponse, Response response) {
                    if (twystResponse.isResponse()) {
                        final OTPCode otpCode = twystResponse.getData();
                        Toast.makeText(UserVerificationActivity.this, "Verifying Number", Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();

                        Runnable runnable = new Runnable() {
                            int retry = 0;

                            @Override
                            public void run() {
                                if (checkSmsCode()) {
                                    validateOTP(otpCode);

                                } else {
                                    if (retry < AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS) {
                                        handler.postDelayed(this, 1000);
                                        Log.d("retry", "" + retry);
                                        retry++;

                                    } else {
                                        sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, otpCode.getPhone());
                                        sharedPreferences.apply();
                                        Intent intent = new Intent(getBaseContext(), OTPVerificationActivity.class);
                                        intent.putExtra(AppConstants.INTENT_PARAM_OTP_PHONE, otpCode.getPhone());
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            }

                        };
                        handler.postDelayed(runnable, 1000);

                    } else {
                        verifyNumberGo.setEnabled(true);
                        findViewById(R.id.circularProgressBar).setVisibility(View.INVISIBLE);
                        Log.d("error message", "" + twystResponse.getMessage());
                        Toast.makeText(UserVerificationActivity.this, twystResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (twystResponse.getMessage().equalsIgnoreCase("We have already sent you an authentication code.")) {
                            sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, etPhoneCodeInput.getText().toString());
                            sharedPreferences.apply();
                            Intent intent = new Intent(getBaseContext(), OTPVerificationActivity.class);
                            intent.putExtra(AppConstants.INTENT_PARAM_OTP_PHONE, etPhoneCodeInput.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    verifyNumberProgressBar.setVisibility(View.INVISIBLE);
                    tvVerifyNumberGoText.setBackground(getResources().getDrawable(R.drawable.checkout_arrow));
//                    handleRetrofitError(error);
                }
            });

        }
    }

    private boolean checkSmsCode() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String msgData = "";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }


        SharedPreferences pref = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String smsBody = pref.getString(AppConstants.PREFERENCE_SMS_BODY, "");
        if (TextUtils.isEmpty(smsBody)) {
            return false;
        } else {
            Toast.makeText(UserVerificationActivity.this, "Reading Verification SMS", Toast.LENGTH_SHORT).show();
            Pattern p = Pattern.compile("[0-9]{4}");
            Matcher m = p.matcher(smsBody);
            if (m.find()) {
                otpCodeReaded = m.group(0).trim();
            } else {
                otpCodeReaded = null;
            }

            sharedPreferences.remove(AppConstants.PREFERENCE_SMS_BODY);
            sharedPreferences.commit();
            return true;
        }

    }

    private void validateOTP(final OTPCode otpCode) {
        HttpService.getInstance().userAuthToken(otpCodeReaded, otpCode.getPhone(), new Callback<BaseResponse<AuthToken>>() {
            @Override
            public void success(BaseResponse<AuthToken> baseResponse, Response response) {
                if (baseResponse.isResponse()) {

                    AuthToken authToken = baseResponse.getData();
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_TOKEN, authToken.getToken());
                    sharedPreferences.putBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, true);
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, otpCode.getPhone());
                    sharedPreferences.commit();

                    Toast.makeText(UserVerificationActivity.this, "Number Verified!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();

                } else {
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, otpCode.getPhone());
                    sharedPreferences.apply();
                    Intent intent = new Intent(getBaseContext(), OTPVerificationActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_OTP_PHONE, otpCode.getPhone());
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void failure(RetrofitError error) {
//                handleRetrofitError(error);
            }
        });
    }

}
