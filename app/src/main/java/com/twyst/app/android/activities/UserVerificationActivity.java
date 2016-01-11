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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;

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
    RelativeLayout tvVerifyNumberGoLayout;

    MyRunnable myRunnable;

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
        tvVerifyNumberGoLayout = (RelativeLayout) findViewById(R.id.verify_number_go_layout);

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();

        verifyNumberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchOTP(v);
            }
        });

    }

    private void fetchOTP(View view) {
        //Case: Fetch OTP
        if (TextUtils.isEmpty(etPhoneCodeInput.getText()) || etPhoneCodeInput.getText().toString().trim().length() < 10) {
            etPhoneCodeInput.setError("Please enter valid Mobile Number");
        } else {
            etPhoneCodeInput.setError(null);
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            verifyNumberProgressBar.setVisibility(View.VISIBLE);
            tvVerifyNumberGoText.setBackground(null);
            tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_fetch));
            verifyNumberGo.setEnabled(false);
            HttpService.getInstance().getMobileAuthCode(etPhoneCodeInput.getText().toString(), new Callback<BaseResponse<OTPCode>>() {
                @Override
                public void success(final BaseResponse<OTPCode> twystResponse, Response response) {
                    if (twystResponse.isResponse()) {
                        final OTPCode otpCode = twystResponse.getData();
//                        Toast.makeText(UserVerificationActivity.this, "Verifying Number", Toast.LENGTH_SHORT).show();
                        //Waiting for OTP
                        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_waiting));
                        tvVerifyNumberLowerHint.setVisibility(View.GONE);
                        tvVerifyNumberResendManually.setVisibility(View.VISIBLE);
                        tvVerifyNumberResendManually.setText(getResources().getString(R.string.verify_number_manually));

                        tvVerifyNumberResendManually.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                askUserEnterOTP(etPhoneCodeInput.getText().toString());
                            }
                        });

                        myRunnable = new MyRunnable(otpCode);
                        handler.postDelayed(myRunnable, 1000);

                    } else {
                        verifyNumberGo.setEnabled(true);
                        Log.d("error message", "" + twystResponse.getMessage());
                        Toast.makeText(UserVerificationActivity.this, twystResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (twystResponse.getMessage().equalsIgnoreCase("We have already sent you an authentication code.")) {
                            sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, etPhoneCodeInput.getText().toString());
                            sharedPreferences.apply();
                            askUserEnterOTP(etPhoneCodeInput.getText().toString());
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
                    //Number verified
                    numberVerifiedUIUpdate();

                } else {
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, otpCode.getPhone());
                    sharedPreferences.apply();
                    askUserEnterOTP(etPhoneCodeInput.getText().toString());

                }
            }

            @Override
            public void failure(RetrofitError error) {
//                handleRetrofitError(error);
            }
        });
    }

    private void numberVerifiedUIUpdate() {
        etPhoneCodeInput.setText(getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_USER_PHONE, ""));
        etPhoneCodeInput.setEnabled(false);
        etPhonePre.setVisibility(View.VISIBLE);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_verified));
        tvVerifyNumberGoLayout.setVisibility(View.INVISIBLE);
        tvVerifyNumberResendManually.setVisibility(View.INVISIBLE);
        tvVerifyNumberLowerHint.setVisibility(View.INVISIBLE);
    }

    final Handler handler = new Handler();

    private class MyRunnable implements Runnable {
        private int retry = 0;
        private OTPCode otpCode;

        public MyRunnable(OTPCode otp) {
            this.otpCode = otp;
        }

        public void run() {
            if (checkSmsCode()) {
                validateOTP(otpCode);

            } else {
                if (retry < AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS) {
                    handler.postDelayed(this, 1000);
                    tvVerifyNumberGoText.setText(String.valueOf(AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS - retry));
                    Log.d("retry", "" + retry);
                    retry++;
                } else {
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, otpCode.getPhone());
                    sharedPreferences.apply();
                    // Couldn't fetch OTP, ask user to input
                    askUserEnterOTP(etPhoneCodeInput.getText().toString());
                }

            }
        }
    }

    private void askUserEnterOTP(final String phoneNumber) {
        handler.removeCallbacks(myRunnable);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_enter_code));
        etPhonePre.setVisibility(View.INVISIBLE);
        etPhoneCodeInput.setText("");
        etPhoneCodeInput.setHint(getResources().getString(R.string.verify_number_code_hint));
        tvVerifyNumberLowerHint.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setText(getResources().getString(R.string.verify_number_resend));
        tvVerifyNumberGoText.setBackground(getResources().getDrawable(R.drawable.checkout_arrow));
        tvVerifyNumberGoText.setText("");
        verifyNumberProgressBar.setVisibility(View.GONE);

        verifyNumberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOTP(v, etPhoneCodeInput.getText().toString());
            }
        });

        //Resend code
        tvVerifyNumberResendManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });
        verifyNumberGo.setEnabled(true);
    }

    private void resendCode() {
        verifyNumberProgressBar.setVisibility(View.VISIBLE);
        String phone = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_USER_PHONE, "");
        HttpService.getInstance().getMobileAuthCode(phone, new Callback<BaseResponse<OTPCode>>() {
            @Override
            public void success(BaseResponse<OTPCode> baseResponse, Response response) {
                verifyNumberProgressBar.setVisibility(View.INVISIBLE);
                if (baseResponse.isResponse()) {

                    Toast.makeText(UserVerificationActivity.this, "Code resent successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("error message", "" + baseResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                verifyNumberProgressBar.setVisibility(View.INVISIBLE);
                handleRetrofitError(error);
            }
        });
    }

    private void submitOTP(View view, final String otpEntered) {
        if (TextUtils.isEmpty(otpEntered) || otpEntered.trim().length() < 4) {
            etPhoneCodeInput.setError("Invalid code !!");
        } else {
            etPhoneCodeInput.setError(null);
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            verifyNumberProgressBar.setVisibility(View.VISIBLE);
            tvVerifyNumberGoText.setBackground(null);
            tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_verify));
            verifyNumberGo.setEnabled(false);

            String phone = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_USER_PHONE, "");
            HttpService.getInstance().userAuthToken(otpEntered, phone, new Callback<BaseResponse<AuthToken>>() {
                @Override
                public void success(BaseResponse<AuthToken> baseResponse, Response response) {
                    verifyNumberProgressBar.setVisibility(View.GONE);
                    if (baseResponse.isResponse()) {
                        AuthToken authToken = baseResponse.getData();
                        saveUserToken(authToken.getToken());

                        numberVerifiedUIUpdate();
                    } else {
                        verifyNumberGo.setEnabled(true);
                        Toast.makeText(UserVerificationActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    handleRetrofitError(error);
                }
            });

        }
    }

    private void saveUserToken(String userToken) {
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(AppConstants.PREFERENCE_USER_TOKEN, userToken);
        sharedPreferences.putBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, true);
        sharedPreferences.commit();
    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage("No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage("An unexpected error has occurred. Please try after some time.");
        }
        Log.e(getTagName(), "failure", error);
    }

    public void buildAndShowSnackbarWithMessage(String msg) {
        final Snackbar snackbar = Snackbar.with(getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
//                .color(getResources().getColor(R.color.snackbar_bg))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }

}
