package com.cooltey.rpchecker.util;

import android.content.Context;


public class UnitPrice {

    private static double SIZE_VALUE = 0.3025;

	public static String get(Context context, String total_price, String car_price, String total_size, String car_size){
        String returnVal = "0";
        try {
            double totalPrice = Double.parseDouble(total_price);
            double carPrice = Double.parseDouble(car_price);
            double totalSize = Double.parseDouble(total_size);
            double carSize = Double.parseDouble(car_size);

            double finalPrice = (totalPrice + carPrice) / (totalSize*SIZE_VALUE + carSize*SIZE_VALUE);

            returnVal = (float) finalPrice + "";
        }catch(Exception e){

        }
        return NumberFormatter.get(context, returnVal, 1);
	}

    public static int getPure(Context context, String total_price, String car_price, String total_size, String car_size){
        String returnVal = "0";
        try {
            double totalPrice = Double.parseDouble(total_price);
            double carPrice = Double.parseDouble(car_price);
            double totalSize = Double.parseDouble(total_size);
            double carSize = Double.parseDouble(car_size);

            double finalPrice = (totalPrice + carPrice) / (totalSize*SIZE_VALUE + carSize*SIZE_VALUE);

            returnVal = (float) finalPrice + "";
        }catch(Exception e){

        }
        return NumberFormatter.getPure(context, returnVal, 1);
    }
}
