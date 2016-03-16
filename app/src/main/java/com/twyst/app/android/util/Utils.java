package com.twyst.app.android.util;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.twyst.app.android.R;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.DeliveryZone;
import com.twyst.app.android.model.TimeStamp;
import com.twyst.app.android.service.HttpService;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by Vipul Sharma on 22/07/15.
 */
public class Utils {
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    public static String costString(double costDouble) {
        //Upto two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        String costDoubleString = df.format(costDouble);

        int costInteger = Double.valueOf(costDouble).intValue();
        String costString = (costInteger == costDouble) ? String.valueOf(costInteger) : costDoubleString;
        return "₹ " + costString;
    }

    public static void populateText(LinearLayout ll, View[] views, Context mContext, int width) {
        ll.removeAllViews();
        int maxWidth = width;
        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(mContext);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0; i < views.length; i++) {
            LinearLayout LL = new LinearLayout(mContext);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL);
            LL.setLayoutParams(new ListView.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            views[i].measure(0, 0);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 8, 5, 8);  // YOU CAN USE THIS
            //LL.addView(TV, params);
            LL.addView(views[i], params);
            LL.measure(0, 0);
            widthSoFar += views[i].getMeasuredWidth();// YOU MAY NEED TO ADD THE MARGINS
            if (widthSoFar >= maxWidth) {
                ll.addView(newLL);

                newLL = new LinearLayout(mContext);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
//                    params = new LinearLayout.LayoutParams(LL
//                            .getMeasuredWidth(), LL.getMeasuredHeight());
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        ll.addView(newLL);
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static Calendar nextDayOfWeek(int dow) {
        Calendar date = Calendar.getInstance();
        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
        System.out.println("GeneralUtils.nextDayOfWeek() diff=" + diff);
        if (!(diff > -1)) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }


    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (isDelimiter(ch, delimiters)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(Character.toLowerCase(ch));
            }
        }
        return buffer.toString();
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (int i = 0, isize = delimiters.length; i < isize; i++) {
            if (ch == delimiters[i]) {
                return true;
            }
        }
        return false;
    }

    public static StringBuilder getbuildVersionStringBuilder() {
        StringBuilder builder = new StringBuilder();

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(fieldName).append(" : ").append(Build.VERSION.RELEASE);
                break;
            }
        }
        return builder;
    }

    //Added by Raman for getting date & time.
    public static TimeStamp getTimeStamp(String orderDate) {
        String orderDateOld = orderDate;
        TimeStamp timeStamp = new TimeStamp();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);
        DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            Date d = sdf.parse(orderDateOld);
            timeStamp.setDate(dateInstance.format(d));
            timeStamp.setTime(timeInstance.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static int getOfferDisplayIcon(String rewardType) {
        int imageId = 0;

        switch (rewardType) {
            case AppConstants.OFFER_BUYXGETY:
                imageId = R.drawable.offer_oneplusone;
                break;

            case AppConstants.OFFER_BUYONEGETONE:
                imageId = R.drawable.offer_oneplusone;
                break;

            case AppConstants.OFFER_DISCOUNT:
                imageId = R.drawable.offer_percentage;
                break;

            case AppConstants.OFFER_FLATOFF:
                imageId = R.drawable.offer_flat_off;
                break;

            case AppConstants.OFFER_FREE:
                imageId = R.drawable.offer_free;
                break;
        }
        return imageId;
    }


    public static DeliveryZone getMinimumDeliveryZone(ArrayList<DeliveryZone> deliveryZonesList) {
        DeliveryZone minimumDeliveryZone = null;
        for (int i = 0; i < deliveryZonesList.size(); i++) {
            if (minimumDeliveryZone == null) {
                minimumDeliveryZone = deliveryZonesList.get(i);
            } else {
                DeliveryZone deliveryZone = deliveryZonesList.get(i);
                if (Double.parseDouble(deliveryZone.getMinDeliveryAmt()) < Double.parseDouble(minimumDeliveryZone.getMinDeliveryAmt())) {
                    minimumDeliveryZone = deliveryZone;
                }
            }
        }
        return minimumDeliveryZone;
    }

    public static DeliveryZone getFilteredDeliveryZone(ArrayList<DeliveryZone> deliveryZonesList) {
        AddressDetailsLocationData currentLocationData = SharedPreferenceSingleton.getInstance().getCurrentUsedLocation();
        LatLng currentPosition = new LatLng(Double.parseDouble(currentLocationData.getCoords().getLat()), Double.parseDouble(currentLocationData.getCoords().getLon()));

        DeliveryZone filteredDeliveryZone = null;
        for (int i = 0; i < deliveryZonesList.size(); i++) {
            DeliveryZone deliveryZone = deliveryZonesList.get(i);
            ArrayList<LatLng> lngArrayList = new ArrayList<>();
            for (int j = 0; j < deliveryZone.getCoordList().size(); j++) {
                LatLng latLng = new LatLng(Double.valueOf(deliveryZone.getCoordList().get(j).getLat()), Double.valueOf(deliveryZone.getCoordList().get(j).getLon()));
                lngArrayList.add(latLng);
            }
            if (PolyUtil.containsLocation(currentPosition, lngArrayList, true)) {
                if (filteredDeliveryZone == null) {
                    filteredDeliveryZone = deliveryZone;
                } else {
                    if (deliveryZone.getZoneType() > filteredDeliveryZone.getZoneType()) {
                        filteredDeliveryZone = deliveryZone;
                    }
                }
            }
        }
        return filteredDeliveryZone;
    }

    public static int getTwystCash() {
        return HttpService.getInstance().getSharedPreferences().getInt(AppConstants.PREFERENCE_LAST_TWYST_CASH, -1);
    }

    public static void setTwystCash(int twystCash) {
        HttpService.getInstance().getSharedPreferences().edit().putInt(AppConstants.PREFERENCE_LAST_TWYST_CASH, twystCash).apply();
    }
}
