package com.twyst.app.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.OrderHistory;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raman on 1/14/2016.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final ArrayList<OrderHistory> mOrderHistoryList;
    private final Context mContext;

    public OrderHistoryAdapter(Context context, List<OrderHistory> orderHistoryList) {
        mContext = context;
        this.mOrderHistoryList = (ArrayList) orderHistoryList;
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_orders_card, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(OrderHistoryAdapter.ViewHolder holder, int position) {
        holder.outletNameTextView.setText(mOrderHistoryList.get(position).getOutletName());
        holder.outletAddressTextView.setText("No data from server");
        holder.orderCostTextView.setText(Utils.costString(mOrderHistoryList.get(position).getOrderCost()));
        String orderDate = mOrderHistoryList.get(position).getOrderDate();
        holder.dateTextView.setText(Utils.formatDateTime(orderDate));
        holder.itemBodyTextView.setText(getCompleteItemName(position));
        //change the drawable icon if the item is a favourite
//        if(mOrderHistoryList.get(position).isFavourite()){
//        }
    }

    private String getCompleteItemName(int position) {
        int itemsCount = mOrderHistoryList.get(position).getItems().size();
        String completeItemName = "";
        String completeItemName3 = "";

        for (int i = 0; i < itemsCount; i++) {
            Items item = mOrderHistoryList.get(position).getItems().get(i);

            if (i == 0) {
                completeItemName = completeItemName + item.getItemName() + " x " + item.getItemQuantity();
            } else {
                completeItemName = completeItemName + "\n" + item.getItemName() + " x " + item.getItemQuantity();
                if (i == 2) {
                    //Complete item name to show till 3 rows
                    completeItemName3 = completeItemName;
                }
            }
        } // for loop i

        if (itemsCount <= 3) {
            return completeItemName;
        } else {
            //Complete item name to show for more than 3 rows
            return completeItemName3 + " ... + " + (itemsCount - 3) + " items";
        }

    }


    @Override
    public int getItemCount() {
        return mOrderHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView outletNameTextView;
        public TextView outletAddressTextView;
        public TextView itemBodyTextView;
        public TextView orderCostTextView;
        public TextView dateTextView;
        public TextView reOrderTextView;
        public Button favouriteIconButton;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            outletNameTextView = (TextView) itemLayoutView.findViewById(R.id.outletName);
            outletAddressTextView = (TextView) itemLayoutView.findViewById(R.id.outletAddress);
            itemBodyTextView = (TextView) itemLayoutView.findViewById(R.id.itemBody);
            orderCostTextView = (TextView) itemLayoutView.findViewById(R.id.orderCost_tv);
            dateTextView = (TextView) itemLayoutView.findViewById(R.id.date_tv);
            reOrderTextView = (TextView) itemLayoutView.findViewById(R.id.reorder_TextView);
            favouriteIconButton = (Button) itemLayoutView.findViewById(R.id.icon_favourite);
        }
    }
}
