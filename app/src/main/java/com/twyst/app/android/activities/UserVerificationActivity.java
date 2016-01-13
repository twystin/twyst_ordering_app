package com.twyst.app.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.Referral;
import com.twyst.app.android.model.ReferralMeta;
import com.twyst.app.android.model.UpdateProfile;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.PhoneBookContacts;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 1/9/2016.
 */
public class UserVerificationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Submit button
    View btnSubmit;

    // Verify Number
    ImageView ivCorrectSymbolVerifyNumber;
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
    private String mPhoneEntered;
    private String otpCodeReaded;

    //Verify Email
    private ImageView ivEditEmail;
    private EditText etVerifyEmail;

    //Signup
    private CallbackManager callbackManager;

    private String userImage;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String city;
    private String source = "phonebook";
    private String socialEmail;
    private String id, fbid, linkUri;
    private Friend friend;
    private List<Friend.Friends> friendsList;
    private Friend.Friends friends;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    //Google +
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);

        setupVerifyEmail();
        setupSubmitButton();
        setupVerifyNumber();
        setupSignup();
        setupUI(findViewById(R.id.layout_user_verification));
    }

    private void setupSubmitButton() {
        btnSubmit = findViewById(R.id.submit);
        btnSubmit.setEnabled(false);
        final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etVerifyEmail.getText().toString()) || !Pattern.matches(EMAIL_REGEX, etVerifyEmail.getText().toString())) {
                    etVerifyEmail.setError("Invalid email");
                } else {
                    etVerifyEmail.setError(null);
                    socialEmail = etVerifyEmail.getText().toString();
                    source = "phonebook";
                    userImage = "";
                    firstName = "";
                    middleName = "";
                    lastName = "";
                    dob = "";
                    id = "";
                    final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    friendsList = PhoneBookContacts.getInstance().getPhoneContactList();
                    sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, "");
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, prefs.getString(AppConstants.PREFERENCE_USER_PHONE, ""));
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, prefs.getString(AppConstants.PREFERENCE_USER_PHONE, ""));
                    sharedPreferences.apply();
                    updateUserEmail();
                }
            }
        });
        btnSubmit.setEnabled(false);

    }

    private void setupVerifyEmail() {
        ivEditEmail = (ImageView) findViewById(R.id.iv_edit_email);
        etVerifyEmail = (EditText) findViewById(R.id.et_verify_email);

        ivEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusShowKeyBoard(etVerifyEmail);
            }
        });

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(UserVerificationActivity.this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                etVerifyEmail.setText(possibleEmail);
                findViewById(R.id.iv_email_verify_correct).setBackground(getResources().getDrawable(R.drawable.checked));
                break;
            }
        }

    }

    private void focusShowKeyBoard(final EditText editTextView) {
        editTextView.requestFocus();
        editTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(editTextView, 0);
            }
        }, 50);
    }

    private void setupSignup() {
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestBatch batch = new GraphRequestBatch(GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                        try {
                            id = jsonObject.getString("id");
                            socialEmail = jsonObject.getString("email");

                        } catch (JSONException jsone) {
                            jsone.printStackTrace();
                        }
                        try {
                            dob = jsonObject.getString("birthday");

                        } catch (JSONException jsone) {
                            jsone.printStackTrace();
                        }
                    }
                }),
                        GraphRequest.newMyFriendsRequest(accessToken, new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                friendsList = new ArrayList<Friend.Friends>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        friends = new Friend.Friends();
                                        friends.setId(jsonArray.getJSONObject(i).getString("id"));
                                        friends.setName(jsonArray.getJSONObject(i).getString("name"));
                                        friends.setPhone(null);
                                        friendsList.add(friends);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                );
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                        updateUserEmail();
                    }
                });
                batch.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("LoginActivity.onCancel called");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Profile.fetchProfileForCurrentAccessToken();

                userImage = String.valueOf(currentProfile.getProfilePictureUri(250, 250));
                firstName = currentProfile.getFirstName();
                middleName = currentProfile.getMiddleName();
                lastName = currentProfile.getLastName();
                fbid = currentProfile.getId();
                linkUri = String.valueOf(currentProfile.getLinkUri());
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, String.valueOf(currentProfile.getProfilePictureUri(250, 250)));
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, currentProfile.getFirstName());
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, currentProfile.getName());
                sharedPreferences.putBoolean(AppConstants.PREFERENCE_IS_FACEBOOK_CONNECTED, true);
                sharedPreferences.apply();
            }

        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        findViewById(R.id.fbLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = "FACEBOOK";
                facebookLogin();
            }
        });

        findViewById(R.id.gPlusLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = "GOOGLE";
                // User clicked the sign-in button, so begin the sign-in process and automatically
                // attempt to resolve any errors that occur.
                mShouldResolve = true;
                mGoogleApiClient.connect();
            }
        });
    }

    private void setupVerifyNumber() {
        ivCorrectSymbolVerifyNumber = (ImageView) findViewById(R.id.verify_number_correct_symbol);
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
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean phoneVerified = prefs.getBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, false);

        if (phoneVerified) {
            numberVerifiedUIUpdate();
        } else {
            numberToEnterUIUpdate();
        }
    }

    private void fetchOTP() {
        //Case: Fetch OTP
        if (TextUtils.isEmpty(etPhoneCodeInput.getText()) || etPhoneCodeInput.getText().toString().trim().length() < 10) {
            etPhoneCodeInput.setError("Please enter valid Mobile Number");
        } else {
            otpBeingFetchedUIUpdate();
            HttpService.getInstance().getMobileAuthCode(etPhoneCodeInput.getText().toString(), new Callback<BaseResponse<OTPCode>>() {
                @Override
                public void success(final BaseResponse<OTPCode> twystResponse, Response response) {
                    if (twystResponse.isResponse()) {
                        final OTPCode otpCode = twystResponse.getData();
                        mPhoneEntered = etPhoneCodeInput.getText().toString();
                        waitForOTPUIUpdate(otpCode);
                    } else {
                        Toast.makeText(UserVerificationActivity.this, twystResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (twystResponse.getMessage().equalsIgnoreCase("We have already sent you an authentication code.")) {
                            sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, etPhoneCodeInput.getText().toString());
                            sharedPreferences.apply();
                            mPhoneEntered = etPhoneCodeInput.getText().toString();
                            askUserToEnterOTPUIUpdate();
                        } else {
                            numberToEnterUIUpdate();
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    numberToEnterUIUpdate();
                    handleRetrofitError(error);
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
                    mPhoneEntered = otpCode.getPhone();
                    askUserToEnterOTPUIUpdate();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                handleRetrofitError(error);
            }
        });
    }

    private void waitForOTPUIUpdate(OTPCode otpCode) {
        //Waiting for OTP
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_waiting));
        tvVerifyNumberLowerHint.setVisibility(View.GONE);
        tvVerifyNumberResendManually.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setText(getResources().getString(R.string.verify_number_manually));
        tvVerifyNumberGoLayout.setVisibility(View.VISIBLE);
        verifyNumberGo.setEnabled(false);
        verifyNumberProgressBar.setVisibility(View.VISIBLE);
        tvVerifyNumberGoText.setBackground(null);
        tvVerifyNumberResendManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askUserToEnterOTPUIUpdate();
            }
        });

        myRunnable = new MyRunnable(otpCode);
        handler.postDelayed(myRunnable, 1000);
        hideSnackbar();
    }

    private void numberToEnterUIUpdate() {
        //Enter your number
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_enter_phone));
        etPhonePre.setVisibility(View.VISIBLE);
        etPhoneCodeInput.setHint(getResources().getString(R.string.verify_number_phone_hint));
        tvVerifyNumberGoLayout.setVisibility(View.VISIBLE);
        tvVerifyNumberGoText.setBackground(getResources().getDrawable(R.drawable.checkout_arrow));
        ivCorrectSymbolVerifyNumber.setBackground(getResources().getDrawable(R.drawable.unchecked));
        verifyNumberProgressBar.setVisibility(View.GONE);
        tvVerifyNumberLowerHint.setVisibility(View.INVISIBLE);
        tvVerifyNumberResendManually.setVisibility(View.INVISIBLE);
        etPhoneCodeInput.setEnabled(true);
        etPhoneCodeInput.requestFocus();

        verifyNumberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchOTP();
            }
        });
        verifyNumberGo.setEnabled(true);
        hideSnackbar();

        focusShowKeyBoard(etPhoneCodeInput);
    }

    private void otpBeingFetchedUIUpdate() {
        etPhoneCodeInput.setError(null);
        etPhoneCodeInput.setEnabled(false);
        verifyNumberProgressBar.setVisibility(View.VISIBLE);
        tvVerifyNumberGoText.setBackground(null);
        tvVerifyNumberLowerHint.setVisibility(View.INVISIBLE);
        tvVerifyNumberResendManually.setVisibility(View.INVISIBLE);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_fetch));
        verifyNumberGo.setEnabled(false);
    }

    private void hideSoftKeyBoard(final View view) {
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 50);
    }

    @Override
    protected void onResume(){
        super.onResume();
        hideSoftKeyBoard(findViewById(R.id.card_verify_number));
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyBoard(v);
//                    v.requestFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void numberVerifiedUIUpdate() {
        etPhoneCodeInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        etPhoneCodeInput.setText("+91-" + getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_USER_PHONE, ""));
        etPhoneCodeInput.setEnabled(false);
        etPhonePre.setVisibility(View.GONE);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_verified));
        tvVerifyNumberGoLayout.setVisibility(View.GONE);
        tvVerifyNumberResendManually.setVisibility(View.INVISIBLE);
        tvVerifyNumberLowerHint.setVisibility(View.INVISIBLE);
        ivCorrectSymbolVerifyNumber.setBackground(getResources().getDrawable(R.drawable.checked));
        btnSubmit.setEnabled(true);
        hideSnackbar();
    }

    private void askUserToEnterOTPUIUpdate() {
        handler.removeCallbacks(myRunnable);
        etPhoneCodeInput.setEnabled(true);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_enter_code));
        etPhonePre.setVisibility(View.INVISIBLE);
        etPhoneCodeInput.setText("");
        etPhoneCodeInput.setHint(getResources().getString(R.string.verify_number_code_hint));
        tvVerifyNumberLowerHint.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setText(getResources().getString(R.string.verify_number_resend));
        tvVerifyNumberResendManually.setEnabled(true);
        tvVerifyNumberGoText.setVisibility(View.VISIBLE);
        tvVerifyNumberGoText.setBackground(getResources().getDrawable(R.drawable.checkout_arrow));
        tvVerifyNumberGoText.setText("");
        verifyNumberProgressBar.setVisibility(View.GONE);
        hideSnackbar();
        verifyNumberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOTP(etPhoneCodeInput.getText().toString());
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

        focusShowKeyBoard(etPhoneCodeInput);
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
                    mPhoneEntered = otpCode.getPhone();
                    // Couldn't fetch OTP, ask user to input
                    askUserToEnterOTPUIUpdate();
                }

            }
        }
    }

    private void resendCode() {
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_fetch));
        verifyNumberProgressBar.setVisibility(View.VISIBLE);
        etPhoneCodeInput.setEnabled(false);
        verifyNumberGo.setEnabled(false);
        tvVerifyNumberGoLayout.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setEnabled(false);
        tvVerifyNumberGoText.setVisibility(View.INVISIBLE);

        HttpService.getInstance().getMobileAuthCode(mPhoneEntered, new Callback<BaseResponse<OTPCode>>() {
            @Override
            public void success(BaseResponse<OTPCode> baseResponse, Response response) {
                askUserToEnterOTPUIUpdate();
                if (baseResponse.isResponse()) {
                    Toast.makeText(UserVerificationActivity.this, "Code resent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("error message", "" + baseResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                askUserToEnterOTPUIUpdate();
                handleRetrofitError(error);
            }
        });
    }

    private void submitOTP(final String otpEntered) {
        if (TextUtils.isEmpty(otpEntered) || otpEntered.trim().length() < 4) {
            etPhoneCodeInput.setError("Invalid code!");
        } else {
            etPhoneCodeInput.setError(null);

            etPhoneCodeInput.setEnabled(false);
            verifyNumberProgressBar.setVisibility(View.VISIBLE);
            tvVerifyNumberGoText.setBackground(null);
            tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_verify));
            verifyNumberGo.setEnabled(false);
            tvVerifyNumberResendManually.setEnabled(false);

            HttpService.getInstance().userAuthToken(otpEntered, mPhoneEntered, new Callback<BaseResponse<AuthToken>>() {
                @Override
                public void success(BaseResponse<AuthToken> baseResponse, Response response) {
                    if (baseResponse.isResponse()) {
                        AuthToken authToken = baseResponse.getData();
                        saveUserToken(authToken.getToken());
                        sharedPreferences.putString(AppConstants.PREFERENCE_USER_PHONE, mPhoneEntered);
                        sharedPreferences.commit();
                        numberVerifiedUIUpdate();
                    } else {
                        askUserToEnterOTPUIUpdate();
                        Toast.makeText(UserVerificationActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    handleRetrofitError(error);
                    askUserToEnterOTPUIUpdate();
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

    public void hideSnackbar() {
        SnackbarManager.dismiss();
    }

    private void showDiscoverScreen() {
        sharedPreferences.putBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, true);
        sharedPreferences.commit();

        Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("setChildNo");
        intent.putExtra("Search", false);
        startActivity(intent);
        finish();
    }

    private void updateUserEmail() {
        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String token = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");
        String deviceId = null;

        deviceId = prefs.getString(AppConstants.PREFERENCE_REGISTRATION_ID, "");

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        final String email;
        if (TextUtils.isEmpty(socialEmail)) {
            email = "";
        } else {
            email = socialEmail;
        }
        city = prefs.getString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER, "");
        Log.i("dob", dob + " firstName" + firstName + " lastName" + lastName + " middleName" + middleName + " city" + city + " image" + userImage);
        String facebookUri = null;
        String googleplusUri = null;
        if (source.equalsIgnoreCase("FACEBOOK")) {
            facebookUri = linkUri;

        } else if (source.equalsIgnoreCase("GOOGLE")) {
            googleplusUri = linkUri;
        }

        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setEmail(email);
        updateProfile.setImage(userImage);
        updateProfile.setFname(firstName);
        updateProfile.setMname(middleName);
        updateProfile.setLname(lastName);
        updateProfile.setCity(city);
        updateProfile.setId(id);
        updateProfile.setSource(source);
        updateProfile.setFacebookUri(facebookUri);
        updateProfile.setGoogleplusUri(googleplusUri);
        updateProfile.setDeviceId(deviceId);
        updateProfile.setVersion(Utils.getbuildVersionStringBuilder().toString());
        updateProfile.setDevice(android.os.Build.DEVICE);
        updateProfile.setModel(android.os.Build.MODEL);
        updateProfile.setProduct(android.os.Build.PRODUCT);

        HttpService.getInstance().updateProfile(token, updateProfile, new Callback<BaseResponse<ProfileUpdate>>() {
            @Override
            public void success(BaseResponse<ProfileUpdate> loginDataBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (loginDataBaseResponse.isResponse()) {
                    final String code = prefs.getString(AppConstants.PREFERENCE_USER_REFERRAL, "");
                    if (!TextUtils.isEmpty(code)) {
                        postReferral(token, code);
                    } else {
                        saveSocialFriendListLocally(token);
                    }

                } else {
                    Log.d(getTagName(), "" + loginDataBaseResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });
    }

    private void postReferral(final String token, final String code) {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        Referral referral = new Referral();
        ReferralMeta referralMeta = new ReferralMeta();
        referralMeta.setReferralCode(code);
        referralMeta.setSource(source);
        referral.setReferralMeta(referralMeta);

        HttpService.getInstance().postReferral(token, referral, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                twystProgressHUD.dismiss();
                saveSocialFriendListLocally(token);
//                Toast.makeText(LoginActivity.this,"Referrer code "+"( "+code+" )"+" used successfully!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                handleRetrofitError(error);
            }
        });
    }

    private void saveSocialFriendListLocally(String token) {
        friend = new Friend();
        friend.setSource(source);
        friend.setList(friendsList);

        Gson gson = new Gson();
        String json = gson.toJson(friend);

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(AppConstants.PREFERENCE_FRIEND_LIST, json);
        if (sharedPreferences.commit()) {
            showDiscoverScreen();
        }

    }


    public void facebookLogin() {

        Collection<String> permissions = Arrays.asList("email", "user_friends", "public_profile", "user_birthday");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(getTagName(), "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        socialEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

        System.out.println("LoginActivity.onConnected google email : " + etVerifyEmail);

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personFullName = currentPerson.getDisplayName();
                String personName = currentPerson.getName().getGivenName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                linkUri = currentPerson.getUrl();
                Log.i("LoginActivity", "Name: " + personFullName
                        + ", Image: " + personPhotoUrl);

                userImage = personPhotoUrl;
                firstName = personName;
                middleName = currentPerson.getName().getMiddleName();
                lastName = currentPerson.getName().getFamilyName();
                dob = currentPerson.getBirthday();
                id = currentPerson.getId();
                sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                sharedPreferences.putBoolean(AppConstants.PREFERENCE_IS_GOOGLE_CONNECTED, true);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, personPhotoUrl);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, personName);
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, personFullName);
                sharedPreferences.apply();
//

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    friendsList = new ArrayList<Friend.Friends>();
                    try {
                        int count = personBuffer.getCount();
                        for (int i = 0; i < count; i++) {
                            Log.d(getTagName(), "Person " + i + " name: " + personBuffer.get(i).getDisplayName() + " - id: " + personBuffer.get(i).getId());
                            friends = new Friend.Friends();
                            friends.setId(personBuffer.get(i).getId());
                            friends.setName(personBuffer.get(i).getDisplayName());
                            friends.setPhone(null);
                            friendsList.add(friends);
                        }

                        updateUserEmail();

                    } finally {
                        personBuffer.close();
                    }


                } else {
                    Log.e(getTagName(), "Error while getting friends from G+");
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(getTagName(), "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(getTagName(), "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}
