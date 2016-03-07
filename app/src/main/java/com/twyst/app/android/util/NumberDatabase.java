package com.twyst.app.android.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 3/5/2016.
 */
public class NumberDatabase extends SQLiteOpenHelper {
    private static String DATABASE = "Numbers.db";
    private static String TABLE = "NUMBER_MAPPING";
    private static String FIELD_OPERATOR = "operator";
    private static String FIELD_CIRCLE = "circle";
    private static String FIELD_PREFIX = "prefix";

    public NumberDatabase(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableEmp = "create table " + TABLE + "(" +
                FIELD_OPERATOR + " text, " +
                FIELD_CIRCLE + " text, " +
                FIELD_PREFIX + " text)";
        db.execSQL(tableEmp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertValues(String loadJSONFromAsset) {
        this.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        sqLiteDatabase.beginTransaction();
        try {
            try {
                JSONObject jsonObject = new JSONObject(loadJSONFromAsset);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String operator = jsonObject1.getString(FIELD_OPERATOR).toString();
                    String circle = jsonObject1.getString(FIELD_CIRCLE).toString();
                    String prefix = jsonObject1.getString(FIELD_PREFIX).toString();
                    values.put(FIELD_OPERATOR, operator);
                    values.put(FIELD_CIRCLE, circle);
                    values.put(FIELD_PREFIX, prefix);
                    sqLiteDatabase.insert(TABLE, null, values);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    public String[] getFilteredNumberMapping(String number) {
        String[] result = new String[2];
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE, new String[]{FIELD_OPERATOR, FIELD_CIRCLE}
                , FIELD_PREFIX + " LIKE ?", new String[]{getFormattedNumber(number) + "%"}, null, null, null);
        if (cursor.moveToFirst()) {
            result[0] = cursor.getString(0);
            result[1] = cursor.getString(1);
        }
        return result;
    }

    private String getFormattedNumber(String number) {
        if (number.length() == 4) {
            return number;
        } else {
            return number.substring(0, 5);
        }
    }
}