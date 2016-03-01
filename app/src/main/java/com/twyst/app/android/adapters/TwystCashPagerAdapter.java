package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.fragments.AllTwystCashFragment;
import com.twyst.app.android.fragments.CreditTwystCashFragment;
import com.twyst.app.android.fragments.DebitTwystCashFragment;

/**
 * Created by Raman on 2/3/2016.
 */
public class TwystCashPagerAdapter extends FragmentPagerAdapter {
    private int mTabCount;
    private Context mContext;

    public TwystCashPagerAdapter(int tabCount, FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mTabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllTwystCashFragment();
            case 1:
                return new CreditTwystCashFragment();
            case 2:
                return new DebitTwystCashFragment();
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
