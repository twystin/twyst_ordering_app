package com.twyst.app.android.activities;

/**
 * Created by Tushar on 1/29/2016.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.adapters.FeedbackAdapter;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OrderFeedback;
import com.twyst.app.android.model.OrderTrackingState;
import com.twyst.app.android.model.OrderUpdate;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.OrderSummary;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedbackActivity extends BaseActionActivity {
    //    String[] cartlist_temp = {"Dish 1", "Dish 2", "Dish 3", "Dish 4", "Dish 5"};
    String[] cartlist_temp = {};

    private ImageView[] foodOverallRatingStarIcon = new ImageView[5];
    private TextView submit;
    private boolean foodDeliveredOnTimeResponseGiven = false;
    private boolean foodOverallRatingGiven = false;
    private FeedbackAdapter myAdapter;

    // SharedPref Value
    boolean feedbackSubmitted = false;

    // below variable needs to come from some other activity
    private ArrayList<String> orderedItems = new ArrayList<>();

    // Variables to collect feedback -- also to be sent to server
    boolean foodDeliveredOnTime;
    int foodOverallRating;
    ArrayList<Integer> dishRating = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        setupToolBar();
        for (int i = 0; i < cartlist_temp.length; i++) {
            orderedItems.add(cartlist_temp[i]);
        }

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
                foodDeliveredOnTime = true;
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
                foodDeliveredOnTime = false;
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
                sb.append(foodDeliveredOnTime);
                sb.append("Food Over All Rating: ");
                sb.append(foodOverallRating);
//                Toast.makeText(FeedbackActivity.this, sb.toString() + dishRating.toString(), Toast.LENGTH_SHORT).show();

                submitFeedback(HttpService.getInstance().getSharedPreferences().getString(AppConstants.INTENT_ORDER_ID_FEEDBACK, ""),
                        foodOverallRating, foodDeliveredOnTime);
            }
        });
    }

    private void submitFeedback(String orderId, int foodOverallRating, boolean foodDeliveredOnTime) {
        final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(this, false, null);
        OrderUpdate feedbackOrderUpdate = new OrderUpdate(orderId, foodDeliveredOnTime, foodOverallRating);

        HttpService.getInstance().putOrderUpdate(UtilMethods.getUserToken(FeedbackActivity.this), feedbackOrderUpdate, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    HttpService.getInstance().getSharedPreferences().edit().putString(AppConstants.INTENT_ORDER_ID_FEEDBACK, "").apply();
                    Toast.makeText(FeedbackActivity.this, "Thank You for your feedback!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FeedbackActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                twystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                twystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError(FeedbackActivity.this, error);
                UtilMethods.hideSnackbar();
            }
        });
    }

    /**
     * Fill in the Feed back.
     */
    private void fillFeedback() {
        OrderFeedback orderFeedback = new OrderFeedback();
        orderFeedback.setFb_foodDeliveredOnTime(foodDeliveredOnTime);
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

