package com.twyst.app.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.DebugLogQueue;
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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.AuthToken;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.ContainerHolderSingleton;
import com.twyst.app.android.model.Friend;
import com.twyst.app.android.model.OTPCode;
import com.twyst.app.android.model.ProfileUpdate;
import com.twyst.app.android.model.Referral;
import com.twyst.app.android.model.ReferralMeta;
import com.twyst.app.android.model.UpdateProfile;
import com.twyst.app.android.model.UserProfile;
import com.twyst.app.android.model.order.Coords;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.LocationFetchUtil;
import com.twyst.app.android.util.PermissionUtil;
import com.twyst.app.android.util.PhoneBookContacts;
import com.twyst.app.android.util.SharedPreferenceSingleton;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anshul on 1/18/2016.
 */
public class PreMainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationFetchUtil.LocationFetchResultCodeListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private AddressDetailsLocationData mAddressDetailsLocationData;
    private LocationFetchUtil locationFetchUtil;
    private Location mLocation;
    private TwystProgressHUD twystProgressHUD = null;
    private static final int REQUEST_CONTATCS = 0;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SMS = 3;
    private static final String TAG = "PreMainActivity";
    private boolean isAddressesSynced = false;

    // User Verification Variables
    //Submit button
    View btnSubmit;

    // Verify Number
    private boolean isNumberVerified = false;
    @Bind(R.id.verify_number_hint)
    TextView tvVerifyNumberHint;
    @Bind(R.id.verify_number_phone_pre)
    EditText etPhonePre;
    @Bind(R.id.verify_number_phone_code)
    EditText etPhoneCodeInput;
    @Bind(R.id.verify_number_go)
    View verifyNumberGo;
    @Bind(R.id.verify_number_go_text)
    TextView tvVerifyNumberGoText;
    @Bind(R.id.tv_register_line1)
    TextView tvRegister1;
    @Bind(R.id.tv_register_line2)
    TextView tvRegister2;

    @Bind(R.id.verify_number_progress_bar)
    CircularProgressBar verifyNumberProgressBar;
    @Bind(R.id.verify_number_lower_hint)
    TextView tvVerifyNumberLowerHint;
    @Bind(R.id.verify_number_resend_enter_manually)
    TextView tvVerifyNumberResendManually;
    @Bind(R.id.verify_number_go_layout)
    RelativeLayout tvVerifyNumberGoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);
        ButterKnife.bind(this);
        PermissionUtil askPermission = PermissionUtil.getInstance();
        startInitialAnimation();
        splashCode();
    }

    private void startInitialAnimation() {
        final ImageView app_background = (ImageView) findViewById(R.id.app_background_iv);
        final View getRegisteredTV = (View) findViewById(R.id.ll_get_registered);
        final Animation zoomInBackground = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        final Animation fadeInGetRegistered = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        app_background.startAnimation(zoomInBackground);
        getRegisteredTV.startAnimation(fadeInGetRegistered);
    }

    // Choose Location Variables
    private List<AddressDetailsLocationData> addressList = new ArrayList<AddressDetailsLocationData>();
    private com.twyst.app.android.adapters.SimpleArrayAdapter adapter = null;
    private ListView listViewSavedLocations;
    private boolean isSaveLocationClicked = false;

    //Choose Location Layout
    private void showChooseLocationLayout() {
        synAddressFromProfile();
        startChooseLocationAnimation();

        PermissionUtil.getInstance().approveLocation(PreMainActivity.this, false);

        findViewById(R.id.ll_terms_of_use).setVisibility(View.GONE);
        findViewById(R.id.layout_choose_location).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_user_verification).setVisibility(View.GONE);
        LinearLayout linLayCurrentLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_current);
        LinearLayout linlaySavedLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_saved);
        LinearLayout linlayAdNewLocation = (LinearLayout) findViewById(R.id.linlay_choose_location_add_new);
        linLayCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.getInstance().approveLocation(PreMainActivity.this, false)) {
                    twystProgressHUD = TwystProgressHUD.show(PreMainActivity.this, false, null);
                    locationFetchUtil = new LocationFetchUtil(PreMainActivity.this);
                    locationFetchUtil.requestLocation(true);
                }
            }
        });

        listViewSavedLocations = (ListView) findViewById(R.id.lv_saved_locations);
        adapter = new com.twyst.app.android.adapters.SimpleArrayAdapter(PreMainActivity.this, addressList, null);
        listViewSavedLocations.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewSavedLocations.setAdapter(adapter);
        final RelativeLayout loaderRow = (RelativeLayout) findViewById(R.id.loader_row);
        loaderRow.setVisibility(View.GONE);

        final SharedPreferenceSingleton preference = SharedPreferenceSingleton.getInstance();
        addressList = preference.getAddresses();
        linlaySavedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSaveLocationClicked) {
                    isSaveLocationClicked = true;
                    int minNoOfRows = 3;
                    if (!isAddressesSynced) {
                        loaderRow.setVisibility(View.VISIBLE);
                        minNoOfRows = 2;
                    }

                    addressList = preference.getAddresses();
                    if (addressList != null && addressList.size() > 0) {
                        adapter.clear();
                        adapter.addAll(addressList);

                        if (adapter.getCount() > minNoOfRows) {
                            View item = adapter.getView(0, null, listViewSavedLocations);
                            item.measure(0, 0);
                            ViewGroup.LayoutParams params = listViewSavedLocations.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = (int) ((minNoOfRows + 0.5) * item.getMeasuredHeight());
                            listViewSavedLocations.requestLayout();
                        }

                        adapter.notifyDataSetChanged();
                        listViewSavedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                AddressDetailsLocationData chosenLocation = addressList.get(position);
                                preference.saveCurrentUsedLocation(chosenLocation);
                                preference.setSaveLocationClicked(true);

                                Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_SAVED);
                                startActivity(intent);
                            }
                        });
                        listViewSavedLocations.setVisibility(View.VISIBLE);
                    } else {
                        if (isAddressesSynced) {
                            findViewById(R.id.no_saved_address_row).setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    isSaveLocationClicked = false;
                    listViewSavedLocations.setVisibility(View.GONE);
                    findViewById(R.id.no_saved_address_row).setVisibility(View.GONE);
                    findViewById(R.id.loader_row).setVisibility(View.GONE);

                }
            }
        });


        linlayAdNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreMainActivity.this, AddressMapActivity.class);
                intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
                startActivity(intent);
            }
        });

        TextView tvSkipLocation = (TextView) findViewById(R.id.tv_skip_location);
        tvSkipLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.setSkipLocationClicked(true);
                Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_SKIPPED);
                startActivity(intent);
            }
        });
    }

    private void startEndAnimation() {
        final View deliveryLocationTV = (View) findViewById(R.id.ll_delivery_location);
        final View locations = (View) findViewById(R.id.ll_locations);
        final Animation fadeOutDeliveryLocation = AnimationUtils.loadAnimation(PreMainActivity.this, R.anim.fade_out);
        final Animation exitLocations = AnimationUtils.loadAnimation(PreMainActivity.this, R.anim.exit_to_right);
        deliveryLocationTV.setAnimation(fadeOutDeliveryLocation);
        locations.setAnimation(exitLocations);
    }

    private void startChooseLocationAnimation() {
        final View deliveryLocationTV = (View) findViewById(R.id.ll_delivery_location);
        final Animation fadeInDeliveryLocation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        deliveryLocationTV.setAnimation(fadeInDeliveryLocation);

        final View locations = (View) findViewById(R.id.ll_locations);
        final Animation enterLocations = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        final Animation exitLocations = AnimationUtils.loadAnimation(this, R.anim.exit_to_right);
        locations.setAnimation(enterLocations);
    }


    MyRunnable myRunnable;

    private SharedPreferences.Editor sharedPreferences;
    private String mPhoneEntered;
    private String otpCodeReaded;

    //Verify Email
    private EditText etVerifyName;

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
    private String socialEmail = "";
    private String mPossibleEmail = "";
    private String socialName;
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

    private void openTermsOfUseURL() {
        String url = "http://docs.google.com/gview?embedded=true&url=" + AppConstants.HOST + "/terms_of_use.pdf";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    // Show User Verification Layout
    private void showUserVerificationLayout() {
        startUserVerficationAnimation();

        findViewById(R.id.tv_terms_of_use).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTermsOfUseURL();
            }
        });
        findViewById(R.id.layout_choose_location).setVisibility(View.GONE);
        findViewById(R.id.layout_user_verification).setVisibility(View.VISIBLE);
        setupVerifyEmail();
        setupSubmitButton();
        setupSignup();
        setupVerifyNumber();
        setupUI(findViewById(R.id.layout_user_verification));
    }

    private void startUserVerficationAnimation() {
        final View userVerification = (View) findViewById(R.id.card_verify_number);
        final Animation enterUserVerification = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        userVerification.startAnimation(enterUserVerification);
    }


    private void setupSubmitButton() {
        btnSubmit = findViewById(R.id.submit);
        btnSubmit.setEnabled(isNumberVerified);
        final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableButtonsRunnable(view);

                if (TextUtils.isEmpty(etVerifyEmail.getText().toString()) || !Pattern.matches(EMAIL_REGEX, etVerifyEmail.getText().toString())) {
                    etVerifyEmail.setError("Invalid email");
                } else if (TextUtils.isEmpty(etVerifyName.getText().toString())) {
                    etVerifyName.setError("Please enter your Name");
                } else {
                    etVerifyName.setError(null);
                    etVerifyEmail.setError(null);
                    socialEmail = etVerifyEmail.getText().toString();
                    socialName = etVerifyName.getText().toString();
                    source = "phonebook";
                    userImage = "";
                    firstName = etVerifyName.getText().toString();
                    middleName = "";
                    lastName = "";
                    dob = "";
                    id = "";
                    friendsList = PhoneBookContacts.getInstance().getPhoneContactList();
                    sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_PIC, "");
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_NAME, etVerifyName.getText().toString());
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_FULL_NAME, (etVerifyName.getText().toString()));
                    sharedPreferences.putString(AppConstants.PREFERENCE_USER_EMAIL, (etVerifyEmail.getText().toString()));
                    sharedPreferences.apply();
                    updateUserEmail();
                }
            }
        });
        btnSubmit.setEnabled(isNumberVerified);
    }

    private void setupVerifyEmail() {
        etVerifyName = (EditText) findViewById(R.id.et_verify_name);
        etVerifyEmail = (EditText) findViewById(R.id.et_verify_email);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(PreMainActivity.this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                mPossibleEmail = account.name;
                etVerifyEmail.setText(mPossibleEmail);
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
        findViewById(R.id.fbLogin).setEnabled(false);
        findViewById(R.id.gPlusLogin).setEnabled(false);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    id = object.getString("id");
                                    socialEmail = object.getString("email");

                                    if (TextUtils.isEmpty(socialEmail)) {
                                        socialEmail = mPossibleEmail;
                                    }

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
//                request.executeAsync();

                GraphRequestBatch batch = new GraphRequestBatch(request, GraphRequest.newMyFriendsRequest(accessToken, new GraphRequest.GraphJSONArrayCallback() {
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
                System.out.println("PreMainActivity.onCancel called");
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
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_EMAIL, socialEmail);
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
    }

    @OnClick(R.id.gPlusLogin)
    protected void onGooglePlusLoginClick(View v) {
        source = "GOOGLE";
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
        enableButtonsRunnable(v);
    }

    @OnClick(R.id.fbLogin)
    protected void onFacebookLoginClick(View v) {
        source = "FACEBOOK";
        facebookLogin();
        enableButtonsRunnable(v);
    }


    private void enableButtonsRunnable(View v) {
        findViewById(R.id.fbLogin).setEnabled(false);
        findViewById(R.id.gPlusLogin).setEnabled(false);
        btnSubmit.setEnabled(false);

        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.fbLogin).setEnabled(true);
                findViewById(R.id.gPlusLogin).setEnabled(true);
                btnSubmit.setEnabled(isNumberVerified);
            }
        }, 500);
    }

    private void setupVerifyNumber() {
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
                        Toast.makeText(PreMainActivity.this, twystResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (!PermissionUtil.getInstance().approveSMS(PreMainActivity.this, true)) return false;

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
            Toast.makeText(PreMainActivity.this, "Reading Verification SMS", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(PreMainActivity.this, "Number Verified!", Toast.LENGTH_SHORT).show();
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

    private void hideSoftKeyBoard(final View view) {
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 50);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
        hideSoftKeyBoard(findViewById(R.id.card_verify_number));
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
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

    private void numberToEnterUIUpdate() {
        findViewById(R.id.card_verify_number).setVisibility(View.VISIBLE);
        findViewById(R.id.card_signup).setVisibility(View.GONE);
        //Enter your number
        //Enter your number
        //        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_enter_phone));
        tvVerifyNumberHint.setVisibility(View.GONE);

        etPhonePre.setVisibility(View.VISIBLE);
        etPhoneCodeInput.setHint(getResources().getString(R.string.verify_number_phone_hint));
        verifyNumberProgressBar.setVisibility(View.GONE);
        tvVerifyNumberLowerHint.setVisibility(View.GONE);
        tvVerifyNumberResendManually.setVisibility(View.GONE);


        etPhoneCodeInput.setEnabled(true);
        etPhoneCodeInput.requestFocus();
        verifyNumberGo.setBackground(getResources().getDrawable(R.drawable.verify_number_arrow));

        verifyNumberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.getInstance().approveSMS(PreMainActivity.this, false)) {
                    fetchOTP();
                }
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
        tvVerifyNumberLowerHint.setVisibility(View.GONE);
        tvVerifyNumberResendManually.setVisibility(View.GONE);
        tvVerifyNumberHint.setVisibility(View.VISIBLE);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_fetch));
        verifyNumberGo.setBackground(null);
        verifyNumberGo.setEnabled(false);
    }

    private void waitForOTPUIUpdate(OTPCode otpCode) {
        //Waiting for OTP
        tvVerifyNumberHint.setVisibility(View.VISIBLE);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_fetch));
        tvVerifyNumberLowerHint.setVisibility(View.GONE);
        tvVerifyNumberResendManually.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setText(getResources().getString(R.string.verify_number_manually));
        tvVerifyNumberGoLayout.setVisibility(View.VISIBLE);
        verifyNumberGo.setEnabled(false);
        verifyNumberProgressBar.setVisibility(View.INVISIBLE);
        tvVerifyNumberGoText.setBackground(null);
        verifyNumberGo.setBackground(getResources().getDrawable(R.drawable.verify_number_circle));
        tvVerifyNumberResendManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askUserToEnterOTPUIUpdate();
            }
        });

        tvVerifyNumberGoText.setText(String.valueOf(AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS));
        myRunnable = new MyRunnable(otpCode);
        handler.postDelayed(myRunnable, 1000);
        hideSnackbar();
    }

    private void askUserToEnterOTPUIUpdate() {
        handler.removeCallbacks(myRunnable);
        etPhoneCodeInput.setEnabled(true);
        tvVerifyNumberHint.setVisibility(View.GONE);
        //        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_enter_code));
        etPhonePre.setVisibility(View.INVISIBLE);
        etPhoneCodeInput.setText("");
        etPhoneCodeInput.setHint(getResources().getString(R.string.verify_number_code_hint));
        tvVerifyNumberLowerHint.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setVisibility(View.VISIBLE);
        verifyNumberGo.setBackground(getResources().getDrawable(R.drawable.verify_number_arrow));
        tvVerifyNumberResendManually.setText(getResources().getString(R.string.verify_number_resend));
        tvVerifyNumberResendManually.setEnabled(true);
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

    private void numberVerifiedUIUpdate() {
        startSignUpAnimation();
        findViewById(R.id.card_verify_number).setVisibility(View.GONE);
        findViewById(R.id.card_signup).setVisibility(View.VISIBLE);
        tvRegister2.setVisibility(View.GONE);
        tvRegister1.setText(getResources().getString(R.string.complete_signup));
        isNumberVerified = true;
        etPhoneCodeInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        etPhoneCodeInput.setText("+91-" + getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(AppConstants.PREFERENCE_USER_PHONE, ""));
        etPhoneCodeInput.setEnabled(false);
        etPhonePre.setVisibility(View.GONE);
        tvVerifyNumberHint.setVisibility(View.VISIBLE);
        tvVerifyNumberHint.setText(getResources().getString(R.string.verify_number_hint_verified));
        tvVerifyNumberGoLayout.setVisibility(View.GONE);
        tvVerifyNumberResendManually.setVisibility(View.INVISIBLE);
        tvVerifyNumberLowerHint.setVisibility(View.INVISIBLE);
        hideSnackbar();
        btnSubmit.setEnabled(isNumberVerified);
        findViewById(R.id.fbLogin).setEnabled(true);
        findViewById(R.id.gPlusLogin).setEnabled(true);
    }

    private void startSignUpAnimation() {
        final View getRegisteredTV = (View) findViewById(R.id.ll_get_registered);
        final View signUp = (View) findViewById(R.id.card_signup);
        final Animation enterSignUp = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        final Animation fadeInGetRegistered = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        getRegisteredTV.startAnimation(fadeInGetRegistered);
        signUp.setAnimation(enterSignUp);
    }

    final Handler handler = new Handler();

    @Override
    public void onReceiveAddress(int resultCode, Address address) {
        SharedPreferenceSingleton sharedPreferenceSingleton = SharedPreferenceSingleton.getInstance();
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            int index = address.getMaxAddressLineIndex();
//            String mAddressOutput = "";
//            for (int i = 0; i < index; i++) {
//                mAddressOutput += address.getAddressLine(i);
//                mAddressOutput += ", ";
//            }
//            mAddressOutput += address.getAddressLine(index);
//            mAddressDetailsLocationData.setLine1(mAddressOutput);
            if (index >= 0) mAddressDetailsLocationData.setLine1(address.getAddressLine(0));
            if (index >= 1) mAddressDetailsLocationData.setLine2(address.getAddressLine(1));
//            if (index >= 2)mAddressDetailsLocationData.setLandmark(address.getAddressLine(2));
            if (address.getSubAdminArea() != null) {
                mAddressDetailsLocationData.setCity(address.getSubAdminArea()); // to be checked
            } else {
                mAddressDetailsLocationData.setCity(address.getLocality()); // to be checked
            }
            mAddressDetailsLocationData.setState(address.getAdminArea()); // to be checked

            sharedPreferenceSingleton.saveCurrentUsedLocation(mAddressDetailsLocationData);
            if (twystProgressHUD != null) {
                twystProgressHUD.dismiss();
            }
            Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_CURRENT);
            startActivity(intent);
        } else {
//            Toast.makeText(PreMainActivity.this, "onReceiveAddressError : " + resultCode, Toast.LENGTH_LONG).show();
            Toast.makeText(PreMainActivity.this, R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            if (twystProgressHUD != null) {
                twystProgressHUD.dismiss();
            }
//            mAddressDetailsLocationData.setAddress("Unnamed Address");
//            mAddressDetailsLocationData.setNeighborhood("Unnamed Address");
//            mAddressDetailsLocationData.setLandmark("Unnamed Address");
//            sharedPreferenceSingleton.saveCurrentUsedLocation(mAddressDetailsLocationData);
//            Intent intent = new Intent(PreMainActivity.this, MainActivity.class);
//            intent.putExtra(AppConstants.CHOOSE_LOCATION_OPTION_SELECTED, AppConstants.CHOOSE_LOCATION_OPTION_CURRENT);
//            startActivity(intent);
            Toast.makeText(PreMainActivity.this, R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PreMainActivity.this, AddressMapActivity.class);
            intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
            startActivity(intent);
        }
    }

    @Override
    public void onReceiveLocation(int resultCode, Location location) {
        if (resultCode == AppConstants.SHOW_CURRENT_LOCATION) {
            mLocation = location;
            mAddressDetailsLocationData = new AddressDetailsLocationData();
            Coords coords = new Coords(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
            mAddressDetailsLocationData.setCoords(coords);
            locationFetchUtil.requestAddress(location, false);
        } else {
            if (twystProgressHUD != null) {
                twystProgressHUD.dismiss();
            }
//            Toast.makeText(PreMainActivity.this, "onReceiveLocationError : " + resultCode, Toast.LENGTH_LONG).show();
            Toast.makeText(PreMainActivity.this, R.string.couldnot_fetch_location, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PreMainActivity.this, AddressMapActivity.class);
            intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
            startActivity(intent);
        }
    }


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
                if (retry < AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS - 1) {
                    handler.postDelayed(this, 1000);
                    retry++;
                    Log.d("retry", "" + retry);
                    tvVerifyNumberGoText.setText(String.valueOf(AppConstants.MAX_WAIT_FOR_SMS_IN_SECONDS - retry));

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
        verifyNumberGo.setBackground(null);
        verifyNumberGo.setEnabled(false);
        tvVerifyNumberGoLayout.setVisibility(View.VISIBLE);
        tvVerifyNumberResendManually.setEnabled(false);
        tvVerifyNumberGoText.setVisibility(View.INVISIBLE);

        HttpService.getInstance().getMobileAuthCode(mPhoneEntered, new Callback<BaseResponse<OTPCode>>() {
            @Override
            public void success(BaseResponse<OTPCode> baseResponse, Response response) {
                askUserToEnterOTPUIUpdate();
                if (baseResponse.isResponse()) {
                    Toast.makeText(PreMainActivity.this, "Code resent successfully", Toast.LENGTH_SHORT).show();
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
            verifyNumberGo.setBackground(null);
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
                        Toast.makeText(PreMainActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void userVerifiedShowChooseLocation() {
        sharedPreferences.putBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, true);
        sharedPreferences.commit();

        showChooseLocationLayout();
    }

    private void updateUserEmail() {
        if (TextUtils.isEmpty(socialEmail)) {
            String fullName = firstName + " " + lastName;
            etVerifyName.setText(fullName.trim());
            Toast.makeText(PreMainActivity.this, "Please enter email address to proceed.", Toast.LENGTH_LONG).show();
            return;
        }

        HttpService.getInstance().getSharedPreferences().edit().putString(AppConstants.PREFERENCE_USER_EMAIL, socialEmail).apply();

        final String token = HttpService.getInstance().getSharedPreferences().getString(AppConstants.PREFERENCE_USER_TOKEN, "");
        String deviceId = HttpService.getInstance().getSharedPreferences().getString(AppConstants.PREFERENCE_REGISTRATION_ID, "");

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        city = HttpService.getInstance().getSharedPreferences().getString(AppConstants.PREFERENCE_LOCALITY_SHOWN_DRAWER, "");
        Log.i("dob", dob + " firstName" + firstName + " lastName" + lastName + " middleName" + middleName + " city" + city + " image" + userImage);
        String facebookUri = null;
        String googleplusUri = null;
        if (source.equalsIgnoreCase("FACEBOOK")) {
            facebookUri = linkUri;

        } else if (source.equalsIgnoreCase("GOOGLE")) {
            googleplusUri = linkUri;
        }

        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setEmail(socialEmail);
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
                    final String code = HttpService.getInstance().getSharedPreferences().getString(AppConstants.PREFERENCE_USER_REFERRAL, "");
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
            userVerifiedShowChooseLocation();
        }

    }

    public void facebookLogin() {
        Collection<String> permissions = Arrays.asList("email", "user_friends", "public_profile", "user_birthday");
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    locationFetchUtil.requestLocation(false);
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
                    if (twystProgressHUD != null) {
                        twystProgressHUD.dismiss();
                    }
                    Intent intent = new Intent(PreMainActivity.this, AddressMapActivity.class);
                    intent.putExtra(AppConstants.FROM_CHOOSE_ACTIVITY_TO_MAP, true);
                    startActivity(intent);
                    break;
            }
        } else {
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

        if (TextUtils.isEmpty(socialEmail)) {
            socialEmail = mPossibleEmail;
        }

        System.out.println("PreMainActivity.onConnected google email : " + etVerifyEmail);

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
                sharedPreferences.putString(AppConstants.PREFERENCE_USER_EMAIL, socialEmail);
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


    // App Starts, Splash Screen Code

    private GoogleCloudMessaging googleCloudMessaging;
    private static final String CONTAINER_ID = "GTM-PNRJQ9";
    private static final long TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS = 2000;

    private void splashCode() {
        setupAppsFlyer();

        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(AppConstants.PREFERENCE_IS_FIRST_RUN, true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_PUSH_ENABLED, true).apply();
            prefs.edit().putBoolean(AppConstants.PREFERENCE_IS_FIRST_RUN, false).apply();
        }

        if (PermissionUtil.getInstance().approveContacts(PreMainActivity.this, false)) {
            new FetchContact().execute();
        }

        if (checkPlayServices()) {
            buildGoogleApiClient();
            googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
            registerInBackground();
            downloadContainer();
            handler.postDelayed(timedTask, TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS + 1000);
        } else {
            Log.i(getClass().getSimpleName(), "No valid Google Play Services APK found.");
        }


        SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.remove(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED).commit();
        sharedPreferences.remove(AppConstants.PREFERENCE_PARAM_SEARCH_QUERY).commit();

        boolean phoneVerified = prefs.getBoolean(AppConstants.PREFERENCE_PHONE_VERIFIED, false);
        boolean emailVerified = prefs.getBoolean(AppConstants.PREFERENCE_EMAIL_VERIFIED, false);

        if (phoneVerified && emailVerified) {
            showChooseLocationLayout();
        } else {
            showUserVerificationLayout();
        }
    }

    private ContainerHolder mContainerHolder = null;

    private void setContainerHolder(ContainerHolder containerHolder) {
        this.mContainerHolder = containerHolder;
    }

    private Runnable timedTask = new Runnable() {

        @Override
        public void run() {
            setContainerHolder(ContainerHolderSingleton.getContainerHolder());
            if (mContainerHolder != null) {
                mContainerHolder.refresh();
            }
            updateConstantsfromContainer();
        }
    };


    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void downloadContainer() {
        TagManager tagManager = TagManager.getInstance(this);

        // Modify the log level of the logger to print out not only
        // warning and error messages, but also verbose, debug, info messages.
//        tagManager.setVerboseLoggingEnabled(true);

        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(CONTAINER_ID,
                        R.raw.gtm);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the 2-second timeout occurs
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("PreMainActivity", "failure loading container");
//                    displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                ContainerLoadedCallback.registerCallbacksForContainer(container);
                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
//                startMainActivity();
            }
        }, TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }


    private void registerInBackground() {
//        if (AppConstants.IS_DEVELOPMENT) {
//            return;
//        }

        final SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(prefs.getString(AppConstants.PREFERENCE_REGISTRATION_ID, ""))) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    final String registrationId = googleCloudMessaging.register(AppConstants.GCM_PROJECT_ID);
                    Log.i(getClass().getSimpleName(), "Device registered, registration ID=" + registrationId);
                    final SharedPreferences prefs = HttpService.getInstance().getSharedPreferences();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(AppConstants.PREFERENCE_REGISTRATION_ID, registrationId);
                    editor.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }

    protected boolean checkPlayServices() {
        if (AppConstants.IS_DEVELOPMENT) {
            return true;
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }


    private void setupAppsFlyer() {
        // Set the Currency
        AppsFlyerLib.setCurrencyCode("USD");

        AppsFlyerLib.setAppsFlyerKey("yezoub3j6KZJt3VPyKoJ2Z");
        AppsFlyerLib.sendTracking(this);

        final String LOG_TAG = "AppsFlyer";
        AppsFlyerLib.registerConversionListener(this, new AppsFlyerConversionListener() {
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                DebugLogQueue.getInstance().push("\nGot conversion data from server");
                for (String attrName : conversionData.keySet()) {
                    Log.d(LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            public void onInstallConversionFailure(String errorMessage) {
                Log.d(LOG_TAG, "error getting conversion data: " + errorMessage);
            }

            public void onAppOpenAttribution(Map<String, String> attributionData) {
                printMap(attributionData);
            }

            public void onAttributionFailure(String errorMessage) {
                Log.d(LOG_TAG, "error onAttributionFailure : " + errorMessage);

            }

            private void printMap(Map<String, String> map) {
                for (String key : map.keySet()) {
                    Log.d(LOG_TAG, key + "=" + map.get(key));
                }

            }
        });
    }

    public class FetchContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            PhoneBookContacts.getInstance().loadContacts(getApplicationContext());
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            registerCallbacksForContainer(container);
        }

        public static void registerCallbacksForContainer(Container container) {
            // Register two custom function call macros to the container.
            container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
            container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
            // Register a custom function call tag to the container.
            container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());
        }
    }

    private static class CustomMacroCallback implements Container.FunctionCallMacroCallback {
        private int numCalls;

        @Override
        public Object getValue(String name, Map<String, Object> parameters) {
            if ("increment".equals(name)) {
                return ++numCalls;
            } else if ("mod".equals(name)) {
                return (Long) parameters.get("key1") % Integer.valueOf((String) parameters.get("key2"));
            } else {
                throw new IllegalArgumentException("Custom macro name: " + name + " is not supported.");
            }
        }
    }

    private static class CustomTagCallback implements Container.FunctionCallTagCallback {
        @Override
        public void execute(String tagName, Map<String, Object> parameters) {
            // The code for firing this custom tag.
            Log.i("SplashActivity", "Custom function call tag :" + tagName + " is fired.");
        }
    }

    private void updateConstantsfromContainer() {
        if (mContainerHolder != null) {
            Double USER_ONE_LOCATION_CHECK_TIME = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_USER_ONE_LOCATION_CHECK_TIME));
            Double DISTANCE_LIMIT = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_DISTANCE_LIMIT));
            Double LOCATION_REQUEST_REFRESH_INTERVAL = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_REQUEST_REFRESH_INTERVAL));
            Double LOCATION_REQUEST_SMALLEST_DISPLACEMENT = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_REQUEST_SMALLEST_DISPLACEMENT));
            Double LOCATION_REQUEST_PRIORITY = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_REQUEST_PRIORITY));
            Double LOCATION_OFFLINE_LIST_MAX_SIZE = (mContainerHolder.getContainer().getDouble(AppConstants.PREFERENCE_LOCATION_OFFLINE_LIST_MAX_SIZE));

            //Earn-burn values update
            Double TWYST_CASH_INVITE_FRIENDS = (mContainerHolder.getContainer().getDouble("invite_friends"));
            Double TWYST_CASH_FOLLOW = (mContainerHolder.getContainer().getDouble("follow"));
            Double TWYST_CASH_LIKE_OFFER = (mContainerHolder.getContainer().getDouble("like_offer"));
            Double TWYST_CASH_CHECKIN_OUTLET_NON_PAYING = (mContainerHolder.getContainer().getDouble("checkin"));
            Double TWYST_CASH_CHECKIN_OUTLET_PAYING = (mContainerHolder.getContainer().getDouble("checkin_is_paying"));
            Double TWYST_CASH_SUBMIT_OFFER = (mContainerHolder.getContainer().getDouble("submit_offer"));
            Double TWYST_CASH_SHARE_OFFER = (mContainerHolder.getContainer().getDouble("share_offer"));
            Double TWYST_CASH_SHARE_OUTLET = (mContainerHolder.getContainer().getDouble("share_outlet"));
            Double TWYST_CASH_SUGGESTION = (mContainerHolder.getContainer().getDouble("suggestion"));
            Double TWYST_CASH_SHARE_CHECKIN = (mContainerHolder.getContainer().getDouble("share_checkin"));
            Double TWYST_CASH_SHARE_REDEMPTION = (mContainerHolder.getContainer().getDouble("share_redemption"));
            Double TWYST_CASH_GRAB = (mContainerHolder.getContainer().getDouble("grab"));
            Double TWYST_CASH_EXTEND = (mContainerHolder.getContainer().getDouble("extend"));
            Double TWYST_CASH_REDEEM = (mContainerHolder.getContainer().getDouble("redeem"));
            Double TWYST_CASH_BUY_CHECKIN = (mContainerHolder.getContainer().getDouble("buy_checkin"));

            //Recharge Handling
            Double RECHARGE_HANDLING_FEE_FACTOR = (mContainerHolder.getContainer().getDouble("recharge_handling_fee_factor"));
            Double RECHARGE_MIN_HANDLING_FEE = (mContainerHolder.getContainer().getDouble("recharge_min_handling_fee"));

            final SharedPreferences.Editor prefsEdit = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            prefsEdit.putInt((AppConstants.PREFERENCE_USER_ONE_LOCATION_CHECK_TIME), USER_ONE_LOCATION_CHECK_TIME.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_DISTANCE_LIMIT), DISTANCE_LIMIT.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_REQUEST_REFRESH_INTERVAL), LOCATION_REQUEST_REFRESH_INTERVAL.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_REQUEST_SMALLEST_DISPLACEMENT), LOCATION_REQUEST_SMALLEST_DISPLACEMENT.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_REQUEST_PRIORITY), LOCATION_REQUEST_PRIORITY.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_LOCATION_OFFLINE_LIST_MAX_SIZE), LOCATION_OFFLINE_LIST_MAX_SIZE.intValue());

            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_INVITE_FRIENDS), TWYST_CASH_INVITE_FRIENDS.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_FOLLOW), TWYST_CASH_FOLLOW.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_LIKE_OFFER), TWYST_CASH_LIKE_OFFER.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_CHECKIN_OUTLET_NON_PAYING), TWYST_CASH_CHECKIN_OUTLET_NON_PAYING.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_CHECKIN_OUTLET_PAYING), TWYST_CASH_CHECKIN_OUTLET_PAYING.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_SUBMIT_OFFER), TWYST_CASH_SUBMIT_OFFER.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_SHARE_OFFER), TWYST_CASH_SHARE_OFFER.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_SHARE_OUTLET), TWYST_CASH_SHARE_OUTLET.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_SUGGESTION), TWYST_CASH_SUGGESTION.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_SHARE_CHECKIN), TWYST_CASH_SHARE_CHECKIN.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_SHARE_REDEMPTION), TWYST_CASH_SHARE_REDEMPTION.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_GRAB), TWYST_CASH_GRAB.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_EXTEND), TWYST_CASH_EXTEND.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_REDEEM), TWYST_CASH_REDEEM.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_TWYST_CASH_BUY_CHECKIN), TWYST_CASH_BUY_CHECKIN.intValue());

            prefsEdit.putInt((AppConstants.PREFERENCE_RECHARGE_HANDLING_FEE_FACTOR), RECHARGE_HANDLING_FEE_FACTOR.intValue());
            prefsEdit.putInt((AppConstants.PREFERENCE_RECHARGE_MIN_HANDLING_FEE), RECHARGE_MIN_HANDLING_FEE.intValue());

            prefsEdit.apply();
        }
    }

    private void synAddressFromProfile() {
        isAddressesSynced = false;
        HttpService.getInstance().getProfile(UtilMethods.getUserToken(PreMainActivity.this), new Callback<BaseResponse<UserProfile>>() {
            @Override
            public void success(final BaseResponse<UserProfile> profileBaseResponse, Response response) {

                if (profileBaseResponse.isResponse()) {
                    UserProfile userProfile = profileBaseResponse.getData();
                    ArrayList<AddressDetailsLocationData> addressList = (ArrayList<AddressDetailsLocationData>) userProfile.getAddressList();
                    ArrayList<AddressDetailsLocationData> savedAddressList = new ArrayList<AddressDetailsLocationData>();
                    for (AddressDetailsLocationData address : addressList) {
                        if (address.getTag() != null && address.getTag() != "") {
                            savedAddressList.add(address);
                        }
                    }
                    SharedPreferenceSingleton.getInstance().saveAddresses(savedAddressList);
                    if (isSaveLocationClicked) {
                        findViewById(R.id.loader_row).setVisibility(View.GONE);
                        if (savedAddressList != null && savedAddressList.size() > 0) {
                            if (adapter.getCount() > 3) {
                                View item = adapter.getView(0, null, listViewSavedLocations);
                                item.measure(0, 0);
                                ViewGroup.LayoutParams params = listViewSavedLocations.getLayoutParams();
                                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                params.height = (int) ((3 + 0.5) * item.getMeasuredHeight());
                                listViewSavedLocations.requestLayout();
                            }
                            adapter.clear();
                            adapter.addAll(savedAddressList);
                            adapter.notifyDataSetChanged();
                        } else {
                            findViewById(R.id.no_saved_address_row).setVisibility(View.VISIBLE);
                        }
                    }
                    isAddressesSynced = true;

                    //save Twyst Cash
                    Utils.setTwystCash(userProfile.getTwystCash());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                findViewById(R.id.loader_row).setVisibility(View.GONE);
                UtilMethods.handleRetrofitError(PreMainActivity.this, error);
                isAddressesSynced = false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for location permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {

            } else {
                Log.i(TAG, "Location permissions were NOT granted.");

                Intent intent = new Intent(PreMainActivity.this, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_LOCATION);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, getResources().getString(R.string.permission_location_rationale));
                startActivity(intent);

            }

        } else if (requestCode == REQUEST_SMS) {
            Log.i(TAG, "Received response for sms permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                fetchOTP();
            } else {
                Log.i(TAG, "SMS permissions were NOT granted.");
//                Intent intent = new Intent(PreMainActivity.this, NoPermissionsActivity.class);
//                intent.putExtra(AppConstants.INTENT_PERMISSION,REQUEST_SMS);
//                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, getResources().getString(R.string.permission_sms_rationale));
//                startActivity(intent);
                fetchOTP();
            }

        } else if (requestCode == REQUEST_CONTATCS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for contacts permission.
            Log.i(TAG, "Received response for Contact permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new FetchContact().execute();
            } else {
                Log.i(TAG, "CONTACT permission was NOT granted.");
//                Intent intent = new Intent(PreMainActivity.this, NoPermissionsActivity.class);
//                intent.putExtra(AppConstants.INTENT_PERMISSION,REQUEST_CONTATCS);
//                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, getResources().getString(R.string.permission_contacts_rationale));
//                startActivity(intent);

            }
            // END_INCLUDE(permission_result)

        }
    }
}
