package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.TwystCashPagerAdapter;

import java.util.ArrayList;

public class TwystCashHistoryActivity extends BaseActionActivity {
    private final int TAB_COUNT = 3;
    private ViewPager viewPager;
    private TwystCashPagerAdapter mPagerAdapter;

    static ArrayList<CashHistory> cashData = new ArrayList<CashHistory>();
    static ArrayList<CashHistory> creditList = new ArrayList<CashHistory>();
    static ArrayList<CashHistory> debitList = new ArrayList<CashHistory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twyst_cash_history);
        setupToolBar();

        createFakeData();
        debitList = getDebitData(cashData);
        creditList = getCreditData(cashData);

        viewPager = (ViewPager) findViewById(R.id.pager_my_twyst_cash);
        mPagerAdapter = new TwystCashPagerAdapter(TAB_COUNT, getSupportFragmentManager(), TwystCashHistoryActivity.this);
        viewPager.setAdapter(mPagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_my_twyst_cash);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static ArrayList<CashHistory> getCreditList() {
        return creditList;
    }

    public void setCreditList(ArrayList<CashHistory> creditList) {
        this.creditList = creditList;
    }

    public static ArrayList<CashHistory> getDebitList() {
        return debitList;
    }

    public void setDebitList(ArrayList<CashHistory> debitList) {
        this.debitList = debitList;
    }

    public static ArrayList<CashHistory> getCashData() {
        return cashData;
    }

    public static void setCashData(ArrayList<CashHistory> cashData) {
        TwystCashHistoryActivity.cashData = cashData;
    }

    private void createFakeData() {
        cashData.add(new CashHistory("16 Jan", "2016", "Biryani Blues", "500", "+", "50", "550"));
        cashData.add(new CashHistory("12 Jan", "2015", "Leaping Caravan", "700", "-", "75", "505"));
        cashData.add(new CashHistory("21 Feb", "2016", "Flipkart Voucher", "1500", "+", "220", "220"));
        cashData.add(new CashHistory("16 Mar", "2015", "La Pino'z Pizza", "420", "-", "250", "450"));
        cashData.add(new CashHistory("15 Apr", "2016", "Chaayos", "500", "+", "750", "550"));
        cashData.add(new CashHistory("18 Jul", "2015", "Biryaani Blues Heaven Voucher", "Free Biryaani", "-", "350", "789"));
        cashData.add(new CashHistory("1 Jan", "2016", "Haye Mirchi", "1000", "+", "750", "879"));
        cashData.add(new CashHistory("26 Jun", "2015", "Dominos Pizza", "350", "-", "500", "1000"));
        cashData.add(new CashHistory("16 Aug", "2016", "Pizza Hut", "250", "+", "245", "2764"));
        cashData.add(new CashHistory("14 Jan", "2015", "Gyani's", "1800", "+", "756", "4856"));
        cashData.add(new CashHistory("16 Jan", "2016", "Captain Bill Rocking Delivery", "2350", "-", "420", "457"));
    }

    private ArrayList<CashHistory> getDebitData(ArrayList<CashHistory> mDataList) {
        ArrayList<CashHistory> tempList = new ArrayList<CashHistory>();
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getTransaction().equalsIgnoreCase("-")) {
                tempList.add(mDataList.get(i));
            }
        }
        return tempList;
    }

    private ArrayList<CashHistory> getCreditData(ArrayList<CashHistory> mDataList) {
        ArrayList<CashHistory> tempList = new ArrayList<CashHistory>();
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getTransaction().equalsIgnoreCase("+")) {
                tempList.add(mDataList.get(i));
            }
        }
        return tempList;
    }

    public class CashHistory {
        String date;
        String year;
        String itemName;
        String transactionAmount;
        String amount;
        String balance;
        String transaction;

        public CashHistory(String date, String year, String itemName, String transactionAmount, String transaction, String amount, String balance) {
            this.date = date;
            this.year = year;
            this.itemName = itemName;
            this.transactionAmount = transactionAmount;
            this.amount = amount;
            this.balance = balance;
            this.transaction = transaction;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getTransactionAmount() {
            return transactionAmount;
        }

        public void setTransactionAmount(String transactionAmount) {
            this.transactionAmount = transactionAmount;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getTransaction() {
            return transaction;
        }

        public void setTransaction(String transaction) {
            this.transaction = transaction;
        }
    }

}
