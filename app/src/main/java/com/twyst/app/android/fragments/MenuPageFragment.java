package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class MenuPageFragment extends Fragment {
    private static final String ARG_SUB_CATEGORIES_LIST = "sub_categories_list";
    private static final String ARG_IS_RECOMMENDED = "is_recommended";

    public MenuPageFragment() {
    }

    public static MenuPageFragment newInstance(ArrayList<SubCategories> subCategoriesList, boolean isRecommended) {
        MenuPageFragment fragment = new MenuPageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SUB_CATEGORIES_LIST, subCategoriesList);
        args.putBoolean(ARG_IS_RECOMMENDED, isRecommended);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_layout, container, false);

        ArrayList<ParentListItem> subCategoriesList = (ArrayList<ParentListItem>) (getArguments().getSerializable(ARG_SUB_CATEGORIES_LIST));
        boolean isRecommended = getArguments().getBoolean(ARG_IS_RECOMMENDED);

        RecyclerView menuExpandableList = (RecyclerView) rootView.findViewById(R.id.menu_recycler_view);
        menuExpandableList.setHasFixedSize(true);
        menuExpandableList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        final MenuExpandableAdapter menuExpandableAdapter = new MenuExpandableAdapter(getActivity(), subCategoriesList, menuExpandableList, isRecommended);
        menuExpandableList.setAdapter(menuExpandableAdapter);

        OrderOnlineActivity activity = (OrderOnlineActivity) container.getContext();
        activity.addAdaptersList(menuExpandableAdapter);

        return rootView;
    }
}