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
import android.widget.ListView;
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
    final boolean mIsRecommended;

    public MenuExpandableAdapter(Context context, List<ParentListItem> itemList, RecyclerView menuExpandableList, boolean isRecommended) {
        super(itemList);
        mContext = context;
        mMenuExpandableList = menuExpandableList;
        mInflater = LayoutInflater.from(context);
        mDataTransferInterfaceMenu = (DataTransferInterfaceMenu) context;
        mIsRecommended = isRecommended;
    }

    @Override
    public void onParentListItemExpanded(int parentPosition) {
        super.onParentListItemExpanded(parentPosition);
        LinearLayoutManager llm = (LinearLayoutManager) mMenuExpandableList.getLayoutManager();
        llm.scrollToPositionWithOffset(parentPosition, 0);
        // Alternatively keep track of the single item that is expanded and explicitly collapse that row (more efficient)

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
    public void onBindChildViewHolder(final MenuChildViewHolder childViewHolder, final int childPosition, Object childListItem) {
        final Items item = (Items) childListItem;
        if (!item.isAvailable()) {
            childViewHolder.tvUnavailable.setVisibility(View.VISIBLE);
            childViewHolder.mIvPLus.setVisibility(View.INVISIBLE);
            childViewHolder.mIvMinus.setVisibility(View.INVISIBLE);
            childViewHolder.tvQuantity.setVisibility(View.INVISIBLE);
        } else {
            childViewHolder.tvUnavailable.setVisibility(View.GONE);
            childViewHolder.mIvPLus.setVisibility(View.VISIBLE);

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

        }

        final Drawable img;
        if (item.isVegetarian()) {
            img = mContext.getResources().getDrawable(
                    R.drawable.veg);
        } else {
            img = mContext.getResources().getDrawable(
                    R.drawable.nonveg);
        }

        childViewHolder.mIvVegNonVegIcon.setImageDrawable(img);

        if (mVegIconHeight == 0) {
            final TextView tvMenuItemName = childViewHolder.menuItemName;
            tvMenuItemName.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mVegIconHeight = childViewHolder.menuItemName.getLineHeight() * 7 / 8;
                            ViewGroup.LayoutParams lp = childViewHolder.mIvVegNonVegIcon.getLayoutParams();
                            lp.width = mVegIconHeight;
                            lp.height = mVegIconHeight;
                            childViewHolder.mIvVegNonVegIcon.setLayoutParams(lp);
                            childViewHolder.mIvVegNonVegIcon.setImageDrawable(img);

                            tvMenuItemName.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    });
        } else {
            ViewGroup.LayoutParams lp = childViewHolder.mIvVegNonVegIcon.getLayoutParams();
            lp.width = mVegIconHeight;
            lp.height = mVegIconHeight;
            childViewHolder.mIvVegNonVegIcon.setLayoutParams(lp);
            childViewHolder.mIvVegNonVegIcon.setImageDrawable(img);
        }

        childViewHolder.menuItemName.setText(item.getItemName());

        if (item.getItemDescription() != null) {
            childViewHolder.menuItemDesc.setVisibility(View.VISIBLE);
            childViewHolder.menuItemDesc.setText(item.getItemDescription());
        } else {
            childViewHolder.menuItemDesc.setVisibility(View.GONE);
        }

        String breadCrumb = "";
        if (mIsRecommended && item.getCategoryName() != null) {
            breadCrumb = item.getCategoryName();
            if (item.getSubCategoryName() != null && !item.getSubCategoryName().equalsIgnoreCase(AppConstants.DEFAULT_SUB_CATEGORY)) {
                breadCrumb = breadCrumb + " > " + item.getSubCategoryName();
            }
            childViewHolder.menuItemBreadCrumb.setVisibility(View.VISIBLE);
            childViewHolder.menuItemBreadCrumb.setText(breadCrumb);
        } else {
            childViewHolder.menuItemBreadCrumb.setVisibility(View.GONE);
        }

        childViewHolder.llCustomisations.setVisibility(View.GONE);

        childViewHolder.tvCost.setText(Utils.costString(item.getItemCost()));
    }

    private void add(Items item) {
        Items cartItem = new Items(item);

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
        final TextView bOK = (TextView) dialogView.findViewById(R.id.bOK);
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

        if (cartItem.isOptionIsAddon()) {
            bOK.setText("CONFIRM");
            bOK.setEnabled(true);

            final MenuMultipleOptionsAdapter menuMultipleOptionsAdapter = new MenuMultipleOptionsAdapter(mContext, cartItem.getOptionsList());
            listMenuOptions.setAdapter(menuMultipleOptionsAdapter);

            listMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    bOK.setEnabled(true);
                    menuMultipleOptionsAdapter.clickedPosition(position);
                    menuMultipleOptionsAdapter.notifyDataSetChanged();
                }
            });

            bOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Options> optionsArrayList = new ArrayList<Options>();
                    for (int i = 0; i < menuMultipleOptionsAdapter.getSelectedPositions().size(); i++) {
                        Options option = cartItem.getOptionsList().get(menuMultipleOptionsAdapter.getSelectedPositions().get(i));
                        Options optionNew = new Options(option);
                        cartItem.setItemCost(cartItem.getItemCost() + optionNew.getOptionCost());
                        optionsArrayList.add(optionNew);
                    }
                    cartItem.getOptionsList().clear();
                    cartItem.getOptionsList().addAll(optionsArrayList);

                    addToCart(cartItem);
                    dialog.dismiss();
                }
            });

        } else {
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
                    if (cartItem.getItemOriginalReference().isOptionPriceIsAdditive()) {
                        cartItem.setItemCost(cartItem.getItemOriginalReference().getItemCost() + optionNew.getOptionCost());
                    } else {
                        cartItem.setItemCost(optionNew.getOptionCost());
                    }
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
        }

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
        final TextView bOK = (TextView) dialogView.findViewById(R.id.bOK);
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
        final TextView bOK = (TextView) dialogView.findViewById(R.id.bOK);
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

                if ((currentIndex + 1) < option.getAddonsList().size()) {
                    showDialogAddons(cartItem, currentIndex + 1);
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

