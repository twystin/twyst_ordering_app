package com.twyst.app.android.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.CartAdapter;
import com.twyst.app.android.adapters.MenuExpandableAdapter;
import com.twyst.app.android.adapters.MenuTabsPagerAdapter;
import com.twyst.app.android.adapters.ScrollingOffersAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.Offer;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.ReorderMenuAndCart;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.MenuCategories;
import com.twyst.app.android.model.menu.MenuData;
import com.twyst.app.android.model.menu.SubCategories;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.SharedPreferenceSingleton;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vipul Sharma on 11/16/2015.
 */
public class OrderOnlineActivity extends AppCompatActivity implements MenuExpandableAdapter.DataTransferInterfaceMenu, CartAdapter.DataTransferInterfaceCart, SearchView.OnQueryTextListener {
    private ScrollingOffersAdapter mScrollingOffersAdapter;
    private ViewPager mScrollingOffersViewPager;
    private ViewPager mMenuViewPager;

    SlidingUpPanelLayout mSlidingUpPanelLayout;
    TextView tvCartCount;
    TextView tvCartTotalCost;

    private CircularProgressBar circularProgressBar;
    private CircularProgressBar scrollingOfffersProgressBar;

    MenuExpandableAdapter mSearchExpandableAdapter;

    private boolean mSearchViewToShow;
    //Toolbar search widget
    private SearchView searchView;

