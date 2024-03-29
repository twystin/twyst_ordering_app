package com.twyst.app.android.util;

import com.twyst.app.android.BuildConfig;

/**
 * Created by satish on 31/05/15.
 */
public class AppConstants {
    //    public static final String HOST = "http://beta.twyst.in";
//    public static final String HOST = "http://192.168.1.18:3000";
//    public static final String HOST = "http://staging.twyst.in";
    public static final String HOST = BuildConfig.HOST;
//    public static final String HOST = "http://twyst.in";

    public static final String CRITTERCISM_APP_ID = "2c30199d047748fb90eb07a1a7c3f9c000555300";

    public static final String GCM_PROJECT_ID = "216832068690";
    public static final String GOOGLE_ANALYTICS_ID = "UA-51763503-2";

    public static final boolean IS_DEVELOPMENT = BuildConfig.DEBUG;
    public static final boolean DEGUG_PICASSO = false;

    public static final int DISCOVER_LIST_PAGESIZE = 20;
    public static final int MAX_WAIT_FOR_SMS_IN_SECONDS = (IS_DEVELOPMENT) ? 30 : 30;

    public static final String INDIAN_RUPEE_SYMBOL = "₹";

    //Earn-burn default values
    public static final int TWYST_CASH_INVITE_FRIENDS = 250;
    public static final int TWYST_CASH_FOLLOW = 20;
    public static final int TWYST_CASH_LIKE_OFFER = 20;
    public static final int TWYST_CASH_CHECKIN_OUTLET_NON_PAYING = 50;
    public static final int TWYST_CASH_CHECKIN_OUTLET_PAYING = 50;
    public static final int TWYST_CASH_SUBMIT_OFFER = 50;
    public static final int TWYST_CASH_SHARE_OFFER = 20;
    public static final int TWYST_CASH_SHARE_OUTLET = 20;
    public static final int TWYST_CASH_SUGGESTION = 20;
    public static final int TWYST_CASH_SHARE_CHECKIN = 25;
    public static final int TWYST_CASH_SHARE_REDEMPTION = 25;
    public static final int TWYST_CASH_GRAB = 100;
    public static final int TWYST_CASH_EXTEND = 150;
    public static final int TWYST_CASH_REDEEM = 100;
    public static final int TWYST_CASH_BUY_CHECKIN = 150;

    public static final String PREFERENCE_TWYST_CASH_INVITE_FRIENDS = "twyst_cash_invite_friends";
    public static final String PREFERENCE_TWYST_CASH_FOLLOW = "twyst_cash_follow";
    public static final String PREFERENCE_TWYST_CASH_LIKE_OFFER = "twyst_cash_like_offer";
    public static final String PREFERENCE_TWYST_CASH_CHECKIN_OUTLET_NON_PAYING = "twyst_cash_checkin_outlet_non_paying";
    public static final String PREFERENCE_TWYST_CASH_CHECKIN_OUTLET_PAYING = "twyst_cash_checkin_outlet_paying";
    public static final String PREFERENCE_TWYST_CASH_SUBMIT_OFFER = "twyst_cash_submit_offer";
    public static final String PREFERENCE_TWYST_CASH_SHARE_OFFER = "twyst_cash_share_offer";
    public static final String PREFERENCE_TWYST_CASH_SHARE_OUTLET = "twyst_cash_share_outlet";
    public static final String PREFERENCE_TWYST_CASH_SUGGESTION = "twyst_cash_suggestion";
    public static final String PREFERENCE_TWYST_CASH_SHARE_CHECKIN = "twyst_cash_share_checkin";
    public static final String PREFERENCE_TWYST_CASH_SHARE_REDEMPTION = "twyst_cash_share_redemption";
    public static final String PREFERENCE_TWYST_CASH_GRAB = "twyst_cash_grab";
    public static final String PREFERENCE_TWYST_CASH_EXTEND = "twyst_cash_extend";
    public static final String PREFERENCE_TWYST_CASH_REDEEM = "twyst_cash_redeem";
    public static final String PREFERENCE_TWYST_CASH_BUY_CHECKIN = "twyst_cash_buy_checkin";

    public static final String PREFERENCE_RECHARGE_HANDLING_FEE_FACTOR = "recharge_handling_fee_factor";
    public static final String PREFERENCE_RECHARGE_MIN_HANDLING_FEE = "recharge_min_handling_fee";

    public static final int RECHARGE_HANDLING_FEE_FACTOR = 10;
    public static final int RECHARGE_MIN_HANDLING_FEE = 10;

