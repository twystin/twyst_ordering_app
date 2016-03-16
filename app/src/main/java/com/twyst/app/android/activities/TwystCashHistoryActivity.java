package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.TwystCashPagerAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.CashHistoryData;
import com.twyst.app.android.model.TwystCashHistory;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TwystCashHistoryActivity extends BaseActionActivity {
    private final int TAB_COUNT = 3;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twyst_cash_history);
        setupToolBar();

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_my_twyst_cash);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Earned"));
        tabLayout.addTab(tabLayout.newTab().setText("Spent"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        HttpService.getInstance().getTwystCashHistory(getUserToken(), new Callback<BaseResponse<CashHistoryData>>() {
            @Override
            public void success(BaseResponse<CashHistoryData> historyDataBaseResponse, Response response) {
                if (historyDataBaseResponse.isResponse()) {
                    CashHistoryData cashHistoryData = historyDataBaseResponse.getData();
                    ArrayList<TwystCashHistory> cashHistoryList = cashHistoryData.getCashHistoryList();
                    Utils.setTwystCash(cashHistoryData.getTwystCash());
                    updateTwystCash();
                    viewPager = (ViewPager) findViewById(R.id.pager_my_twyst_cash);

                    if (cashHistoryList.size() > 0) {
                        TwystCashPagerAdapter twystCashPagerAdapter = new TwystCashPagerAdapter(TAB_COUNT, cashHistoryList, getSupportFragmentManager(), TwystCashHistoryActivity.this);
                        viewPager.setAdapter(twystCashPagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                    } else {
                        showNoDataLayout();
                    }
                } else {
                    Toast.makeText(TwystCashHistoryActivity.this, historyDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                hideProgressHUDInLayout();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressHUDInLayout();
                UtilMethods.handleRetrofitError(TwystCashHistoryActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });

        updateTwystCash();
    }

    private void showNoDataLayout() {
        findViewById(R.id.no_twystCash_transactions).setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        ImageView iv_no_data = (ImageView) findViewById(R.id.iv_no_data);
        iv_no_data.setImageResource(R.drawable.twyst_cash_icon);
        TextView tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        tv_no_data.setText(getResources().getString(R.string.no_data_transactions));
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

    private void updateTwystCash() {
        if (Utils.getTwystCash() != -1) {
            ((TextView) findViewById(R.id.tv_cash_amount)).setText(AppConstants.INDIAN_RUPEE_SYMBOL+ " "+String.valueOf(Utils.getTwystCash()));
        }
    }
}
