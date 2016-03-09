package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.TwystCashPagerAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.CashHistoryData;
import com.twyst.app.android.model.TwystCashHistory;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TwystCashHistoryActivity extends BaseActionActivity {
    private final int TAB_COUNT = 3;
    private ViewPager viewPager;
    private TwystCashPagerAdapter mPagerAdapter;
    private TextView TwystCashTV;
    private CashHistoryData cashHistoryData = new CashHistoryData();
    private ArrayList<TwystCashHistory> cashHistory = new ArrayList<TwystCashHistory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twyst_cash_history);
        setupToolBar();

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_my_twyst_cash);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Credit"));
        tabLayout.addTab(tabLayout.newTab().setText("Debit"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        HttpService.getInstance().getTwystCashHistory(getUserToken(), new Callback<BaseResponse<CashHistoryData>>() {
            @Override
            public void success(BaseResponse<CashHistoryData> TwystCashResponse, Response response) {
                if (TwystCashResponse.isResponse()) {
                    cashHistoryData = TwystCashResponse.getData();
                    cashHistory = cashHistoryData.getCashHistory();
                    setTwystCash(cashHistoryData.getTwystCash());

                    TwystCashTV = (TextView) findViewById(R.id.tv_cash_amount);
                    TwystCashTV.setText(Integer.toString(getTwystCash()));

                    viewPager = (ViewPager) findViewById(R.id.pager_my_twyst_cash);
                    mPagerAdapter = new TwystCashPagerAdapter(TAB_COUNT, cashHistory, getSupportFragmentManager(), TwystCashHistoryActivity.this);
                    viewPager.setAdapter(mPagerAdapter);
//                    final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_my_twyst_cash);
//                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    tabLayout.setupWithViewPager(viewPager);

                } else {
                    Toast.makeText(TwystCashHistoryActivity.this, TwystCashResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(TwystCashHistoryActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });

//        TextView TwystCashTV = (TextView) findViewById(R.id.tv_cash_amount);
//        TwystCashTV.setText(Integer.toString(twystCash));
//
//        viewPager = (ViewPager) findViewById(R.id.pager_my_twyst_cash);
//        mPagerAdapter = new TwystCashPagerAdapter(TAB_COUNT, cashHistory, getSupportFragmentManager(), TwystCashHistoryActivity.this);
//        viewPager.setAdapter(mPagerAdapter);
//        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_my_twyst_cash);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setupWithViewPager(viewPager);

    }
}