    public static final String INTENT_PARAM_OTP_CODE = "otp_code";
    public static final String INTENT_PARAM_OTP_PHONE = "otp_phone_number";
    public static final String INTENT_PARAM_OUTLET_LOCATION_LAT = "lat";
    public static final String INTENT_PARAM_OUTLET_LOCATION_LONG = "lng";
    public static final String INTENT_PARAM_OUTLET_NAME = "outlet_name";
    public static final String INTENT_PARAM_OUTLET_ID = "outlet_id";
    public static final String INTENT_PARAM_COUPON_CODE = "coupon_code";
    public static final String INTENT_PARAM_PHONE = "outlet_phone";
    public static final String INTENT_PARAM_OFFER_ID = "offer_id";
    public static final String INTENT_PARAM_OUTLET_OBJECT = "outlet_object";
    public static final String INTENT_PARAM_OFFER_OBJECT = "offer_object";
    public static final String INTENT_PARAM_USE_OFFER_DATA_OBJECT = "use_offer_object";
    public static final String PREFERENCE_PARAM_SEARCH_QUERY = "SEARCH_QUERY";

    public static final String PREFERENCE_SHARED_PREF_NAME = "com.twyst.android";
    public static final String PREFERENCE_IS_FIRST_RUN = "first_run";
    public static final String PREFERENCE_REGISTRATION_ID = "registration_id";
    public static final String PREFERENCE_DEVICE_ID = "deviceId";
    public static final String PREFERENCE_TUTORIAL_COUNT = "tutorial_count";
    public static final String PREFERENCE_EMAIL_VERIFIED = "email_verified";
    public static final String PREFERENCE_PHONE_VERIFIED = "phone_verified";
    public static final String PREFERENCE_TUTORIAL_SKIPPED = "tutorial_skipped";
    public static final String PREFERENCE_USER_TOKEN = "token_user";
    public static final String PREFERENCE_SMS_BODY = "sms_body";
    public static final String PREFERENCE_LAST_DRAWERITEM_CLICKED = "last_drawer_item";
    public static final String PREFERENCE_LAST_TWYST_CASH = "twyst_cash";
    public static final String PREFERENCE_USER_PIC = "user_pic";
    public static final String PREFERENCE_USER_NAME = "user_name";
    public static final String PREFERENCE_USER_FULL_NAME = "user_full_name";
    public static final String PREFERENCE_USER_PHONE = "user_phone";
    public static final String PREFERENCE_USER_EMAIL = "user_email";
    public static final String PREFERENCE_USER_REFERRAL = "user_referral";
    public static final String PREFERENCE_NOTIFICATION_COUNT = "notification_count";
    public static final String PREFERENCE_IS_FRIEND_LIST_UPDATED = "is_friend_list_updated";
    public static final String PREFERENCE_FRIEND_LIST = "friend_list";
    public static final String PREFERENCE_LAST_SAVED_LOCATIONS_LIST = "last_saved_location_list";

    public static final String PREFERENCE_USER_ONE_LOCATION_CHECK_TIME = "user_one_location_check_time";
    public static final String PREFERENCE_DISTANCE_LIMIT = "distance_limit";
    public static final String PREFERENCE_LOCATION_REQUEST_REFRESH_INTERVAL = "location_request_refresh_interval";
    public static final String PREFERENCE_LOCATION_REQUEST_SMALLEST_DISPLACEMENT = "location_request_smallest_displacement";
    public static final String PREFERENCE_LOCATION_REQUEST_PRIORITY = "location_request_priority";
    public static final String PREFERENCE_LOCATION_OFFLINE_LIST_MAX_SIZE = "location_offline_list_max_size";

    public static final String PREFERENCE_IS_FACEBOOK_CONNECTED = "is_facebook_connected";
    public static final String PREFERENCE_IS_GOOGLE_CONNECTED = "is_google_connected";
    public static final String PREFERENCE_IS_PUSH_ENABLED = "is_push_enabled";

    public static final String PREFERENCE_DOB_DAY = "dob_day";
    public static final String PREFERENCE_DOB_MONTH = "dob_month";
    public static final String PREFERENCE_DOB_YEAR = "dob_year";

    public static final String PREFERENCE_ANNIVERSARY_DAY = "anniversary_day";
    public static final String PREFERENCE_ANNIVERSARY_MONTH = "anniversary_month";
    public static final String PREFERENCE_ANNIVERSARY_YEAR = "anniversary_year";

    public static final String PREFERENCE_ANNIVERSARY = "anniversary";
    public static final String PREFERENCE_DOB = "dob";

    public static final String SMS_FROM = "BW-TWYSTR";

    public static final String INTENT_PARAM_CHECKIN_HEADER = "checkin_header";
    public static final String INTENT_PARAM_CHECKIN_LINE1 = "check_in_line";
    public static final String INTENT_PARAM_CHECKIN_OUTLET_NAME = "checkin_outlet";
    public static final String INTENT_PARAM_CHECKIN_CODE = "checkin_code";
    public static final String INTENT_PARAM_CHECKIN_LINE2 = "line2";
    public static final String INTENT_PARAM_CHECKIN_OUTLET_ID = "checkin_outlet_id";
    public static final String INTENT_PARAM_CHECKIN_COUNT = "checkin_count";

