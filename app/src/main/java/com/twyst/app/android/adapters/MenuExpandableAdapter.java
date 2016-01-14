package com.twyst.app.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.twyst.app.android.R;
import com.twyst.app.android.menu.MenuChildViewHolder;
import com.twyst.app.android.menu.MenuParentViewHolder;
import com.twyst.app.android.model.menu.AddonSet;
import com.twyst.app.android.model.menu.Addons;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubCategories;
import com.twyst.app.android.model.menu.SubOptionSet;
import com.twyst.app.android.model.menu.SubOptions;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vipul Sharma on 12/22/2015.
 */
public class MenuExpandableAdapter extends ExpandableRecyclerAdapter<MenuParentViewHolder, MenuChildViewHolder> {
    private static int mVegIconHeight = 0; //menuItemName height fixed for a specific device
    private LayoutInflater mInflater;
    private Context mContext;
    DataTransferInterfaceMenu mDataTransferInterfaceMenu;
    final RecyclerView mMenuExpandableList;

    public MenuExpandableAdapter(Context context, List<ParentListItem> itemList, RecyclerView menuExpandableList) {
        super(itemList);
        mContext = context;
        mMenuExpandableList = menuExpandableList;
        mInflater = LayoutInflater.from(context);
        mDataTransferInterfaceMenu = (DataTransferInterfaceMenu) context;
    }

    @Override
    public void onParentListItemExpanded(int parentPosition) {
        LinearLayoutManager llm = (LinearLayoutManager) mMenuExpandableList.getLayoutManager();
        llm.scrollToPositionWithOffset(parentPosition, 0);
        // Alternatively keep track of the single item that is expanded and explicitly collapse that row (more efficient)
        super.onParentListItemExpanded(parentPosition);
    }

