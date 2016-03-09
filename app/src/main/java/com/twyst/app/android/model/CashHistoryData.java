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
    int twystCash;

    @SerializedName("twyst_cash_history")
    ArrayList<TwystCashHistory> cashHistory = new ArrayList<>();

    public int getTwystCash() {
        return twystCash;
    }

    public void setTwystCash(int twystCash) {
        this.twystCash = twystCash;
    }

    public ArrayList<TwystCashHistory> getCashHistory() {
        return cashHistory;
    }

    public void setCashHistory(ArrayList<TwystCashHistory> cashHistory) {
        this.cashHistory = cashHistory;
    }
}
