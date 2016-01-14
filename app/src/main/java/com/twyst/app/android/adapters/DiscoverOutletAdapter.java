package com.twyst.app.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twyst.app.android.R;
import com.twyst.app.android.activities.OrderOnlineActivity;
import com.twyst.app.android.model.Outlet;
import com.twyst.app.android.util.AppConstants;
import com.twyst.app.android.util.RoundedTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 06/06/15.
 */
public class DiscoverOutletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Outlet> items = new ArrayList<>();
    private OnViewHolderListener onViewHolderListener;
    private Context mContext;

    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_FOOTER = 1;

    private boolean outletsNotFound = false;

    public List<Outlet> getItems() {
        return items;
    }
    public void setmContext(Context context){
        mContext = context;
    }

    public void setItems(List<Outlet> items) {
        this.items = items;
    }

    public void setOnViewHolderListener(OnViewHolderListener onViewHolderListener) {
        this.onViewHolderListener = onViewHolderListener;
    }

    public void updateList(ArrayList<Outlet> outlets) {
        items.clear();
        items.addAll(outlets);
        this.notifyDataSetChanged();
    }

    public void updateListl(List<Outlet> outlets) {
        items.clear();
        items.addAll(outlets);
        this.notifyDataSetChanged();
    }

    public interface OnViewHolderListener {
        void onRequestedLastItem();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_outlet, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(10, 10, 10, -5);
            //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, actionBarActivity.getResources().getDisplayMetrics());
            v.setLayoutParams(layoutParams);

            OutletViewHolder vh = new OutletViewHolder(v);
            addImagesToLayoutDynamically(vh);
            return vh;
        } else if (viewType == VIEW_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_discover_footer, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 15, 0, 15);
            v.setLayoutParams(layoutParams);

            OutletViewHolderFooter vh = new OutletViewHolderFooter(v);
            return vh;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    public void addImagesToLayoutDynamically(final OutletViewHolder vh){
        vh.outletAddress.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.location);
                        int height = vh.outletAddress.getMeasuredHeight() * 1/2;
                        int offset = height / 4;
                        int smallOffset = height /8;
                        img.setBounds(0, 0, offset + height + smallOffset, offset + height - smallOffset);
                        vh.outletAddress.setCompoundDrawables(img, null, null, null);
                        vh.outletAddress.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });


        vh.deliveryTime.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.clock);
                        int height = vh.deliveryTime.getMeasuredHeight() * 2/3;
                        int offset = height / 6;
                        img.setBounds(0, 0, offset + height, offset + height);
                        vh.deliveryTime.setCompoundDrawables(img, null, null, null);
                        vh.deliveryTime.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        vh.minOrder.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.bill);
                        int height = vh.minOrder.getMeasuredHeight() * 2/3;
                        int offset = height / 6;
                        img.setBounds(0, 0, offset + height, offset + height);
                        vh.minOrder.setCompoundDrawables(img, null, null, null);
                        vh.minOrder.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

        vh.noOfOffers.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Drawable img = mContext.getResources().getDrawable(
                                R.drawable.offerblack);
                        int height = vh.noOfOffers.getMeasuredHeight() * 2/3;
                        int offset = height / 6;
                        img.setBounds(0, 0, offset + height, offset + height);
                        vh.noOfOffers.setCompoundDrawables(img, null, null, null);
                        vh.noOfOffers.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
    }
    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            return VIEW_FOOTER;
        } else {
            return VIEW_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OutletViewHolder) {
            System.out.println("DiscoverOutletAdapter.onBindViewHolder data called... position: " + position);

            final OutletViewHolder outletViewHolder = (OutletViewHolder) holder;

            if (onViewHolderListener != null && position == getItemCount() - 5) {
                onViewHolderListener.onRequestedLastItem();
            }

            final Outlet outlet = items.get(position);
            View view = outletViewHolder.itemView;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Order online
                    if (outlet.getMenuId() != null && outlet.getMenuId() != "") {
                        Intent intent = new Intent(view.getContext(), OrderOnlineActivity.class);
                        intent.putExtra(AppConstants.INTENT_PARAM_MENU_ID, outlet.getMenuId());
                        view.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(mContext,"No menuId available",Toast.LENGTH_SHORT).show();
                    }
                }
            });