    @Override
    public MenuParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.layout_menu_group, viewGroup, false);
        return new MenuParentViewHolder(view);
    }

    @Override
    public MenuChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.layout_menu, viewGroup, false);
        return new MenuChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(MenuParentViewHolder menuParentViewHolder, final int parentPosition, ParentListItem parentListItem) {
        SubCategories subCategories = (SubCategories) parentListItem;
        // Removing divider when 1st parent as shadow is there in TabLayout
        if (parentPosition == 0) {
            menuParentViewHolder.menuGroupDivider.setVisibility(View.GONE);
        }
        menuParentViewHolder.text.setText(subCategories.getSubCategoryName());
        if (subCategories.getSubCategoryName().equalsIgnoreCase(AppConstants.DEFAULT_SUB_CATEGORY)) {
            menuParentViewHolder.llMenuGroup.getLayoutParams().height = 0;
            menuParentViewHolder.llMenuGroup.post(new Runnable() {
                @Override
                public void run() {
                    expandParent(parentPosition);
                }
            });
        }
    }

    @Override
    public void onBindChildViewHolder(MenuChildViewHolder childViewHolder, final int childPosition, Object childListItem) {
        final Items item = (Items) childListItem;
        childViewHolder.mIvPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(item);
            }
        });

        childViewHolder.mIvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(item);
            }
        });

        if (item.getItemQuantity() == 0) {
            childViewHolder.mIvMinus.setVisibility(View.INVISIBLE);
            childViewHolder.tvQuantity.setVisibility(View.INVISIBLE);
        } else {
            childViewHolder.mIvMinus.setVisibility(View.VISIBLE);
            childViewHolder.tvQuantity.setVisibility(View.VISIBLE);
            childViewHolder.tvQuantity.setText(String.valueOf(item.getItemQuantity()));
        }

        if (mVegIconHeight == 0) {
            final TextView tvMenuItemName = childViewHolder.menuItemName;
            final TextView tvMenuItemDesc = childViewHolder.menuItemDesc;
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
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvMenuItemDesc.getLayoutParams();
                            params.setMargins((mVegIconHeight + tvMenuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
                            tvMenuItemDesc.setLayoutParams(params);

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
            childViewHolder.menuItemName.setCompoundDrawables(img, null, null, null);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) childViewHolder.menuItemDesc.getLayoutParams();
            params.setMargins((mVegIconHeight + childViewHolder.menuItemName.getCompoundDrawablePadding()), params.topMargin, 0, 0);
            childViewHolder.menuItemDesc.setLayoutParams(params);
        }

        childViewHolder.menuItemName.setText(item.getItemName());
        if (item.getItemDescription() != null) {
            childViewHolder.menuItemDesc.setText(item.getItemDescription());
        }
        childViewHolder.tvCost.setText(Utils.costString(item.getItemCost()));
    }

    private void add(Items item) {
        Items cartItem = new Items(item);
        cartItem.setCategoryID(item.getCategoryID());
        cartItem.setSubCategoryID(item.getSubCategoryID());
        if (cartItem.getOptionsList().size() > 0) {
            showDialogOptions(cartItem);
        } else {
            addToCart(cartItem);
        }
    }

    private void remove(Items item) {
        mDataTransferInterfaceMenu.removeMenu(item);
    }

    private void addToCart(Items cartItemToBeAdded) {
        mDataTransferInterfaceMenu.addMenu(cartItemToBeAdded);
    }

    private void showDialogOptions(final Items cartItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = mInflater.inflate(R.layout.dialog_menu, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final Button bOK = (Button) dialogView.findViewById(R.id.bOK);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        ListView listMenuOptions = (ListView) dialogView.findViewById(R.id.listMenuOptions);
        bOK.setText("CONFIRM");
        Options option = cartItem.getOptionsList().get(0);
        if (option.getSubOptionsList().size() > 0 || option.getAddonsList().size() > 0) {
            bOK.setText("NEXT");
        }
        tvCancel.setText("CANCEL");
        tvTitle.setText(cartItem.getOptionTitle());
        builder.setView(dialogView);
        bOK.setEnabled(false);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        final MenuOptionsAdapter menuOptionsAdapter = new MenuOptionsAdapter(mContext, cartItem.getOptionsList());
        listMenuOptions.setAdapter(menuOptionsAdapter);

        listMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                bOK.setEnabled(true);
                Options option = cartItem.getOptionsList().get(position);
                if (option.getSubOptionsList().size() > 0 || option.getAddonsList().size() > 0) {
                    bOK.setText("NEXT");
                }
                menuOptionsAdapter.setSelectedPosition(position);
                menuOptionsAdapter.notifyDataSetChanged();
            }
        });

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Options option = cartItem.getOptionsList().get(menuOptionsAdapter.getSelectedPosition());
                Options optionNew = new Options(option);
                cartItem.getOptionsList().clear();
                cartItem.getOptionsList().add(optionNew);
                cartItem.setItemCost(optionNew.getOptionCost());
                if (option.getSubOptionsList().size() > 0) {
                    showDialogSubOptions(cartItem, 0);
                } else {
                    if (option.getAddonsList().size() > 0) {
                        showDialogAddons(cartItem, 0);
                    } else {
                        addToCart(cartItem);
                    }
                }
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void showDialogSubOptions(final Items cartItem, final int currentIndex) {
        final Options option = cartItem.getOptionsList().get(0); // one option selected
        final SubOptions subOptionNew = new SubOptions(option.getSubOptionsList().get(currentIndex));

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = mInflater.inflate(R.layout.dialog_menu, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final Button bOK = (Button) dialogView.findViewById(R.id.bOK);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        ListView listMenuOptions = (ListView) dialogView.findViewById(R.id.listMenuOptions);
        bOK.setText("CONFIRM");

        if ((currentIndex + 1) < option.getSubOptionsList().size() || option.getAddonsList().size() > 0) {
            bOK.setText("NEXT");
        }
        tvCancel.setText("CANCEL");
        tvTitle.setText(subOptionNew.getSubOptionTitle());
        builder.setView(dialogView);
        bOK.setEnabled(false);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        final MenuSubOptionsAdapter menuSubOptionsAdapter = new MenuSubOptionsAdapter(mContext, subOptionNew.getSubOptionSetList());
        listMenuOptions.setAdapter(menuSubOptionsAdapter);

        listMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                bOK.setEnabled(true);
                menuSubOptionsAdapter.setSelectedPosition(position);
                menuSubOptionsAdapter.notifyDataSetChanged();
            }
        });

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<SubOptionSet> subOptionSetListNew = new ArrayList<>();
                SubOptionSet subOptionSet = subOptionNew.getSubOptionSetList().get(menuSubOptionsAdapter.getSelectedPosition());
                subOptionSetListNew.add(subOptionSet);
                subOptionNew.setSubOptionSetList(subOptionSetListNew);
                option.getSubOptionsList().set(currentIndex, subOptionNew);
                double itemCostNew = cartItem.getItemCost() + subOptionSet.getSubOptionCost();
                cartItem.setItemCost(itemCostNew);
                if ((currentIndex + 1) < option.getSubOptionsList().size()) {
                    showDialogSubOptions(cartItem, currentIndex + 1);
                } else {
                    if (option.getAddonsList().size() > 0) {
                        showDialogAddons(cartItem, 0);
                    } else {
                        addToCart(cartItem);
                    }
                }
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void showDialogAddons(final Items cartItem, final int currentIndex) {
        final Options option = cartItem.getOptionsList().get(0); // one option selected
        final Addons addonsNew = new Addons(option.getAddonsList().get(currentIndex));

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = mInflater.inflate(R.layout.dialog_menu, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final Button bOK = (Button) dialogView.findViewById(R.id.bOK);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        ListView listMenuOptions = (ListView) dialogView.findViewById(R.id.listMenuOptions);
        bOK.setText("CONFIRM");

        if ((currentIndex + 1) < option.getAddonsList().size()) {
            bOK.setText("NEXT");
        }
        tvCancel.setText("CANCEL");
        tvTitle.setText(addonsNew.getAddonTitle());
        builder.setView(dialogView);
        bOK.setEnabled(true);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        final MenuAddonsAdapter menuAddonsAdapter = new MenuAddonsAdapter(mContext, addonsNew.getAddonSetList());
        listMenuOptions.setAdapter(menuAddonsAdapter);

        listMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                bOK.setEnabled(true);
                menuAddonsAdapter.clickedPosition(position);
                menuAddonsAdapter.notifyDataSetChanged();
            }
        });

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<AddonSet> addonSetListNew = new ArrayList<>();
                for (int i = 0; i < menuAddonsAdapter.getSelectedPositions().size(); i++) {
                    AddonSet addonSet = addonsNew.getAddonSetList().get(menuAddonsAdapter.getSelectedPositions().get(i));
                    addonSetListNew.add(addonSet);
                    double itemCostNew = cartItem.getItemCost() + addonSet.getAddonCost();
                    cartItem.setItemCost(itemCostNew);
                }
                addonsNew.setAddonSetList(addonSetListNew);
                option.getAddonsList().set(currentIndex, addonsNew);

                if ((currentIndex + 1) < option.getSubOptionsList().size()) {
                    showDialogAddons(cartItem, 0);
                } else {
                    addToCart(cartItem);
                }
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public interface DataTransferInterfaceMenu {
        void addMenu(Items cartItemToBeAdded);

        void removeMenu(Items item);
    }


}

