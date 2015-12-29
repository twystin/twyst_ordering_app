package com.twyst.app.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.order.OrderSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/21/2015.
 */
public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Items> mCartItemsList;
    private static int mVegIconHeight = 0; //menuItemName height fixed for a specific device
    private final Context mContext;

    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_FOOTER = 1;

    public SummaryAdapter(Context context, OrderSummary orderSummary) {
        mContext = context;
        mCartItemsList = orderSummary.getmCartItemsList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_summary, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(10, 10, 10, -5);
            v.setLayoutParams(layoutParams);

            SummaryViewHolder vh = new SummaryViewHolder(v);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_summary_footer, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 15, 0, 15);
            v.setLayoutParams(layoutParams);

            SummaryViewHolderFooter vh = new SummaryViewHolderFooter(v);
            return vh;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mCartItemsList.size()) {
            return VIEW_FOOTER;
        } else {
            return VIEW_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SummaryViewHolder) {
            final SummaryViewHolder summaryViewHolder = (SummaryViewHolder) holder;
            View view = summaryViewHolder.itemView;

            final Items item = mCartItemsList.get(position);
            summaryViewHolder.menuItemName.setText(item.getItemName());
            summaryViewHolder.tvCost.setText(item.getItemCost());

            summaryViewHolder.tvItemQuantity.setText("x " + String.valueOf(item.getItemQuantity()));

            if (mVegIconHeight == 0) {
                final TextView tvMenuItemName = summaryViewHolder.menuItemName;
                final LinearLayout llCustomisationsFinal = summaryViewHolder.llCustomisations;
                tvMenuItemName.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                Drawable img;
                                if (item.isVegetarian()) {
                                    img = mContext.getResources().getDrawable(
                                            R.drawable.veg);
                                } else {
                                    img = mContext.getResources().getDrawable(
                                            R.drawable.nonveg);
                                }
                                mVegIconHeight = tvMenuItemName.getMeasuredHeight() * 2 / 3;
                                img.setBounds(0, 0, mVegIconHeight, mVegIconHeight);
                                tvMenuItemName.setCompoundDrawables(img, null, null, null);
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llCustomisationsFinal.getLayoutParams();
                                params.setMargins((mVegIconHeight + tvMenuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
                                llCustomisationsFinal.setLayoutParams(params);

                                tvMenuItemName.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            }
                        });
            } else {
                Drawable img;
                if (item.isVegetarian()) {
                    img = mContext.getResources().getDrawable(
                            R.drawable.veg);
                } else {
                    img = mContext.getResources().getDrawable(
                            R.drawable.nonveg);
                }
                img.setBounds(0, 0, mVegIconHeight, mVegIconHeight);
                summaryViewHolder.menuItemName.setCompoundDrawables(img, null, null, null);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) summaryViewHolder.llCustomisations.getLayoutParams();
                params.setMargins((mVegIconHeight + summaryViewHolder.menuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
                summaryViewHolder.llCustomisations.setLayoutParams(params);

            }

            ArrayList<String> customisationList = item.getCustomisationList();

            final LinearLayout hiddenLayout = summaryViewHolder.llCustomisations;
            final TextView menuItemNameFinal = summaryViewHolder.menuItemName;
            if (customisationList.size() != 0){
                final TextView[] textViews = new TextView[customisationList.size()];
                for (int i = 0;i < customisationList.size();i++){
                    textViews[i] =  new TextView(mContext);
                    textViews[i].setText(customisationList.get(i));
                    textViews[i].setTextColor(mContext.getResources().getColor(R.color.customisations_text_color));
                    textViews[i].setTextSize(12.0f);
                    textViews[i].setPadding(15, 4, 15, 4);
                    textViews[i].setBackgroundResource(R.drawable.border_customisations);
                }
                hiddenLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        int maxWidth = hiddenLayout.getWidth();
                        populateText(hiddenLayout, textViews, mContext, maxWidth - mVegIconHeight - menuItemNameFinal.getCompoundDrawablePadding());
                    }
                });

            } else {
                if (hiddenLayout.getChildCount() != 0) {
                    hiddenLayout.removeAllViews();
                }
            }



        } else {

            SummaryViewHolderFooter summaryViewHolderFooter = (SummaryViewHolderFooter) holder;

        }
    }

    private void populateText(LinearLayout ll, View[] views , Context mContext, int width) {
        ll.removeAllViews();
        int maxWidth = width;
        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(mContext);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0 ; i < views.length ; i++ ){
            LinearLayout LL = new LinearLayout(mContext);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL);
            LL.setLayoutParams(new ListView.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            views[i].measure(0,0);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 8, 5, 8);  // YOU CAN USE THIS
            //LL.addView(TV, params);
            LL.addView(views[i], params);
            LL.measure(0, 0);
            widthSoFar += views[i].getMeasuredWidth();// YOU MAY NEED TO ADD THE MARGINS
            if (widthSoFar >= maxWidth) {
                ll.addView(newLL);

                newLL = new LinearLayout(mContext);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
//                    params = new LinearLayout.LayoutParams(LL
//                            .getMeasuredWidth(), LL.getMeasuredHeight());
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        ll.addView(newLL);
    }

    @Override
    public int getItemCount() {
        return mCartItemsList.size() + 1;
    }

    public static class SummaryViewHolderFooter extends RecyclerView.ViewHolder {

        public SummaryViewHolderFooter(View itemView) {
            super(itemView);
        }
    }

    public static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView menuItemName;
        TextView tvItemQuantity;
        TextView tvCost;
        LinearLayout llCustomisations;

        public SummaryViewHolder(View itemView) {
            super(itemView);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
            this.tvItemQuantity = (TextView) itemView.findViewById(R.id.tvItemQuantity);
            this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);
            this.llCustomisations = (LinearLayout) itemView.findViewById(R.id.llCustomisations);
        }
    }

}


