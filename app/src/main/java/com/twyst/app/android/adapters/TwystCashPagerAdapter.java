package com.twyst.app.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twyst.app.android.activities.TwystCashHistoryActivity;
import com.twyst.app.android.fragments.AllTwystCashFragment;
import com.twyst.app.android.fragments.CreditTwystCashFragment;
import com.twyst.app.android.fragments.DebitTwystCashFragment;
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
        switch (position) {
            case 0: {
                Bundle allBundle = new Bundle();
                allBundle.putSerializable(AppConstants.BUNDLE_ALL_CASH_HISTORY, mCashHistory);
                AllTwystCashFragment allFragment = new AllTwystCashFragment();
                allFragment.setArguments(allBundle);
                return allFragment;
            }
            case 1: {
                Bundle creditBundle = new Bundle();
                creditBundle.putSerializable(AppConstants.BUNDLE_CREDIT_CASH_HISTORY, creditList);
                CreditTwystCashFragment creditFragment = new CreditTwystCashFragment();
                creditFragment.setArguments(creditBundle);
                return creditFragment;
            }
            case 2: {
                Bundle debitBundle = new Bundle();
                debitBundle.putSerializable(AppConstants.BUNDLE_DEBIT_CASH_HISTORY, debitList);
                DebitTwystCashFragment debitFragment = new DebitTwystCashFragment();
                debitFragment.setArguments(debitBundle);
                return debitFragment;
            }
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
