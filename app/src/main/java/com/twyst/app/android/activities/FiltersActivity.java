package com.twyst.app.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.fragments.CuisinesFragment;
import com.twyst.app.android.fragments.OffersFragment;
import com.twyst.app.android.fragments.PaymentFragment;
import com.twyst.app.android.fragments.SortFragment;
import com.twyst.app.android.fragments.TagFragment;
import com.twyst.app.android.model.TagItem;
import com.twyst.app.android.util.AppConstants;

import java.util.HashMap;

/**
 * Created by anshul on 1/16/2016.
 */
public class FiltersActivity extends BaseActionActivity implements TagFragment.OnTagSelectedListener {
    private SortFragment sortFragment;
    private CuisinesFragment cuisinesFragment;
    private PaymentFragment paymentFragment;
    private OffersFragment offersFragment;
    private FragmentTransaction transaction;
    private HashMap<String, long[]> tagsMap;
    private Toolbar toolbar;
    private TextView applyBtn;
    private TextView resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        setupToolBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tagsMap = (HashMap<String, long[]>) bundle.getSerializable(AppConstants.FILTER_MAP);
            if (tagsMap != null && tagsMap.size() == 4) {
                sortFragment = new SortFragment(tagsMap.get(AppConstants.sortTag));
                cuisinesFragment = new CuisinesFragment(tagsMap.get(AppConstants.cuisinetag));
                paymentFragment = new PaymentFragment(tagsMap.get(AppConstants.paymentTag));
                offersFragment = new OffersFragment(tagsMap.get(AppConstants.offersTag));
            } else {
                sortFragment = new SortFragment(null);
                cuisinesFragment = new CuisinesFragment(null);
                paymentFragment = new PaymentFragment(null);
                offersFragment = new OffersFragment(null);
            }

        }

        applyBtn = (TextView) findViewById(R.id.btn_apply_filters);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagsMap.clear();
                tagsMap = new HashMap<String, long[]>();
                tagsMap.put("SORT BY", sortFragment.getSelectedOptions());
                tagsMap.put("CUISINES", cuisinesFragment.getSelectedOptions());
                tagsMap.put("PAYMENT OPTION", paymentFragment.getSelectedOptions());
                tagsMap.put("OFFERS AVAILABLE", offersFragment.getSelectedOptions());

                Bundle data = new Bundle();
                data.putSerializable(AppConstants.FILTER_TAGS, tagsMap);
                Intent intent = new Intent();
                intent.putExtras(data);
                setResult(AppConstants.GOT_FILTERS_SUCCESS, intent);
                finish();
            }
        });

        resetBtn = (TextView) findViewById(R.id.btn_reset_filters);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sortFragment.clearSelection();
//                cuisinesFragment.clearSelection();
//                offersFragment.clearSelection();
//                paymentFragment.clearSelection();
                sortFragment = new SortFragment(null);
                cuisinesFragment = new CuisinesFragment(null);
                paymentFragment = new PaymentFragment(null);
                offersFragment = new OffersFragment(null);
                TagFragment tagFragment = (TagFragment) getFragmentManager().findFragmentById(R.id.tag_fragment);
                switch (tagFragment.returnCurrentSelectedTag()) {
                    case "SORT BY":
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, sortFragment);
                        transaction.commit();
                        break;
                    case "CUISINES":
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, cuisinesFragment);
                        transaction.commit();
                        break;
                    case "PAYMENT OPTION":
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, paymentFragment);
                        transaction.commit();
                        break;
                    case "OFFERS AVAILABLE":
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, offersFragment);
                        transaction.commit();
                        break;
                    default:
                        break;

                }
            }
        });
    }

    @Override
    public void onTagSelected(int position, TagItem tagItem) {
        switch (tagItem.getName()) {
            case "SORT BY":
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, sortFragment);
                transaction.commit();
                break;
            case "CUISINES":
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, cuisinesFragment);
                transaction.commit();
                break;
            case "PAYMENT OPTION":
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, paymentFragment);
                transaction.commit();
                break;
            case "OFFERS AVAILABLE":
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, offersFragment);
                transaction.commit();
                break;
            default:
                break;
        }

    }

}