//            outletViewHolder.outletImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    //Order online
//                    Intent intent= new Intent(view.getContext(), OrderOnlineActivity.class);
//                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet.get_id());
//                    view.getContext().startActivity(intent);
//
////                    Intent intent= new Intent(view.getContext(), OutletDetailsActivity.class);
////                    intent.putExtra(AppConstants.INTENT_PARAM_OUTLET_OBJECT, outlet);
////                    view.getContext().startActivity(intent);
//                }
//            });

            outletViewHolder.outletName.setText(outlet.getName());

            String address = "";
            boolean added = false;
            if (!TextUtils.isEmpty(outlet.getLocality_1())) {
                address += outlet.getLocality_1();
                added = true;
            }

            if (!TextUtils.isEmpty(outlet.getLocality_2())) {
                if (added) {
                    address += ", ";
                }
                address += outlet.getLocality_2();
                added = true;
            }

            if (!TextUtils.isEmpty(outlet.getCity())) {
                if (added) {
                    address += ", ";
                }
                address += outlet.getCity();
            }


            outletViewHolder.outletAddress.setText(address);

            if (outlet.getCuisines() != null && outlet.getCuisines().size() > 0) {
                String cuisines = outlet.getCuisines().toString();
                outletViewHolder.cuisinesNames.setText(cuisines.substring(1,cuisines.length()-1));
            }

            String deliveryString = "Delivers in " + outlet.getDeliveryTime() + " minutes";
            outletViewHolder.deliveryTime.setText(deliveryString);

            String minOrderString = outlet.getMinimumOrder() + " min order";
            outletViewHolder.minimumOrder.setText(minOrderString);

            String maxCashBack = outlet.getCashback().getMax();
            if (Double.parseDouble(maxCashBack) < 10 && !maxCashBack.contains(".")) {
                outletViewHolder.twystBucksPercentage.setText(" " + maxCashBack + "%");
            } else {
                outletViewHolder.twystBucksPercentage.setText(maxCashBack + "%");
            }

//            if (TextUtils.isEmpty(outlet.getDistance())) {
//                outletViewHolder.outletDistance.setText("");
//                outletViewHolder.outletDistance.setVisibility(View.GONE);
//            } else {
//                outletViewHolder.outletDistance.setText(outlet.getDistance() + " km");
//                outletViewHolder.outletDistance.setVisibility(View.VISIBLE);
//            }

            // rating
//            if (outlet.getRating() == null) {
////                outletViewHolder.ratingBar.setNumStars(5);
////                outletViewHolder.ratingBar.setRating(1.0f);
//                outletViewHolder.ratingBar.setScore(1.0f);
//            } else {
////                outletViewHolder.ratingBar.setNumStars(5);
////                outletViewHolder.ratingBar.setRating(Float.parseFloat(outlet.getRating()));
//                outletViewHolder.ratingBar.setScore(Float.parseFloat(outlet.getRating()));
//            }

            // offers to be uncommented
//            List<Offer> subList;
//            boolean hasMoreOffers = false;
//            if (outlet.getOffers().size() > 5) {
//                subList = outlet.getOffers().subList(0, 4);
//                hasMoreOffers = true;
//            } else {
//                subList = outlet.getOffers();
//                hasMoreOffers = false;
//            }

            //outletViewHolder.viewPager.setAdapter(new DiscoverOfferPagerAdapter(subList, outlet, isCheckinExclusiveOffersAvailable(outlet.getOffers()), hasMoreOffers));

            Picasso picasso = Picasso.with(view.getContext());
            picasso.setIndicatorsEnabled(AppConstants.DEGUG_PICASSO);
            picasso.setLoggingEnabled(AppConstants.DEGUG_PICASSO);

            if (outlet.getBackground() != null && outlet.getLogo() != null)
            picasso.load(outlet.getBackground())
                    .noFade()
                    .transform(new RoundedTransformation(10, 0))
                    .into(outletViewHolder.outletImage);


//            picasso.load(outlet.getBackground())
//                    .noFade()
//                    .centerCrop()
//                    .resize(250, 372)
//                    .transform(new RoundedTransformation(24, 0))
//                    .into(outletViewHolder.outletImage);

