package com.cooltey.rpchecker.util;

import android.content.Context;

import com.cooltey.rpchecker.R;

import java.text.DecimalFormat;


public class NumberFormatter {

	public static String get(Context context, String number, int pointer_num){
        float getNumber = Float.parseFloat(number);
        float getTransNum = getNumber / 10000;

        DecimalFormat formatter;
        if(pointer_num != 0){
            formatter = new DecimalFormat("#,###,###,###.#");
        }else{
            formatter = new DecimalFormat("#,###,###,###");
        }
        String finalNum = formatter.format(getTransNum) + context.getString(R.string.realestate_price_lastfix);

        return finalNum;
	}

    public static int getPure(Context context, String number, int pointer_num){
        float getNumber = Float.parseFloat(number);
        int getTransNum = (int) getNumber / 10000;

        DecimalFormat formatter;
        if(pointer_num != 0){
            formatter = new DecimalFormat("#,###,###,###.#");
        }else{
            formatter = new DecimalFormat("#,###,###,###");
        }
        int finalNum = Integer.parseInt(formatter.format(getTransNum));

        return finalNum;
    }

}
