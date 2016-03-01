package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.TwystCashHistoryActivity;
import com.twyst.app.android.adapters.CashRowAdapter;

import java.util.ArrayList;

/**
 * Created by Raman on 2/4/2016.
 */
public class DebitTwystCashFragment extends Fragment {
    ArrayList<TwystCashHistoryActivity.CashHistory> list = TwystCashHistoryActivity.getDebitList();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_layout, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_cash_fragment);

        setUpDebitCashRV(recyclerView);
        return view;
    }

    private void setUpDebitCashRV(RecyclerView recyclerView) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        CashRowAdapter mCashRowAdapter = new CashRowAdapter(getContext(),list);
        recyclerView.setAdapter(mCashRowAdapter);
    }
}
