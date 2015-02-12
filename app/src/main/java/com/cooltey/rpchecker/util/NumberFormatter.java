package com.cooltey.rpchecker.util;

import android.content.Context;

import com.cooltey.rpchecker.R;

import java.text.DecimalFormat;


public class NumberFormatter {

	public static String get(Context context, String number, int pointer_num){
        String finalNum;
        if(number.length() > 0) {
            float getNumber = Float.parseFloat(number);
            float getTransNum = getNumber / 10000;

            DecimalFormat formatter;
            if (pointer_num != 0) {
                formatter = new DecimalFormat("#,###,###,###.#");
            } else {
                formatter = new DecimalFormat("#,###,###,###");
            }
            finalNum = formatter.format(getTransNum) + context.getString(R.string.realestate_price_lastfix);
        }else{
            finalNum = 0 + context.getString(R.string.realestate_price_lastfix);
        }
        return finalNum;
	}

    public static int getPure(Context context, String number, int pointer_num){
        int finalNum = 0;
        if(number.length() > 0) {
            float getNumber = Float.parseFloat(number);
            int getTransNum = (int) getNumber / 10000;

            DecimalFormat formatter;
            if (pointer_num != 0) {
                formatter = new DecimalFormat("#,###,###,###.#");
            } else {
                formatter = new DecimalFormat("#,###,###,###");
            }
            finalNum = Integer.parseInt(formatter.format(getTransNum));
        }
        return finalNum;
    }

}
