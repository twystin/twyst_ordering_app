package com.twyst.app.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.fragments.DiscoverOutletFragment;
import com.twyst.app.android.fragments.RedeemFragment;

/**
 * Created by anshul on 1/12/2016.
 */
public class DiscoverPagerAdapter extends FragmentPagerAdapter {
    int NumOfTabs;

    public DiscoverPagerAdapter(android.support.v4.app.FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.NumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DiscoverOutletFragment();
            case 1:
                return new RedeemFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NumOfTabs;
    }
}
