package com.twyst.app.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.CartAdapter;
import com.twyst.app.android.adapters.MenuAdapter;
import com.twyst.app.android.adapters.MenuTabsPagerAdapter;
import com.twyst.app.android.adapters.ScrollingOffersAdapter;
import com.twyst.app.android.model.menu.DataTransferInterface;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.MenuData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vipul Sharma on 11/16/2015.
 */
public class OrderOnlineActivity extends AppCompatActivity implements DataTransferInterface {

    private ScrollingOffersAdapter mScrollingOffersAdapter;
    private ViewPager mScrollingOffersViewPager;
    private ViewPager mMenuViewPager;
    int mLowerLimit, mUpperLimit;

    SlidingUpPanelLayout mSlidingUpPanelLayout;
    TextView tvCartCount;
    TextView tvCartTotalCost;

    RecyclerView mCartRecyclerView;
    CartAdapter mCartAdapter;
    List<MenuAdapter> mMenuAdaptersList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_online);
        setupToolBar();
        setupScrollingOfferAdapters();
        setupMenu();
        setupCartRecyclerView();

//        final RelativeLayout rlTopLayout = (RelativeLayout) findViewById(R.id.topLayout);
//        final RelativeLayout rHidableLayout = (RelativeLayout) findViewById(R.id.hideableLayout);
//
//        ViewTreeObserver observer = rHidableLayout.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                mLowerLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, rlTopLayout.getHeight() - rHidableLayout.getHeight(), getResources().getDisplayMetrics());
//                mUpperLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, rlTopLayout.getHeight(), getResources().getDisplayMetrics());
//
//                rHidableLayout.getViewTreeObserver().removeGlobalOnLayoutListener(
//                        this);
//            }
//        });
    }

    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Striker Pub");

    }

    private void setupMenu() {
        String menuString = getMenuString();
        Gson gson = new Gson();
        MenuData[] menuDataArray = gson.fromJson(menuString, MenuData[].class);
        List<MenuData> menuDataList = Arrays.asList(menuDataArray);

        MenuData menuData = getMenuData(menuDataList);

// Get the ViewPager and set it's PagerAdapter so that it can display items
        MenuTabsPagerAdapter adapter = new MenuTabsPagerAdapter(menuData.getMenuCategoriesList(), getSupportFragmentManager(), OrderOnlineActivity.this);
        mMenuViewPager = (ViewPager) findViewById(R.id.menuPager);
        mMenuViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mMenuViewPager);
//        tabLayout.setTabsFromPagerAdapter(adapter);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mMenuViewPager));
//        mMenuViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    private void setupCartRecyclerView() {
        mCartRecyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        mCartRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderOnlineActivity.this, LinearLayoutManager.VERTICAL, false);
        mCartRecyclerView.setLayoutManager(mLayoutManager);
        mCartAdapter = new CartAdapter(OrderOnlineActivity.this);
        mCartRecyclerView.setAdapter(mCartAdapter);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        tvCartCount = (TextView) findViewById(R.id.tv_cart_count);
        tvCartTotalCost = (TextView) findViewById(R.id.tvCartTotalCost);
        mSlidingUpPanelLayout.setPanelHeight(0);

        findViewById(R.id.cartLowerLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout != null &&
                (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED ||
                        mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING)) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    public void addAdaptersList(MenuAdapter menuAdapter){
        mMenuAdaptersList.add(menuAdapter);
    }

    @Override
    public void addToCart(Items item) {
        mCartAdapter.addToAdapter(item);
        updateCart();
    }

    @Override
    public void removeFromCart(Items item) {
        mCartAdapter.removeFromAdapter(item);
        updateCart();
    }

    private void updateCart() {
        tvCartTotalCost.setText(String.valueOf(mCartAdapter.getTotalCost()));
        tvCartCount.setText(String.valueOf(mCartAdapter.getmCartItemsList().size()));
        if (mCartAdapter.getmCartItemsList().size() > 0) {
            mSlidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.slidingup_panel_height));
            for (int i = 0; i < mMenuAdaptersList.size(); i++) {
                mMenuAdaptersList.get(i).setFooterEnabled(true);
                mMenuAdaptersList.get(i).notifyDataSetChanged();
            }
        } else {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            mSlidingUpPanelLayout.setPanelHeight(0);
            for (int i = 0; i < mMenuAdaptersList.size(); i++) {
                mMenuAdaptersList.get(i).setFooterEnabled(false);
                mMenuAdaptersList.get(i).notifyDataSetChanged();
            }
        }
    }

    private void setupScrollingOfferAdapters() {
        mScrollingOffersAdapter = new ScrollingOffersAdapter(null);
        mScrollingOffersViewPager = (ViewPager) findViewById(R.id.scrollingOffersPager);
        mScrollingOffersViewPager.setAdapter(mScrollingOffersAdapter);
    }
