package com.twyst.app.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

public class OfferDisplayActivity extends Activity implements View.OnClickListener {

    private final static String OFFER_TYPE = "offer";
    // View related vars
    private TextView mHeader;
    private TextView mLine1;
    private TextView mLine2;
    private TextView mTwystBucks;
    private TextView mOkButton;
    private RelativeLayout mRlOfferDisplayScreen;
    private ImageView mOfferIcon;
    private LinearLayout mLlOfferDisplay;
    private LinearLayout ll_tvplusview;

    private ArrayList<Offer> mOffersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_display);

        initVars();
        displaySelectedOffer();

        mOkButton.setOnClickListener(this);
        mRlOfferDisplayScreen.setOnClickListener(this);
        mOfferIcon.setOnClickListener(this);
        mLlOfferDisplay.setOnClickListener(this);
    }

    private void displaySelectedOffer() {
        Bundle bundle = getIntent().getExtras();
        int clickedOfferPosition = bundle.getInt(AppConstants.INTENT_CLICKED_OFFER_POSITION);
        mOffersList = (ArrayList<Offer>) bundle.getSerializable(AppConstants.INTENT_OFFER_LIST);

        setupView(clickedOfferPosition);

/*        mScrollingOffersAdapter = new ScrollingOffersAdapter(null);
        mScrollingOffersViewPager = (ViewPager) findViewById(R.id.scrollingOffersPager);
        mScrollingOffersViewPager.setAdapter(mScrollingOffersAdapter);*/
    }

    private void initVars() {
        mHeader = (TextView) findViewById(R.id.offer_item_header);
        mLine1 = (TextView) findViewById(R.id.offer_item_line1);
        mLine2 = (TextView) findViewById(R.id.offer_item_line2);
        mTwystBucks = (TextView) findViewById(R.id.my_twyst_bucks);
        mOkButton = (TextView) findViewById(R.id.press_ok_button);
        mRlOfferDisplayScreen = (RelativeLayout) findViewById(R.id.offer_display_screen);
        mLlOfferDisplay = (LinearLayout) findViewById(R.id.ll_offer);
        mOfferIcon = (ImageView) findViewById(R.id.offer_display_icon);
        ll_tvplusview = (LinearLayout) findViewById(R.id.ll_tvplusview);
    }

    private void setupView(int pos) {
        Offer offer = mOffersList.get(pos);
        String typeOffer = offer.getType();
        String header = offer.getHeader();
        String line1 = offer.getLine1();
        String line2 = offer.getLine2();
        int twystBucks = offer.getOfferCost();

        // Set header & lines
        mHeader.setText(header);
        mLine1.setText(line1);
        if (line2 != null) {
            mLine2.setText(line2);
        }
        else
            ll_tvplusview.setVisibility(View.INVISIBLE);

        // Twyst bucks
        if ((typeOffer.equals(OFFER_TYPE)) && (twystBucks != 0)) {
            mTwystBucks.setText(" " + String.valueOf(twystBucks) + " ");
        } else {
            (findViewById(R.id.twystBucksInfo)).setVisibility(View.INVISIBLE);
        }

        // Offer Icon
        int offerIcon = Utils.getOfferDisplayIcon(offer.getMeta().getRewardType());
        if (offerIcon != 0)
            mOfferIcon.setImageResource(offerIcon);
        else
            mOfferIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.press_ok_button):
                finish();
                break;

            case (R.id.offer_display_screen):
                finish();
                break;
        }
    }
}