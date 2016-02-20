package com.twyst.app.android.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.BucksPagerAdapter;

import java.util.ArrayList;

public class TwystBucksHistoryActivity extends BaseActionActivity {
    private final int TAB_COUNT = 3;
    private ViewPager viewPager;
    private BucksPagerAdapter mPagerAdapter;

    static ArrayList<BucksHistory> bucksData = new ArrayList<BucksHistory>();
    static ArrayList<BucksHistory> creditList = new ArrayList<BucksHistory>();
    static ArrayList<BucksHistory> debitList = new ArrayList<BucksHistory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twyst_bucks_history);
        setupToolBar();


        createFakeData();
        debitList = getDebitData(bucksData);
        creditList = getCreditData(bucksData);

        viewPager = (ViewPager) findViewById(R.id.pager_my_twyst_bucks);
        mPagerAdapter = new BucksPagerAdapter(TAB_COUNT, getSupportFragmentManager(), TwystBucksHistoryActivity.this);
        viewPager.setAdapter(mPagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_my_twyst_bucks);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static ArrayList<BucksHistory> getCreditList() {
        return creditList;
    }

    public void setCreditList(ArrayList<BucksHistory> creditList) {
        this.creditList = creditList;
    }

    public static ArrayList<BucksHistory> getDebitList() {
        return debitList;
    }

    public void setDebitList(ArrayList<BucksHistory> debitList) {
        this.debitList = debitList;
    }

    public static ArrayList<BucksHistory> getBucksData() {
        return bucksData;
    }

    public static void setBucksData(ArrayList<BucksHistory> bucksData) {
        TwystBucksHistoryActivity.bucksData = bucksData;
    }

    private void createFakeData() {
        bucksData.add(new BucksHistory("16 Jan", "2016", "Biryani Blues", "500", "+", "50", "550"));
        bucksData.add(new BucksHistory("12 Jan", "2015", "Leaping Caravan", "700", "-", "75", "505"));
        bucksData.add(new BucksHistory("21 Feb", "2016", "Flipkart Voucher", "1500", "+", "220", "220"));
        bucksData.add(new BucksHistory("16 Mar", "2015", "La Pino'z Pizza", "420", "-", "250", "450"));
        bucksData.add(new BucksHistory("15 Apr", "2016", "Chaayos", "500", "+", "750", "550"));
        bucksData.add(new BucksHistory("18 Jul", "2015", "Biryaani Blues Heaven Voucher", "Free Biryaani", "-", "350", "789"));
        bucksData.add(new BucksHistory("1 Jan", "2016", "Haye Mirchi", "1000", "+", "750", "879"));
        bucksData.add(new BucksHistory("26 Jun", "2015", "Dominos Pizza", "350", "-", "500", "1000"));
        bucksData.add(new BucksHistory("16 Aug", "2016", "Pizza Hut", "250", "+", "245", "2764"));
        bucksData.add(new BucksHistory("14 Jan", "2015", "Gyani's", "1800", "+", "756", "4856"));
        bucksData.add(new BucksHistory("16 Jan", "2016", "Captain Bill Rocking Delivery", "2350", "-", "420", "457"));
    }

    private ArrayList<BucksHistory> getDebitData(ArrayList<BucksHistory> mDataList) {
        ArrayList<BucksHistory> tempList = new ArrayList<BucksHistory>();
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getTransaction().equalsIgnoreCase("-")) {
                tempList.add(mDataList.get(i));
            }
        }
        return tempList;
    }

    private ArrayList<BucksHistory> getCreditData(ArrayList<BucksHistory> mDataList) {
        ArrayList<BucksHistory> tempList = new ArrayList<BucksHistory>();
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getTransaction().equalsIgnoreCase("+")) {
                tempList.add(mDataList.get(i));
            }
        }
        return tempList;
    }

    public class BucksHistory {
        String date;
        String year;
        String itemName;
        String transactionAmount;
        String amount;
        String balance;
        String transaction;

        public BucksHistory(String date, String year, String itemName, String transactionAmount, String transaction, String amount, String balance) {
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
