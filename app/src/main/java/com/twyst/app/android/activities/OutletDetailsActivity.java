package com.twyst.app.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.OutletDetailsAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Data;
import com.twyst.app.android.model.Feedback;
import com.twyst.app.android.model.FeedbackMeta;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.OutletDetailData;
import com.twyst.app.android.model.ShareOutlet;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by satish on 19/06/15.
 */
public class OutletDetailsActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    private OutletDetailsAdapter outletAdapter;
    private TextView outletAddress, outletDistance, outletName;
    private ImageView outletBgImage, placeIcon;
    private RelativeLayout layout;
    private ObservableRecyclerView outletDetailsRecyclerView;
    private ImageView followOutletBtn;
    private static final int REQUEST_CAMERA = 14;
    private static final int SELECT_FILE = 15;
    private ImageView attachImage;
    public String imagePath, fileName, encodedString;
    private String uploadingImage;

    public static int IMAGE_RESULTS = 103;
    private FloatingActionButton submitFab;
    private FloatingActionButton checkinFab;
    private FloatingActionsMenu fabMenu;
    private RelativeLayout obstructor;
    private View feedbackLayout;

    Bitmap bitmap;

    public Outlet outlet;
    private LinearLayout outlet_view_call, outlet_view_follow, outlet_view_map, outlet_view_feedback;
    private View feedbackObstructor2, feedbackObstructor;
    private LinearLayout takePhotoLayout;
    private LinearLayout attachImageLayout;
    private Button submitBtn;
    private TextView editImageBtn;

    @Override
    protected String getTagName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_outlet_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAsChild = true;
        super.onCreate(savedInstanceState);

        if (getIntent().getStringExtra(AppConstants.INTENT_PARAM_OUTLET_ID) != null) {
            String outletId = getIntent().getStringExtra(AppConstants.INTENT_PARAM_OUTLET_ID);
            fetchOutlet(outletId);
        } else {
            outlet = (Outlet) getIntent().getExtras().getSerializable(AppConstants.INTENT_PARAM_OUTLET_OBJECT);
            setup();
        }

    }

    private void fetchOutlet(String outletId) {
        SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String appLocationLatiude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LAT, "");
        final String appLocationLongitude = preferences.getString(AppConstants.PREFERENCE_CURRENT_USED_LNG, "");


        HttpService.getInstance().getOutletDetails(outletId, getUserToken(), appLocationLatiude, appLocationLongitude, new Callback<BaseResponse<OutletDetailData>>() {

            @Override
            public void success(BaseResponse<OutletDetailData> outletBaseResponse, Response response) {
                outlet = outletBaseResponse.getData().getOutlet();

                String twystBucks = outletBaseResponse.getData().getTwystBucks();

                sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_TWYST_BUCK, Integer.parseInt(twystBucks));
                sharedPreferences.commit();
                Log.d("Response", outletBaseResponse.toString());

                setup();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                handleRetrofitError(error);
            }
        });
    }

    private void setup() {
        outletAddress = (TextView) findViewById(R.id.outletAddress);
        outletDistance = (TextView) findViewById(R.id.outletDistance);
        outletName = (TextView) findViewById(R.id.outletName);
        outletBgImage = (ImageView) findViewById(R.id.outletImage);
        placeIcon = (ImageView) findViewById(R.id.placeIcon);
        layout = (RelativeLayout) findViewById(R.id.layout);
        followOutletBtn = (ImageView) findViewById(R.id.followOutletBtn);

        outlet_view_call = (LinearLayout) findViewById(R.id.outlet_view_call);
        outlet_view_follow = (LinearLayout) findViewById(R.id.outlet_view_follow);
        outlet_view_map = (LinearLayout) findViewById(R.id.outlet_view_map);
        outlet_view_feedback = (LinearLayout) findViewById(R.id.outlet_view_feedback);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        takePhotoLayout = (LinearLayout) findViewById(R.id.takePhotoLayout);
        attachImageLayout = (LinearLayout) findViewById(R.id.attachImageLayout);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        editImageBtn = (TextView) findViewById(R.id.editImgButton);

        outletDetailsRecyclerView = (ObservableRecyclerView) findViewById(R.id.outletDetailsRecyclerView);
        outletDetailsRecyclerView.setHasFixedSize(true);
        outletDetailsRecyclerView.setScrollViewCallbacks(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OutletDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        outletDetailsRecyclerView.setLayoutManager(mLayoutManager);

        outlet_view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OutletDetailsActivity.this, MapViewActivity.class);
                intent1.putExtra(AppConstants.INTENT_PARAM_OUTLET_LOCATION_LAT, outlet.getLat());
                intent1.putExtra(AppConstants.INTENT_PARAM_OUTLET_LOCATION_LONG, outlet.getLng());
                intent1.putExtra(AppConstants.INTENT_PARAM_OUTLET_NAME, outlet.getName());
                startActivity(intent1);

                // To open GoogleNavigation
                /*Float latSource = Float.parseFloat(appLocationLatiude);
                Float lonSource = Float.parseFloat(appLocationLongitude);
                Float latDest = Float.parseFloat(outlet.getLat());
                Float lonDest = Float.parseFloat(outlet.getLng());
                String outletName = Uri.encode(outlet.getName());

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
                        latSource, lonSource, "My Location", latDest, lonDest, outletName);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);*/

            }
        });

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        obstructor = (RelativeLayout) findViewById(R.id.obstructor);
        submitFab = (FloatingActionButton) findViewById(R.id.submitFab);
        checkinFab = (FloatingActionButton) findViewById(R.id.checkinFab);

        outletAdapter = new OutletDetailsAdapter();
        outletDetailsRecyclerView.setAdapter(outletAdapter);
        outlet_view_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Do you want to call " + outlet.getName() + "?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = "tel:" + outlet.getPhone();
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.create().show();
            }
        });

        feedbackObstructor = findViewById(R.id.feedbackObstructor);
        feedbackObstructor2 = findViewById(R.id.feedbackObstructor2);


        final String[] feedbackTypeList = {"Appreciation", "Complaint", "Suggestion"};
        final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner2, feedbackTypeList);
        dataAdapter.setDropDownViewResource(R.layout.popup_spinner_view2);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        attachImage = (ImageView) findViewById(R.id.attachImage);
        final TextView feedbackTV = (TextView) findViewById(R.id.feedbackET);

        feedbackObstructor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.feedbackObstructor).setVisibility(View.GONE);
                fabMenu.setVisibility(View.VISIBLE);
                collapse(feedbackLayout);
                spinner.setSelection(0);
                feedbackTV.setText("");
                takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                takePhotoLayout.setClickable(true);
                takePhotoLayout.setEnabled(true);
                attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                attachImageLayout.setClickable(true);
                attachImageLayout.setEnabled(true);
                submitBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                editImageBtn.setVisibility(View.GONE);
                attachImage.setBackgroundResource(android.R.color.transparent);
                attachImage.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.submitOfferBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (!TextUtils.isEmpty(feedbackTV.getText())) {// && attachImage.getDrawable() != null
                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(OutletDetailsActivity.this, false, null);
                    Feedback feedback = new Feedback();
                    FeedbackMeta meta = new FeedbackMeta();
                    meta.setFeedbackType(spinner.getSelectedItem().toString());
                    meta.setFeedback(feedbackTV.getText().toString());

                    String imagePat = uploadingImage;
                    if (TextUtils.isEmpty(imagePat)) {
                        meta.setPhoto("");
                    } else {
                        meta.setPhoto(imagePat);
                    }

                    feedback.setOutletId(outlet.get_id());
                    feedback.setFeedbackMeta(meta);


                    HttpService.getInstance().outletFeedback(getUserToken(), feedback, new Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse dataBaseResponse, Response response) {
                            if (dataBaseResponse.isResponse()) {
                                Toast.makeText(view.getContext(), "Feedback sent.", Toast.LENGTH_SHORT).show();
                                twystProgressHUD.dismiss();
                                collapse(feedbackLayout);
                                fabMenu.setVisibility(View.VISIBLE);
                                spinner.setSelection(0);
                                feedbackTV.setText("");
                                takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                                takePhotoLayout.setClickable(true);
                                takePhotoLayout.setEnabled(true);
                                attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                                attachImageLayout.setClickable(true);
                                attachImageLayout.setEnabled(true);
                                submitBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                                editImageBtn.setVisibility(View.GONE);
                                attachImage.setBackgroundResource(android.R.color.transparent);
                                attachImage.setVisibility(View.GONE);

                            }


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            collapse(feedbackLayout);
                            fabMenu.setVisibility(View.VISIBLE);
                            spinner.setSelection(0);
                            feedbackTV.setText("");
                            takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                            takePhotoLayout.setClickable(true);
                            takePhotoLayout.setEnabled(true);
                            attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                            attachImageLayout.setClickable(true);
                            attachImageLayout.setEnabled(true);
                            submitBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                            editImageBtn.setVisibility(View.GONE);
                            attachImage.setBackgroundResource(android.R.color.transparent);
                            attachImage.setVisibility(View.GONE);

                            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                handleRetrofitError(error);
                            } else {
                                buildAndShowSnackbarWithMessage("Unable to submit feedback!");
                            }
                        }
                    });
                } else if (!TextUtils.isEmpty(feedbackTV.getText())) {// && attachImage.getDrawable() == null
                    feedbackTV.setError("Comments required");
//                    Toast.makeText(OutletDetailsActivity.this, "Please attach a photo or take photo!", Toast.LENGTH_SHORT).show();
                } else {
                    feedbackTV.setError("Please enter some feedback before submitting!");
                    Toast.makeText(OutletDetailsActivity.this, "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Take Photo", "Choose Photo", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(OutletDetailsActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose Photo")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();


            }
        });
        findViewById(R.id.takePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        findViewById(R.id.attachBill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });


        findViewById(R.id.submitCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse(feedbackLayout);
                fabMenu.setVisibility(View.VISIBLE);
                spinner.setSelection(0);
                feedbackTV.setText("");
                feedbackTV.setError(null);
                takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                takePhotoLayout.setClickable(true);
                takePhotoLayout.setEnabled(true);
                attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                attachImageLayout.setClickable(true);
                attachImageLayout.setEnabled(true);
                submitBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                editImageBtn.setVisibility(View.GONE);
                attachImage.setBackgroundResource(android.R.color.transparent);
                attachImage.setVisibility(View.GONE);

            }
        });

        outlet_view_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackLayout.getVisibility() == View.VISIBLE) {
                    collapse(feedbackLayout);
                    fabMenu.setVisibility(View.VISIBLE);
                    spinner.setSelection(0);
                    feedbackTV.setText("");
                    takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                    takePhotoLayout.setClickable(true);
                    takePhotoLayout.setEnabled(true);
                    attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_red));
                    attachImageLayout.setClickable(true);
                    attachImageLayout.setEnabled(true);
                    submitBtn.setBackground(getResources().getDrawable(R.drawable.button_grey));
                    editImageBtn.setVisibility(View.GONE);
                    attachImage.setBackgroundResource(android.R.color.transparent);
                    attachImage.setVisibility(View.GONE);

                } else {
                    expand(feedbackLayout);
                    fabMenu.setVisibility(View.GONE);

                }
            }
        });

        findViewById(R.id.outletShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text2 = "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName() + "&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM";
                String text = "Hey Checkout the offers being offered by " + outlet.getName() + " outlet on \"Twyst\" app.\n" + "Download Now:" + text2;
                showShareIntents("Share using", text);

                ShareOutlet shareOutlet = new ShareOutlet();
                shareOutlet.setOutletId(outlet.get_id());
                HttpService.getInstance().shareOutlet(getUserToken(), shareOutlet, new Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse baseResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        handleRetrofitError(error);
                    }
                });

            }
        });


        outlet_view_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(OutletDetailsActivity.this, false, null);
                if (outlet.isFollowing()) {
                    followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                    HttpService.getInstance().unFollowEvent(getUserToken(), outlet.get_id(), new Callback<BaseResponse<Data>>() {
                        @Override
                        public void success(BaseResponse<Data> dataBaseResponse, Response response) {
                            if (dataBaseResponse.isResponse()) {
                                outlet.setFollowing(false);
                                followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);

                                System.out.println("unfollow event response" + response);
                            } else {
                                followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                                System.out.println("false response" + dataBaseResponse.getMessage());
                            }
                            twystProgressHUD.dismiss();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            handleRetrofitError(error);
                        }
                    });


                } else {
                    followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                    HttpService.getInstance().followEvent(getUserToken(), outlet.get_id(), new Callback<BaseResponse<Data>>() {
                        @Override
                        public void success(BaseResponse<Data> dataBaseResponse, Response response) {
                            if (dataBaseResponse.isResponse()) {
                                int twystBucks = dataBaseResponse.getData().getTwyst_bucks();
                                if (twystBucks > getTwystBucks()) {
                                    setTwystBucks(twystBucks);
                                    Toast.makeText(OutletDetailsActivity.this, OutletDetailsActivity.this.getResources().getString(R.string.bucks_earned_follow_outlet), Toast.LENGTH_SHORT).show();
                                }
                                followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
                                outlet.setFollowing(true);

                                System.out.println("follow event response" + response);
                            } else {
                                followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
                                System.out.println("false response" + dataBaseResponse.getMessage());
                            }
                            twystProgressHUD.dismiss();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            twystProgressHUD.dismiss();
                            handleRetrofitError(error);
                        }
                    });

                }
            }
        });


        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                if (obstructor.getVisibility() == View.INVISIBLE) {
                    obstructor.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onMenuCollapsed() {
                if (obstructor.getVisibility() == View.VISIBLE) {
                    obstructor.setVisibility(View.INVISIBLE);
                }
            }
        });

        obstructor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getTagName(), "fab menu obstructor clicked .... ");

                if (obstructor.getVisibility() == View.VISIBLE) {
                    fabMenu.collapse();
                    return true;
                }

                return false;
            }
        });

        final View checkinObstructor = findViewById(R.id.checkinObstructor);
        final View checkinObstructor2 = findViewById(R.id.checkinObstructor2);

        checkinObstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkinObstructor.setVisibility(View.GONE);
                checkinObstructor2.setVisibility(View.GONE);
            }
        });

        checkinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.collapse();
                checkinObstructor.setVisibility(View.VISIBLE);
                checkinObstructor2.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.scanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkinObstructor.setVisibility(View.GONE);
                checkinObstructor2.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //IntentIntegrator integrator = new IntentIntegrator(DiscoverActivity.this);
                        //integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                        Intent intent = new Intent(OutletDetailsActivity.this, ScannerActivity.class);
                        startActivity(intent);
                    }
                }, 100);
            }
        });

        findViewById(R.id.uploadBillBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkinObstructor.setVisibility(View.GONE);
                checkinObstructor2.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(OutletDetailsActivity.this, UploadBillActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_ID, outlet.get_id());
                        intent.putExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_ADDRESS, outlet.getAddress());
                        intent.putExtra(AppConstants.INTENT_PARAM_UPLOAD_BILL_OUTLET_NAME, outlet.getName());
                        startActivity(intent);
                    }
                }, 100);
            }
        });

        submitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obstructor.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(OutletDetailsActivity.this, SubmitOfferActivity.class);
                    intent.putExtra(AppConstants.INTENT_PARAM_SUBMIT_OFFER_OUTLET_NAME, outlet.getName());
                    intent.putExtra(AppConstants.INTENT_PARAM_SUBMIT_OFFER_OUTLET_ADDRESS, outlet.getAddress());
                    intent.putExtra(AppConstants.INTENT_PARAM_SUBMIT_OFFER_OUTLET_ID, outlet.get_id());
                    startActivity(intent);
                    fabMenu.collapse();

                }
            }
        });

        setupPage();

    }

    private void setupPage() {
        if (outlet.isFollowing()) {
            followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
        } else {
            followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
        }

        if (!TextUtils.isEmpty(outlet.getAddress())) {
            outletAddress.setText(outlet.getAddress());
            placeIcon.setVisibility(View.VISIBLE);
        } else {
            outletAddress.setText("");
            placeIcon.setVisibility(View.INVISIBLE);
        }

        outletName.setText(outlet.getName());
        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
        picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);
        picasso.load(outlet.getBackground())
                .noFade()
                .fit()
                .centerCrop()
                .into(outletBgImage);

        if (TextUtils.isEmpty(outlet.getDistance())) {
            outletDistance.setText("");
            outletDistance.setVisibility(View.INVISIBLE);
        } else {
            outletDistance.setText(outlet.getDistance() + " Km");
            outletDistance.setVisibility(View.VISIBLE);
        }

        hideProgressHUDInLayout();
        fabMenu.setVisibility(View.VISIBLE);
        outletDetailsRecyclerView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        outletAdapter.setItems(outlet.getOffers(), outlet.getName(), outlet.getAddress(), outlet.get_id());
        outletAdapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(getClass().getSimpleName(), "onActivityResult called");
        attachImage.setVisibility(View.VISIBLE);


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Log.d(getClass().getSimpleName(), "picture taken");
                try {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath(), btmapOptions);

                    attachImage.setImageBitmap(bm);


                    imagePath = f.getAbsolutePath();

                    Log.i("imagePath", "" + imagePath);
                    Toast.makeText(this, "You have clicked Image", Toast.LENGTH_LONG).show();
                    String fileNameSegments[] = imagePath.split("/");
                    fileName = fileNameSegments[fileNameSegments.length - 1];
                    if (imagePath != null && !imagePath.isEmpty()) {
                        takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        takePhotoLayout.setClickable(false);
                        takePhotoLayout.setEnabled(false);
                        attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        attachImageLayout.setClickable(false);
                        attachImageLayout.setEnabled(false);
                        submitBtn.setBackground(getResources().getDrawable(R.drawable.button_red));
                        editImageBtn.setVisibility(View.VISIBLE);
                        // Convert image to String using Base64
                        encodeImagetoString();
                        // When Image is not selected from Gallery
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, OutletDetailsActivity.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                attachImage.setImageBitmap(bm);


                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
                cursor.moveToFirst();
                imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                Log.i("imagePath", "" + imagePath);
                Toast.makeText(this, "You have picked Image", Toast.LENGTH_LONG).show();
                String fileNameSegments[] = imagePath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                if (imagePath != null && !imagePath.isEmpty()) {
                    takePhotoLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                    takePhotoLayout.setClickable(false);
                    takePhotoLayout.setEnabled(false);
                    attachImageLayout.setBackground(getResources().getDrawable(R.drawable.button_grey));
                    attachImageLayout.setClickable(false);
                    attachImageLayout.setEnabled(false);
                    submitBtn.setBackground(getResources().getDrawable(R.drawable.button_red));
                    editImageBtn.setVisibility(View.VISIBLE);
                    // Convert image to String using Base64
                    encodeImagetoString();
                    // When Image is not selected from Gallery
                }
            }
        }

    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            ;

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imagePath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, Base64.NO_WRAP);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                uploadingImage = encodedString;
            }
        }.execute(null, null, null);
    }

    public void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        final AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.7f);
        animation1.setDuration(75);
        feedbackObstructor.setVisibility(View.VISIBLE);
        feedbackObstructor2.setVisibility(View.VISIBLE);
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        feedbackObstructor.startAnimation(animation1);
        a.setDuration(75);
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        final AlphaAnimation animation1 = new AlphaAnimation(0.7f, 0.0f);
        animation1.setDuration(75);
        //animation1.setStartOffset(10);
        feedbackObstructor.setVisibility(View.GONE);
        feedbackObstructor2.setVisibility(View.GONE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        feedbackObstructor.startAnimation(animation1);
        a.setDuration(75);
        v.startAnimation(a);
    }

    @Override
    public void onScrollChanged(final int scrollY, boolean firstScroll, boolean dragging) {
        final int lowerLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
        final int upperLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 185, getResources().getDisplayMetrics());

        final View layout = findViewById(R.id.layout);
        final View hideableLayout = findViewById(R.id.hideableLayout);

