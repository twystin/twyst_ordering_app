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
    public static final String PREFS_NAME = "SAVED_ADDRESS";
    public static final String FAVORITES = "Product_Favorite";

    public static final String PREFS_NAME2 = "LAST_CURRENT_LOCATION";
    public static final String CURRENT = "CURRENT_LOCATION";

    public SharedPreferenceAddress() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveAddresses(Context context, List<AddressDetailsLocationData> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
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

        settings = context.getSharedPreferences(PREFS_NAME,
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
