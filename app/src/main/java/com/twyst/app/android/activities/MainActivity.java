package com.twyst.app.android.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DiscoverOutletAdapter;
import com.twyst.app.android.adapters.DiscoverPagerAdapter;
import com.twyst.app.android.fragments.DiscoverOutletFragment;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;

/**
 * Created by Anshul on 1/14/2016.
 */
public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    protected boolean search;
    private DiscoverOutletFragment outletsFragment;
    private DiscoverOutletAdapter mSearchExpandableAdapter;
    private RecyclerView searchExpandableList;
    private ArrayList<Outlet> mSearchedOutletsList;

    private MenuItem mSearchMenuItem;
    //Toolbar search widget
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showSearchView();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Order"));
        tabLayout.addTab(tabLayout.newTab().setText("Redeem"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final DiscoverPagerAdapter adapter = new DiscoverPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    //Discover fragment selected, enable searchView
                    showSearchView();
                } else {
                    closeSearchView();
                    hideSearchView();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(getTagName(), "onActivityResult: requestCode: " + requestCode + ", resultcode: " + resultCode);

        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {
            outletsFragment = (DiscoverOutletFragment) getSupportFragmentManager().getFragments().get(0);

            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(getTagName(), "User agreed to make required location settings changes.");
                    outletsFragment.fetchLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(getTagName(), "User chose not to make required location settings changes.");
                    outletsFragment.resolveError(AppConstants.SHOW_TURN_ON_GPS);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!searchView.isIconified()) {
                closeSearchView();
            } else {
                MainActivity.this.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    protected String getTagName() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private String createJsonObject() {
        return "[{\"_id\":\"5602e407d7ffbaef7ac31701\",\"name\":\"The Club Chemistry\",\"city\":\"Gurgaon\",\"address\":\"Ground Floor , Grand Mall,MG Road Gurgaon\",\"locality_1\":\"Grand Mall\",\"locality_2\":\"MG Road\",\"lat\":28.480004,\"long\":77.090141,\"distance\":1.2,\"open\":true,\"phone\":\"9999039301\",\"rating\":\"3.0\",\"offers\":[{\"_id\":\"560f6c55c2b4deab91b06eb7\",\"header\":\"Unlimited\",\"line1\":\"IMFL + 2 Starters\",\"line2\":\"at Rs. 1499\",\"description\":\"\",\"terms\":\"Couples Only \\nValid for 2.5 Hours\",\"type\":\"offer\",\"meta\":{\"conditions\":\"1499\",\"item\":\"IMFL + 2 Starters\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c56c2b4deab91b06eb8\",\"header\":\"Unlimited\",\"line1\":\"IMFL\",\"line2\":\"at Rs. 899\",\"description\":\"\",\"terms\":\"Valid Per Person\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"conditions\":\"899\",\"item\":\"IMFL\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c56c2b4deab91b06eb9\",\"header\":\"Unlimited\",\"line1\":\"IMFL\",\"line2\":\"at Rs. 3999\",\"description\":\"\",\"terms\":\"Valid for a Table of Maximum 5 People\\nValid for 2.5 Hours\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"conditions\":\"3999\",\"item\":\"IMFL\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c56c2b4deab91b06eba\",\"header\":\"Combo\",\"line1\":\"8 IMFL/Domestic + 2 Starters\",\"line2\":\"for Rs. 899\",\"description\":\"\",\"terms\":\"Taxes Extra\",\"type\":\"offer\",\"meta\":{\"_for\":899,\"items\":\"8 IMFL/Domestic + 2 Starters\",\"reward_type\":\"combo\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c57c2b4deab91b06ebb\",\"header\":\"Combo\",\"line1\":\"2 Starters + 2 Main Course + 6 IMFL\",\"line2\":\"for Rs. 1099\",\"description\":\"\",\"terms\":\"Taxes Extra\",\"type\":\"offer\",\"meta\":{\"_for\":1099,\"items\":\"2 Starters + 2 Main Course + 6 IMFL\",\"reward_type\":\"combo\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c57c2b4deab91b06ebc\",\"header\":\"Unlimited\",\"line1\":\"IMFL +  Starters ( Any 3 Veg/Chicken)\",\"line2\":\"at Rs. 1599\",\"description\":\"\",\"terms\":\"Taxes Extra\\nCouples Only\\nValid for 2.5 Hours\",\"type\":\"offer\",\"meta\":{\"conditions\":\"1599\",\"item\":\"IMFL +  Starters ( Any 3 Veg/Chicken)\",\"reward_type\":\"unlimited\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100}],\"following\":false,\"logo\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602e407d7ffbaef7ac31701/logo\",\"background\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602e407d7ffbaef7ac31701/background\",\"open_next\":{\"time\":\"1:00 PM\",\"day\":\"Tomorrow\"}},{\"_id\":\"5602f1add7ffbaef7ac31746\",\"name\":\"S Bar Club and Lounge\",\"city\":\"Gurgaon\",\"address\":\"Lower Ground Floor, 1,2,3,4 . Grand Mall ,MG Road ,Gurgaon\",\"locality_1\":\"Grand Mall\",\"locality_2\":\"MG Road\",\"lat\":28.480004,\"long\":77.090141,\"distance\":1.2,\"open\":true,\"phone\":\"01244108006\",\"rating\":\"4.0\",\"offers\":[{\"_id\":\"560f6c57c2b4deab91b06ebd\",\"header\":\"Combo\",\"line1\":\"4 Domestic Beer/IMFL(30ML) + 2 Starters (Veg+Chicken)\",\"line2\":\"for Rs. 799\",\"description\":\"\",\"terms\":\"Valid For 2\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"combo\",\"items\":\"4 Domestic Beer/IMFL(30ML) + 2 Starters (Veg+Chicken)\",\"_for\":799},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":1,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c57c2b4deab91b06ebe\",\"header\":\"Combo\",\"line1\":\"8 Domestic Beer/IMFL (30ML) + 2 Starters (Veg/Chicken)\",\"line2\":\"for Rs. 1099\",\"description\":\"\",\"terms\":\"Taxes Extra\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"combo\",\"items\":\"8 Domestic Beer/IMFL (30ML) + 2 Starters (Veg/Chicken)\",\"_for\":1099},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c58c2b4deab91b06ebf\",\"header\":\"Unlimited\",\"line1\":\"IMFL + 2 Starters (Veg/Chicken)\",\"line2\":\"at Rs. 1499\",\"description\":\"\",\"terms\":\"Taxes Extra\\nCouples Only\\n3 Hours of Service\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"unlimited\",\"item\":\"IMFL + 2 Starters (Veg/Chicken)\",\"conditions\":\"1499\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c58c2b4deab91b06ec0\",\"header\":\"Combo\",\"line1\":\"2 Domestic Beer/IMFL(30 ML) + 1 Starter\",\"line2\":\"for Rs. 349\",\"description\":\"\",\"terms\":\"For 1 Person\\nTaxes Extra\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"combo\",\"items\":\"2 Domestic Beer/IMFL(30 ML) + 1 Starter\",\"_for\":349},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100},{\"_id\":\"560f6c58c2b4deab91b06ec1\",\"header\":\"Unlimited\",\"line1\":\"2 Domestic Beer/ IMFL(30 ML) + 3 Starters(Veg/NVeg)\",\"line2\":\"at Rs. 899/999\",\"description\":\"\",\"terms\":\"Taxes Extra\\nApplicable Per Person\",\"type\":\"offer\",\"meta\":{\"reward_type\":\"unlimited\",\"item\":\"2 Domestic Beer/ IMFL(30 ML) + 3 Starters(Veg/NVeg)\",\"conditions\":\"899/999\"},\"expiry\":\"2015-12-30T18:30:00.000Z\",\"offer_likes\":0,\"is_like\":false,\"available_now\":true,\"offer_cost\":100}],\"following\":false,\"logo\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602f1add7ffbaef7ac31746/logo\",\"background\":\"https://s3-us-west-2.amazonaws.com/retwyst-merchants/retwyst-outlets/5602f1add7ffbaef7ac31746/background\",\"open_next\":{\"time\":\"12:00 PM\",\"day\":\"Tomorrow\"}}]";
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateOutletList(newText);
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        hideSoftKeyBoard(getCurrentFocus());
        findViewById(R.id.layout_search_outlet).setVisibility(View.GONE);
        findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem notificationsMenuItem = menu.findItem(R.id.action_notifications);
        final MenuItem walletMenuItem = menu.findItem(R.id.action_wallet);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        final MenuItem homeMenuItem = menu.findItem(R.id.action_home);

        //Hide all action buttons
        homeMenuItem.setVisible(false);
        notificationsMenuItem.setVisible(false);
        walletMenuItem.setVisible(false);
        mSearchMenuItem.setVisible(false);

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
                    findViewById(R.id.layout_search_outlet).setVisibility(View.VISIBLE);
                    findViewById(R.id.tab_layout).setVisibility(View.GONE);
                    setupSearchView();
                } else {
//                    findViewById(R.id.layout_search_outlet).setVisibility(View.GONE);
//                    findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
//                    hideSoftKeyBoard(view);
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                findViewById(R.id.layout_search_outlet).setVisibility(View.GONE);
                findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setQueryHint(getResources().getString(R.string.search_outlet_hint));

        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        }
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);

        if (searchPlate != null) {
            searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_light);
        }

        showSearchView();
        return true;
    }

    private void setupSearchView() {
        outletsFragment = (DiscoverOutletFragment) getSupportFragmentManager().getFragments().get(0);
        searchExpandableList = (RecyclerView) findViewById(R.id.search_recycler_view);
        searchExpandableList.setHasFixedSize(true);
        searchExpandableList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        mSearchExpandableAdapter = new DiscoverOutletAdapter();
        mSearchExpandableAdapter.setmContext(MainActivity.this);
        searchExpandableList.setAdapter(mSearchExpandableAdapter);
    }

    private void updateOutletList(String newText) {
        if (outletsFragment == null) {
            return;
        }
        if (outletsFragment.getFetchedOutlets() == null) {
            return;
        }
        ArrayList<Outlet> filteredOutlets = new ArrayList<>();
        for (int i = 0; i < outletsFragment.getFetchedOutlets().size(); i++) {
            Outlet outlet = outletsFragment.getFetchedOutlets().get(i);
            if (outlet.getName() != null && outlet.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredOutlets.add(outlet);
            } else if (outlet.getCuisines() != null && outlet.getCuisines().size() > 0) {
                for (String cuisine : outlet.getCuisines()) {
                    if (cuisine.toLowerCase().contains(newText.toLowerCase())) {
                        filteredOutlets.add(outlet);
                        break;
                    }
                }
            }
        }

        mSearchExpandableAdapter.setItems(filteredOutlets);
        mSearchExpandableAdapter.notifyDataSetChanged();

//        SubCategories subCategories = new SubCategories();
//        subCategories.setSubCategoryName(AppConstants.DEFAULT_SUB_CATEGORY);
//
//        for (int i = 0; i < mMenuTabsPagerAdapter.getMenuCategoriesList().size(); i++) {
//            MenuCategories menuCategory = mMenuTabsPagerAdapter.getMenuCategoriesList().get(i);
//            // skip loop, if recommended
//            if (menuCategory.getCategoryName().equalsIgnoreCase(getResources().getString(R.string.recommended_category)))
//                continue;
//
//            for (int j = 0; j < menuCategory.getSubCategoriesList().size(); j++) {
//                SubCategories subCategory = menuCategory.getSubCategoriesList().get(j);
//                for (int k = 0; k < subCategory.getItemsList().size(); k++) {
//                    Items item = subCategory.getItemsList().get(k);
//
//                    if ((item.getItemName() != null && item.getItemName().toLowerCase().contains(newText.toLowerCase()))
//                            || (item.getItemDescription() != null && item.getItemDescription().toLowerCase().contains(newText.toLowerCase()))
//                            || (item.getCategoryName() != null && item.getCategoryName().toLowerCase().contains(newText.toLowerCase()))
//                            || (item.getSubCategoryName() != null) &&
//                            !item.getSubCategoryName().equalsIgnoreCase(AppConstants.DEFAULT_SUB_CATEGORY) && item.getSubCategoryName().toLowerCase().contains(newText.toLowerCase())) {
//                        subCategories.getItemsList().add(item);
//                    }
//
//                } // k loop
//            } // j loop
//        } // i loop

//        ArrayList<ParentListItem> filteredSearchList = new ArrayList<>();
//        filteredSearchList.add(subCategories);
//        mSearchExpandableAdapter = new MenuExpandableAdapter(OrderOnlineActivity.this, filteredSearchList, searchExpandableList, true);
//        searchExpandableList.setAdapter(mSearchExpandableAdapter);
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

    private void showSearchView() {
        if (mSearchMenuItem != null) {
            mSearchMenuItem.setVisible(true);
        }
    }

    private void closeSearchView() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
        findViewById(R.id.layout_search_outlet).setVisibility(View.GONE);
        findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
    }

    private void hideSearchView() {
        if (mSearchMenuItem != null) {
            mSearchMenuItem.setVisible(false);
        }
    }
}
