package com.twyst.app.android.fragments;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.adapters.MenuAdapter;
import com.twyst.app.android.adapters.MenuExpandableAdapter;
import com.twyst.app.android.model.menu.SubCategories;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/19/2015.
 */
public class MenuPageFragment extends Fragment{// implements ObservableScrollViewCallbacks {
    private static final String ARG_SECTION_LIST = "section_list";
    private static final String ARG_CATEGORY_ID = "category_id";
    static int mScrollYInitial = 0;

//    private OnFragmentScrollChangedListener mFragmentScrollChangedListener;

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

//        final FloatingGroupExpandableListView list = (FloatingGroupExpandableListView) rootView.findViewById(R.id.menuList);

        RecyclerView menuExpandableList = (RecyclerView) rootView.findViewById(R.id.menu_recycler_view);
        menuExpandableList.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<ParentListItem> sectionsList = (ArrayList<ParentListItem>) (getArguments().getSerializable(ARG_SECTION_LIST));
        String categoryID = getArguments().getString(ARG_CATEGORY_ID);

        final MenuExpandableAdapter menuExpandableAdapter = new MenuExpandableAdapter(getActivity(),sectionsList,categoryID);
//        final MenuAdapter adapter = new MenuAdapter(getActivity(), sectionsList, categoryID);
//        final WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
//        list.setAdapter(wrapperAdapter);

        menuExpandableList.setAdapter(menuExpandableAdapter);
        OrderOnlineActivity activity = (OrderOnlineActivity) container.getContext();
        activity.addAdaptersList(menuExpandableAdapter);

//        for(int i = 0; i < wrapperAdapter.getGroupCount(); i++) {
//            list.expandGroup(i);
//        }

//        list.setOnScrollFloatingGroupListener(new FloatingGroupExpandableListView.OnScrollFloatingGroupListener() {
//
//            @Override
//            public void onScrollFloatingGroupListener(View floatingGroupView, int scrollY) {
//                float interpolation = - scrollY / (float) floatingGroupView.getHeight();
//
//                // Changing from RGB(162,201,85) to RGB(255,255,255)
//                final int greenToWhiteRed = (int) (162 + 93 * interpolation);
//                final int greenToWhiteGreen = (int) (201 + 54 * interpolation);
//                final int greenToWhiteBlue = (int) (85 + 170 * interpolation);
//                final int greenToWhiteColor = Color.argb(255, greenToWhiteRed, greenToWhiteGreen, greenToWhiteBlue);
//
//                // Changing from RGB(255,255,255) to RGB(0,0,0)
//                final int whiteToBlackRed = (int) (255 - 255 * interpolation);
//                final int whiteToBlackGreen = (int) (255 - 255 * interpolation);
//                final int whiteToBlackBlue = (int) (255 - 255 * interpolation);
//                final int whiteToBlackColor = Color.argb(255, whiteToBlackRed, whiteToBlackGreen, whiteToBlackBlue);
//
////                final ImageView image = (ImageView) floatingGroupView.findViewById(R.id.sample_activity_list_group_item_image);
////                image.setBackgroundColor(greenToWhiteColor);
//
////                final Drawable imageDrawable = image.getDrawable().mutate();
////                imageDrawable.setColorFilter(whiteToBlackColor, PorterDuff.Mode.SRC_ATOP);
//
////                final View background = floatingGroupView.findViewById(R.id.sample_activity_list_group_item_background);
////                background.setBackgroundColor(greenToWhiteColor);
////
////                final TextView text = (TextView) floatingGroupView.findViewById(R.id.sample_activity_list_group_item_text);
////                text.setTextColor(whiteToBlackColor);
//
////                final ImageView expanded = (ImageView) floatingGroupView.findViewById(R.id.sample_activity_list_group_expanded_image);
////                final Drawable expandedDrawable = expanded.getDrawable().mutate();
////                expandedDrawable.setColorFilter(whiteToBlackColor, PorterDuff.Mode.SRC_ATOP);
//            }
//        });


//        ObservableRecyclerView mRecyclerView = (ObservableRecyclerView) rootView.findViewById(R.id.menuRecyclerView);
//        mRecyclerView.setHasFixedSize(true);
////        mRecyclerView.setScrollViewCallbacks(this);

//        //Assigning resources
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        MenuAdapter menuAdapter = new MenuAdapter();
//        mRecyclerView.setAdapter(menuAdapter);

//        TextView txt = (TextView) rootView.findViewById(R.id.page_number_label);
//        int page = getArguments().getInt(ARG_PAGE_NUMBER, -1);
//        txt.setText(String.format("Page %d", page));


//        mFragmentScrollChangedListener = (OnFragmentScrollChangedListener) getActivity();

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