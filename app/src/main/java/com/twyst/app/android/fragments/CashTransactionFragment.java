package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.CashRowAdapter;
import com.twyst.app.android.model.TwystCashHistory;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

/**
 * Created by Raman on 3/16/2016.
 */
public class CashTransactionFragment extends Fragment {
    ArrayList<TwystCashHistory> list = new ArrayList<TwystCashHistory>();
    private int flag = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_cash_fragment);

        Bundle bundle = getArguments();
        list = (ArrayList<TwystCashHistory>) bundle.getSerializable(AppConstants.BUNDLE_CASH_HISTORY);
        flag = bundle.getInt(AppConstants.CASH_HISTORY_TYPE, -1);

        if (list != null && list.size() > 0) {
            setUpCashRV(recyclerView);
            return view;
        } else {
            View noDataView = inflater.inflate(R.layout.layout_no_cash_transaction, container, false);
            TextView textView = (TextView) noDataView.findViewById(R.id.tv_no_transaction);
            switch (flag) {
                case 0:{
                    textView.setText(R.string.no_cash_data);
                    break;
                }
                case 1:{
                    textView.setText(R.string.no_cash_earned);
                    break;
                }
                case 2:{
                    textView.setText(R.string.no_cash_spent);
                    break;
                }
                default:
                    textView.setText("No Data!");
            }
            return noDataView;
        }
    }

    private void setUpCashRV(RecyclerView recyclerView) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        CashRowAdapter mCashRowAdapter = new CashRowAdapter(getContext(), list);
        recyclerView.setAdapter(mCashRowAdapter);
    }


}
