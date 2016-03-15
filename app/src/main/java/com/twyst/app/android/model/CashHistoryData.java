package com.twyst.app.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.InterruptedIOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 3/9/2016.
 */
public class CashHistoryData implements Serializable {
    @SerializedName("twyst_cash")
    private int twystCash;

    @SerializedName("twyst_cash_history")
    private ArrayList<TwystCashHistory> cashHistoryList = new ArrayList<TwystCashHistory>();

    public int getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(int twystCash) {
        this.twystCash = twystCash;
    }

    public ArrayList<TwystCashHistory> getCashHistoryList() {
        return cashHistoryList;
    }

    public void setCashHistoryList(ArrayList<TwystCashHistory> cashHistoryList) {
        this.cashHistoryList = cashHistoryList;
    }
}
