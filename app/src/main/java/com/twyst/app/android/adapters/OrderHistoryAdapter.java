package com.twyst.app.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderHistoryActivity;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.activities.OrderTrackingActivity;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.DeliveryZone;
import com.twyst.app.android.model.OrderHistory;
import com.twyst.app.android.model.OrderUpdate;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.model.ReorderMenuAndCart;
import com.twyst.app.android.model.TimeStamp;
import com.twyst.app.android.model.menu.AddonSet;
import com.twyst.app.android.model.menu.Addons;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.MenuCategories;
import com.twyst.app.android.model.menu.MenuData;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubCategories;
import com.twyst.app.android.model.menu.SubOptionSet;
import com.twyst.app.android.model.menu.SubOptions;
import com.twyst.app.android.service.HttpService;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.TwystProgressHUD;
import com.twyst.app.android.util.UtilMethods;
import com.twyst.app.android.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Raman on 1/14/2016.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private ArrayList<OrderHistory> orderHistoryList = new ArrayList<>();
    private final Context mContext;
    private ReorderMenuAndCart reorderMenuAndCart = null;
    private TwystProgressHUD mTwystProgressHUD;

    public ArrayList<OrderHistory> getOrderHistoryList() {
        return orderHistoryList;
    }

    public void setOrderHistoryList(ArrayList<OrderHistory> mOrderHistoryList) {
        this.orderHistoryList = mOrderHistoryList;
        customSortOrderHistoryList();
    }

    public OrderHistoryAdapter(Context context) {
        mContext = context;
    }

    /*
     * There are three categories to be maintained for sorting to be placed in the following order:
     * 1. Trackable
     * 2. Favourite (Old Order)
     * 3. Not-Favourite (Old Order)
     *
     * In each category sorting must be chronologically.
     */
    private void customSortOrderHistoryList() {
        Collections.sort(orderHistoryList, new Comparator<OrderHistory>() {
            @Override
            public int compare(OrderHistory lhs, OrderHistory rhs) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                try {
                    Date orderDateLhs = sdf.parse(lhs.getOrderDate());
                    Date orderDateRhs = sdf.parse(rhs.getOrderDate());
                    return orderDateLhs.compareTo(orderDateRhs);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        int orderHistoryListSize = orderHistoryList.size();
        ArrayList<OrderHistory> orderHistorySortedList = new ArrayList<>();
        int fav_flag = 0;
        int unfav_flag = 0;

        for (int i = 0; i < orderHistoryListSize; i++) {
            OrderHistory order = orderHistoryList.get(i);
            if (order.isTrackable()) {
                orderHistorySortedList.add(0, order);
                fav_flag++;
                unfav_flag++;
            } else if (order.isFavourite()) {
                orderHistorySortedList.add(fav_flag, order);
                unfav_flag++;
            } else {
                orderHistorySortedList.add(unfav_flag, order);
            }
        }
        orderHistoryList = orderHistorySortedList;
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_orders_card, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final OrderHistoryAdapter.ViewHolder holder, final int position) {
        final OrderHistory orderHistory = orderHistoryList.get(position);

        holder.outletNameTextView.setText(orderHistory.getOutletName());
        holder.outletAddressTextView.setText(orderHistory.addressString());
        holder.orderCostTextView.setText(Utils.costString(orderHistory.getOrderCost()));
        String orderDate = orderHistory.getOrderDate();
        TimeStamp timeStamp = Utils.getTimeStamp(orderDate);
        holder.dateTextView.setText(timeStamp.getDate() + " at " + timeStamp.getTime());
        holder.itemBodyTextView.setText(getCompleteItemName(orderHistory));

        if (orderHistory.isTrackable()) {
            holder.reOrderTextView.setText("Track");
            holder.reorder_button.setBackground(mContext.getResources().getDrawable(R.drawable.button_secondary));
        } else {
            holder.reOrderTextView.setText("Re-Order");
            holder.reorder_button.setBackground(mContext.getResources().getDrawable(R.drawable.button_primary));
        }
        holder.reorder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTwystProgressHUD = TwystProgressHUD.show(mContext, false, null);
                if (orderHistory.isTrackable()) {
                    trackOrder(orderHistory);
                    mTwystProgressHUD.dismiss();
                } else {
                    reorderProcessing(orderHistory);
                }
            }
        });

        if (orderHistory.getDeliveryExperience() > 0) {
            holder.deliveryExperience.setText(String.valueOf(orderHistory.getDeliveryExperience()));
            holder.ivStar.setVisibility(View.VISIBLE);
        } else {
            holder.deliveryExperience.setText("- -");
            holder.ivStar.setVisibility(View.GONE);
        }

        holder.favouriteIconButton.setSelected(orderHistory.isFavourite());
        holder.favouriteIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderFavourite(holder.favouriteIconButton, orderHistory);
            }
        });


        if (orderHistory.getBackground() != null) {

            Glide.with(holder.itemView.getContext())
                    .load(orderHistory.getBackground())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.backgroundImage);
        }
    }

    private void updateOrderFavourite(final Button favIcon, final OrderHistory orderHistory) {
        final boolean finalValue = !favIcon.isSelected();
        mTwystProgressHUD = TwystProgressHUD.show(mContext, false, null);
        OrderUpdate orderUpdateFavourite = new OrderUpdate(orderHistory.getOrderID(), OrderUpdate.FAVOURITE, finalValue);

        HttpService.getInstance().putOrderUpdate(UtilMethods.getUserToken((OrderHistoryActivity) mContext), orderUpdateFavourite, new Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse baseResponse, Response response) {
                if (baseResponse.isResponse()) {
                    favIcon.setSelected(finalValue);
                    orderHistory.setIsFavourite(finalValue);
                } else {
                    Toast.makeText(mContext, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mTwystProgressHUD.dismiss();
                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                mTwystProgressHUD.dismiss();
                UtilMethods.handleRetrofitError((OrderHistoryActivity) mContext, error);
                UtilMethods.hideSnackbar();
            }
        });
    }


    private void trackOrder(OrderHistory orderHistory) {
        Intent orderTrackingIntent = new Intent(mContext, OrderTrackingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.INTENT_ORDER_HISTORY, orderHistory);
        bundle.putBoolean(AppConstants.INTENT_PARAM_FROM_ORDER_HISTORY, true);
        bundle.putString(AppConstants.INTENT_ORDER_ID, orderHistory.getOrderID());
        bundle.putString(AppConstants.INTENT_PARAM_PHONE, orderHistory.getPhone());
        bundle.putString(AppConstants.INTENT_ORDER_NUMBER, orderHistory.getOrderNumber());
        orderTrackingIntent.putExtras(bundle);
        orderTrackingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(orderTrackingIntent);
    }


    private String getCompleteItemName(OrderHistory orderHistory) {
        int itemsCount = orderHistory.getItems().size();
        String completeItemName = "";
        String completeItemName3 = "";

        for (int i = 0; i < itemsCount; i++) {
            Items item = orderHistory.getItems().get(i);

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
        return orderHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView outletNameTextView;
        public TextView outletAddressTextView;
        public TextView itemBodyTextView;
        public TextView orderCostTextView;
        public TextView dateTextView;
        public TextView reOrderTextView;
        public View reorder_button;
        public Button favouriteIconButton;
        public ImageView backgroundImage;
        public TextView deliveryExperience;
        public ImageView ivStar;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            outletNameTextView = (TextView) itemLayoutView.findViewById(R.id.outletName);
            outletAddressTextView = (TextView) itemLayoutView.findViewById(R.id.outletAddress);
            itemBodyTextView = (TextView) itemLayoutView.findViewById(R.id.itemBody);
            orderCostTextView = (TextView) itemLayoutView.findViewById(R.id.orderCost_tv);
            dateTextView = (TextView) itemLayoutView.findViewById(R.id.date_tv);
            reOrderTextView = (TextView) itemLayoutView.findViewById(R.id.reorder_TextView);
            reorder_button = itemLayoutView.findViewById(R.id.reorder_button);
            favouriteIconButton = (Button) itemLayoutView.findViewById(R.id.icon_favourite);
            backgroundImage = (ImageView) itemLayoutView.findViewById(R.id.outlet_logo_reorder);
            deliveryExperience = (TextView) itemView.findViewById(R.id.tvDeliveryExperience);
            ivStar = (ImageView) itemView.findViewById(R.id.iv_star);
        }
    }


    private void reorderProcessing(final OrderHistory reOrder) {
        String menuId;
        if (reOrder.getMenuId() != null) {
            menuId = reOrder.getMenuId();
        } else if (reOrder.getItems().size() > 0 && reOrder.getItems().get(0).getMenuId() != null) {
            menuId = reOrder.getItems().get(0).getMenuId();
        } else {
            Toast.makeText(mContext, "Couldn't proceed as MenuId not available", Toast.LENGTH_SHORT);
            return;
        }

        HttpService.getInstance().getMenu(menuId, (UtilMethods.getUserToken((OrderHistoryActivity) mContext)), new Callback<BaseResponse<MenuData>>() {
            @Override
            public void success(BaseResponse<MenuData> menuDataBaseResponse, Response response) {
                if (menuDataBaseResponse.isResponse()) {
                    reorderMenuAndCart = new ReorderMenuAndCart();
                    reorderMenuAndCart.setMenuData(menuDataBaseResponse.getData());
                    MenuData menuData = reorderMenuAndCart.getMenuData();
                    if (menuData != null) {
                        placeReorderProcessing(menuData, reOrder);

                    } else {
                        mTwystProgressHUD.dismiss();
                        Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mTwystProgressHUD.dismiss();
                    Toast.makeText(mContext, menuDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                UtilMethods.hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                UtilMethods.hideSnackbar();
                UtilMethods.handleRetrofitError((OrderHistoryActivity) mContext, error);
            }
        });
    }

    private void placeReorderProcessing(MenuData menuData, OrderHistory reOrder) {
        ArrayList<String> issuesFound = new ArrayList<>();
        ArrayList<Items> itemsToBeAddedToCart = new ArrayList<>();

        if (menuData != null) {
            String mOutletId = menuData.getOutlet();
            for (Items reOrderItem : reOrder.getItems()) {

                boolean costIssueFound = false;
                String costIssueString = null;
                boolean found = false;
                String foundIssueString = null;
                boolean itemFound = false;

                for (MenuCategories menuCategories : menuData.getMenuCategoriesList()) {
//                    MenuPageFragment menuPageFragment = (MenuPageFragment)getSupportFragmentManager().getFragments().get((menuData.getMenuCategoriesList()).indexOf(menuCategories));
                    for (SubCategories subCategories : menuCategories.getSubCategoriesList()) {
                        for (Items items : subCategories.getItemsList()) {
                            if (items.getId().equals(reOrderItem.getId())) { //found item
                                itemFound = true;

                                // added code to add subCategoryId and menuCategoryId to item
                                // Setting menuCategory ID & subCategory ID
                                items.setCategoryID(menuCategories.getId());
                                items.setSubCategoryID(subCategories.getId());

                                //Setting menuCategoryName & subCategoryName
                                items.setCategoryName(menuCategories.getCategoryName());
                                items.setSubCategoryName(subCategories.getSubCategoryName());


                                Items cartItem = new Items(items);
                                Options selected = reOrderItem.getSelectedOption();

                                if ((items.getItemCost() - reOrderItem.getItemCost()) > 0) {
                                    costIssueFound = true;
                                    costIssueString = "Item cost differs by : " + String.valueOf((items.getItemCost() - reOrderItem.getItemCost()));
                                    issuesFound.add(cartItem.getItemName() + " " + costIssueString);
                                }

                                if (selected != null && cartItem.getOptionsList().size() > 0) {

                                    found = false;
                                    for (Options options : cartItem.getOptionsList()) {
                                        if (options.getId().equals(selected.getId())) {
                                            found = true;// found the option

                                            //check for isOptionPriceIsAdditive
                                            if (cartItem.isOptionPriceIsAdditive() || cartItem.isOptionIsAddon()) {
                                                cartItem.setItemCost(cartItem.getItemCost() + options.getOptionCost());
                                            } else {
                                                cartItem.setItemCost(options.getOptionCost());
                                            }

                                            if ((options.getOptionCost() - selected.getOptionCost()) > 0) {
                                                costIssueFound = true;
                                                costIssueString = options.getOptionValue() + " cost differs by : " + String.valueOf((items.getItemCost() - reOrderItem.getItemCost()));
                                                issuesFound.add(cartItem.getItemName() + " " + costIssueString);
                                            }
                                            //check for addons
                                            if (selected.getAddonsList().size() != 0) {
                                                for (Addons selectedAddon : selected.getAddonsList()) {
                                                    found = false;
                                                    for (Addons addon : options.getAddonsList()) {
                                                        if (addon.getId().equals(selectedAddon.getId())) {
                                                            found = true;

                                                            // check for addon
                                                            for (AddonSet selectedAddonSet : selectedAddon.getAddonSetList()) {
                                                                found = false;
                                                                for (AddonSet addonSet : addon.getAddonSetList()) {
                                                                    if ((addonSet.getId()).equals(selectedAddonSet.getId())) {
                                                                        found = true;
                                                                        // updating cartItem ItmCost
                                                                        cartItem.setItemCost(cartItem.getItemCost() + addonSet.getAddonCost());

                                                                        if ((addonSet.getAddonCost() - selectedAddonSet.getAddonCost() > 0)) {
                                                                            costIssueFound = true;
                                                                            costIssueString = addon.getAddonTitle() + " " + addonSet.getAddonValue() + " cost differs by : " + String.valueOf(addonSet.getAddonCost() - selectedAddonSet.getAddonCost());
                                                                            issuesFound.add(cartItem.getItemName() + " " + costIssueString);
                                                                        }
                                                                        break;
                                                                    }
                                                                }
                                                                if (!found) {
                                                                    /// some AddonSet not found ,set inelligibleMisc
                                                                    foundIssueString = selectedAddon.getAddonTitle() + " " + selectedAddonSet.getAddonValue() + " not found in the menu";
                                                                    issuesFound.add(cartItem.getItemName() + " " + foundIssueString);
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    if (!found) {
                                                        // addon not found
                                                        foundIssueString = selectedAddon.getAddonTitle() + " not found in the menu";
                                                        issuesFound.add(cartItem.getItemName() + " " + foundIssueString);
                                                        break;
                                                    }
                                                }
                                            }


                                            //check for subOptions
                                            if (selected.getSubOptionsList().size() > 0) {
                                                found = true;
                                                for (SubOptions selectedSubOption : selected.getSubOptionsList()) {
                                                    found = false;
                                                    for (SubOptions subOption : options.getSubOptionsList()) {
                                                        if ((subOption.getId()).equals(selectedSubOption.getId())) {
                                                            found = true;

                                                            if (selectedSubOption.getSubOptionSetList().size() > 0) {
                                                                found = false;
                                                                SubOptionSet selectedSubOptionSet = selectedSubOption.getSubOptionSetList().get(0);
                                                                for (SubOptionSet subOptionSet : subOption.getSubOptionSetList()) {
                                                                    if ((subOptionSet.getId()).equals(selectedSubOptionSet)) {
                                                                        found = true;
                                                                        // updating cartItem ItmCost
                                                                        cartItem.setItemCost(cartItem.getItemCost() + subOptionSet.getSubOptionCost());

                                                                        if ((subOptionSet.getSubOptionCost() - selectedSubOptionSet.getSubOptionCost()) > 0) {
                                                                            costIssueFound = true;
                                                                            costIssueString = subOption.getSubOptionTitle() + " " + selectedSubOptionSet.getSubOptionValue() + " cost differs by " + String.valueOf(subOptionSet.getSubOptionCost() - selectedSubOptionSet.getSubOptionCost());
                                                                            issuesFound.add(cartItem.getItemName() + " " + costIssueString);
                                                                        }
                                                                        break;
                                                                    }
                                                                }
                                                                if (!found) {
                                                                    // subOptionSet not found
                                                                    foundIssueString = subOption.getSubOptionTitle() + " " + selectedSubOptionSet.getSubOptionValue() + " not found in the menu";
                                                                    issuesFound.add(cartItem.getItemName() + " " + foundIssueString);
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    if (!found) {
                                                        // subOption not found
                                                        foundIssueString = selectedSubOption.getSubOptionTitle() + " not found in the menu";
                                                        issuesFound.add(cartItem.getItemName() + " " + foundIssueString);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!found && foundIssueString == null) {
                                        foundIssueString = selected.getOptionValue() + " not found in the menu";
                                        issuesFound.add(cartItem.getItemName() + " " + foundIssueString);
                                    }

                                    /// needs to be added to cart
                                    cartItem.getOptionsList().clear();
                                    cartItem.setOptionsList(new ArrayList<Options>(Arrays.asList(selected)));
                                    for (int i = 0; i < reOrderItem.getItemQuantity(); i++) {
                                        itemsToBeAddedToCart.add(cartItem);
                                    }
//                                        Toast.makeText(mContext, "Item found: " + cartItem.getItemName(), Toast.LENGTH_LONG).show();

                                } else {

                                    for (int i = 0; i < reOrderItem.getItemQuantity(); i++) {
                                        itemsToBeAddedToCart.add(cartItem);
                                    }
                                }

                            }
                        }
                    }
                }

                if (!itemFound) {
                    issuesFound.add(reOrderItem.getItemName() + " not found int the current menu");
                }
            }
        }

        reorderMenuAndCart.setCartItemsList(itemsToBeAddedToCart);

        if (issuesFound.size() > 0) {
            showReorderErrorsDialog(issuesFound, reOrder);
        } else {
            // intent needs to be fired to OrderOnlineActivity
            mTwystProgressHUD.dismiss();
            fetchOutletAndPassIntent(reOrder);

        }

    }

    private void showReorderErrorsDialog(ArrayList<String> list, final OrderHistory orderHistory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogView = inflater.inflate(R.layout.dialog_menu, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final TextView bOK = (TextView) dialogView.findViewById(R.id.bOK);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        ListView listMenuOptions = (ListView) dialogView.findViewById(R.id.listMenuOptions);

        bOK.setText("CONTINUE ANYWAY");
        tvCancel.setText("CANCEL");
        tvTitle.setText("Reorder can't be continued");
        builder.setView(dialogView);
        bOK.setEnabled(true);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        mTwystProgressHUD.dismiss();
        dialog.show();

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(mContext, R.layout.reorder_error_row_layout, list);
//        TextView tvErrorMessage = (TextView)dia
        listMenuOptions.setAdapter(mAdapter);

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent needs to be fired
                dialog.dismiss();
                fetchOutletAndPassIntent(orderHistory);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void fetchOutletAndPassIntent(final OrderHistory orderHistory) {
        Outlet reorderOutlet = new Outlet();

        // getting relevant delivery zone:
        DeliveryZone filteredDeliveryZone = getFilteredDeliveryZone(orderHistory.getDelivery_zone());

        reorderOutlet.setName(orderHistory.getOutletName());
        reorderOutlet.set_id(orderHistory.getOutletId());
        reorderOutlet.setDeliveryTime(filteredDeliveryZone.getDeliveryEstimatedTime());
        reorderOutlet.setMinimumOrder(filteredDeliveryZone.getMinDeliveryAmt());
        reorderOutlet.setPaymentOptions(filteredDeliveryZone.getPaymentOptions());
        reorderOutlet.setBackground(orderHistory.getBackground());
        if (orderHistory.getMenuId() != null) {
            reorderOutlet.setMenuId(orderHistory.getMenuId());
        } else if (orderHistory.getItems().size() > 0) {
            reorderOutlet.setMenuId(orderHistory.getItems().get(0).getMenuId());
        } else {
            Toast.makeText(mContext, "Unable to Proceed", Toast.LENGTH_SHORT);
            return;
        }
        reorderOutlet.setLogo(orderHistory.getBackground());
        reorderOutlet.setPhone(orderHistory.getPhone());

        Intent intent = new Intent(mContext, OrderOnlineActivity.class);
        intent.putExtra(AppConstants.INTENT_PLACE_REORDER_MENUID, orderHistory.getMenuId());
        intent.putExtra(AppConstants.INTENT_PLACE_REORDER, reorderMenuAndCart);
        intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, reorderOutlet);
        mContext.startActivity(intent);
    }

    private DeliveryZone getFilteredDeliveryZone(ArrayList<DeliveryZone> delivery_zone) {

        return null;
    }
}
