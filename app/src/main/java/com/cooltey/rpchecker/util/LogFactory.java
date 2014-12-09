package com.cooltey.rpchecker.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class LogFactory{
	
	// set debug mode
	private static boolean DEBUG_MODE = true;
	
	public static void set(String logName, Object logVal){
		if(DEBUG_MODE){
			Log.d("====Custom Log====" + logName, logVal + "====Custom Log====");
		}
	}

	public static void toast(Context context, String logName, Object logVal){
		if(DEBUG_MODE){
			Toast.makeText(context, logName + "=>" + logVal , 1500).show();
		}
	}
}