    private MenuTabsPagerAdapter mMenuTabsPagerAdapter;
    private String mOutletId;
    RecyclerView mCartRecyclerView;
    CartAdapter mCartAdapter;
    List<MenuExpandableAdapter> mMenuAdaptersList = new ArrayList();
    boolean ifReordered = false;
    //    private ArrayList<Items> cartItemsTobeAddedFromReorder = null;
    private ReorderMenuAndCart reorderMenuAndCart = null;
    private Outlet mOutlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_online);

        if (savedInstanceState != null) {
            finish();
        }

        mOutlet = (Outlet) getIntent().getSerializableExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT);

        // To be deleted
        if (mOutlet == null) {
            mOutlet = new Outlet();
        }

        setupToolBar();
        setupTopLayout();
        setupScrollingOfferAdapters();
        setupCartRecyclerView();
        fetchMenu();

        ifReordered = checkifReordered();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    public boolean checkifReordered() {
        reorderMenuAndCart = (ReorderMenuAndCart) getIntent().getSerializableExtra(AppConstants.INTENT_PLACE_REORDER);
        return (reorderMenuAndCart != null);
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
        if (mOutlet.getDeliveryTime() != null) {
            outletDeliveryTime.setText(" Delivers in " + mOutlet.getDeliveryTime() + " minutes");
        }

        final TextView outletMinimumOrder = (TextView) findViewById(R.id.outletMinimumOrder);
        outletMinimumOrder.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = getResources().getDrawable(
                                R.drawable.bill_white);
                        int height = outletMinimumOrder.getMeasuredHeight() * 2 / 3;
                        img.setBounds(0, 0, height, height);
                        outletMinimumOrder.setCompoundDrawables(img, null, null, null);
                        outletMinimumOrder.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
        if (mOutlet.getMinimumOrder() != null) {
            outletMinimumOrder.setText("Minimum order: " + Utils.costString(Double.parseDouble(mOutlet.getMinimumOrder())));
        }

        ImageView ivOutletImage = (ImageView) findViewById(R.id.outletImage);
        if (mOutlet.getBackground() != null)
            Glide.with(ivOutletImage.getContext())
                    .load(mOutlet.getBackground())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivOutletImage);
    }

    private void setupToolBar() {
        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mOutlet.getName() != null) {
            this.setTitle(mOutlet.getName());
        }

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float ratio = (float) verticalOffset / (-1 * scrollRange);
                if (ratio > .99f) {
                    collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.transparent));
                }
            }
        });
    }

    private void fetchMenu() {
        String menuId;
        if (ifReordered) {
            menuId = getIntent().getStringExtra(AppConstants.INTENT_PLACE_REORDER_MENUID);
        } else {
            menuId = mOutlet.getMenuId();
        }

        if (ifReordered) {
            setupMenuFetched(reorderMenuAndCart.getMenuData());
            hideProgressHUDInLayout();
            for (Items item : reorderMenuAndCart.getCartItemsList()) {
                addMenu(item);
            }
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            findViewById(R.id.fNextMenu).setVisibility(View.GONE);
            findViewById(R.id.bAddNewItem).setVisibility(View.VISIBLE);
        } else {

            HttpService.getInstance().getMenu(menuId, UtilMethods.getUserToken(OrderOnlineActivity.this), new Callback<BaseResponse<MenuData>>() {
                @Override
                public void success(BaseResponse<MenuData> menuDataBaseResponse, Response response) {
                    if (menuDataBaseResponse.isResponse()) {
                        MenuData menuData = menuDataBaseResponse.getData();
                        if (menuData != null) {
                            setupMenuFetched(menuData);
                        } else {
                            Toast.makeText(OrderOnlineActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(OrderOnlineActivity.this, menuDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    hideProgressHUDInLayout();
                    hideSnackbar();
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgressHUDInLayout();
                    hideSnackbar();
                    handleRetrofitError(error);
                }
            });
        }
    }


    private void setupMenuFetched(MenuData menuData) {
        mOutletId = menuData.getOutlet();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mMenuTabsPagerAdapter =
                new MenuTabsPagerAdapter(getUpdatedMenuCategoriesList(menuData.getMenuCategoriesList()), getSupportFragmentManager(), OrderOnlineActivity.this);
        mMenuViewPager = (ViewPager) findViewById(R.id.menuPager);
        mMenuViewPager.setAdapter(mMenuTabsPagerAdapter);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mMenuViewPager);

        mSearchViewToShow = true;
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        supportInvalidateOptionsMenu();
//        showSearhView();
    }


    private ArrayList<MenuCategories> getUpdatedMenuCategoriesList(ArrayList<MenuCategories> menuCategoriesList) {
        MenuCategories recommendedMenuCategory = new MenuCategories();
        recommendedMenuCategory.setCategoryName(getResources().getString(R.string.recommended_category));
        SubCategories recommendedSubCategory = new SubCategories();
        recommendedSubCategory.setSubCategoryName(AppConstants.DEFAULT_SUB_CATEGORY);

        for (int i = 0; i < menuCategoriesList.size(); i++) {
            MenuCategories menuCategory = menuCategoriesList.get(i);
            for (int j = 0; j < menuCategory.getSubCategoriesList().size(); j++) {
                SubCategories subCategory = menuCategory.getSubCategoriesList().get(j);
                for (int k = 0; k < subCategory.getItemsList().size(); k++) {
                    Items item = subCategory.getItemsList().get(k);

                    // Setting menuCategory ID & subCategory ID
                    item.setCategoryID(menuCategory.getId());
                    item.setSubCategoryID(subCategory.getId());

                    //Setting menuCategoryName & subCategoryName
                    item.setCategoryName(menuCategory.getCategoryName());
                    item.setSubCategoryName(subCategory.getSubCategoryName());

                    // isRecommended
                    if (item.isRecommended()) {
                        recommendedSubCategory.getItemsList().add(item);
                    }

                } // k loop
            } // j loop

        } // i loop

        if (recommendedSubCategory.getItemsList().size() > 0) {
            recommendedMenuCategory.getSubCategoriesList().add(recommendedSubCategory);
            menuCategoriesList.add(0, recommendedMenuCategory);
        }

        return menuCategoriesList;
    }

    private void hideSoftKeyBoard(final View view) {
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 50);
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
                        img.setBounds(0, 0, height, height);
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

        View bAddNewItem = (View) findViewById(R.id.bAddNewItem);
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
        SharedPreferenceSingleton.getInstance().setPassedCartCheckoutStage(true);

        Outlet outlet = mOutlet;
        if (getIntent().getBooleanExtra(AppConstants.INTENT_PARAM_FROM_FOOD_OFFER, false) || SharedPreferenceSingleton.getInstance().isSkipLocationClicked()) {
            Intent addressDetailsIntent = new Intent(OrderOnlineActivity.this, AddressDetailsActivity.class);
            OrderSummary orderSummary = new OrderSummary(mCartAdapter.getmCartItemsList(), mOutletId, mOutlet, null); // location will be set in AddressDetailsActivity
            OrderInfoSingleton.getInstance().setOrderSummary(orderSummary);

//            Bundle addressDetailsBundle = new Bundle();
//            addressDetailsBundle.putString(AppConstants.INTENT_PARAM_OUTLET_ID, mOutletId);
//            addressDetailsBundle.putSerializable(AppConstants.INTENT_PARAM_CART_LIST, mCartAdapter.getmCartItemsList());
            startActivity(addressDetailsIntent);
        } else {
            UtilMethods.checkOut(SharedPreferenceSingleton.getInstance().getDeliveryLocation(), mCartAdapter.getmCartItemsList(), mOutletId, mOutlet, OrderOnlineActivity.this, false);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateSearchList(newText);
        return false;
    }

    private void updateSearchList(String newText) {
        RecyclerView searchExpandableList = (RecyclerView) findViewById(R.id.search_recycler_view);
        searchExpandableList.setHasFixedSize(true);
        searchExpandableList.setLayoutManager(new LinearLayoutManager(OrderOnlineActivity.this, LinearLayoutManager.VERTICAL, false));

        searchExpandableList.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard(v);
                return false;
            }
        });

        SubCategories subCategories = new SubCategories();
        subCategories.setSubCategoryName(AppConstants.DEFAULT_SUB_CATEGORY);

        for (int i = 0; i < mMenuTabsPagerAdapter.getMenuCategoriesList().size(); i++) {
            MenuCategories menuCategory = mMenuTabsPagerAdapter.getMenuCategoriesList().get(i);
            // skip loop, if recommended
            if (menuCategory.getCategoryName().equalsIgnoreCase(getResources().getString(R.string.recommended_category)))
                continue;

            for (int j = 0; j < menuCategory.getSubCategoriesList().size(); j++) {
                SubCategories subCategory = menuCategory.getSubCategoriesList().get(j);
                for (int k = 0; k < subCategory.getItemsList().size(); k++) {
                    Items item = subCategory.getItemsList().get(k);

                    if ((item.getItemName() != null && item.getItemName().toLowerCase().contains(newText.toLowerCase()))
                            || (item.getItemDescription() != null && item.getItemDescription().toLowerCase().contains(newText.toLowerCase()))
                            || (item.getCategoryName() != null && item.getCategoryName().toLowerCase().contains(newText.toLowerCase()))
                            || (item.getSubCategoryName() != null) &&
                            !item.getSubCategoryName().equalsIgnoreCase(AppConstants.DEFAULT_SUB_CATEGORY) && item.getSubCategoryName().toLowerCase().contains(newText.toLowerCase())) {
                        subCategories.getItemsList().add(item);
                    }

                } // k loop
            } // j loop
        } // i loop

        ArrayList<ParentListItem> filteredSearchList = new ArrayList<>();
        filteredSearchList.add(subCategories);
        mSearchExpandableAdapter = new MenuExpandableAdapter(OrderOnlineActivity.this, filteredSearchList, searchExpandableList, true);
        searchExpandableList.setAdapter(mSearchExpandableAdapter);

//        mSearchExpandableAdapter.notifyDataSetChanged();


//                mSearchExpandableAdapter.getParentItemList().add(subCategoryList);
//                SubCategories subCategories = (SubCategories) mSearchExpandableAdapter.getParentItemList().get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem notificationsMenuItem = menu.findItem(R.id.action_notifications);
        final MenuItem walletMenuItem = menu.findItem(R.id.action_wallet);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final MenuItem homeMenuItem = menu.findItem(R.id.action_home);

        //Hide all action buttons
        homeMenuItem.setVisible(false);
        notificationsMenuItem.setVisible(false);
        walletMenuItem.setVisible(false);
        searchMenuItem.setVisible(mSearchViewToShow);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                searchView.setSuggestionsAdapter(null);
                if (isFocused) {
                    findViewById(R.id.layout_search_food).setVisibility(View.VISIBLE);
                } else {
//                    findViewById(R.id.layout_search_food).setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                findViewById(R.id.layout_search_food).setVisibility(View.GONE);
                return false;
            }
        });

        searchView.setQueryHint(getResources().getString(R.string.search_menu_hint));

        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        }
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);

        if (searchPlate != null) {
            searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_light);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        SharedPreferenceSingleton.getInstance().setPassedCartCheckoutStage(false);
        if (mSlidingUpPanelLayout != null &&
                (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED ||
                        mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING)) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if (!searchView.isIconified()) {
                closeSearchView();
            } else {
                if (mCartAdapter.getItemCount() > 0) {
                    askUserToDiscardOrder();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void askUserToDiscardOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(OrderOnlineActivity.this).inflate(R.layout.dialog_generic, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView tvTitle = ((TextView) (dialog.findViewById(R.id.tv_title)));
        tvTitle.setText("Discard order?");
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
        tvMessage.setText("Are you sure, you want to discard this order?");
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvOk.setText("Yes");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        tvCancel.setText("No");

        dialog.findViewById(R.id.fOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderOnlineActivity.this.finish();
            }
        });
        dialog.findViewById(R.id.fCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void closeSearchView() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
        findViewById(R.id.layout_search_food).setVisibility(View.GONE);
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

    private void updateCartMenu() {
        if (searchView != null && !searchView.isIconified()) {
            closeSearchView();
        }

        mCartAdapter.notifyDataSetChanged();
        tvCartTotalCost.setText(String.valueOf(mCartAdapter.getTotalCost()));
        tvCartCount.setText(String.valueOf(mCartAdapter.getmCartItemsList().size()));
        if (mCartAdapter.getmCartItemsList().size() > 0) {
            mSlidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.slidingup_panel_height));
            mMenuViewPager.setPadding(mMenuViewPager.getPaddingLeft(), mMenuViewPager.getPaddingTop(), mMenuViewPager.getPaddingRight(), this.getResources().getDimensionPixelSize(R.dimen.slidingup_panel_height));
            for (int i = 0; i < mMenuAdaptersList.size(); i++) {
                mMenuAdaptersList.get(i).notifyDataSetChanged();
            }
        } else {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            mSlidingUpPanelLayout.setPanelHeight(0);
            mMenuViewPager.setPadding(mMenuViewPager.getPaddingLeft(), mMenuViewPager.getPaddingTop(), mMenuViewPager.getPaddingRight(), 0);
            for (int i = 0; i < mMenuAdaptersList.size(); i++) {
                mMenuAdaptersList.get(i).notifyDataSetChanged();
            }
        }
    }

    private void setupScrollingOfferAdapters() {
        if (mOutlet.get_id() == null) {
            return;
        }

        scrollingOfffersProgressBar = (CircularProgressBar) findViewById(R.id.scrollingOfffersProgressBar);
        HttpService.getInstance().getOffers(mOutlet.get_id(), UtilMethods.getUserToken(OrderOnlineActivity.this), new Callback<BaseResponse<ArrayList<Offer>>>() {
            @Override
            public void success(BaseResponse<ArrayList<Offer>> offersBaseResponse, Response response) {
                if (offersBaseResponse.isResponse()) {
                    View view = (View) findViewById(R.id.middleLayout);
                    ArrayList<Offer> offersList = offersBaseResponse.getData();
                    if (offersList.size() > 0) {
                        view.setVisibility(View.VISIBLE);
                        mScrollingOffersAdapter = new ScrollingOffersAdapter(OrderOnlineActivity.this, offersList);
                        mScrollingOffersViewPager = (ViewPager) findViewById(R.id.scrollingOffersPager);
                        mScrollingOffersViewPager.setPadding(32, 0, 32, 0);
                        mScrollingOffersViewPager.setPageMargin(16);
                        mScrollingOffersViewPager.setAdapter(mScrollingOffersAdapter);
                    } else {
                        view.setVisibility(View.GONE);
                        Log.d("OrderOnlineActivity", "No offers available");
                    }
                } else {
                    Toast.makeText(OrderOnlineActivity.this, offersBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                hideProgressHUDInOffers();
                hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInOffers();
                hideSnackbar();
                handleRetrofitError(error);
            }
        });
    }

    public void hideProgressHUDInOffers() {
        if (scrollingOfffersProgressBar != null) {
            scrollingOfffersProgressBar.progressiveStop();
            scrollingOfffersProgressBar.setVisibility(View.GONE);
        }
    }

    public void hideProgressHUDInLayout() {
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage("No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage("An unexpected error has occurred.");
        }
        Log.e(getTagName(), "failure", error);
    }

    private String getTagName() {
        return this.getClass().getName();
    }

    public void buildAndShowSnackbarWithMessage(String msg) {
        final Snackbar snackbar = Snackbar.with(getApplicationContext())
                .type(SnackbarType.MULTI_LINE)
                        //.color(getResources().getColor(android.R.color.black))
                .text(msg)
                .actionLabel("RETRY") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .swipeToDismiss(false)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
        snackbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSnackbar(snackbar); // activity where it is displayed
            }
        }, 500);

    }

    protected void showSnackbar(Snackbar snackbar) {
        SnackbarManager.show(snackbar, this);
    }

    private void hideSnackbar() {
        SnackbarManager.dismiss();
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
}
