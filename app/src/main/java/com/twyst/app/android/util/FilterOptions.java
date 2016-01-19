package com.twyst.app.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by anshul on 1/14/2016.
 */
public class FilterOptions {


        private static HashMap<String, ArrayList<String>> myMap;
        static {
            myMap = new HashMap<String, ArrayList<String>>();
            myMap.put(AppConstants.sortTag, new ArrayList<String>(Arrays.asList("Delivery Time - Low to High","Minimum Bill - Low to High","Cost for Two - Low to High","Cost for Two - Hign to Low","Minimum Bill - High to Low")));
            myMap.put(AppConstants.cuisinetag, new ArrayList<String>(Arrays.asList("Pure Veg Only","American","Arabian","Asian","Assamese","Bakery","Bangladeshi","Bengali","Beverages","Biryani","Cafe","Chinese","continental","Desert","European","Fast Food","Goan")));
            myMap.put(AppConstants.paymentTag, new ArrayList<String>(Arrays.asList("COD Available")));
            myMap.put(AppConstants.offersTag, new ArrayList<String>(Arrays.asList("Only Show Outlets with Offers")));
        }


    public static HashMap<String, ArrayList<String>> getMyMap() {
        return myMap;
    }


}
