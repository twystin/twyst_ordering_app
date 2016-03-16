package com.twyst.app.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.fragments.CashTransactionFragment;
import com.twyst.app.android.model.TwystCashHistory;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

/**
 * Created by Raman on 2/3/2016.
 */
public class TwystCashPagerAdapter extends FragmentPagerAdapter {
    private int mTabCount;
    private Context mContext;
    private ArrayList<TwystCashHistory> mCashHistory = new ArrayList<TwystCashHistory>();
    private ArrayList<TwystCashHistory> creditList = new ArrayList<TwystCashHistory>();
    private ArrayList<TwystCashHistory> debitList = new ArrayList<TwystCashHistory>();

    public TwystCashPagerAdapter(int tabCount, ArrayList<TwystCashHistory> cashHistory, FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mTabCount = tabCount;
        mCashHistory = cashHistory;
        generateTransactionLists(mCashHistory);
    }

    @Override
    public Fragment getItem(int position) {
        CashTransactionFragment cashTransactionFragment = new CashTransactionFragment();
        Bundle bundle = new Bundle();

        switch (position) {
            case 0: {
                bundle.putSerializable(AppConstants.BUNDLE_CASH_HISTORY, mCashHistory);
                bundle.putInt(AppConstants.CASH_HISTORY_TYPE, position);
                break;
            }
            case 1: {
                bundle.putSerializable(AppConstants.BUNDLE_CASH_HISTORY, creditList);
                bundle.putInt(AppConstants.CASH_HISTORY_TYPE, position);
                break;
            }
            case 2: {
                bundle.putSerializable(AppConstants.BUNDLE_CASH_HISTORY, debitList);
                bundle.putInt(AppConstants.CASH_HISTORY_TYPE, position);
                break;
            }
        }
        cashTransactionFragment.setArguments(bundle);
        return cashTransactionFragment;
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
                return "Earned";
            case 2:
                return "Spent";
            default:
                return null;
        }
    }

    private void generateTransactionLists(ArrayList<TwystCashHistory> mDataList) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).isEarn()) {
                creditList.add(mDataList.get(i));
            } else if (!mDataList.get(i).isEarn()) {
                debitList.add(mDataList.get(i));
            }
        }
    }
}
