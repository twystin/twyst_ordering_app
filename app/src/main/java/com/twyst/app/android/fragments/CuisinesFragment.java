/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twyst.app.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twyst.app.android.R;
import com.twyst.app.android.util.FilterOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class CuisinesFragment extends Fragment {
    ArrayList<String> optionsList;
    OptionsListAdapter optionsListAdapter;
    private ListView listView;
    long[] previouslySelectedPosition ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.cuisines_fragment_layout, container, false);
        HashMap<String, ArrayList<String>> myMap = FilterOptions.getMyMap();
        optionsList = myMap.get("CUISINES");
        listView = (ListView) view.findViewById(R.id.lv_cuisines_options);


        return view;
    }



    public CuisinesFragment(long[] positions){
        previouslySelectedPosition = positions;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        optionsListAdapter = new OptionsListAdapter();
        listView.setAdapter(optionsListAdapter);
        if (previouslySelectedPosition != null && previouslySelectedPosition.length > 0){
            for (long position: previouslySelectedPosition){
                listView.setItemChecked((int)position,true);
            }
        }

    }


    public long[] getSelectedOptions() {
        if (listView != null && listView.getCheckedItemCount() > 0) {
            return listView.getCheckedItemIds();
        }
        return null;

    }

    public void clearSelection(){
        if (listView.getCheckedItemCount() > 0){
            for (long position : listView.getCheckedItemIds()){
                listView.setItemChecked((int)position,false);
            }
        }
    }


    class OptionsListAdapter extends ArrayAdapter<String> {


        public OptionsListAdapter() {
            super(getActivity(), 0, optionsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            LayoutInflater inflater = getActivity().getLayoutInflater();
            if (optionsList.get(position).equals("American")) {
                row = inflater.inflate(R.layout.cuisine_american_row_option_layout, parent, false);
            } else {
                row = inflater.inflate(R.layout.options_row_layout, parent, false);
            }
            TextView tv1 = (TextView) row.findViewById(R.id.tv_option_name);
            tv1.setText(optionsList.get(position));
            ImageView ivCheckbox = (ImageView) row.findViewById(R.id.iv_option_checkbox);
            ivCheckbox.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.option_checkbox_config));

            return row;
        }


        @Override
        public long getItemId(int position) {
            return optionsList.indexOf(getItem(position));
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


}