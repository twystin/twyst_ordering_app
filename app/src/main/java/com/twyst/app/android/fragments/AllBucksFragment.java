package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.TwystBucksHistoryActivity;
import com.twyst.app.android.adapters.BucksRowAdapter;

import java.util.ArrayList;

/**
 * Created by Raman on 2/4/2016.
 */
public class AllBucksFragment extends Fragment {
    ArrayList<TwystBucksHistoryActivity.BucksHistory> list = TwystBucksHistoryActivity.getBucksData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bucks_layout, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_bucks_fragment);

        setUpAllBucksRV(recyclerView);
        return view;
    }

    private void setUpAllBucksRV(RecyclerView recyclerView) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        BucksRowAdapter mBucksRowAdapter = new BucksRowAdapter(getContext(),list);
        recyclerView.setAdapter(mBucksRowAdapter);
    }


}
