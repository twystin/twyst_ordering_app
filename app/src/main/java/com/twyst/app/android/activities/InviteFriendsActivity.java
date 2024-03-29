package com.twyst.app.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.twyst.app.android.R;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.UserProfile;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InviteFriendsActivity extends BaseActionActivity {
    private List<UserProfile.FriendLists> friendLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        setupToolBar();

        final SharedPreferences prefs = this.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int twystCashToDisplay = prefs.getInt(AppConstants.PREFERENCE_TWYST_CASH_INVITE_FRIENDS, AppConstants.TWYST_CASH_INVITE_FRIENDS);
        String inviteText = this.getResources().getString(R.string.invite_friends_text);
        String inviteTextFormatted = String.format(inviteText, twystCashToDisplay);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(inviteTextFormatted);
        final ListView inviteTwystList = (ListView) findViewById(R.id.inviteTwystList);

        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);

        HttpService.getInstance().getProfile(UtilMethods.getUserToken(InviteFriendsActivity.this), new Callback<BaseResponse<UserProfile>>() {
            @Override
            public void success(BaseResponse<UserProfile> friendDataBaseResponse, Response response) {
                twystProgressHUD.dismiss();
                if (friendDataBaseResponse.isResponse()) {
                    if (friendDataBaseResponse.getData() != null) {
                        friendLists = friendDataBaseResponse.getData().getTwystFriendLists();
                        if (friendLists.size() > 0) {

                            inviteTwystList.setAdapter(new ArrayAdapter<UserProfile.FriendLists>(InviteFriendsActivity.this, R.layout.listview_item_invite_contact, R.id.contactName, friendLists));

                        } else {
                            Toast.makeText(InviteFriendsActivity.this, "Friend list is empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(InviteFriendsActivity.this, error);
            }
        });


        findViewById(R.id.inviteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String phone = prefs.getString(AppConstants.PREFERENCE_USER_PHONE, "");
                String referral = "promotion%3Dappinvite%26code%3D" + phone;
                String text = "Hey! I wanted to tell you about Twyst - a great app that shows you the best offers and deals on food and drink at places around you. I found it super useful and I think you will too! Download it using the link below and get 500 Twyst Cash when you start - https://play.google.com/store/apps/details?id=" + getApplication().getPackageName() + "&referrer=" + referral;
//                String text = "Hey! I wanted to tell you about Twyst - a great app that shows you the best offers and deals on food and drink at places around you. I found it super useful and I think you will too! Download it using the link below and get 500 Twyst Cash when you start - https://play.google.com/store/apps/details?id="+getApplication().getPackageName()+"&ah=-smMDxRK7pmXEK32N7mSNcbZ2ZM&referrer="+referral;

                showShareIntents("Invite using", text);
            }
        });


        hideProgressHUDInLayout();
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

}
