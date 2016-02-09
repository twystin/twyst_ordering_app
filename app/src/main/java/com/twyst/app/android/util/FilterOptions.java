package com.twyst.app.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by anshul on 1/14/2016.
 */
public class FilterOptions {

    private static ArrayList<String> cuisinesList = new ArrayList<>();
    private static HashMap<String, ArrayList<String>> myMap;
    static {
        myMap = new HashMap<String, ArrayList<String>>();
        myMap.put(AppConstants.sortTag, new ArrayList<String>(Arrays.asList("Delivery Time - Low to High","Minimum Bill - Low to High","Minimum Bill - High to Low")));
        myMap.put(AppConstants.cuisinetag, getCuisinesList());
        myMap.put(AppConstants.paymentTag, new ArrayList<String>(Arrays.asList("COD Available")));
        myMap.put(AppConstants.offersTag, new ArrayList<String>(Arrays.asList("Only Show Outlets with Offers")));
    }


    public static HashMap<String, ArrayList<String>> getMyMap() {
        return myMap;
    }

    public static void setMyMap(HashMap<String, ArrayList<String>> myMap) {
        FilterOptions.myMap = myMap;
    }

    public static void updateCuisinesList(ArrayList<String> list){
        cuisinesList = list;
        myMap.put(AppConstants.cuisinetag,cuisinesList);
    }

    public static ArrayList<String> getCuisinesList(){
        return cuisinesList;
    }


}
