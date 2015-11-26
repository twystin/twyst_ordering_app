package com.twyst.app.android.adapters;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.fragments.MenuPageFragment;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.menu.MenuDescription;
import com.twyst.app.android.model.menu.Sections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 11/19/2015.
 */
public class MenuTabsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<MenuDescription> items = new ArrayList<>();

    public MenuTabsPagerAdapter(List<MenuDescription> items, FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        ArrayList<Sections> sectionsList = items.get(position).getSectionList();
        return MenuPageFragment.newInstance(sectionsList);
//        return MenuPageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).getMenuCategory();
//        return "TAB " + (position + 1);
    }
}