    public static final String INTENT_PARAM_WEBVIEW_URL = "webview_url";
    public static final String INTENT_PARAM_WEBVIEW_HEADER = "webview_header";

    public static final String INTENT_PARAM_FROM_DRAWER = "from_drawer";
    public static final String INTENT_PARAM_FROM_PUSH_NOTIFICATION_CLICKED = "from_push_notification";
    public static final String INTENT_PARAM_FROM_ORDER_HISTORY = "from_order_history";
    public static final String INTENT_PARAM_FROM_FOOD_OFFER = "from_food_offer";
    public static final String INTENT_PARAM_SUBMIT_OFFER_OUTLET_NAME = "submit_offer_outlet_name";
    public static final String INTENT_PARAM_SUBMIT_OFFER_OUTLET_ADDRESS = "submit_offer_outlet_address";
    public static final String INTENT_PARAM_SUBMIT_OFFER_OUTLET_ID = "submit_offer_outlet_id";
    public static final String INTENT_PARAM_UPLOAD_BILL_OUTLET_ADDRESS = "upload_bill_outlet_address";
    public static final String INTENT_PARAM_UPLOAD_BILL_OUTLET_ID = "upload_bill_outlet_id";
    public static final String INTENT_PARAM_UPLOAD_BILL_OUTLET_NAME = "upload_bill_outlet_name";
    public static final String PREFERENCE_LOCALITY_SHOWN_DRAWER = "locality";
    public static final String PREFERENCE_PREVIOUS_LOCALITY_SHOWN_DRAWER = "previous_locality";
    public static final String PREFERENCE_CHECK_FIRST_LAUNCH = "first_launch";

    public static final String PREFERENCE_OUTLETS_LIST = "outlet_list";

    public static final String PREFERENCE_UPDATED_LAT = "updated_lat";
    public static final String PREFERENCE_UPDATED_LNG = "updated_lng";

    public static final String PREFERENCE_CURRENT_USED_LAT = "current_used_lat";
    public static final String PREFERENCE_CURRENT_USED_LNG = "current_used_lng";
    public static final String PREFERENCE_CURRENT_USED_LOCATION_NAME = "current_used_location_name";

    public static final String INTENT_SPLASH_RECO_DOWNLOADED = "intent_splash_reco_downloaded";
    public static final String INTENT_DISCOVER_STARTED = "intent_discover_started";
    public static final String INTENT_SPLASH_OUTLET_LIST = "intent_splash_outlet_list";
    public static final String INTENT_FROM_SPLASH_DOWNLOADING = "intent_from_splash_downloading";

    public static final String INTENT_ORDER_SUMMARY = "intent_order_summary";
    public static final String INTENT_FREE_ITEM_INDEX = "intent_free_item_index";

    public static final String DEFAULT_SUB_CATEGORY = "Default";

    //Address Details (by anshul)
    public static final String INTENT_PARAM_MENU_ID = "menu_id";

    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final int SHOW_PROGRESS_BAR = 1;
    public static final int SHOW_CURRENT_LOCATION = 2;
    public static final int SHOW_TURN_ON_GPS = 3;
    public static final int SHOW_FETCH_LOCATION_AGAIN = 4;

    //    public static final String MAP_TO_BE_SHOWN = "MAP_TO_BE_SHOWN";
    public static final String DATA_TO_BE_SHOWN = "DATA_TO_BE_SHOWN";
    public static final String ADDRESS_FOUND = "ADDRESS_FOUND";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String ADDRESS_DATA_KEY = PACKAGE_NAME + ".ADDRESS_DATA_KEY";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String sortTag = "SORT BY";
    public static final String cuisinetag = "CUISINES";
    public static final String paymentTag = "PAYMENT OPTION";
    public static final String offersTag = "OFFERS AVAILABLE";
    public static final int GET_FILTER_ACTIVITY = 5;
    public static final int GOT_FILTERS_SUCCESS = -5;
    public static final int EDIT_ADDRESS_SUCCESS = -111;
    public static final int EDIT_ADDRESS = 111;
    public static final String FILTER_TAGS = "FILTER_TAGS_SENT";
    public static final String FILTER_MAP = "filter_map";

    public static final String CHOOSE_LOCATION_OPTION_SELECTED = "Choose location selected option";
    public static final String CHOOSE_LOCATION_OPTION_CURRENT = "Current Location";
    public static final String CHOOSE_LOCATION_OPTION_SAVED = "Saved Location";
    public static final String CHOOSE_LOCATION_OPTION_ADD = "Add New Location";
    public static final String CHOOSE_LOCATION_OPTION_SKIPPED = "Choose Location Skipped";
    public static final String CHOOSE_LOCATION_DEFAULT = "Default Location chosen";

