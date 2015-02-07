package com.cooltey.rpchecker.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.cooltey.rpchecker.R;


public class TipDialog {

    private Context mContext;
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    public TipDialog(Context context){
        mContext     = context;
        mAlertDialogBuilder  = new AlertDialog.Builder(context);
    }

    public void close(){
        mAlertDialog.cancel();
    }
	public void show(View view){
        mAlertDialogBuilder.setView(null);
        mAlertDialogBuilder.setMessage(null);
        mAlertDialogBuilder.setView(view);
        mAlertDialogBuilder.setNeutralButton(mContext.getString(R.string.tip_close_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
	}

    public void showPlain(String msg){
        mAlertDialogBuilder.setView(null);
        mAlertDialogBuilder.setMessage(null);
        mAlertDialogBuilder.setMessage(msg);
        mAlertDialogBuilder.setNeutralButton(mContext.getString(R.string.tip_close_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

}
