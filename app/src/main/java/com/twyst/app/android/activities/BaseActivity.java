package com.twyst.app.android.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;
import com.twyst.app.android.R;
import com.twyst.app.android.adapters.DrawerListAdapter;
import com.twyst.app.android.model.DrawerItem;
import com.twyst.app.android.model.OutletList;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends ActionBarActivity {
    protected static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected Toolbar toolbar;
    private SearchView searchView;
    public Menu menu;
    private CharSequence mTitle;
    private ArrayList<DrawerItem> drawerItems;
    private ListView drawerList;
    protected DrawerLayout drawerLayout;
    private CircularProgressBar circularProgressBar;
    protected boolean setupAsChild;
    protected boolean drawerOpened;
    private DrawerItem invite, faq, bill, wallet, notifications, submitOffer, suggestOutlet, feedback, rate, addressDetails, reorder, home, about;
    protected SharedPreferences.Editor sharedPreferences;

    TextView localityDrawer;
    TextView userName;
    ImageView userImage;

    private final int DRAWER_ITEM_POS_HEADER = 0;
    // One should not set any of the below Drawer item values to 0 or less.
    private final String DRAWER_ITEM_MY_ORDERS = "MY ORDERS";
    public static final String DRAWER_ITEM_INVITE_FRIENDS = "INVITE FRIENDS";
    private final String DRAWER_ITEM_SUGGEST = "SUGGEST AN OUTLET";
    private final String DRAWER_ITEM_HOME = "HOME";
    public static final String DRAWER_ITEM_NOTIFICATIONS = "NOTIFICATIONS";
    private final String DRAWER_ITEM_FEEDBACK = "FEEDBACK";
    private final String DRAWER_ITEM_FAQ = "FAQs";
    private final String DRAWER_ITEM_RATE = "RATE TWYST";
    private final String DRAWER_ITEM_ABOUT = "ABOUT US";

    protected abstract String getTagName();

    protected abstract int getLayoutResource();

    public void hideProgressHUDInLayout() {
        if (circularProgressBar != null) {
            circularProgressBar.progressiveStop();
            circularProgressBar.setVisibility(View.GONE);
        }
    }

    //Applying font to the BaseActivity
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(BaseActivity.class.getSimpleName(), "Toolbar item clicked: " + item.getItemId());
                switch (item.getItemId()) {
                    case R.id.action_notifications:
                        showNotifications();
                        break;

                    case R.id.action_wallet:
                        showWallet();
                        break;

                    case R.id.action_home:
                        showHome();
                        break;
                }
                return true;
            }
        });

        mTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
                invite.setSelected(false);
                rate.setSelected(false);
                faq.setSelected(false);
                notifications.setSelected(false);
                reorder.setSelected(false);
                suggestOutlet.setSelected(false);
                feedback.setSelected(false);
                rate.setSelected(false);
                drawerOpened = false;
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
                getSupportActionBar().setTitle(mTitle);
                invite.setSelected(false);
                rate.setSelected(false);
                faq.setSelected(false);
                notifications.setSelected(false);
                reorder.setSelected(false);
                suggestOutlet.setSelected(false);
                feedback.setSelected(false);
                rate.setSelected(false);
                drawerOpened = true;
            }

            public void onDrawerSlide(View drawerView, float slideOffset) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerList = (ListView) findViewById(R.id.drawer_listview);
        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        View list_footer = getLayoutInflater().inflate(R.layout.drawerlist_footer, null);

        userName = (TextView) list_header.findViewById(R.id.userName);
        userImage = (ImageView) list_header.findViewById(R.id.userImage);
/*        LinearLayout editProfile = (LinearLayout) list_header.findViewById(R.id.editProfile);
        localityDrawer = (TextView) list_header.findViewById(R.id.localityDrawer);*/

        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
/*        String locality = prefs.getString(AppConstants.PREFERENCE_CURRENT_USED_LOCATION_NAME, "");
        if (locality.equals("")) {
            editProfile.setVisibility(View.GONE);
        } else {
            localityDrawer.setText(locality);
        }*/

        updatePicName();

/*
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerOpened) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getBaseContext(), EditProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, true);
                        startActivity(intent);
                    }
                }, 40);

            }
        });
        */

        drawerList.addHeaderView(list_header, null, true);
