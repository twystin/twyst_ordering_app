package com.twyst.app.android.util;

import android.content.Context;

import com.twyst.app.android.service.HttpService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Vipul Sharma on 3/5/2016.
 */
public class NumberDatabaseSingleton {
    private static NumberDatabaseSingleton instance = null;
    private NumberDatabase numberDatabase;

    public NumberDatabase getNumberDatabase() {
        return numberDatabase;
    }

    public void setNumberDatabase(NumberDatabase numberDatabase) {
        this.numberDatabase = numberDatabase;
    }

    private NumberDatabaseSingleton() {
        super();
    }

    public static NumberDatabaseSingleton getInstance() {
        if (instance == null) {
            return instance = new NumberDatabaseSingleton();
        } else {
            return instance;
        }
    }

    public void setup(Context context) {
        numberDatabase = new NumberDatabase(context);
        if (!HttpService.getInstance().getSharedPreferences().getBoolean(AppConstants.KEY_DATABASE_SAVED, false)) {
            numberDatabase.insertValues(loadJSONFromAsset(context));
            HttpService.getInstance().getSharedPreferences().edit().putBoolean(AppConstants.KEY_DATABASE_SAVED, true).apply();
        }
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("number_mapping.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
