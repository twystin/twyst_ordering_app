package com.twyst.app.android.fragments;

/**
 * Created by anshul on 1/13/2016.
 */

import android.app.Activity;
import android.app.ListFragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.model.TagItem;

import java.util.ArrayList;

public class TagFragment extends ListFragment implements AdapterView.OnItemClickListener {
    OnTagSelectedListener mCallback;
    SimpleArrayAdapter mAdapter;
    private ArrayList<TagItem> tagList;
    String[] tagTitles;
    TypedArray tagIcons;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mCallback.onTagSelected(position,mAdapter.getItem(position));

    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnTagSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onTagSelected(int position, TagItem tagItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         return inflater.inflate(R.layout.filter_tags_layout,null,false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        tagTitles = getResources().getStringArray(R.array.tag_titles);
        tagIcons = getResources().obtainTypedArray(R.array.tag_icons);

        tagList = new ArrayList<TagItem>();
        for (int i =0;i<tagTitles.length;i++){
            tagList.add(new TagItem(tagTitles[i],tagIcons.getResourceId(i,-1)));
        }

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mAdapter = new SimpleArrayAdapter();
        setListAdapter(mAdapter);
        getListView().setItemChecked(0,true);
        mCallback.onTagSelected(0,mAdapter.getItem(0));
        getListView().setOnItemClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnTagSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTagSelectedListener");
        }
    }

    public String returnCurrentSelectedTag(){
        return tagList.get(getListView().getCheckedItemPosition()).getName();
    }

    class SimpleArrayAdapter extends ArrayAdapter<TagItem> {


        public SimpleArrayAdapter(){
            super(getActivity(),R.layout.filter_tag_row_layout,tagList);
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            View row = convertView;

            if(row == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.filter_tag_row_layout,parent,false);
            }

            ImageView tagImage = (ImageView)row.findViewById(R.id.tag_image);
            TextView tagName = (TextView)row.findViewById(R.id.tag_name);
            TagItem tagItem= tagList.get(position);
//            tagImage.setImageDrawable(tagItem.getIcon());
            tagImage.setImageDrawable(getActivity().getDrawable(tagItem.getIcon()));
            tagName.setText(tagItem.getName());
            return row;
        }

        @Override
        public int getCount() {

            return tagList.size();
        }

        @Override
        public TagItem getItem(int position) {

            return tagList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return tagList.indexOf(getItem(position));
        }

    }
}