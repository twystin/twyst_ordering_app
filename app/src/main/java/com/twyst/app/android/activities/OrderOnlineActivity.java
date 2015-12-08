package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vipul Sharma on 11/16/2015.
 */
public class OrderOnlineActivity extends BaseActivity implements DataTransferInterface {

    private ScrollingOffersAdapter mScrollingOffersAdapter;
    private ViewPager mScrollingOffersViewPager;
    private ViewPager mMenuViewPager;
    int mLowerLimit, mUpperLimit;

    SlidingUpPanelLayout mSlidingUpPanelLayout;
    TextView tvCartCount;

    RecyclerView mCartRecyclerView;
    CartAdapter mCartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupScrollingOfferAdapters();
        setupMenu();
        setupCartRecyclerView();

        final RelativeLayout rlTopLayout = (RelativeLayout) findViewById(R.id.topLayout);
        final RelativeLayout rHidableLayout = (RelativeLayout) findViewById(R.id.hideableLayout);

        ViewTreeObserver observer = rHidableLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mLowerLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, rlTopLayout.getHeight() - rHidableLayout.getHeight(), getResources().getDisplayMetrics());
                mUpperLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, rlTopLayout.getHeight(), getResources().getDisplayMetrics());

                rHidableLayout.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
            }
        });
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
        mSlidingUpPanelLayout.setPanelHeight(0);
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
        tvCartCount.setText(String.valueOf(mCartAdapter.getmCartItemsList().size()));
        if (mCartAdapter.getmCartItemsList().size() > 0) {
            mSlidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.slidingup_panel_height));
        } else {
            mSlidingUpPanelLayout.setPanelHeight(0);
        }
    }

    private void setupScrollingOfferAdapters() {
        mScrollingOffersAdapter = new ScrollingOffersAdapter(null);
        mScrollingOffersViewPager = (ViewPager) findViewById(R.id.scrollingOffersPager);
        mScrollingOffersViewPager.setAdapter(mScrollingOffersAdapter);
    }

    @Override
    protected String getTagName() {
        return OrderOnlineActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_online;
    }

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
        return "[{\"status\":\"active\",\"menu_categories\":[{\"sub_categories\":[{\"sub_category_name\":\"Default\",\"items\":[{\"options\":[{\"sub_options\":[],\"addons\":[],\"option_value\":\"Default\",\"option_cost\":168}],\"is_vegetarian\":true,\"option_is_addon\":false,\"item_name\":\"Garden Dream\",\"item_cost\":168,\"item_description\":\"Lettuce, Tomato, Caramalized Onions\",\"item_tags\":[\"garden\",\"dream\",\"burger\",\"veg\"],\"option_title\":\"Default\"}]},{\"sub_category_name\":\"Default1\",\"items\":[{\"options\":[{\"sub_options\":[],\"addons\":[],\"option_value\":\"Default\",\"option_cost\":168}],\"is_vegetarian\":true,\"option_is_addon\":false,\"item_name\":\"Garden Dream1\",\"item_cost\":168,\"item_description\":\"Lettuce, Tomato, Caramalized Onions\",\"item_tags\":[\"garden\",\"dream\",\"burger\",\"veg\"],\"option_title\":\"Default\"}]},{\"sub_category_name\":\"Default2\",\"items\":[{\"options\":[{\"sub_options\":[],\"addons\":[],\"option_value\":\"Default\",\"option_cost\":168}],\"is_vegetarian\":true,\"option_is_addon\":false,\"item_name\":\"Garden Dream2\",\"item_cost\":168,\"item_description\":\"Lettuce, Tomato, Caramalized Onions\",\"item_tags\":[\"garden\",\"dream\",\"burger\",\"veg\"],\"option_title\":\"Default\"}]}],\"category_name\":\"Burgers\"},{\"sub_categories\":[{\"sub_category_name\":\"Default\",\"items\":[{\"options\":[{\"sub_options\":[],\"addons\":[{\"addon_set\":[{\"addon_value\":\"Grilled Chicken\",\"addon_cost\":38}],\"addon_title\":\"Addons\"}],\"option_value\":\"Default\",\"option_cost\":138}],\"is_vegetarian\":false,\"\t\":false,\"item_name\":\"Garden Goodness\",\"item_cost\":138,\"item_description\":\"Garden Goodness\",\"item_tags\":[\"garden\",\"goodness\"],\"option_title\":\"Default\"}]}],\"category_name\":\"Salads\"},{\"sub_categories\":[{\"sub_category_name\":\"Default\",\"items\":[{\"options\":[{\"sub_options\":[],\"addons\":[],\"option_value\":\"Thin\",\"option_cost\":348},{\"sub_options\":[],\"addons\":[],\"option_value\":\"Pan\",\"option_cost\":378}],\"is_vegetarian\":true,\"option_is_addon\":false,\"item_name\":\"Garden Field\",\"item_cost\":348,\"item_description\":\"sample\",\"item_tags\":[\"sample\",\"tag\",\"set\"],\"option_title\":\"Crust\"}]}],\"category_name\":\"Pizza\"}],\"menu_type\":\"Menu\",\"outlet\":\"54d0b4a6ea25f3200dfe124a\"}]";
    }

}
