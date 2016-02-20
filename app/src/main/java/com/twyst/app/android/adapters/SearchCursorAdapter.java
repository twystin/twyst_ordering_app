package com.twyst.app.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.twyst.app.android.model.Offer;

import java.util.List;

/**
 * Created by rahuls on 2/9/15.
 */
public class SearchCursorAdapter extends CursorAdapter{

    private List<Offer> offers;

    private TextView textView;


    public SearchCursorAdapter(Context context, Cursor cursor, List<Offer> offers) {
        super(context, cursor, false);
        this.offers = offers;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
