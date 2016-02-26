package com.twyst.app.android.activities;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twyst.app.android.R;
import com.twyst.app.android.model.CashbackOffers;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.AppConstants;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.twyst.app.android.R.id.free;
import static com.twyst.app.android.R.id.ll_minBillValRow;

public class VoucherDetailsActivity extends BaseActionActivity {

    private boolean tncExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_details);

        setupToolBar();

        final CashbackOffers offer = (CashbackOffers) getIntent().getSerializableExtra(AppConstants.INTENT_VOUCHER_DETAIL);

        // Load the merchant logo
        String merchantLogoUri = getIntent().getStringExtra(AppConstants.INTENT_MERCHANT_LOGO);
        ImageView merchantLogo = (ImageView) findViewById(R.id.iv_merchantLogo);
        Picasso picasso = Picasso.with(this);
        if (merchantLogoUri != null) {
            picasso.load(merchantLogoUri).noFade().into(merchantLogo);
        }

        // Load the banner
        ImageView offerBanner = (ImageView) findViewById(R.id.iv_banner);
        if (merchantLogoUri != null) {
            picasso.load(merchantLogoUri).noFade().into(offerBanner);
        }

        // Load the valid date
        final TextView offerValidity = (TextView) findViewById(R.id.tv_voucherValidity);
        offerValidity.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getDrawable(R.drawable.voucher_validity_icon);
                        int height = offerValidity.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        offerValidity.setCompoundDrawables(img, null, null, null);
                        offerValidity.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
        offerValidity.setText(String.format("Valid till %s", getValidityDate(offer.getOffer_end_date())));

        // Load the offer Cost
        TextView offerCost = (TextView) findViewById(R.id.tv_offerCost);
        offerCost.setText(offer.getOffer_value());

        // Load the minimum bill value if present
        TextView minBillVal = (TextView) findViewById(R.id.tv_minBillVal);
        if (offer.getMin_bill_value() != null) {
            minBillVal.setText(String.format("Minimum Bill Rs. %s", offer.getMin_bill_value()));
        } else {
            ((LinearLayout) findViewById(R.id.ll_minBillValRow)).setVisibility(View.GONE);
        }

        // Load the Cost (Twyst Bucks)
        final TextView twystBucksCost = (TextView) findViewById(R.id.tv_twystBucksFee);
        twystBucksCost.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getDrawable(R.drawable.twystbucksicon);
                        int height = twystBucksCost.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        twystBucksCost.setCompoundDrawables(img, null, null, null);
                        twystBucksCost.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
        twystBucksCost.setText(String.format("%s Twyst Bucks", offer.getOffer_cost()));

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
                canOfferbeUsed();
            }
        });

        // Load TnC
        LinearLayout tncRow = (LinearLayout) findViewById(R.id.ll_tncLayout);
        final ImageView tncArrow = (ImageView) findViewById(R.id.iv_tncArrow);
        final LinearLayout tncLayout = (LinearLayout) findViewById(R.id.ll_tncText);
        final TextView tnc = (TextView) findViewById(R.id.tv_tncText);
        tnc.setText(offer.getOffer_tnc());
        tncRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tncExpanded) {
                    tncLayout.setVisibility(View.VISIBLE);
                    tncArrow.setImageDrawable(getDrawable(R.drawable.expanded));
                    tncExpanded = true;
                } else {
                    tncLayout.setVisibility(View.GONE);
                    tncArrow.setImageDrawable(getDrawable(R.drawable.collapsed));
                    tncExpanded = false;
                }
            }
        });
    }

    private void canOfferbeUsed() {
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
