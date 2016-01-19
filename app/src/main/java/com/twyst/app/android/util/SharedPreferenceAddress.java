package com.twyst.app.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.twyst.app.android.model.AddressDetailsLocationData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vipul Sharma on 1/8/2016.
 */
public class SharedPreferenceAddress {
    public static final String FAVORITES = "Product_Favorite";


    public SharedPreferenceAddress() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveAddresses(Context context, List<AddressDetailsLocationData> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }



    public void saveCurrentUsedLocation(Context context,AddressDetailsLocationData currentUsedLocation) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonCurrentUsedLocation = gson.toJson(currentUsedLocation);

        editor.putString(AppConstants.CURRENT_USED_LOCATION, jsonCurrentUsedLocation);

        editor.commit();
    }

    public void saveLastUsedLocation(Context context,AddressDetailsLocationData lastUsedLocation) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonLastUsedLocation = gson.toJson(lastUsedLocation);

        editor.putString(AppConstants.LAST_USED_LOCATION, jsonLastUsedLocation);

        editor.commit();
    }

    public AddressDetailsLocationData getCurrentUsedLocation(Context context) {
        SharedPreferences settings;

        settings = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(AppConstants.CURRENT_USED_LOCATION)) {
            String jsonFavorites = settings.getString(AppConstants.CURRENT_USED_LOCATION, null);
            Gson gson = new Gson();
            AddressDetailsLocationData currentUsedLocation = gson.fromJson(jsonFavorites,
                    AddressDetailsLocationData.class);

            return currentUsedLocation;
        } else
            return null;

    }

    public AddressDetailsLocationData getLastUsedLocation(Context context) {
        SharedPreferences settings;

        settings = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(AppConstants.LAST_USED_LOCATION)) {
            String jsonFavorites = settings.getString(AppConstants.LAST_USED_LOCATION, null);
            Gson gson = new Gson();
            AddressDetailsLocationData lastUsedLocation = gson.fromJson(jsonFavorites,
                    AddressDetailsLocationData.class);

            return lastUsedLocation;
        } else
            return null;

    }


    public void addAddress(Context context, AddressDetailsLocationData product) {
        List<AddressDetailsLocationData> favorites = getAddresses(context);
        if (favorites == null)
            favorites = new ArrayList<AddressDetailsLocationData>();
        favorites.add(0, product);
        saveAddresses(context, favorites);
    }

    public void removeAddress(Context context, AddressDetailsLocationData product) {
        ArrayList<AddressDetailsLocationData> favorites = getAddresses(context);
        if (favorites != null) {
            favorites.remove(product);
            saveAddresses(context, favorites);
        }
    }

    public ArrayList<AddressDetailsLocationData> getAddresses(Context context) {
        SharedPreferences settings;
        List<AddressDetailsLocationData> favorites;

        settings = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            AddressDetailsLocationData[] favoriteItems = gson.fromJson(jsonFavorites,
                    AddressDetailsLocationData[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<AddressDetailsLocationData>(favorites);
        } else
            return null;

        return (ArrayList<AddressDetailsLocationData>) favorites;
    }
}