//        drawerList.addFooterView(list_footer, null, false);

        createDrawerProfileBanner();
        //drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navigationTitles));

        final int pos = prefs.getInt(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED, -1);
        drawerItems = getDrawerItems(pos);
        drawerList.setAdapter(new DrawerListAdapter(this, getLayoutInflater(), drawerItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (setupAsChild) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if (drawerOpened) {
            invite.setSelected(false);
            rate.setSelected(false);
            faq.setSelected(false);
            notifications.setSelected(false);
            reorder.setSelected(false);
            suggestOutlet.setSelected(false);
            feedback.setSelected(false);
            rate.setSelected(false);
        } else {
            invite.setSelected(false);
            rate.setSelected(false);
            faq.setSelected(false);
            notifications.setSelected(false);
            reorder.setSelected(false);
            suggestOutlet.setSelected(false);
            feedback.setSelected(false);
            rate.setSelected(false);
        }

        setTitle(mTitle);
    }

    private ArrayList<DrawerItem> getDrawerItems(int pos) {
        ArrayList<DrawerItem> drawerItems = new ArrayList<>();

        reorder = new DrawerItem(DRAWER_ITEM_MY_ORDERS, R.drawable.drawer_list_item_myorders);
        reorder.setNotifcation_needed(false);
        reorder.setNotification_text(2);

        invite = new DrawerItem(DRAWER_ITEM_INVITE_FRIENDS, R.drawable.drawer_list_item_invitefriends);
        suggestOutlet = new DrawerItem(DRAWER_ITEM_SUGGEST, R.drawable.drawer_list_item_suggestanoutlet);
        home = new DrawerItem(DRAWER_ITEM_HOME, R.drawable.drawer_list_item_home3);
        notifications = new DrawerItem(DRAWER_ITEM_NOTIFICATIONS, R.drawable.drawer_list_item_notification);
        feedback = new DrawerItem(DRAWER_ITEM_FEEDBACK, R.drawable.drawer_list_item_feedback);
        faq = new DrawerItem(DRAWER_ITEM_FAQ, R.drawable.drawer_list_item_faqs);
        rate = new DrawerItem(DRAWER_ITEM_RATE, R.drawable.drawer_list_item_ratetwyst);
        about = new DrawerItem(DRAWER_ITEM_ABOUT, R.drawable.drawer_list_item_about);

        //SEQUENCE DEFINED HERE
//        drawerItems.add(home);
        drawerItems.add(reorder);
        drawerItems.add(invite);
        drawerItems.add(notifications);
        drawerItems.add(suggestOutlet);
        drawerItems.add(feedback);
        drawerItems.add(faq);
        drawerItems.add(rate);
        drawerItems.add(about);

        for (DrawerItem drawerItem : drawerItems) {
            if (pos == drawerItems.indexOf(drawerItem)) {
                drawerItem.setSelected(false);
            }
        }
        if (pos > -1) {
            DrawerItem drawerItem = drawerItems.get(pos);
            drawerItem.setSelected(true);
        }
        return drawerItems;
    }

    protected void createDrawerProfileBanner() {

    }

    private void showNotifications() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    private void showWallet() {
//        Intent intent = new Intent(this, WalletActivity.class);
//        startActivity(intent);
    }

    private void showHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("Search", false);
//        intent.setAction("setChildNo");
        startActivity(intent);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(BaseActivity.class.getSimpleName(), "Drawer item clicked at position " + position);

            /*for (DrawerItem drawerItem : drawerItems) {
                drawerItem.setSelected(false);
            }

            DrawerItem drawerItem = drawerItems.get(position - 1);
            drawerItem.setSelected(true);

            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) drawerList.getAdapter();
            DrawerListAdapter drawerListAdapter = (DrawerListAdapter) headerViewListAdapter.getWrappedAdapter();
            drawerListAdapter.notifyDataSetChanged();
*/
            selectItem(position);
            for (DrawerItem drawerItem : drawerItems) {
                drawerItem.setSelected(false);
            }
            if (position > 0) {
                DrawerItem drawerItem = drawerItems.get(position - 1);
                drawerItem.setSelected(true);

            }
            updateDrawer();
            SharedPreferences.Editor sharedPreferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPreferences.putInt(AppConstants.PREFERENCE_LAST_DRAWERITEM_CLICKED, position - 1);
            sharedPreferences.commit();
        }
    }


    private void selectItem(final int position) {
        //updateTitle = false;
        drawerLayout.closeDrawer(findViewById(R.id.drawer_list));
        //drawerList.setItemChecked(position, true);
        drawerList.setItemChecked(position, false);

        drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;

                if (position == DRAWER_ITEM_POS_HEADER) {
                    intent = new Intent(getBaseContext(), EditProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, true);

                } else {
                    switch (drawerItems.get(position - 1).getTitle()) {
                        case DRAWER_ITEM_MY_ORDERS:
                            intent = new Intent(getBaseContext(), OrderHistoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_INVITE_FRIENDS:
                            //invite friends
                            intent = new Intent(getBaseContext(), InviteFriendsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_SUGGEST:
                            //suggest an outlet
                            intent = new Intent(getBaseContext(), SuggestOutletActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_HOME:
                            intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_NOTIFICATIONS:
                            //notification
                            intent = new Intent(getBaseContext(), NotificationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_FEEDBACK:
                            //write to us
                            intent = new Intent(getBaseContext(), WriteToUsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_FAQ:
                            //faq
                            intent = new Intent(getBaseContext(), FaqActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;

                        case DRAWER_ITEM_RATE:
                            //Rate
                            rateApp();
                            return;

                        case DRAWER_ITEM_ABOUT:
                            //about
                            intent = new Intent(getBaseContext(), AboutActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            break;
                    }
                }

                if (intent != null) {
                    intent.putExtra(AppConstants.INTENT_PARAM_FROM_DRAWER, true);
                }
                startActivity(intent);
            }
        }, 250);
    }


    public void updateDrawer() {
        drawerList.setAdapter(new DrawerListAdapter(this, getLayoutInflater(), drawerItems));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawerLayout toggl
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.isDrawerIndicatorEnabled() &&
                mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // The action bar home/up action should open or close the drawerLayout.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Handle home button in non-drawerLayout mode
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateDrawer();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void handleRetrofitError(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            buildAndShowSnackbarWithMessage("No internet connection.");
        } else {
            buildAndShowSnackbarWithMessage("An unexpected error has occurred.");
        }
        Log.e(getTagName(), "failure", error);
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

    public void hideSnackbar() {
        SnackbarManager.dismiss();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    protected boolean checkPlayServices() {
        if (AppConstants.IS_DEVELOPMENT) {
            return true;
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(getTagName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        updatePicName();
    }

    public int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    public int getScreenWidth() {
        return findViewById(android.R.id.content).getWidth();
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplication().getPackageName() + "&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM")));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void showShareIntents(String title, String text) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent facebookIntent = getShareIntent("facebook", "", text);
        if (facebookIntent != null) {
            targetedShareIntents.add(facebookIntent);
        }

        Intent whatsappIntent = getShareIntent("whatsapp", "", text);
        if (whatsappIntent != null) {
            targetedShareIntents.add(whatsappIntent);
        }

        Intent twIntent = getShareIntent("twitter", "", text);
        if (twIntent != null) {
            targetedShareIntents.add(twIntent);
        }

        Intent gmailIntent = getShareIntent("gmail", "", text);
        if (gmailIntent != null) {
            targetedShareIntents.add(gmailIntent);
        }

        Intent mmsIntent = getShareIntent("mms", "", text);
        if (mmsIntent != null) {
            targetedShareIntents.add(mmsIntent);
        }

        Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), title);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        startActivity(chooser);
    }

    private Intent getShareIntent(String type, String subject, String text) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = this.getPackageManager().queryIntentActivities(share, 0);
        System.out.println("resinfo: " + resInfo);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT, subject);
                    share.putExtra(Intent.EXTRA_TEXT, text);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return null;

            return share;
        }
        return null;
    }

    public String getUserToken() {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");
    }

    public void showEarnMoreInstructions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_earn_more, null);
