package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.MenuTabsPagerAdapter;
import com.twyst.app.android.adapters.ScrollingOffersAdapter;
import com.twyst.app.android.fragments.MenuPageFragment;
import com.twyst.app.android.model.menu.MenuData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vipul Sharma on 11/16/2015.
 */
public class OrderOnlineActivity extends BaseActivity implements MenuPageFragment.OnFragmentScrollChangedListener, ObservableScrollViewCallbacks {

    private ScrollingOffersAdapter mScrollingOffersAdapter;
    private ViewPager mScrollingOffersViewPager;
    private ViewPager mMenuViewPager;
    int mLowerLimit, mUpperLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupScrollingOfferAdapters();
        setupMenu();

        final ObservableScrollView observableScrollView = (ObservableScrollView) findViewById(R.id.observableScrollView);
//        mRecyclerView.setHasFixedSize(true);
        observableScrollView.setScrollViewCallbacks(this);

        final RelativeLayout rlTopLayout = (RelativeLayout) findViewById(R.id.topLayout);
        final RelativeLayout rHidableLayout = (RelativeLayout) findViewById(R.id.hideableLayout);

        ViewTreeObserver observer = rHidableLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mLowerLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, rlTopLayout.getHeight() - rHidableLayout.getHeight(), getResources().getDisplayMetrics());
                mUpperLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, rlTopLayout.getHeight(), getResources().getDisplayMetrics());

                observableScrollView.requestFocus();

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
        MenuTabsPagerAdapter adapter = new MenuTabsPagerAdapter(menuData.getMenuDescriptionList(), getSupportFragmentManager(), OrderOnlineActivity.this);
        mMenuViewPager = (ViewPager) findViewById(R.id.menuPager);
        mMenuViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mMenuViewPager);
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

    @Override
    public void onFragmentScrollChange(int scrollY, boolean firstScroll, boolean dragging) {
//        final int lowerLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
//        final int upperLimit = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        final View topLayout = findViewById(R.id.topLayout);
        final View hideableLayout = findViewById(R.id.hideableLayout);

//        Log.d(getTagName(), "scrollY: " + scrollY + " upperLimit:" + upperLimit + " lowerLimit:" + lowerLimit);
        final int diff = mUpperLimit - scrollY;
        if (diff > mUpperLimit) {
            topLayout.getLayoutParams().height = mUpperLimit;
            topLayout.requestLayout();
            hideableLayout.setAlpha(1f);
        } else if (diff < mLowerLimit) {
            topLayout.getLayoutParams().height = mLowerLimit;
            topLayout.requestLayout();
            hideableLayout.setAlpha(0f);
        } else if (diff <= mUpperLimit && diff > mLowerLimit) {
            topLayout.getLayoutParams().height = diff;
            topLayout.requestLayout();
            float ratio = (scrollY * 1f / (mUpperLimit - mLowerLimit) * 1f) * 1f;
            hideableLayout.setAlpha(1f - ratio);
        }

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        final View topLayout = findViewById(R.id.topLayout);
        final View hideableLayout = findViewById(R.id.hideableLayout);

        final int diff = mUpperLimit - scrollY;
        if (diff > mUpperLimit) {
            topLayout.getLayoutParams().height = mUpperLimit;
            topLayout.requestLayout();
            hideableLayout.setAlpha(1f);
        } else if (diff < mLowerLimit) {
            topLayout.getLayoutParams().height = mLowerLimit;
            topLayout.requestLayout();
            hideableLayout.setAlpha(0f);
        } else if (diff <= mUpperLimit && diff > mLowerLimit) {
            topLayout.getLayoutParams().height = diff;
            topLayout.requestLayout();
            float ratio = (scrollY * 1f / (mUpperLimit - mLowerLimit) * 1f) * 1f;
            hideableLayout.setAlpha(1f - ratio);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private MenuData getMenuData(List<MenuData> menuDataList) {
        for(int i = 0 ; i < menuDataList.size() ; i++){
            MenuData menuData = (MenuData) menuDataList.get(i);
            if (menuData.getMenuType().equalsIgnoreCase("All") || menuData.getMenuType().equalsIgnoreCase("Delivery")){
                return menuData;
            }
        }
        return null;
    }

    private String getMenuString() {
        return "[{\"status\":\"draft\",\"menu_description\":[{\"sections\":[{\"items\":[{\"item_options\":[{\"add_on\":[],\"option\":\"Double Aloo\",\"option_cost\":20}],\"item_name\":\"Aloo Roll\",\"item_description\":\"Single Aloo Roll\",\"item_tags\":[\"Aloo Roll\"],\"item_cost\":40},{\"item_options\":[{\"add_on\":[],\"option\":\"Double Veggie\",\"option_cost\":20}],\"item_name\":\"Veggie Roll\",\"item_description\":\"Single Veggie Roll\",\"item_tags\":[\"Veggie Roll\"],\"item_cost\":50},{\"item_options\":[]}],\"section_name\":\"Veg - Kathi Roll\",\"section_description\":\"Vegetarian Kathi Rolls\"},{\"items\":[{\"item_options\":[{\"add_on\":[],\"option\":\"Double Egg Roll\",\"option_cost\":10}],\"item_name\":\"Egg Roll\",\"item_description\":\"Single Egg Roll\",\"item_tags\":[\"Egg Roll\"],\"item_cost\":30}],\"section_name\":\"Non Veg - Kathi Roll\",\"section_description\":\"Non Veg - Kathi Roll\"},{\"items\":[{\"item_options\":[],\"item_name\":\"Veg Jumbo Roll\",\"item_description\":\"Roll with Soya Chunks + Paneer + Vegetables (as available)\",\"item_tags\":[\"Veg Jumbo Roll Soya Paneer Vegetables\"],\"item_cost\":120},{\"item_options\":[],\"item_name\":\"Non Veg Jumbo Roll\",\"item_description\":\"Roll with Chicken + Mutton + Seekh Kebab filling\",\"item_tags\":[\"Non Veg Jumbo Roll Seekh Mutton Chicken\"],\"item_cost\":140}],\"section_name\":\"Jumbo Rolls\"}],\"menu_category\":\"Indian\"},{\"sections\":[{\"items\":[{\"item_options\":[{\"add_on\":[],\"option\":\"Double Aloo\",\"option_cost\":20}],\"item_name\":\"Aloo Roll\",\"item_description\":\"Single Aloo Roll\",\"item_tags\":[\"Aloo Roll\"],\"item_cost\":40},{\"item_options\":[{\"add_on\":[],\"option\":\"Double Veggie\",\"option_cost\":20}],\"item_name\":\"Veggie Roll\",\"item_description\":\"Single Veggie Roll\",\"item_tags\":[\"Veggie Roll\"],\"item_cost\":50},{\"item_options\":[]}],\"section_name\":\"Veg - Kathi Roll\",\"section_description\":\"Vegetarian Kathi Rolls\"},{\"items\":[{\"item_options\":[{\"add_on\":[],\"option\":\"Double Egg Roll\",\"option_cost\":10}],\"item_name\":\"Egg Roll\",\"item_description\":\"Single Egg Roll\",\"item_tags\":[\"Egg Roll\"],\"item_cost\":30}],\"section_name\":\"Non Veg - Kathi Roll\",\"section_description\":\"Non Veg - Kathi Roll\"},{\"items\":[{\"item_options\":[],\"item_name\":\"Veg Jumbo Roll\",\"item_description\":\"Roll with Soya Chunks + Paneer + Vegetables (as available)\",\"item_tags\":[\"Veg Jumbo Roll Soya Paneer Vegetables\"],\"item_cost\":120},{\"item_options\":[],\"item_name\":\"Non Veg Jumbo Roll\",\"item_description\":\"Roll with Chicken + Mutton + Seekh Kebab filling\",\"item_tags\":[\"Non Veg Jumbo Roll Seekh Mutton Chicken\"],\"item_cost\":140}],\"section_name\":\"Jumbo Rolls\"}],\"menu_category\":\"Indian2\"}],\"menu_type\":\"All\",\"outlet\":{\"_id\":\"56505b7d550a479f031efd80\",\"name\":\"Kathi Junction\",\"loc1\":\"\",\"loc2\":\"Netaji Subhash Place\"},\"_id\":\"5652b8be550a479f031f016a\"},{\"status\":\"draft\",\"menu_description\":[{\"sections\":[{\"items\":[{\"item_options\":[{\"add_on\":[],\"option\":\"Double Aloo\",\"option_cost\":20}],\"item_name\":\"Aloo Roll\",\"item_description\":\"Single Aloo Roll\",\"item_tags\":[\"Aloo Roll\"],\"item_cost\":40},{\"item_options\":[{\"add_on\":[],\"option\":\"Double Veggie\",\"option_cost\":20}],\"item_name\":\"Veggie Roll\",\"item_description\":\"Single Veggie Roll\",\"item_tags\":[\"Veggie Roll\"],\"item_cost\":50},{\"item_options\":[]}],\"section_name\":\"Veg - Kathi Roll\",\"section_description\":\"Vegetarian Kathi Rolls\"},{\"items\":[{\"item_options\":[{\"add_on\":[],\"option\":\"Double Egg Roll\",\"option_cost\":10}],\"item_name\":\"Egg Roll\",\"item_description\":\"Single Egg Roll\",\"item_tags\":[\"Egg Roll\"],\"item_cost\":30}],\"section_name\":\"Non Veg - Kathi Roll\",\"section_description\":\"Non Veg - Kathi Roll\"},{\"items\":[{\"item_options\":[],\"item_name\":\"Veg Jumbo Roll\",\"item_description\":\"Roll with Soya Chunks + Paneer + Vegetables (as available)\",\"item_tags\":[\"Veg Jumbo Roll Soya Paneer Vegetables\"],\"item_cost\":120},{\"item_options\":[],\"item_name\":\"Non Veg Jumbo Roll\",\"item_description\":\"Roll with Chicken + Mutton + Seekh Kebab filling\",\"item_tags\":[\"Non Veg Jumbo Roll Seekh Mutton Chicken\"],\"item_cost\":140}],\"section_name\":\"Jumbo Rolls\"}],\"menu_category\":\"Indian3\"}],\"menu_type\":\"All\",\"outlet\":{\"_id\":\"56505b7d550a479f031efd80\",\"name\":\"Kathi Junction\",\"loc1\":\"\",\"loc2\":\"Netaji Subhash Place\"},\"_id\":\"5652b8be550a479f031f016a\"}]";
    }

}
