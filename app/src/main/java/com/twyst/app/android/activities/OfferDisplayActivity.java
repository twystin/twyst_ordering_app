package com.twyst.app.android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

public class OfferDisplayActivity extends AppCompatActivity implements View.OnClickListener {
    // View related vars
    private TextView mLine1;
    private TextView mLine2;
    private TextView mMinBill;
    private TextView mTwystBucks;
    private TextView mOkButton;
    private RelativeLayout mRlOfferDisplayScreen;
    private ImageView mOfferImage;
    private LinearLayout mLlOfferDisplay;

    private ArrayList<Offer> mOffersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_display);

        initVars();
        displaySelectedOffer();

        mOkButton.setOnClickListener(this);
        mRlOfferDisplayScreen.setOnClickListener(this);
        mOfferImage.setOnClickListener(this);
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
        mLine1 = (TextView) findViewById(R.id.offer_item_line1);
        mLine2 = (TextView) findViewById(R.id.offer_item_line2);
        mMinBill = (TextView) findViewById(R.id.offer_condition_part2);
        mTwystBucks = (TextView) findViewById(R.id.my_twyst_bucks);
        mOkButton = (TextView) findViewById(R.id.press_ok_button);
        mRlOfferDisplayScreen = (RelativeLayout) findViewById(R.id.offer_display_screen);
        mLlOfferDisplay = (LinearLayout) findViewById(R.id.ll_offer);
        mOfferImage = (ImageView) findViewById(R.id.offer_display_image);
    }

    private void setupView(int pos) {
        Offer offer = mOffersList.get(pos);
        String typeOffer = offer.getType();
        String line1 = offer.getLine1();
        String line2 = offer.getLine2();
        int twystBucks = offer.getOfferCost();

        mLine1.setText(line1);
        mLine2.setText(line2);

        if ((typeOffer.equals("offer")) && (twystBucks != 0)) {
            mTwystBucks.setText(" " + String.valueOf(twystBucks) + " ");
        } else {
            (findViewById(R.id.twystBucksInfo)).setVisibility(View.INVISIBLE);
        }
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
            case (R.id.offer_display_image):
                break;

            case (R.id.ll_offer):
                break;
        }
    }
}