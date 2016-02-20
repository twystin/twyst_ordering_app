package com.twyst.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twyst.app.android.R;

import java.util.ArrayList;

/**
 * Created by tushar on 28/01/16.
 */
public class FeedbackAdapter extends ArrayAdapter<String> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Integer> dishRating = new ArrayList<>();
    private ArrayList<String> dishName = new ArrayList<>();
    ImageView[] stars = new ImageView[5];

    // Layout Elements
    private ArrayList<ImageView[]> dishRatingStarIcon = new ArrayList<>();


    public FeedbackAdapter(Context context, int resource, ArrayList<String> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.mLayoutInflater = inflater;
        this.dishName = objects;
        for (int k = 0; k < objects.size(); k++) {
            dishRating.add(0);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View myRow = convertView;

        if (myRow == null) {
            myRow = mLayoutInflater.inflate(R.layout.feedback_dishes_row_layout, parent, false);
        }

        // Generate an array of all stars and place it in dishRatingStarIcon List.
        // Reference needs to be saved for all the stars for the user to click on any
        // one of them at any time.
        stars = new ImageView[]
                {
                        (ImageView) myRow.findViewById(R.id.dish_rating_star1),
                        (ImageView) myRow.findViewById(R.id.dish_rating_star2),
                        (ImageView) myRow.findViewById(R.id.dish_rating_star3),
                        (ImageView) myRow.findViewById(R.id.dish_rating_star4),
                        (ImageView) myRow.findViewById(R.id.dish_rating_star5)
                };
        dishRatingStarIcon.add(position, stars);

        // Set the text of Dish name as provided to the adapter.
        TextView tvDishName = (TextView) myRow.findViewById(R.id.tvDishName);
        if (dishName.get(position) != null)
            tvDishName.setText(dishName.get(position));
        else
            tvDishName.setText(String.format("Dish %d", (position + 1)));

        // Setting up setOnclickListener on all the stars
        for (int i = 0; i < stars.length; i++) {
            dishRatingStarIcon.get(position)[i].setOnClickListener(new MyOnClickListener(i, position));
        }

        return myRow;
    }

    public ArrayList<Integer> getDishRating() {
        return dishRating;
    }

    /**
     * Customized OnClickListener to accept a variable index and position of the list clicked.
     */
    class MyOnClickListener implements View.OnClickListener {
        int index;
        int positionClicked;

        MyOnClickListener(int index, int positionClicked) {
            this.index = index;
            this.positionClicked = positionClicked;
        }

        @Override
        public void onClick(View v) {
            for (int j = 0; j < stars.length; j++) {
                if (j <= index)
                    dishRatingStarIcon.get(positionClicked)[j].setImageResource(R.drawable.ratingorange);
                else
                    dishRatingStarIcon.get(positionClicked)[j].setImageResource(R.drawable.ratingwhite);
            }
            dishRating.set(positionClicked, index + 1);
        }
    }
}

