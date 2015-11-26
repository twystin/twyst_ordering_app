package com.twyst.app.android.fragments;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.adapters.MenuAdapter;
import com.twyst.app.android.model.menu.Sections;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/19/2015.
 */
public class MenuPageFragment extends Fragment{// implements ObservableScrollViewCallbacks {
    private static final String ARG_SECTION_LIST = "section_list";
    static int mScrollYInitial = 0;

    private OnFragmentScrollChangedListener mFragmentScrollChangedListener;

    public MenuPageFragment() {
    }

    public static MenuPageFragment newInstance(ArrayList<Sections> sectionsList) {
        MenuPageFragment fragment = new MenuPageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_LIST, sectionsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_layout, container, false);
        ObservableRecyclerView mRecyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.menuRecyclerView);
        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setScrollViewCallbacks(this);

        //Assigning resources
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        MenuAdapter menuAdapter = new MenuAdapter();
        mRecyclerView.setAdapter(menuAdapter);

//        TextView txt = (TextView) rootView.findViewById(R.id.page_number_label);
//        int page = getArguments().getInt(ARG_PAGE_NUMBER, -1);
//        txt.setText(String.format("Page %d", page));


        mFragmentScrollChangedListener = (OnFragmentScrollChangedListener) getActivity();

        return rootView;
    }

//    @Override
//    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        if (mScrollYInitial==0){
//            mScrollYInitial = scrollY;
//        }
//        if (scrollY > mScrollYInitial){
//            mFragmentScrollChangedListener.onFragmentScrollChange(scrollY,firstScroll,dragging);
//        }
//    }
//
//    @Override
//    public void onDownMotionEvent() {
//
//    }
//
//    @Override
//    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//
//    }

    public interface OnFragmentScrollChangedListener {
        public void onFragmentScrollChange(int scrollY, boolean firstScroll, boolean dragging);
    }
}