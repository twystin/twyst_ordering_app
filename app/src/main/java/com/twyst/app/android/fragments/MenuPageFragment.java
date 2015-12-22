package com.twyst.app.android.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.adapters.MenuExpandableAdapter;
import com.twyst.app.android.model.menu.SubCategories;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/19/2015.
 */
public class MenuPageFragment extends Fragment {// implements ObservableScrollViewCallbacks {
    private static final String ARG_SECTION_LIST = "section_list";
    private static final String ARG_CATEGORY_ID = "category_id";
    static int mScrollYInitial = 0;

    public MenuPageFragment() {
    }

    public static MenuPageFragment newInstance(ArrayList<SubCategories> sectionsList, String categoryID) {
        MenuPageFragment fragment = new MenuPageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_LIST, sectionsList);
        args.putString(ARG_CATEGORY_ID, categoryID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_layout, container, false);

        ArrayList<ParentListItem> sectionsList = (ArrayList<ParentListItem>) (getArguments().getSerializable(ARG_SECTION_LIST));
        String categoryID = getArguments().getString(ARG_CATEGORY_ID);
        final MenuExpandableAdapter menuExpandableAdapter = new MenuExpandableAdapter(getActivity(), sectionsList, categoryID);

        RecyclerView menuExpandableList = (RecyclerView) rootView.findViewById(R.id.menu_recycler_view);
        menuExpandableList.setLayoutManager(new LinearLayoutManager(getActivity()));
        menuExpandableList.setAdapter(menuExpandableAdapter);

        OrderOnlineActivity activity = (OrderOnlineActivity) container.getContext();
        activity.addAdaptersList(menuExpandableAdapter);

        return rootView;
    }
}