//        TextView tvEarnMore = (TextView) dialogView.findViewById(R.id.tvEarnMore);
//        tvEarnMore.setText(Html.fromHtml(getString(R.string.earn_more_body)));

        WebView webView = (WebView) dialogView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                twystProgressHUD.dismiss();
                // TODO hide your progress image
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                twystProgressHUD.dismiss();
//                Toast.makeText(BaseActivity.this, description, Toast.LENGTH_SHORT).show();
            }

        });

        webView.loadUrl(AppConstants.HOST + "/api/v4/earn/more");

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void viewMoreDialog(String termsToShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = li.inflate(R.layout.dialog_viewmore_tnc_offer_details, null);
        TextView tvTerms = (TextView) dialogView.findViewById(R.id.tvTerms);
        tvTerms.setText(termsToShow);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialogView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public String getFormattedTermsConditions(String termsFromOffer) {
        String terms;

        Character newLine = '\n';
        Character bullet = '\u2022';
        String space = " ";
        if (!TextUtils.isEmpty(termsFromOffer)) {
            if (termsFromOffer.contains(newLine.toString())) {
                termsFromOffer = termsFromOffer.replace(newLine.toString(), newLine.toString() + space + bullet.toString() + space);
            }
            terms = space + bullet + space + termsFromOffer + newLine + space + getString(R.string.terms_conditions);
        } else {
            terms = space + getString(R.string.terms_conditions);
        }
        return terms;
    }

    public int getTwystCash() {
        return HttpService.getInstance().getSharedPreferences().getInt(AppConstants.PREFERENCE_LAST_TWYST_CASH, -1);
    }

    public void setTwystCash(int twystCash) {
        HttpService.getInstance().getSharedPreferences().edit().putInt(AppConstants.PREFERENCE_LAST_TWYST_CASH, twystCash).apply();
    }

    public void updatePicName() {
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(AppConstants.PREFERENCE_USER_NAME, "");
        String pic = prefs.getString(AppConstants.PREFERENCE_USER_PIC, "");
        userName.setText(name);

        if (!TextUtils.isEmpty(pic)) {
            // Picasso is used only here. If glide is used, its supportRequestFragmentManager mixes up with DiscoverFragmentManager
            // and crashes the app.
            Picasso.with(this)
                    .load(pic)
                    .into(userImage);
//            Glide.with(this)
//                    .load(pic)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .centerCrop()
//                    .into(userImage);
        }
    }

    public ArrayList<OutletList> getOutletListsArray() {
        final SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String outletListString = preferences.getString(AppConstants.PREFERENCE_OUTLETS_LIST, "");

        ArrayList<OutletList> list = new ArrayList<OutletList>();
        try {
            JSONObject jsonObject = new JSONObject(outletListString);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject(key);
                    String name = jsonObject1.getString("name");
                    String address = jsonObject1.getString("address");

                    OutletList outletList = new OutletList();
                    outletList.set_id(key);
                    outletList.setName(name);
                    outletList.setAddress(address);

                    list.add(outletList);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } catch (JSONException e) {
        }

        return list;
    }

    public ArrayList getOutletListNames(ArrayList<OutletList> outletListArrayList) {
        ArrayList outletListNames = new ArrayList();
        for (OutletList outletList : outletListArrayList) {
            String name = outletList.getName();
            // repeated additions:
            if (!outletListNames.contains(name)) {
                outletListNames.add(name);
            }
        }
        return outletListNames;
    }

    public ArrayList<OutletList> getFilteredOutletListsAddressArray(ArrayList<OutletList> outletListArrayList, String selectedName) {
        ArrayList<OutletList> list = new ArrayList<OutletList>();
        for (OutletList outletList : outletListArrayList) {
            String name = outletList.getName();
            if (name.equals(selectedName)) {
                list.add(outletList);
            }
        }
        return list;
    }

    public ArrayList getOutletListAddress(ArrayList<OutletList> outletListArrayList, String selectedName) {
        ArrayList outletListLocations = new ArrayList();
        for (OutletList outletList : outletListArrayList) {
            outletListLocations.add(outletList.getAddress());
        }
        return outletListLocations;
    }

    public String getOutletID(ArrayList<OutletList> outletListArrayList, String selectedAddress) {
        String outletID = "";
        for (OutletList outletList : outletListArrayList) {
            String address = outletList.getAddress();
            if (address.equals(selectedAddress)) {
                outletID = outletList.get_id();
                return outletID;
            }

        }
        return outletID;
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

}