//
//    @Override
//    protected String getTagName() {
//        return OrderOnlineActivity.class.getSimpleName();
//    }
//
//    @Override
//    protected int getLayoutResource() {
//        return R.layout.activity_order_online;
//    }

    private MenuData getMenuData(List<MenuData> menuDataList) {
        for (int i = 0; i < menuDataList.size(); i++) {
            MenuData menuData = (MenuData) menuDataList.get(i);
//            if (menuData.getMenuType().equalsIgnoreCase("All") || menuData.getMenuType().equalsIgnoreCase("Delivery")) {
                return menuData;
//            }
        }
        return null;
    }

    private String getMenuString() {
        return "[{         \"status\": \"active\",         \"menu_type\": \"All\",         \"_id\": \"56690a456413aba8590f1d34\",         \"menu_categories\": [{             \"category_name\": \"Salads\",             \"_id\": \"56690a466413aba8590f1d50\",             \"sub_categories\": [{                 \"sub_category_name\": \"Default\",                 \"_id\": \"56690a466413aba8590f1d51\",                 \"items\": [{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 }, {                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Chicken Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Breast Fillet Chicken, Oven Roasted Crotons, Lettuce Parmesan, Cheese, Caesar Salad Dressing\",                     \"_id\": \"56690a466413aba8590f1d52\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": false,                     \"item_tags\": [                         \"chicken\",                         \"caesar\",                         \"salad\"                     ],                     \"is_available\": true                 }]             },{                 \"sub_category_name\": \"Default\",                 \"_id\": \"56690a466413aba8590f1d51\",                 \"items\": [{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 }, {                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 },{                     \"item_name\": \"Chicken Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Breast Fillet Chicken, Oven Roasted Crotons, Lettuce Parmesan, Cheese, Caesar Salad Dressing\",                     \"_id\": \"56690a466413aba8590f1d52\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": false,                     \"item_tags\": [                         \"chicken\",                         \"caesar\",                         \"salad\"                     ],                     \"is_available\": true                 }]             }]         }, {             \"category_name\": \"Veg Pizza\",             \"_id\": \"56690a466413aba8590f1d35\",             \"sub_categories\": [{                 \"sub_category_name\": \"Default\",                 \"_id\": \"56690a466413aba8590f1d36\",                 \"items\": [{                     \"item_name\": \"Traditional Margherita\",                     \"item_cost\": 142,                     \"item_description\": \"Tomatoes Oregano, Mozzarella & Bocconcini Cheese Toped with Fresh Basil\",                     \"option_title\": \"Size\",                     \"_id\": \"56690a466413aba8590f1d37\",                     \"options\": [{                         \"option_value\": \"6 inch\",                         \"option_cost\": 142,                         \"_id\": \"5668155f6413aba8590f1d08\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 32,                                 \"_id\": \"56690a466413aba8590f1d4d\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 21,                                 \"_id\": \"56690a466413aba8590f1d4c\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 32,                                 \"_id\": \"56690a466413aba8590f1d4b\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Seafood & Meat\",                                 \"addon_cost\": 53,                                 \"_id\": \"56690a466413aba8590f1d4a\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d4f\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d4e\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }, {                         \"option_value\": \"9 inch\",                         \"option_cost\": 307,                         \"_id\": \"5668155f6413aba8590f1d08\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 47,                                 \"_id\": \"56690a466413aba8590f1d47\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 32,                                 \"_id\": \"56690a466413aba8590f1d46\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 42,                                 \"_id\": \"56690a466413aba8590f1d45\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Seafood & Meat\",                                 \"addon_cost\": 84,                                 \"_id\": \"56690a466413aba8590f1d44\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d49\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d48\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }, {                         \"option_value\": \"11 inch\",                         \"option_cost\": 440,                         \"_id\": \"5668155f6413aba8590f1d08\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 63,                                 \"_id\": \"56690a466413aba8590f1d41\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 42,                                 \"_id\": \"56690a466413aba8590f1d40\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 63,                                 \"_id\": \"56690a466413aba8590f1d3f\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Sea Food & Meat\",                                 \"addon_cost\": 116,                                 \"_id\": \"56690a466413aba8590f1d3e\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d43\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d42\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }, {                         \"option_value\": \"15 inch\",                         \"option_cost\": 699,                         \"_id\": \"5668155f6413aba8590f1d08\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 84,                                 \"_id\": \"56690a466413aba8590f1d3b\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 63,                                 \"_id\": \"56690a466413aba8590f1d3a\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 84,                                 \"_id\": \"56690a466413aba8590f1d39\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Sea Food & Meat\",                                 \"addon_cost\": 158,                                 \"_id\": \"56690a466413aba8590f1d38\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d3d\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d3c\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"margerita\",                         \"traditonal\",                         \"classic\"                     ],                     \"is_available\": true                 }]             }]         }],         \"outlet\": \"530ef84902bc583c21000004\"     }]  ";
    }

}
