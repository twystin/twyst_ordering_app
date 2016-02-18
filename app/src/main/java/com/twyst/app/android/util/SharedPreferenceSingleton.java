package com.twyst.app.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.twyst.app.android.model.AddressDetailsLocationData;
import com.twyst.app.android.model.order.Coords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anshul Goyal on 1/8/2016.
 */
public class SharedPreferenceSingleton {
    public static final String FAVORITES = "Product_Favorite";
    private static SharedPreferenceSingleton instance = null;
    private SharedPreferences sharedPreferences = null;
    private Context context;
    private boolean skipLocationClicked = false;
    private boolean saveLocationClicked = false;
    private boolean passedCartCheckoutStage = false;

    public boolean isPassedCartCheckoutStage() {
        return passedCartCheckoutStage;
    }

    public void setPassedCartCheckoutStage(boolean passedCartCheckoutStage) {
        this.passedCartCheckoutStage = passedCartCheckoutStage;
    }

    public boolean isSkipLocationClicked() {
        return skipLocationClicked;
    }

    public void setSkipLocationClicked(boolean skipLocationClicked) {
        this.skipLocationClicked = skipLocationClicked;
    }

    public boolean isSaveLocationClicked() {
        return saveLocationClicked;
    }

    public void setSaveLocationClicked(boolean saveLocationClicked) {
        this.saveLocationClicked = saveLocationClicked;
    }

    private SharedPreferenceSingleton() {
        super();
    }

    public static SharedPreferenceSingleton getInstance() {
        if (instance == null) {
            return instance = new SharedPreferenceSingleton();
        } else {
            return instance;
        }
    }

    public void setup(Context context) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public AddressDetailsLocationData getDeliveryLocation(){
        return getCurrentUsedLocation();
    }

    public Coords getDeliveryCoords(){
        AddressDetailsLocationData addressDetailsLocationData = getDeliveryLocation();
        return addressDetailsLocationData.getCoords();
    }

    // This four methods are used for maintaining favorites.
    public void saveAddresses(List<AddressDetailsLocationData> favorites) {
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }


    public void saveCurrentUsedLocation(AddressDetailsLocationData currentUsedLocation) {
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCurrentUsedLocation = gson.toJson(currentUsedLocation);

        editor.putString(AppConstants.CURRENT_USED_LOCATION, jsonCurrentUsedLocation);

        editor.commit();
    }

    public AddressDetailsLocationData getCurrentUsedLocation() {
        if (sharedPreferences != null && sharedPreferences.contains(AppConstants.CURRENT_USED_LOCATION)) {
            String jsonFavorites = sharedPreferences.getString(AppConstants.CURRENT_USED_LOCATION, null);
            Gson gson = new Gson();
            AddressDetailsLocationData currentUsedLocation = gson.fromJson(jsonFavorites,
                    AddressDetailsLocationData.class);

            return currentUsedLocation;
        } else
            return null;

    }

    public void addAddress(AddressDetailsLocationData product) {
        List<AddressDetailsLocationData> favorites = getAddresses();
        if (favorites == null)
            favorites = new ArrayList<AddressDetailsLocationData>();
        favorites.add(0, product);
        saveAddresses(favorites);
    }

    public void removeAddress(Context context, AddressDetailsLocationData product) {
        ArrayList<AddressDetailsLocationData> favorites = getAddresses();
        if (favorites != null) {
            favorites.remove(product);
            saveAddresses(favorites);
        }
    }

    public ArrayList<AddressDetailsLocationData> getAddresses() {
        List<AddressDetailsLocationData> favorites;
        if (sharedPreferences != null && sharedPreferences.contains(FAVORITES)) {
            String jsonFavorites = sharedPreferences.getString(FAVORITES, null);
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
