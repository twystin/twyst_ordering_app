package com.twyst.app.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderHistoryActivity;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.model.BaseResponse;
import com.twyst.app.android.model.OrderHistory;
import com.twyst.app.android.model.ReorderMenuAndCart;
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
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Raman on 1/14/2016.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private final ArrayList<OrderHistory> mOrderHistoryList;
    private final Context mContext;
    private ReorderMenuAndCart reorderMenuAndCart = null;
    private TwystProgressHUD twystProgressHUD;

    public OrderHistoryAdapter(Context context, ArrayList<OrderHistory> orderHistoryList) {
        mContext = context;
        this.mOrderHistoryList = orderHistoryList;
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_orders_card, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(OrderHistoryAdapter.ViewHolder holder, final int position) {
        holder.outletNameTextView.setText(mOrderHistoryList.get(position).getOutletName());
        holder.outletAddressTextView.setText("No data from server");
        holder.orderCostTextView.setText(Utils.costString(mOrderHistoryList.get(position).getOrderCost()));
        String orderDate = mOrderHistoryList.get(position).getOrderDate();
        holder.dateTextView.setText(Utils.formatDateTime(orderDate));
        holder.itemBodyTextView.setText(getCompleteItemName(position));
        holder.reOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                twystProgressHUD = TwystProgressHUD.show(mContext, false, null);
                reorderProcessing(mOrderHistoryList.get(position));

            }
        });
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


    private void reorderProcessing(final OrderHistory reOrder) {
        String menuId;
        menuId = reOrder.getMenuId();

        HttpService.getInstance().getMenu(menuId, ((OrderHistoryActivity)mContext).getUserToken(), new Callback<BaseResponse<MenuData>>() {
            @Override
            public void success(BaseResponse<MenuData> menuDataBaseResponse, Response response) {
                if (menuDataBaseResponse.isResponse()) {
                    reorderMenuAndCart = new ReorderMenuAndCart();
                    reorderMenuAndCart.setMenuData(menuDataBaseResponse.getData());
                    MenuData menuData = reorderMenuAndCart.getMenuData();
                    if (menuData != null) {

                        placeReorderProcessing(menuData,reOrder);

                    } else {
                        twystProgressHUD.dismiss();
                        Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    twystProgressHUD.dismiss();
                    Toast.makeText(mContext, menuDataBaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                ((OrderHistoryActivity)mContext).hideProgressHUDInLayout();
                ((OrderHistoryActivity)mContext).hideSnackbar();
            }

            @Override
            public void failure(RetrofitError error) {
                ((OrderHistoryActivity)mContext).hideProgressHUDInLayout();
                ((OrderHistoryActivity)mContext).hideSnackbar();
                ((OrderHistoryActivity)mContext).handleRetrofitError(error);
            }
        });
    }

    public void placeReorderProcessing(MenuData menuData,OrderHistory reOrder) {

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
                                Items cartItem = new Items(items);
                                Options selected = reOrderItem.getSelectedOption();

                                if ((items.getItemCost() - reOrderItem.getItemCost()) > 0) {
                                    costIssueFound = true;
                                    costIssueString = "Item cost differs by : " + String.valueOf((items.getItemCost() - reOrderItem.getItemCost()));
                                }

                                if (!costIssueFound && selected != null && cartItem.getOptionsList().size() > 0) {

                                    found = false;
                                    for (Options options : cartItem.getOptionsList()) {
                                        if (options.getId().equals(selected.getId())) {
                                            found = true;// found the option

                                            if ((options.getOptionCost() - selected.getOptionCost()) > 0) {
                                                costIssueFound = true;
                                                costIssueString = options.getOptionValue() + " cost differs by : " + String.valueOf((items.getItemCost() - reOrderItem.getItemCost()));
                                            }
                                            //check for addons
                                            if (!costIssueFound && selected.getAddonsList().size() != 0) {
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

                                                                        if ((addonSet.getAddonCost() - selectedAddonSet.getAddonCost() > 0)) {
                                                                            costIssueFound = true;
                                                                            costIssueString = addon.getAddonTitle() + " " + addonSet.getAddonValue() + " cost differs by : " + String.valueOf(addonSet.getAddonCost() - selectedAddonSet.getAddonCost());
                                                                        }
                                                                        break;
                                                                    }
                                                                }
                                                                if (!found) {
                                                                    /// some AddonSet not found ,set inelligibleMisc
                                                                    foundIssueString = selectedAddon.getAddonTitle() + " " + selectedAddonSet.getAddonValue() + " not found in the menu";
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    if (!found) {
                                                        // addon not found
                                                        foundIssueString = selectedAddon.getAddonTitle() + " not found in the menu";
                                                        break;
                                                    }
                                                }
                                            }


                                            //check for subOptions
                                            if (!costIssueFound && found && selected.getSubOptionsList().size() > 0) {
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

                                                                        if ((subOptionSet.getSubOptionCost() - selectedSubOptionSet.getSubOptionCost()) > 0) {
                                                                            costIssueFound = true;
                                                                            costIssueString = subOption.getSubOptionTitle() + " " + selectedSubOptionSet.getSubOptionValue() + " cost differs by " + String.valueOf(subOptionSet.getSubOptionCost() - selectedSubOptionSet.getSubOptionCost());
                                                                        }
                                                                        break;
                                                                    }
                                                                }
                                                                if (!found) {
                                                                    // subOptionSet not found
                                                                    foundIssueString = subOption.getSubOptionTitle() + " " + selectedSubOptionSet.getSubOptionValue() + " not found in the menu";
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    if (!found) {
                                                        // subOption not found
                                                        foundIssueString = selectedSubOption.getSubOptionTitle() + " not found in the menu";
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!found && foundIssueString == null){
                                        foundIssueString = selected.getOptionValue() + " not found in the menu";
                                    }

                                    if (found && !costIssueFound) {
                                        /// needs to be added to cart
                                        cartItem.getOptionsList().clear();
                                        cartItem.setOptionsList(new ArrayList<Options>(Arrays.asList(selected)));
                                        for (int i = 0;i < reOrderItem.getItemQuantity();i++){
                                            itemsToBeAddedToCart.add(cartItem);
                                        }
                                        Toast.makeText(mContext, "Item found: " + cartItem.getItemName(), Toast.LENGTH_LONG).show();
                                    } else if (!found) {
                                        issuesFound.add(cartItem.getItemName() + " " + foundIssueString);
                                    } else if (costIssueFound) {
                                        issuesFound.add(cartItem.getItemName() + " " + costIssueString);
                                    } else {
                                        issuesFound.add(cartItem.getItemName() + " couldn't add to cart due to misc reasons");
                                    }
                                } else if (costIssueFound) {
                                    issuesFound.add(cartItem.getItemName() + " " + costIssueString);
                                } else {
                                    for (int i = 0;i < reOrderItem.getItemQuantity();i++){
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
            showReorderErrorsDialog(issuesFound,reOrder.getMenuId());
        } else {
            // intent needs to be fired to OrderOnlineActivity
            Intent intent = new Intent(mContext, OrderOnlineActivity.class);
            intent.putExtra(AppConstants.INTENT_PLACE_REORDER,reorderMenuAndCart);
            intent.putExtra(AppConstants.INTENT_PLACE_REORDER_MENUID, reOrder.getMenuId());
            twystProgressHUD.dismiss();
            mContext.startActivity(intent);
        }

    }

    public void showReorderErrorsDialog(ArrayList<String> list,final String menuId){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogView = inflater.inflate(R.layout.dialog_menu, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final Button bOK = (Button) dialogView.findViewById(R.id.bOK);
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
        twystProgressHUD.dismiss();
        dialog.show();

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(mContext,R.layout.reorder_error_row_layout,list);
//        TextView tvErrorMessage = (TextView)dia
        listMenuOptions.setAdapter(mAdapter);

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent needs to be fired
                Intent intent = new Intent(mContext, OrderOnlineActivity.class);
                intent.putExtra(AppConstants.INTENT_PLACE_REORDER_MENUID, menuId);
                intent.putExtra(AppConstants.INTENT_PLACE_REORDER,reorderMenuAndCart);
                int a =1;
                int b = reorderMenuAndCart.getMenuData().getMenuCategoriesList().get(0).getSubCategoriesList().get(0).getItemsList().get(2).hashCode();
                int c = reorderMenuAndCart.getCartItemsList().get(0).hashCode();
                c = reorderMenuAndCart.getCartItemsList().get(0).getItemOriginalReference().hashCode();
                int d = 2;

                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

}
