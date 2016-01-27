package com.twyst.app.android.adapters;

;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

/**
 * Created by tushar on 25/01/16.
 */
public class OfferDisplayPagerAdpater extends PagerAdapter {

    private final static String OFFER_TYPE = "offer";

    private TextView mHeader;
    private TextView mLine1;
    private TextView mLine2;
    private TextView mTwystBucks;
    private LinearLayout mTwystBucksinfo;
    ;
    private TextView mOkButton;

    private ImageView mOfferIcon;
    private LinearLayout mLlOfferDisplay;
    private LinearLayout ll_tvplusview;


    private ArrayList<Offer> mOffersList = new ArrayList<>();
    private Context context;

    public OfferDisplayPagerAdpater(Context context, ArrayList<Offer> OfferList) {
        this.context = context;
        this.mOffersList = OfferList;
    }

    @Override
    public int getCount() {
        return mOffersList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Offer offer = mOffersList.get(position);

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.layout_offer_display, container, false);

        initVars(itemView);
        setupView(offer);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });
        mLlOfferDisplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        ((ViewPager) container).addView(itemView);



        return itemView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.995f;
    }

    private void initVars(View v) {
        mHeader = (TextView) v.findViewById(R.id.offer_item_header);
        mLine1 = (TextView) v.findViewById(R.id.offer_item_line1);
        mLine2 = (TextView) v.findViewById(R.id.offer_item_line2);
        mTwystBucks = (TextView) v.findViewById(R.id.my_twyst_bucks);
        mOkButton = (TextView) v.findViewById(R.id.press_ok_button);

        mLlOfferDisplay = (LinearLayout) v.findViewById(R.id.ll_offer);
        mOfferIcon = (ImageView) v.findViewById(R.id.offer_display_icon);
        ll_tvplusview = (LinearLayout) v.findViewById(R.id.ll_tvplusview);
        mTwystBucksinfo = (LinearLayout) v.findViewById(R.id.twystBucksInfo);
    }

    private void setupView(Offer offer) {
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
        } else
            ll_tvplusview.setVisibility(View.INVISIBLE);

        // Twyst bucks
        if ((typeOffer.equals(OFFER_TYPE)) && (twystBucks != 0)) {
            mTwystBucks.setText(" " + String.valueOf(twystBucks) + " ");
        } else {
            mTwystBucksinfo.setVisibility(View.INVISIBLE);
        }

        // Offer Icon
        int offerIcon = Utils.getOfferDisplayIcon(offer.getMeta().getRewardType());
        if (offerIcon != 0)
            mOfferIcon.setImageResource(offerIcon);
        else
            mOfferIcon.setVisibility(View.INVISIBLE);
    }

}
