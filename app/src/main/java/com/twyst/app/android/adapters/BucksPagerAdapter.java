package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.fragments.AllBucksFragment;
import com.twyst.app.android.fragments.CreditBucksFragment;
import com.twyst.app.android.fragments.DebitBucksFragment;

/**
 * Created by Raman on 2/3/2016.
 */
public class BucksPagerAdapter extends FragmentPagerAdapter {
    private int mTabCount;
    private Context mContext;

    public BucksPagerAdapter(int tabCount, FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mTabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllBucksFragment();
            case 1:
                return new CreditBucksFragment();
            case 2:
                return new DebitBucksFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All";
            case 1:
                return "Credit";
            case 2:
                return "Debit";
            default:
                return null;
        }
    }
}
