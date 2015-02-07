package com.cooltey.rpchecker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Keywords {

    private static String DATA_NAME = "keyword";

	public static String get(Context context){

        SharedPreferences sharePrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String getString = sharePrefs.getString(DATA_NAME, "");

        return getString;
	}

    public static void set(Context context, String keyword){

        SharedPreferences sharePrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharePrefs.edit();
        editor.putString(DATA_NAME, keyword);
        editor.commit();
    }
}
