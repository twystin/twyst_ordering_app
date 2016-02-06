package com.twyst.app.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DiscoverPagerAdapter;
import com.twyst.app.android.fragments.DiscoverOutletFragment;
import com.twyst.app.android.util.AppConstants;

/**
 * Created by Anshul on 1/14/2016.
 */
public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    protected boolean search;
    private DiscoverOutletFragment outletsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            MainActivity.this.finish();
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

    private void updateOutletList(String newText) {

    }
}
