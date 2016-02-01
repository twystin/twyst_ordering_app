package com.twyst.app.android.activities;

/**
 * Created by Tushar on 1/29/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.FeedbackAdapter;
import com.twyst.app.android.model.OrderFeedback;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.util.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    String[] cartlist_temp = {"Dish 1", "Dish 2", "Dish 3", "Dish 4", "Dish 5"};

    private ImageView[] foodOverallRatingStarIcon = new ImageView[5];
    private TextView submit;
    private boolean foodDeliveredOnTimeResponseGiven = false;
    private boolean foodOverallRatingGiven = false;
    private FeedbackAdapter myAdapter;

    // SharedPref Value
    boolean feedbackSubmitted = false;

    // below variable needs to come from some other activity
    private ArrayList<String> orderedItems = new ArrayList<>();
    private String orderId;

    // Variables to collect feedback -- also to be sent to server
    boolean foodDelieveredOnTime;
    int foodOverallRating;
    ArrayList<Integer> dishRating = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //getExtrasFromBundle();
        for (int i = 0; i < cartlist_temp.length; i++) {
            orderedItems.add(cartlist_temp[i]);
        }

        // For every ORDER, we need to store 'feedback given or not' value (boolean).
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean(orderId, feedbackSubmitted);
        editor.commit();

        // Submit button shouldn't be clickable till the first two responses (in the ListView header) are provided.
        submit = (TextView) findViewById(R.id.bSubmit);
        submit.setClickable(false);
        submit.setEnabled(false);

        // Doing the usual here.
        // 1. Setup an Adapter.
        // 2. Setup a ListView.
        // 3. Inflate an header.
        // 4. Add the header to the list.
        // 5. Set the adapter to the List.
        myAdapter = new FeedbackAdapter(FeedbackActivity.this, R.layout.feedback_dishes_row_layout, orderedItems, getLayoutInflater());
        ListView dishRatingLV = (ListView) findViewById(R.id.dishes_rating_list_view);
        View header = getLayoutInflater().inflate(R.layout.feedback_header_layout, null);
        dishRatingLV.addHeaderView(header);
        dishRatingLV.setAdapter(myAdapter);

        // Find all relevant views from the inflated header.
        final TextView timelyDeliveryResponseYES = (TextView) header.findViewById(R.id.bYES);
        final TextView timelyDeliveryResponseNO = (TextView) header.findViewById(R.id.bNO);
        foodOverallRatingStarIcon = new ImageView[]
                {
                        (ImageView) header.findViewById(R.id.food_overall_rating_star1),
                        (ImageView) header.findViewById(R.id.food_overall_rating_star2),
                        (ImageView) header.findViewById(R.id.food_overall_rating_star3),
                        (ImageView) header.findViewById(R.id.food_overall_rating_star4),
                        (ImageView) header.findViewById(R.id.food_overall_rating_star5)
                };

        // Setting up all the click listeners below
        timelyDeliveryResponseYES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodDelieveredOnTime = true;
                foodDeliveredOnTimeResponseGiven = true;
                timelyDeliveryResponseYES.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                timelyDeliveryResponseYES.setTextColor(getResources().getColor(R.color.white));
                timelyDeliveryResponseNO.setBackgroundColor(getResources().getColor(R.color.background_grey));
                timelyDeliveryResponseNO.setTextColor(getResources().getColor(R.color.textColorSecondary));
                makeSubmitClickable();
            }
        });

        timelyDeliveryResponseNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodDelieveredOnTime = false;
                foodDeliveredOnTimeResponseGiven = true;
                timelyDeliveryResponseNO.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                timelyDeliveryResponseNO.setTextColor(getResources().getColor(R.color.white));
                timelyDeliveryResponseYES.setBackgroundColor(getResources().getColor(R.color.background_grey));
                timelyDeliveryResponseYES.setTextColor(getResources().getColor(R.color.textColorSecondary));
                makeSubmitClickable();
            }
        });


        for (int i = 0; i < 5; i++) {
            foodOverallRatingStarIcon[i].setOnClickListener(new MyOnClickListener(i));
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This should be executed only when SUBMIT is pressed to get correct rating.
                dishRating = myAdapter.getDishRating();
                fillFeedback();

                // Temporary Toast.
                StringBuilder sb = new StringBuilder();
                sb.append("Food Delivery Response Given: ");
                sb.append(foodDeliveredOnTimeResponseGiven);
                sb.append("Food Delivered on Time: ");
                sb.append(foodDelieveredOnTime);
                sb.append("Food Over All Rating: ");
                sb.append(foodOverallRating);
                Toast.makeText(FeedbackActivity.this, sb.toString() + dishRating.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getExtrasFromBundle() {
        Bundle bundle = getIntent().getExtras();
        OrderSummary orderSummary = (OrderSummary) bundle.getSerializable(AppConstants.INTENT_ORDER_SUMMARY);
//        orderId = orderSummary.getOrderID();
        orderId = "KJDKKTMEK";
        for (Items i : orderSummary.getmCartItemsList()) {
            orderedItems.add(i.getItemName());
        }
    }

    /**
     * Fill in the Feed back.
     */
    private void fillFeedback() {
        OrderFeedback orderFeedback = new OrderFeedback();
        orderFeedback.setFb_foodDeliveredOnTime(foodDelieveredOnTime);
        orderFeedback.setFb_foodOverallRating(foodOverallRating);

        HashMap<String, Integer> dishRatingHashMap = new HashMap<>();
        for (int s = 0; s < orderedItems.size(); s++) {
            dishRatingHashMap.put(orderedItems.get(s), dishRating.get(s));
        }
        orderFeedback.setFb_dishRating(dishRatingHashMap);
    }

    private void makeSubmitClickable() {
        if (foodOverallRatingGiven & foodDeliveredOnTimeResponseGiven) {
            submit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            submit.setTextColor(getResources().getColor(R.color.white));
            submit.setClickable(true);
            submit.setEnabled(true);
        }
    }

    /**
     * Customized OnClickListener to accept a variable index.
     */
    class MyOnClickListener implements View.OnClickListener {

        private int index;

        MyOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < 5; i++) {
                if (i <= index)
                    foodOverallRatingStarIcon[i].setImageResource(R.drawable.ratingorange);
                else
                    foodOverallRatingStarIcon[i].setImageResource(R.drawable.ratingwhite);
            }

            foodOverallRating = index + 1;
            foodOverallRatingGiven = true;
            makeSubmitClickable();
        }
    }

}

