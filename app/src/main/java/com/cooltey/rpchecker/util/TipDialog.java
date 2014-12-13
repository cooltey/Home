package com.cooltey.rpchecker.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.cooltey.rpchecker.R;


public class TipDialog {

    private Context mContext;
    private AlertDialog.Builder mAlertDialog;

    public TipDialog(Context context){
        mContext     = context;
        mAlertDialog  = new AlertDialog.Builder(context);
    }

	public void show(View view){
        mAlertDialog.setView(view);
        mAlertDialog.setNeutralButton(mContext.getString(R.string.tip_close_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog.show();
	}

    public void showPlain(String msg){
        mAlertDialog.setMessage(msg);
        mAlertDialog.setNeutralButton(mContext.getString(R.string.tip_close_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog.show();
    }

}
