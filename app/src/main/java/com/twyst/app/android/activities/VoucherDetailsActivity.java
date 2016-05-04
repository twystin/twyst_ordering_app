package com.twyst.app.android.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.CashbackOffers;
import com.twyst.app.android.model.ShoppingVoucher;
import com.twyst.app.android.model.ShoppingVoucherResponse;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VoucherDetailsActivity extends BaseActionActivity {
    private CashbackOffers offer;
    //    private boolean tncExpanded = false;
    boolean emailVerified = false;
    boolean emailVerifiedButOtherProblemEncountered = false;
    private String messageToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_details);

        setupToolBar();

        offer = (CashbackOffers) getIntent().getSerializableExtra(AppConstants.INTENT_VOUCHER_DETAIL);

        // Load the merchant logo
        String merchantLogoUri = getIntent().getStringExtra(AppConstants.INTENT_MERCHANT_LOGO);
        ImageView merchantLogo = (ImageView) findViewById(R.id.iv_merchantLogo);
        if (merchantLogoUri != null) {

            Glide.with(this)
                    .load(merchantLogoUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(merchantLogo);
        }

        // Load the banner
        ImageView offerBanner = (ImageView) findViewById(R.id.iv_banner);
        if (merchantLogoUri != null) {
            Glide.with(this)
                    .load(merchantLogoUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(offerBanner);
        }

        // Load the valid date
        final TextView offerValidity = (TextView) findViewById(R.id.tv_voucherValidity);
        offerValidity.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(R.drawable.voucher_validity_icon);
                        int height = offerValidity.getMeasuredHeight() * 7 / 8;
                        img.setBounds(0, 0, height, height);
                        offerValidity.setCompoundDrawables(img, null, null, null);
                        offerValidity.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
        offerValidity.setText(String.format("Valid till %s", getValidityDate(offer.getOffer_end_date())));

        // Load the offer Cost
        TextView offerCost = (TextView) findViewById(R.id.tv_offerCost);
        offerCost.setText(AppConstants.INDIAN_RUPEE_SYMBOL + offer.getOffer_value());

        // Load the minimum bill value if present
        TextView minBillVal = (TextView) findViewById(R.id.tv_minBillVal);
        if (offer.getMin_bill_value() != null) {
            minBillVal.setText(String.format("Minimum Bill Rs. %s", offer.getMin_bill_value()));
        } else {
            ((LinearLayout) findViewById(R.id.ll_minBillValRow)).setVisibility(View.GONE);
        }

        // Load the Cost (Twyst Cash)
        final TextView twystCashCost = (TextView) findViewById(R.id.tv_twystCashFee);
        twystCashCost.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(R.drawable.twyst_cash_icon);
                        int height = twystCashCost.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        twystCashCost.setCompoundDrawables(img, null, null, null);
                        twystCashCost.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
        twystCashCost.setText(String.format("%s Twyst Cash", offer.getOffer_cost()));

        // Load the handling fee
        TextView handlingFee = (TextView) findViewById(R.id.tv_handlingFee);
        handlingFee.setText(String.format("(Incl. %s handling fee)", offer.getOffer_processing_fee()));

        // Load the free text
        TextView freeText = (TextView) findViewById(R.id.tv_freeText);
        if (offer.getOffer_text() != null) {
            freeText.setText(offer.getOffer_text());
        } else {
            freeText.setVisibility(View.GONE);
        }

        FrameLayout useOffer = (FrameLayout) findViewById(R.id.bUseOffer);
        useOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sentEventTracking(VoucherDetailsActivity.this, AppConstants.EVENT_SHOPPING_VOUCHER_REDEEMED);
                canOfferbeUsed();
            }
        });

        // Load TnC
        LinearLayout tncRow = (LinearLayout) findViewById(R.id.ll_tncLayout);
        final ImageView tncArrow = (ImageView) findViewById(R.id.iv_tncArrow);