    public static final String CURRENT_USED_LOCATION = "current_used_location";
    public static final String LAST_USED_LOCATION = "last_used_location";
    public static final String FROM_CHOOSE_ACTIVITY_TO_MAP = "Choose activity directed to map";

    //Offer DisplayActivity
    public static final String INTENT_OFFER_LIST = "intent_offer_list";
    public static final String INTENT_CLICKED_OFFER_POSITION = "intent_clicked_offer_position";
    public static final String OFFER_BUYXGETY = "buyxgety";
    public static final String OFFER_BUYONEGETONE = "buyonegetone";
    public static final String OFFER_FREE = "free";
    public static final String OFFER_FLATOFF = "flatoff";
    public static final String OFFER_DISCOUNT = "discount";

    // OrderHistory to OrderOnline
    public static final String INTENT_PLACE_REORDER = "intent_reorder_data";
    public static final String INTENT_PLACE_REORDER_MENUID = "place_reorder_menuid";

    //AddressDetailActivity from Cart
    public static final String INTENT_PARAM_CART_LIST = "intent_cart_list";

    //Payment Data
    public static final String INTENT_ORDER_CHECKOUT_RESPONSE = "intent_order_checkout_response";
    public static final String INTENT_ORDER_INFO_LOCAL = "intent_order_info_local";
    public static final String INTENT_ORDER_ID = "order_id";
    public static final String INTENT_ORDER_ID_FEEDBACK = "order_id_feedback";
    public static final String INTENT_ORDER_IS_DELIVERED = "order_is_delivered";
    public static final String INTENT_ORDER_NUMBER = "order_number";

    public static final String INTENT_PAYMENT_OPTION_IS_COD = "payment_option_is_cod";
    public static final String INTENT_PAYMENT_OPTION_IS_ONLINE = "payment_option_is_online";

    //Payment Data
    public static final String INTENT_ORDER_HISTORY = "intent_order_history";

    //permissions
    public static final String INTENT_PERMISSION = "intent_permission";
    public static final String INTENT_PERMISSIONS_RATIONALE = "intent_permissions_rationale";
    public static final String INTENT_ORDER_FEEDBACK = "order_feedback";

    // Cashback
    public static final String BUNDLE_CASHBACK_OFFERS = "cashback_offer";

    public static final String INTENT_MERCHANT_LOGO = "merchant_logo";
    public static final String INTENT_VOUCHER_DETAIL = "voucher_detail";

    public static final String KEY_DATABASE_SAVED = "database_saved";

    //Twyst Cash History
    public static final String BUNDLE_CASH_HISTORY = "cash_history";
    public static final String CASH_HISTORY_TYPE = "cash_history_type";

    public static final String EVENT_FIRST_SCREEN = "first_screen";
    public static final String EVENT_OUTLET_LIST_VIEW = "outlet_list_view";
    public static final String EVENT_ADDRESS_CHANGE_MAIN = "address_change_main";
    public static final String EVENT_OUTLET_VIEW = "outlet_view";
    public static final String EVENT_FILTER_CLICKED = "filter_clicked";
    public static final String EVENT_CART_ABANDON = "cart_abandon";
    public static final String EVENT_POST_CART = "post_cart";
    public static final String EVENT_ADDRESS_SAVE = "address_save";
    public static final String EVENT_OFFER_VIEWED = "offer_viewed";
    public static final String EVENT_OFFER_APPLIED = "offer_applied";
    public static final String EVENT_ORDER_SUMMARY = "order_summary";
    public static final String EVENT_CHECKOUT = "checkout";
    public static final String EVENT_ONLINE_PAYMENT = "online_payment";
    public static final String EVENT_COD_PAYMENT = "cod_payment";
    public static final String EVENT_ORDER = "order";
    public static final String EVENT_TWYST_CASH_HISTORY_VIEW = "twyst_cash_history_view";

    public static final String EVENT_REDEEM_TAB = "redeem_tab";
    public static final String EVENT_RECHARGE_VIEW = "recharge_view";
    public static final String EVENT_RECHARGE_DONE = "recharge_done";
    public static final String EVENT_SHOPPING_VOUCHERS_VIEW = "shopping_vouchers_view";
    public static final String EVENT_SHOPPING_VOUCHER_DETAIL_VIEW = "shopping_voucher_detail_view";
    public static final String EVENT_SHOPPING_VOUCHER_REDEEMED = "shopping_voucher_redeemed";
    public static final String EVENT_FOOD_OFFERS_VIEW = "food_offers_view";
    public static final String EVENT_FOOD_OFFER_USED = "food_offer_used";
}
