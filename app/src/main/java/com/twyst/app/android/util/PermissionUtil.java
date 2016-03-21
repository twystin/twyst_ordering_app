/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.twyst.app.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.NoPermissionsActivity;

/**
 * Utility class that wraps access to the runtime permissions API in M and provides basic helper
 * methods.
 */
public class PermissionUtil implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = "PermissionUtil";

    private static PermissionUtil instance = null;
    private Activity activity = null;

    private PermissionUtil() {
    }

    ;

    public static PermissionUtil getInstance() {
        if (instance == null) {
            return instance = new PermissionUtil();
        } else {
            return instance;
        }
    }

    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private View mLayout = null;

    public static final int REQUEST_CONTACTS = 0;
    public static final int REQUEST_LOCATION = 1;
    public static final int REQUEST_PHONE = 2;
    public static final int REQUEST_SMS = 3;
    public static final int REQUEST_GET_ACCOUNTS = 4;

    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static String[] PERMISSIONS_PHONE = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE};
    private static String[] PERMISSIONS_SMS = {Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS};

    public boolean approveContacts(Context context, boolean checkOnly) {
        Log.i(TAG, "Checking Contacts permission.");
        activity = (Activity) context;
        // Check if the Contacts permission is already available.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (!checkOnly) {
                requestContactsPermission((Activity) context);
            }
            return false;
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CONTACT permission has already been granted.");
            return true;
        }
    }

    public boolean approveLocation(Context context, boolean checkOnly) {
        Log.i(TAG, "Checking Location permissions.");
        activity = (Activity) context;

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Location permissions has NOT been granted. Requesting permissions.");
            if (!checkOnly) {
                requestLocationPermissions((Activity) context);
            }
            return false;
        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Location permissions have already been granted. Displaying contact details.");
            return true;
        }
    }

    public boolean approvePhone(Context context, boolean checkOnly) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        activity = (Activity) context;
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Phone permissions has NOT been granted. Requesting permissions.");
            if (!checkOnly) {
                requestPhonePermissions((Activity) context);
            }
            return false;
        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Phone permissions have already been granted. Displaying contact details.");
            return true;
        }
    }

    public boolean approveSMS(Context context, boolean checkOnly) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        activity = (Activity) context;
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "SMS permissions has NOT been granted. Requesting permissions.");
            if (!checkOnly) {
                requestSmsPermissions((Activity) context);
            }
            return false;
        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "SMS permissions have already been granted. Displaying contact details.");
            return true;
        }
    }

    public boolean approveGetAccounts(Context context, boolean checkOnly) {
        Log.i(TAG, "Checking GET_ACCOUNTS permission.");
        activity = (Activity) context;
        // Check if the Contacts permission is already available.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (!checkOnly) {
                requestGetAccountsPermission((Activity) context);
            }
            return false;
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CONTACT permission has already been granted.");
            return true;
        }
    }

    private void requestGetAccountsPermission(final Activity activity) {
        Log.i(TAG, "GET_ACCOUNTS permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.GET_ACCOUNTS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.

            Log.i(TAG,
                    "GET_ACCOUNTS permission rationale to provide additional context.");

            Intent intent = new Intent(activity, NoPermissionsActivity.class);
            intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_GET_ACCOUNTS);
            intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, activity.getResources().getString(R.string.permission_accounts_rationale));
            activity.startActivity(intent);
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.GET_ACCOUNTS},
                    REQUEST_GET_ACCOUNTS);
        }
    }

    private void requestContactsPermission(final Activity activity) {
        Log.i(TAG, "Contacts permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_CONTACTS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.

            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");

            Intent intent = new Intent(activity, NoPermissionsActivity.class);
            intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_CONTACTS);
            intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, activity.getResources().getString(R.string.permission_contacts_rationale));
            activity.startActivity(intent);
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS);
        }
    }

    private void requestLocationPermissions(final Activity activity) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            Intent intent = new Intent(activity, NoPermissionsActivity.class);
            intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_LOCATION);
            intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, activity.getResources().getString(R.string.permission_location_rationale));
            activity.startActivity(intent);

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    private void requestPhonePermissions(final Activity activity) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_PHONE_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CALL_PHONE)) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            Intent intent = new Intent(activity, NoPermissionsActivity.class);
            intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_PHONE);
            intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, activity.getResources().getString(R.string.permission_phone_rationale));
            activity.startActivity(intent);
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, PERMISSIONS_PHONE, REQUEST_PHONE);
        }
    }

    private void requestSmsPermissions(final Activity activity) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_SMS)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.RECEIVE_SMS)) {

            Log.i(TAG,
                    "Displaying SMS permission rationale to provide additional context.");

            Intent intent = new Intent(activity, NoPermissionsActivity.class);
            intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_SMS);
            intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, activity.getResources().getString(R.string.permission_sms_rationale));
            activity.startActivity(intent);

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, PERMISSIONS_SMS, REQUEST_SMS);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for contacts permission.
            Log.i(TAG, "Received response for Contact permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.i(TAG, "CONTACT permission was NOT granted.");
                Intent intent = new Intent(activity, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_CONTACTS);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, R.string.permission_contacts_rationale);
                activity.startActivity(intent);

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for location permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (verifyPermissions(grantResults)) {

            } else {
                Log.i(TAG, "Location permissions were NOT granted.");

                Intent intent = new Intent(activity, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_LOCATION);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, R.string.permission_location_rationale);
                activity.startActivity(intent);

            }

        } else if (requestCode == REQUEST_PHONE) {
            Log.i(TAG, "Received response for phone permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (verifyPermissions(grantResults)) {

            } else {
                Log.i(TAG, "Phone permissions were NOT granted.");
                Intent intent = new Intent(activity, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_PHONE);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, R.string.permission_phone_rationale);
                activity.startActivity(intent);
            }

        } else if (requestCode == REQUEST_SMS) {
            Log.i(TAG, "Received response for sms permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (verifyPermissions(grantResults)) {

            } else {
                Log.i(TAG, "SMS permissions were NOT granted.");
                Intent intent = new Intent(activity, NoPermissionsActivity.class);
                intent.putExtra(AppConstants.INTENT_PERMISSION, REQUEST_SMS);
                intent.putExtra(AppConstants.INTENT_PERMISSIONS_RATIONALE, R.string.permission_sms_rationale);
                activity.startActivity(intent);
            }

        }
    }


}
