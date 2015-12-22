package com.twyst.app.android.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.CartAdapter;
import com.twyst.app.android.adapters.MenuAdapter;
import com.twyst.app.android.adapters.MenuExpandableAdapter;
import com.twyst.app.android.adapters.MenuTabsPagerAdapter;
import com.twyst.app.android.adapters.ScrollingOffersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.MenuData;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 11/16/2015.
 */
public class OrderOnlineActivity extends AppCompatActivity implements MenuExpandableAdapter.DataTransferInterfaceMenu, CartAdapter.DataTransferInterfaceCart {

    private ScrollingOffersAdapter mScrollingOffersAdapter;
    private ViewPager mScrollingOffersViewPager;
    private ViewPager mMenuViewPager;
    int mLowerLimit, mUpperLimit;

    SlidingUpPanelLayout mSlidingUpPanelLayout;
    TextView tvCartCount;
    TextView tvCartTotalCost;

    RecyclerView mCartRecyclerView;
    CartAdapter mCartAdapter;
    List<MenuExpandableAdapter> mMenuAdaptersList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_online);
        setupToolBar();
        setupTopLayout();
        setupScrollingOfferAdapters();
        setupMenu();
//        fetchMenu();
        setupCartRecyclerView();

    }

    private void setupTopLayout() {
        final TextView outletDeliveryTime = (TextView) findViewById(R.id.outletDeliveryTime);
        outletDeliveryTime.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.outlet_estimated_icon);
                        int height = outletDeliveryTime.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        outletDeliveryTime.setCompoundDrawables(img, null, null, null);
                        outletDeliveryTime.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        final TextView outletMinimumOrder = (TextView) findViewById(R.id.outletMinimumOrder);
        outletMinimumOrder.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.outlet_min_order_icon);
                        int height = outletMinimumOrder.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        outletMinimumOrder.setCompoundDrawables(img, null, null, null);
                        outletMinimumOrder.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
    }

    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float ratio = (float) verticalOffset / (-1 * scrollRange);
                if (ratio > .68f) {
                    collapsingToolbar.setTitle("Striker Pub & Brewery");
                } else {
                    collapsingToolbar.setTitle("");
                }
            }
        });
    }

    private void setupMenu() {
        String menuString = getMenuLongString();
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

    private void fetchMenu() {
        HttpService.getInstance().getMenu("56740f12b6188687102c8b9d", "8t2MdEGlJCWD4NXPJ4mWXVlm7VkdNXfe", new Callback<BaseResponse<MenuData>>() {
            @Override
            public void success(BaseResponse<MenuData> menuDataBaseResponse, Response response) {
                MenuData menuData = menuDataBaseResponse.getData();
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

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OrderOnlineActivity.this, "No data found", Toast.LENGTH_SHORT).show();
            }
        });
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


        findViewById(R.id.bCheckOutCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOut();
            }
        });

        final TextView tvNextMenu = (TextView) findViewById(R.id.tvNextMenu);
        tvNextMenu.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.checkout_arrow);
                        int height = tvNextMenu.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height * 2, height);
                        tvNextMenu.setCompoundDrawables(null, null, img, null);
                        tvNextMenu.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        findViewById(R.id.fNextMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        Button bAddNewItem = (Button) findViewById(R.id.bAddNewItem);
        bAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        final TextView tvCheckOutCart = (TextView) findViewById(R.id.tvCheckOutCart);
        tvCheckOutCart.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.checkout_arrow);
                        int height = tvCheckOutCart.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        tvCheckOutCart.setCompoundDrawables(null, null, img, null);
                        tvCheckOutCart.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });


        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                findViewById(R.id.tvTAX).setVisibility(View.VISIBLE);
                findViewById(R.id.fNextMenu).setAlpha(1.0f - slideOffset);
                findViewById(R.id.bAddNewItem).setAlpha(slideOffset);
                findViewById(R.id.tvTAX).setAlpha(slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                findViewById(R.id.fNextMenu).setVisibility(View.VISIBLE);
                findViewById(R.id.bAddNewItem).setVisibility(View.GONE);
                findViewById(R.id.tvTAX).setVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                findViewById(R.id.fNextMenu).setVisibility(View.GONE);
                findViewById(R.id.bAddNewItem).setVisibility(View.VISIBLE);
                findViewById(R.id.tvTAX).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }

    private void checkOut() {
        OrderSummary orderSummary = new OrderSummary(mCartAdapter.getmCartItemsList(), "530ef84902bc583c21000004", "28.6", "77.2");


        Intent checkOutIntent = new Intent(OrderOnlineActivity.this, OrderSummaryActivity.class);
        startActivity(checkOutIntent);
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


    public void addAdaptersList(MenuExpandableAdapter menuExpandableAdapter) {
        mMenuAdaptersList.add(menuExpandableAdapter);
    }


    @Override
    public void addCart(Items cartItem) { //cartItem from cart : to be added
        cartItem.setItemQuantity(cartItem.getItemQuantity() + 1);
        Items menuItem = cartItem.getItemOriginalReference();
        menuItem.setItemQuantity(menuItem.getItemQuantity() + 1);
        updateCartMenu();
    }

    @Override
    public void removeCart(Items cartItem) { //cartItem from cart: to be removed
        cartItem.setItemQuantity(cartItem.getItemQuantity() - 1);
        if (cartItem.getItemQuantity() == 0) {
            mCartAdapter.getmCartItemsList().remove(cartItem);
        }
        Items menuItem = cartItem.getItemOriginalReference();
        menuItem.setItemQuantity(menuItem.getItemQuantity() - 1);
        updateCartMenu();
    }

    @Override
    public void addMenu(Items cartItemToBeAdded) {// (customised) cartItem from menu : to be added
        Items menuItem = cartItemToBeAdded.getItemOriginalReference();
        menuItem.setItemQuantity(menuItem.getItemQuantity() + 1);
        mCartAdapter.addToCart(cartItemToBeAdded);
        updateCartMenu();
    }

    @Override
    public void removeMenu(Items item) { //item from menu : to be removed
        int index = getCartIndex(item);
        if (index >= 0) {
            Items cartItem = mCartAdapter.getmCartItemsList().get(index);
            cartItem.setItemQuantity(cartItem.getItemQuantity() - 1);
            if (cartItem.getItemQuantity() == 0) {
                mCartAdapter.getmCartItemsList().remove(index);
            }
//            else {
//                mCartAdapter.getmCartItemsList().set(index, cartItem);
//            }
            item.setItemQuantity(item.getItemQuantity() - 1);
            updateCartMenu();
        } else { // Duplicate item available in cart
            Toast.makeText(OrderOnlineActivity.this, getResources().getString(R.string.multiple_customisation_error), Toast.LENGTH_LONG).show();
        }
    }

    private int getCartIndex(Items item) {
        int index = -1;
        for (int i = 0; i < mCartAdapter.getmCartItemsList().size(); i++) {
            Items cartItem = mCartAdapter.getmCartItemsList().get(i);
            if (cartItem.getId().equals(item.getId())) {
                if (index >= 0) {
                    return -1; //Duplicate item found in cart
                } else {
                    index = i; //First item found in cart
                }
            }
        }
        return index;
    }

    private boolean isDuplicateItemAvailableCart(Items item) {
        for (Items cartItem : mCartAdapter.getmCartItemsList()) {
            if (cartItem.getId().equals(item.getId())) return true;
        }
        return false;
    }

    private void updateCartMenu() {
        mCartAdapter.notifyDataSetChanged();
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
        return "[{         \"status\": \"active\",         \"menu_type\": \"All\",         \"_id\": \"56690a456413aba8590f1d34\",         \"menu_categories\": [{             \"category_name\": \"Salads\",             \"_id\": \"56690a466413aba8590f1d50\",             \"sub_categories\": [{                 \"sub_category_name\": \"Default\",                 \"_id\": \"56690a466413aba8590f1d51\",                 \"items\": [{                     \"item_name\": \"Cottage Cheese Caesar Salad\",                     \"item_cost\": 229,                     \"item_description\": \"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",                     \"_id\": \"56690a466413aba8590f1d53\",                     \"options\": [],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"salad\",                         \"caesar\",                         \"veg\"                     ],                     \"is_available\": true                 }]             }]         }, {             \"category_name\": \"Veg Pizza\",             \"_id\": \"56690a466413aba8590f1d35\",             \"sub_categories\": [{                 \"sub_category_name\": \"Default\",                 \"_id\": \"56690a466413aba8590f1d36\",                 \"items\": [{                     \"item_name\": \"Traditional Margherita\",                     \"item_cost\": 142,                     \"item_description\": \"Tomatoes Oregano, Mozzarella & Bocconcini Cheese Toped with Fresh Basil\",                     \"option_title\": \"Size\",                     \"_id\": \"56690a466413aba8590f1d37\",                     \"options\": [{                         \"option_value\": \"6 inch\",                         \"option_cost\": 142,                         \"_id\": \"5668155f6413aba8590f1d10\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 32,                                 \"_id\": \"56690a466413aba8590f1d4d\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 21,                                 \"_id\": \"56690a466413aba8590f1d4c\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 32,                                 \"_id\": \"56690a466413aba8590f1d4b\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Seafood & Meat\",                                 \"addon_cost\": 53,                                 \"_id\": \"56690a466413aba8590f1d4a\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d4f\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d4e\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }, {                         \"option_value\": \"9 inch\",                         \"option_cost\": 307,                         \"_id\": \"5668155f6413aba8590f1d12\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 47,                                 \"_id\": \"56690a466413aba8590f1d47\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 32,                                 \"_id\": \"56690a466413aba8590f1d46\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 42,                                 \"_id\": \"56690a466413aba8590f1d45\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Seafood & Meat\",                                 \"addon_cost\": 84,                                 \"_id\": \"56690a466413aba8590f1d44\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d49\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d48\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }, {                         \"option_value\": \"11 inch\",                         \"option_cost\": 440,                         \"_id\": \"5668155f6413aba8590f1d14\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 63,                                 \"_id\": \"56690a466413aba8590f1d41\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 42,                                 \"_id\": \"56690a466413aba8590f1d40\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 63,                                 \"_id\": \"56690a466413aba8590f1d3f\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Sea Food & Meat\",                                 \"addon_cost\": 116,                                 \"_id\": \"56690a466413aba8590f1d3e\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d43\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d42\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }, {                         \"option_value\": \"15 inch\",                         \"option_cost\": 699,                         \"_id\": \"5668155f6413aba8590f1d16\",                         \"addons\": [{                             \"addon_title\": \"Extras\",                             \"_id\": \"5668155f6413aba8590f1d0a\",                             \"addon_set\": [{                                 \"addon_value\": \"Extra Cheese\",                                 \"addon_cost\": 84,                                 \"_id\": \"56690a466413aba8590f1d3b\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Veg Topping\",                                 \"addon_cost\": 63,                                 \"_id\": \"56690a466413aba8590f1d3a\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Chicken Topping\",                                 \"addon_cost\": 84,                                 \"_id\": \"56690a466413aba8590f1d39\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"addon_value\": \"Extra Sea Food & Meat\",                                 \"addon_cost\": 158,                                 \"_id\": \"56690a466413aba8590f1d38\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"sub_options\": [{                             \"sub_option_title\": \"Crust\",                             \"_id\": \"5668155f6413aba8590f1d09\",                             \"sub_option_set\": [{                                 \"sub_option_value\": \"Super Thin\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d3d\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }, {                                 \"sub_option_value\": \"Original Crust\",                                 \"sub_option_cost\": 1,                                 \"_id\": \"56690a466413aba8590f1d3c\",                                 \"is_vegetarian\": true,                                 \"is_available\": true                             }]                         }],                         \"is_vegetarian\": true,                         \"is_available\": true                     }],                     \"option_is_addon\": false,                     \"item_available_on\": [],                     \"item_availability\": {                         \"regular_item\": true                     },                     \"is_vegetarian\": true,                     \"item_tags\": [                         \"margerita\",                         \"traditonal\",                         \"classic\"                     ],                     \"is_available\": true                 }]             }]         }],         \"outlet\": \"530ef84902bc583c21000004\"     }]  ";
    }

    private String getMenuLongString() {
        return "[{\"status\":\"active\",\"menu_type\":\"All\",\"_id\":\"56690a456413aba8590f1d34\",\"menu_categories\":[{\"category_name\":\"Salads\",\"_id\":\"56690a466413aba8590f1d50\",\"sub_categories\":[{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]},{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d51\",\"items\":[{\"item_name\":\"Cottage Cheese Caesar Salad\",\"item_cost\":229,\"item_description\":\"Lettuce, Cottage Cheese, Oven Roasted Croutons, Pamesan Cheese & Fresh Chedeer Dressing\",\"_id\":\"56690a466413aba8590f1d53\",\"options\":[],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"salad\",\"caesar\",\"veg\"],\"is_available\":true}]}]},{\"category_name\":\"Veg Pizza\",\"_id\":\"56690a466413aba8590f1d35\",\"sub_categories\":[{\"sub_category_name\":\"Default\",\"_id\":\"56690a466413aba8590f1d36\",\"items\":[{\"item_name\":\"Traditional Margherita\",\"item_cost\":142,\"item_description\":\"Tomatoes Oregano, Mozzarella & Bocconcini Cheese Toped with Fresh Basil\",\"option_title\":\"Size\",\"_id\":\"56690a466413aba8590f1d37\",\"options\":[{\"option_value\":\"6 inch\",\"option_cost\":142,\"_id\":\"5668155f6413aba8590f1d10\",\"addons\":[{\"addon_title\":\"Extras\",\"_id\":\"5668155f6413aba8590f1d0a\",\"addon_set\":[{\"addon_value\":\"Extra Cheese\",\"addon_cost\":32,\"_id\":\"56690a466413aba8590f1d4d\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Veg Topping\",\"addon_cost\":21,\"_id\":\"56690a466413aba8590f1d4c\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Chicken Topping\",\"addon_cost\":32,\"_id\":\"56690a466413aba8590f1d4b\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Seafood & Meat\",\"addon_cost\":53,\"_id\":\"56690a466413aba8590f1d4a\",\"is_vegetarian\":true,\"is_available\":true}]}],\"sub_options\":[{\"sub_option_title\":\"Crust\",\"_id\":\"5668155f6413aba8590f1d09\",\"sub_option_set\":[{\"sub_option_value\":\"Super Thin\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d4f\",\"is_vegetarian\":true,\"is_available\":true},{\"sub_option_value\":\"Original Crust\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d4e\",\"is_vegetarian\":true,\"is_available\":true}]}],\"is_vegetarian\":true,\"is_available\":true},{\"option_value\":\"9 inch\",\"option_cost\":307,\"_id\":\"5668155f6413aba8590f1d12\",\"addons\":[{\"addon_title\":\"Extras\",\"_id\":\"5668155f6413aba8590f1d0a\",\"addon_set\":[{\"addon_value\":\"Extra Cheese\",\"addon_cost\":47,\"_id\":\"56690a466413aba8590f1d47\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Veg Topping\",\"addon_cost\":32,\"_id\":\"56690a466413aba8590f1d46\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Chicken Topping\",\"addon_cost\":42,\"_id\":\"56690a466413aba8590f1d45\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Seafood & Meat\",\"addon_cost\":84,\"_id\":\"56690a466413aba8590f1d44\",\"is_vegetarian\":true,\"is_available\":true}]}],\"sub_options\":[{\"sub_option_title\":\"Crust\",\"_id\":\"5668155f6413aba8590f1d09\",\"sub_option_set\":[{\"sub_option_value\":\"Super Thin\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d49\",\"is_vegetarian\":true,\"is_available\":true},{\"sub_option_value\":\"Original Crust\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d48\",\"is_vegetarian\":true,\"is_available\":true}]}],\"is_vegetarian\":true,\"is_available\":true},{\"option_value\":\"11 inch\",\"option_cost\":440,\"_id\":\"5668155f6413aba8590f1d14\",\"addons\":[{\"addon_title\":\"Extras\",\"_id\":\"5668155f6413aba8590f1d0a\",\"addon_set\":[{\"addon_value\":\"Extra Cheese\",\"addon_cost\":63,\"_id\":\"56690a466413aba8590f1d41\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Veg Topping\",\"addon_cost\":42,\"_id\":\"56690a466413aba8590f1d40\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Chicken Topping\",\"addon_cost\":63,\"_id\":\"56690a466413aba8590f1d3f\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Sea Food & Meat\",\"addon_cost\":116,\"_id\":\"56690a466413aba8590f1d3e\",\"is_vegetarian\":true,\"is_available\":true}]}],\"sub_options\":[{\"sub_option_title\":\"Crust\",\"_id\":\"5668155f6413aba8590f1d09\",\"sub_option_set\":[{\"sub_option_value\":\"Super Thin\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d43\",\"is_vegetarian\":true,\"is_available\":true},{\"sub_option_value\":\"Original Crust\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d42\",\"is_vegetarian\":true,\"is_available\":true}]}],\"is_vegetarian\":true,\"is_available\":true},{\"option_value\":\"15 inch\",\"option_cost\":699,\"_id\":\"5668155f6413aba8590f1d16\",\"addons\":[{\"addon_title\":\"Extras\",\"_id\":\"5668155f6413aba8590f1d0a\",\"addon_set\":[{\"addon_value\":\"Extra Cheese\",\"addon_cost\":84,\"_id\":\"56690a466413aba8590f1d3b\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Veg Topping\",\"addon_cost\":63,\"_id\":\"56690a466413aba8590f1d3a\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Chicken Topping\",\"addon_cost\":84,\"_id\":\"56690a466413aba8590f1d39\",\"is_vegetarian\":true,\"is_available\":true},{\"addon_value\":\"Extra Sea Food & Meat\",\"addon_cost\":158,\"_id\":\"56690a466413aba8590f1d38\",\"is_vegetarian\":true,\"is_available\":true}]}],\"sub_options\":[{\"sub_option_title\":\"Crust\",\"_id\":\"5668155f6413aba8590f1d09\",\"sub_option_set\":[{\"sub_option_value\":\"Super Thin\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d3d\",\"is_vegetarian\":true,\"is_available\":true},{\"sub_option_value\":\"Original Crust\",\"sub_option_cost\":1,\"_id\":\"56690a466413aba8590f1d3c\",\"is_vegetarian\":true,\"is_available\":true}]}],\"is_vegetarian\":true,\"is_available\":true}],\"option_is_addon\":false,\"item_available_on\":[],\"item_availability\":{\"regular_item\":true},\"is_vegetarian\":true,\"item_tags\":[\"margerita\",\"traditonal\",\"classic\"],\"is_available\":true}]}]}],\"outlet\":\"530ef84902bc583c21000004\"}]";
    }
}
