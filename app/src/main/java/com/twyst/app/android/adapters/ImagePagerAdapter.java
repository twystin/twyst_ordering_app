package com.twyst.app.android.adapters;

/**
 * Created by anshul on 1/12/2016.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    private List<Integer> mResources;
    private boolean isInfiniteLoop;

    public ImagePagerAdapter(Context context, List<Integer> list) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = list;
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder holder = new ViewHolder();
        ImageView view = holder.imageView = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setTag(holder);
        view.setImageResource(mResources.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}