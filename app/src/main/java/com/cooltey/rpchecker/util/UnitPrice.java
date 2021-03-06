package com.cooltey.rpchecker.util;

import android.content.Context;

import java.text.DecimalFormat;


public class UnitPrice {

    public static double SIZE_VALUE = 0.3025;

	public static String get(Context context, String total_price, String car_price, String total_size, String car_size){
        String returnVal = "0";
        try {
            double totalPrice = Double.parseDouble(total_price);
            double carPrice = Double.parseDouble(car_price);
            double totalSize = Double.parseDouble(total_size);
            double carSize = Double.parseDouble(car_size);

            // avoid error calculate
            if(carSize <= 0){
                carPrice = 0.0;
            }

            double finalPrice = (totalPrice - carPrice) / (totalSize*SIZE_VALUE - carSize*SIZE_VALUE);

            returnVal = (float) finalPrice + "";
        }catch(Exception e){

        }
        return NumberFormatter.get(context, returnVal, 1);
	}

    public static String getTip(Context context, String total_price, String car_price, String total_size, String car_size){
        String returnVal = "0";
        try {
            double totalPrice = Double.parseDouble(total_price);
            double carPrice = Double.parseDouble(car_price);
            double totalSize = Double.parseDouble(total_size);
            double carSize = Double.parseDouble(car_size);

            // avoid error calculate
            if(carSize <= 0){
                carPrice = 0.0;
            }

            double finalPrice = (totalPrice - carPrice) / (totalSize*SIZE_VALUE - carSize*SIZE_VALUE);

            DecimalFormat formatter;
            formatter = new DecimalFormat("#,###,###,###.#");
            String totalSizeFormatter =  formatter.format(totalSize*SIZE_VALUE);
            String carSizeFormatter =  formatter.format(carSize*SIZE_VALUE);

            returnVal = "(" + totalPrice/10000 + " - " + carPrice/10000 + ") /  (" +  totalSizeFormatter + " - " +  carSizeFormatter  + ") = " + NumberFormatter.get(context, finalPrice+"", 1);

        }catch(Exception e){

        }
        return returnVal;
    }

    public static int getPure(Context context, String total_price, String car_price, String total_size, String car_size){
        String returnVal = "0";
        try {
            double totalPrice = Double.parseDouble(total_price);
            double carPrice = Double.parseDouble(car_price);
            double totalSize = Double.parseDouble(total_size);
            double carSize = Double.parseDouble(car_size);

            // avoid error calculate
            if(carSize <= 0){
                carPrice = 0.0;
            }

            double finalPrice = (totalPrice - carPrice) / (totalSize*SIZE_VALUE - carSize*SIZE_VALUE);

            returnVal = (float) finalPrice + "";
        }catch(Exception e){

        }
        return NumberFormatter.getPure(context, returnVal, 1);
    }
}