//        final LinearLayout tncLayout = (LinearLayout) findViewById(R.id.ll_tncText);
//        final TextView tnc = (TextView) findViewById(R.id.tv_tncText);
//        tnc.setText(offer.getOffer_tnc());
        tncRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!tncExpanded) {
////                    tncLayout.setVisibility(View.VISIBLE);
//                    tncArrow.setImageDrawable(getResources().getDrawable(R.drawable.expanded));
//                    tncExpanded = true;
//                } else {
////                    tncLayout.setVisibility(View.GONE);
//                    tncArrow.setImageDrawable(getResources().getDrawable(R.drawable.collapsed));
//                    tncExpanded = false;
//                }
                openURL(offer.getOffer_tnc());
            }
        });

        // Launch twyst cash history activity
        (findViewById(R.id.ll_twyst_cash_launcher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twystCashIntent = new Intent(VoucherDetailsActivity.this, TwystCashHistoryActivity.class);
                startActivity(twystCashIntent);
            }
        });
        updateTwystCash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.sentEventTracking(this, AppConstants.EVENT_SHOPPING_VOUCHER_DETAIL_VIEW);
    }

    private void openURL(String url) {
        if (TextUtils.isEmpty(url) || !URLUtil.isValidUrl(url)) return;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void updateTwystCash() {
        if (Utils.getTwystCash() != -1) {
            ((TextView) findViewById(R.id.tv_my_twyst_cash)).setText(String.valueOf(Utils.getTwystCash()));
        }
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

    private void canOfferbeUsed() {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        ShoppingVoucher sv = new ShoppingVoucher(offer.getOffer_id());
        HttpService.getInstance().postCashbackOffer(getUserToken(), sv, new Callback<BaseResponse<ShoppingVoucherResponse>>() {
                    @Override
                    public void success(BaseResponse<ShoppingVoucherResponse> shoppingVoucherResponseBaseResponse, Response response) {
                        if (shoppingVoucherResponseBaseResponse.isResponse()) {
                            emailVerified = true;
                        } else {
                            // from server we get a boolean which indicates whether we need to show resend verification mail dialog box or not.
                            if (shoppingVoucherResponseBaseResponse.getData().isShowResendOption()) {
                                emailVerified = false;
                            } else {
                                emailVerified = true;
                                emailVerifiedButOtherProblemEncountered = true;
                                messageToDisplay = shoppingVoucherResponseBaseResponse.getData().getMessage();
                            }
                        }
                        showMessage();
                        twystProgressHUD.dismiss();
                        UtilMethods.hideSnackbar();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        twystProgressHUD.dismiss();
                        UtilMethods.handleRetrofitError(VoucherDetailsActivity.this, error);
                        UtilMethods.hideSnackbar();
                    }
                }
        );
    }

    private void showMessage() {
        // If email is verified then show happy shopping message.
        if (emailVerified & !emailVerifiedButOtherProblemEncountered) {
            generateDialogBox("Happy Shopping", getString(R.string.happy_shopping_message));
        } else if (emailVerifiedButOtherProblemEncountered) {
            generateDialogBox("Status", messageToDisplay);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(VoucherDetailsActivity.this).inflate(R.layout.dialog_email_not_verified, null);
            String email = HttpService.getInstance().getSharedPreferences().getString(AppConstants.PREFERENCE_USER_EMAIL, "");
            String msg = String.format("Your Email Id %s is not verified. %s", email, getString(R.string.email_not_verified_line2));
            ((TextView) dialogView.findViewById(R.id.tv_emailId_line1)).setText(msg);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.findViewById(R.id.fSendVerifMail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendVerifMailAPI();
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.fChangeMailId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(VoucherDetailsActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void generateDialogBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(VoucherDetailsActivity.this).inflate(R.layout.dialog_generic_with_ok_button, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView tvTitle = ((TextView) (dialog.findViewById(R.id.tv_title)));
        tvTitle.setText(title);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        dialog.findViewById(R.id.fOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sendVerifMailAPI() {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        HttpService.getInstance().getResendVerifMail(getUserToken(), new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse verifMailResonseBaseResponse, Response response) {
                if (verifMailResonseBaseResponse.isResponse()) {
                    generateDialogBox("Status", getString(R.string.verif_mail_sent));
                }
                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(VoucherDetailsActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    /*
     * Date is needed in the format : dd MMM
     */

    public String getValidityDate(String validDate) {
        String date = null;
        SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date d = inputsdf.parse(validDate);
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTime(d);
            String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            int day = cal.get(Calendar.DAY_OF_MONTH);
            date = day + " " + month;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