//            if (outlet.isFollowing()) {
//                outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
//            } else {
//                outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
//            }
            SharedPreferences prefs = view.getContext().getSharedPreferences(AppConstants.PREFERENCE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            final String userToken = prefs.getString(AppConstants.PREFERENCE_USER_TOKEN, "");

//            outletViewHolder.followOutletBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    final TwystProgressHUD twystProgressHUD = TwystProgressHUD.show(view.getContext(), false, null);
//                    if (outlet.isFollowing()) {
//                        outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
//                        HttpService.getInstance().unFollowEvent(userToken, outlet.get_id(), new Callback<BaseResponse<Data>>() {
//                            @Override
//                            public void success(BaseResponse<Data> dataBaseResponse, Response response) {
//                                if (dataBaseResponse.isResponse()) {
//                                    outlet.setFollowing(false);
//                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
//                                    twystProgressHUD.dismiss();
//                                    Log.i("unfollow event response", "" + response);
//                                } else {
//                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
//                                    Log.i("false response", "" + dataBaseResponse.getMessage());
//                                }
//                            }
//                            @Override
//                            public void failure(RetrofitError error) {
//                                twystProgressHUD.dismiss();
//                                DiscoverActivity discoverActivity = (DiscoverActivity) view.getContext();
//                                discoverActivity.handleRetrofitError(error);
//                            }
//                        });
//
//
//                    } else {
//                        outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
//                        HttpService.getInstance().followEvent(userToken, outlet.get_id(), new Callback<BaseResponse<Data>>() {
//                            @Override
//                            public void success(BaseResponse<Data> dataBaseResponse, Response response) {
//                                if (dataBaseResponse.isResponse()) {
//                                    int twystBucks = dataBaseResponse.getData().getTwyst_bucks();
//                                    DiscoverActivity discoverActivity = (DiscoverActivity) view.getContext();
//                                    if (twystBucks > discoverActivity.getTwystBucks()){
//                                        discoverActivity.setTwystBucks(twystBucks);
//                                        Toast.makeText(discoverActivity, discoverActivity.getResources().getString(R.string.bucks_earned_follow_outlet), Toast.LENGTH_SHORT).show();
//                                    }
//                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet);
//                                    outlet.setFollowing(true);
//                                    twystProgressHUD.dismiss();
//                                    Log.i("follow event response", "" + response);
//                                } else {
//                                    outletViewHolder.followOutletBtn.setImageResource(R.drawable.icon_discover_follow_outlet_no);
//                                    Log.i("false response", "" + dataBaseResponse.getMessage());
//                                }
//                            }
//                            @Override
//                            public void failure(RetrofitError error) {
//                                twystProgressHUD.dismiss();
//                                DiscoverActivity discoverActivity = (DiscoverActivity) view.getContext();
//                                discoverActivity.handleRetrofitError(error);
//                            }
//                        });
//
//                    }
//                }
//            });

//            outletViewHolder.callOutletBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    // Call outlet
//                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                    builder.setTitle("Do you want to call "+outlet.getName() +"?")
//                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String number = "tel:" + outlet.getPhone();
//                                    view.getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
//                                }
//                            })
//                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // User cancelled the dialog
//                                }
//                            });
//                    builder.create().show();
//
//                }
//            });

        } else {

            System.out.println("DiscoverOutletAdapter.onBindViewHolder footer called... position: " + position + ", outletsNotFound: " + outletsNotFound);

            OutletViewHolderFooter outletViewHolderFooter = (OutletViewHolderFooter) holder;

            if (position > 0 && !outletsNotFound) {
                outletViewHolderFooter.itemView.findViewById(R.id.circularFooterProgressBar).setVisibility(View.VISIBLE);
            } else {
                outletViewHolderFooter.itemView.findViewById(R.id.circularFooterProgressBar).setVisibility(View.GONE);
            }
        }


    }

    // commented by andshul as not required right now
//    private boolean isCheckinExclusiveOffersAvailable(List<Offer> offers) {
//        for (Offer offer: offers){
//            if ("checkin".equalsIgnoreCase(offer.getType()))return true;
//        }
//        return false;
//    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public void setOutletsNotFound(boolean outletsNotFound) {
        this.outletsNotFound = outletsNotFound;
    }

    public static class OutletViewHolderFooter extends RecyclerView.ViewHolder {

        public OutletViewHolderFooter(View itemView) {
            super(itemView);
        }
    }

    public static class OutletViewHolder extends RecyclerView.ViewHolder {

        TextView outletName;
        TextView outletAddress;
        TextView minOrder;

        ImageView outletImage;
        TextView cuisinesNames;
        TextView deliveryTime;
        TextView minimumOrder;
        TextView twystBucksPercentage;
        TextView noOfOffers;


        public OutletViewHolder(View itemView) {
            super(itemView);

            outletName = (TextView) itemView.findViewById(R.id.outletName);
            outletAddress = (TextView) itemView.findViewById(R.id.outletAddress);
            deliveryTime = (TextView)itemView.findViewById(R.id.delivery_time);
            minOrder = (TextView)itemView.findViewById(R.id.min_order);
            cuisinesNames = (TextView)itemView.findViewById(R.id.cuisines_names);
            deliveryTime = (TextView)itemView.findViewById(R.id.delivery_time);
            minimumOrder = (TextView)itemView.findViewById(R.id.min_order);
            twystBucksPercentage = (TextView)itemView.findViewById(R.id.tv_twyst_bucks_percentage);
            noOfOffers = (TextView)itemView.findViewById(R.id.no_of_offers);

            outletImage = (ImageView) itemView.findViewById(R.id.outlet_logo);

        }
    }

}


