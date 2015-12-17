package com.twyst.app.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.menu.Items;
import com.twyst.app.android.model.menu.Options;
import com.twyst.app.android.model.menu.SubCategories;

import java.util.ArrayList;

/**
 * Created by Vipul Sharma on 11/20/2015.
 */
public class MenuAdapter extends BaseExpandableListAdapter {
    private static int mVegIconHeight = 0; //menuItemName height fixed for a specific device

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private boolean mFooterEnabled = false;
    ArrayList<SubCategories> mSectionsList;

    DataTransferInterfaceMenu mDataTransferInterfaceMenu;

    public MenuAdapter(Context context, ArrayList<SubCategories> sectionsList) {
        mContext = context;
        mDataTransferInterfaceMenu = (DataTransferInterfaceMenu) context;
        mSectionsList = sectionsList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFooterEnabled(boolean footerEnabled) {
        mFooterEnabled = footerEnabled;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (isFooter(groupPosition)) {
            convertView = mLayoutInflater.inflate(R.layout.layout_footer_menu, parent, false);

            final View viewFooterMenu = convertView.findViewById(R.id.viewFooterMenu);
            if (!mFooterEnabled) {
                viewFooterMenu.setVisibility(View.GONE);
            }

        } else {
            convertView = mLayoutInflater.inflate(R.layout.layout_menu_group, parent, false);
            final TextView text = (TextView) convertView.findViewById(R.id.menu_group_text);
            text.setText(mSectionsList.get(groupPosition).getSubCategoryName());

            final ImageView expandedImage = (ImageView) convertView.findViewById(R.id.menu_group_arrow);
            final int resId = isExpanded ? R.drawable.expanded : R.drawable.collapsed;
            expandedImage.setImageResource(resId);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.layout_menu, parent, false);
        ChildViewHolder childViewHolder = new ChildViewHolder(convertView);

        childViewHolder.mIvPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(groupPosition, childPosition);
            }
        });

        childViewHolder.mIvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(groupPosition, childPosition);
            }
        });

        final Items item = mSectionsList.get(groupPosition).getItemsList().get(childPosition);
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
        }

        childViewHolder.menuItemName.setText(item.getItemName());

        childViewHolder.tvCost.setText(item.getItemCost());
        return convertView;
    }

    private void add(int groupPosition, int childPosition) {
        Items item = mSectionsList.get(groupPosition).getItemsList().get(childPosition);
        Items cartItem = new Items(item);
        if (cartItem.getItemOriginalReference().getOptionsList().size() > 0) {
            showDialogOptions(cartItem);
        } else {
            addToCart(cartItem);
        }
    }

    private void remove(int groupPosition, int childPosition) {
        Items item = mSectionsList.get(groupPosition).getItemsList().get(childPosition);
        mDataTransferInterfaceMenu.removeMenu(item);
    }

    private void addToCart(Items cartItemToBeAdded) {
        mDataTransferInterfaceMenu.addMenu(cartItemToBeAdded);
    }

    private void showDialogOptions(final Items item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = mLayoutInflater.inflate(R.layout.dialog_menu, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final Button bOK = (Button) dialogView.findViewById(R.id.bOK);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        ListView listMenuOptions = (ListView) dialogView.findViewById(R.id.listMenuOptions);
        bOK.setText("CONFIRM");
        tvCancel.setText("CANCEL");
        tvTitle.setText(item.getOptionTitle());
        builder.setView(dialogView);
        bOK.setEnabled(false);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        final MenuOptionsAdapter menuOptionsAdapter = new MenuOptionsAdapter(mContext, item.getOptionsList());
        listMenuOptions.setAdapter(menuOptionsAdapter);

        listMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                bOK.setEnabled(true);
                menuOptionsAdapter.setSelectedPosition(position);
                menuOptionsAdapter.notifyDataSetChanged();
            }
        });

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Options options = item.getOptionsList().get(menuOptionsAdapter.getSelectedPosition());
                if (options.getSubOptionsList().size() > 0) {
                    showDialogSubOptions(item, menuOptionsAdapter.getSelectedPosition());
                } else {
                    if (options.getAddonsList().size() > 0) {
                        showDialogAddons(item, menuOptionsAdapter.getSelectedPosition());
                    } else {
                        addToCart(item);
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

    private void showDialogSubOptions(final Items item, final int selectedPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = mLayoutInflater.inflate(R.layout.dialog_menu, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final Button bOK = (Button) dialogView.findViewById(R.id.bOK);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        ListView listMenuOptions = (ListView) dialogView.findViewById(R.id.listMenuOptions);
        bOK.setText("CONFIRM");
        tvCancel.setText("CANCEL");
        tvTitle.setText(item.getOptionTitle());
        builder.setView(dialogView);
        bOK.setEnabled(false);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        final MenuSubOptionsAdapter menuOptionsAdapter = new MenuSubOptionsAdapter(mContext, item.getOptionsList().get(selectedPosition).getSubOptionsList().get(0).getSubOptionSetList());
        listMenuOptions.setAdapter(menuOptionsAdapter);

        listMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                bOK.setEnabled(true);
                menuOptionsAdapter.setSelectedPosition(position);
                menuOptionsAdapter.notifyDataSetChanged();
            }
        });

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Options options = item.getOptionsList().get(menuOptionsAdapter.getSelectedPosition());
                if (options.getAddonsList().size() > 0) {
                    showDialogAddons(item, menuOptionsAdapter.getSelectedPosition());
                } else {
                    addToCart(item);
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

    private void showDialogAddons(Items item, int selectedPosition) {

    }

    @Override
    public int getGroupCount() {
        return mSectionsList.size() + 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (isFooter(groupPosition)) {
            return 0;
        }
        return mSectionsList.get(groupPosition).getItemsList().size();
    }

    private boolean isFooter(int groupPosition) {
        return (groupPosition == mSectionsList.size());
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSectionsList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSectionsList.get(groupPosition).getItemsList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public final class GroupViewHolder {

        TextView mContactName;
        ImageView mContactImage;
    }

    private class ChildViewHolder {
        ImageView mIvMinus;
        ImageView mIvPLus;
        TextView menuItemName;
        TextView tvQuantity;
        TextView tvCost;

        public ChildViewHolder(View itemView) {
            this.mIvMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
            this.mIvPLus = (ImageView) itemView.findViewById(R.id.ivPlus);
            this.menuItemName = (TextView) itemView.findViewById(R.id.menuItem);
            this.tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            this.tvCost = (TextView) itemView.findViewById(R.id.tvCost);
        }
    }

    public interface DataTransferInterfaceMenu {
        void addMenu(Items cartItemToBeAdded);

        void removeMenu(Items item);
    }

}
