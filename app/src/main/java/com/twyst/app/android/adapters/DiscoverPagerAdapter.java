package com.twyst.app.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.fragments.DiscoverOutletFragment;

/**
 * Created by anshul on 1/12/2016.
 */
public class DiscoverPagerAdapter extends FragmentPagerAdapter {
    int NumOfTabs ;
    public DiscoverPagerAdapter(android.support.v4.app.FragmentManager fm, int NumOfTabs){
        super(fm);
        this.NumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                DiscoverOutletFragment tab1 = new DiscoverOutletFragment();
                return tab1;
            case 1:
                DiscoverOutletFragment tab2 = new DiscoverOutletFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NumOfTabs;
    }
}