//        Log.d(getTagName(), "scrollY: " + scrollY + " upperLimit:" + upperLimit + " lowerLimit:" + lowerLimit);
        final int diff = upperLimit - scrollY;
        if (diff > upperLimit) {
            layout.getLayoutParams().height = upperLimit;
            layout.requestLayout();
            hideableLayout.setAlpha(1f);
        } else if (diff < lowerLimit) {
            layout.getLayoutParams().height = lowerLimit;
            layout.requestLayout();
            hideableLayout.setAlpha(0f);
        } else if (diff <= upperLimit && diff > lowerLimit) {
            layout.getLayoutParams().height = diff;
            layout.requestLayout();
            float ratio = (scrollY * 1f / (upperLimit - lowerLimit) * 1f) * 1f;
            hideableLayout.setAlpha(1f - ratio);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//        if (scrollState == ScrollState.UP) {
//            if (detailsIsShown()) {
//                hideDetails();
//            }
//        } else if (scrollState == ScrollState.DOWN) {
//            if (detailsIsHidden()) {
//                showDetails();
//            }
//        }
    }

    private boolean detailsIsShown() {
        View hideableLayout = findViewById(R.id.hideableLayout);
        return (hideableLayout.getVisibility() == View.VISIBLE && hideableLayout.getAlpha() == 1);
    }

    private boolean detailsIsHidden() {
        View hideableLayout = findViewById(R.id.hideableLayout);
        return (hideableLayout.getVisibility() == View.INVISIBLE || hideableLayout.getVisibility() == View.GONE || hideableLayout.getAlpha() == 0);
    }

    private void hideDetails() {

        final View layout = findViewById(R.id.layout);
        final View hideableLayout = findViewById(R.id.hideableLayout);
        final int initialHeight = layout.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                int targetHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());

                if (interpolatedTime == 1) {
                    //layout.setVisibility(View.GONE);

                    //layout.getLayoutParams().height = targetHeight;
                    //layout.requestLayout();

                    hideableLayout.setAlpha(0f);
                    //hideableLayout.requestLayout();
                } else {
                    if (layout.getLayoutParams().height > targetHeight) {
                        layout.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        layout.requestLayout();
                    }

                    hideableLayout.setAlpha(1f - interpolatedTime);
                    //hideableLayout.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / layout.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(((int) (initialHeight / layout.getContext().getResources().getDisplayMetrics().density)) * 3);
        layout.startAnimation(a);

    }


    private void showDetails() {
        final View layout = findViewById(R.id.layout);
        final View hideableLayout = findViewById(R.id.hideableLayout);
        final int initialHeight = layout.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                int targetHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 185, getResources().getDisplayMetrics());

                if (interpolatedTime == 1) {
                    //layout.setVisibility(View.GONE);

                    //layout.getLayoutParams().height = targetHeight;
                    //layout.requestLayout();

                    hideableLayout.setAlpha(1f);
                    //hideableLayout.requestLayout();
                } else {
                    if (layout.getLayoutParams().height < targetHeight) {
                        layout.getLayoutParams().height = initialHeight + (int) (initialHeight * interpolatedTime);
                        layout.requestLayout();
                    }

                    hideableLayout.setAlpha(0f + interpolatedTime);
                    //hideableLayout.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / layout.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(((int) (initialHeight / layout.getContext().getResources().getDisplayMetrics().density)) * 3);
        layout.startAnimation(a);
    }
}