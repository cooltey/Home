package com.cooltey.rpchecker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.NetworkChecker;

public class LoginActivity extends ActionBarActivity {
	
	private EditText mUsernameView;
	private EditText mPasswordView;
	private Button   mLoginBtn;
	private ProgressDialog mProgress;
	private Builder mAlertDialog;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// set dialog
		mProgress     = new ProgressDialog(this);
		mAlertDialog  = new AlertDialog.Builder(this);
		// set view
		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mLoginBtn     = (Button)   findViewById(R.id.login_btn);
		
		
		// set onclick
		mLoginBtn.setOnClickListener(mBasicLoginListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().hide();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	// login listener
    private Cloud.LoginListener mCloudLoginListener = new Cloud.LoginListener() {
        @Override
        public Handler getHandler() {
        	mProgress.cancel();
            return mHandler;
        }
		@Override
		public void onSuccess(String status, String message) {
			// TODO Auto-generated method stub

        	// show feed list
        	Intent intent = new Intent();
        	intent.setClass(getApplicationContext(), MainActivity.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(intent);
        	
        	finish();
		}
		@Override
		public void onFail(String status, String message) {
			// TODO Auto-generated method stub
			// basic login
        	if(message != null){
        		mAlertDialog.setMessage(message);
	       		mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
		       		public void onClick(DialogInterface dialog, int which) {
		       			// do nothing;
		       			mUsernameView.setText("");
		       			mPasswordView.setText("");
		       		}
	       		 });
	       		mAlertDialog.show();
        	}
        	
		}
    };
    
    
    // set basic login
    private OnClickListener mBasicLoginListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub


            NetworkChecker networkChecker = new NetworkChecker(getApplicationContext());

            if(networkChecker.getStatus()) {

                boolean setLogin = true;

                // get username
                String getUsername = mUsernameView.getText().toString();
                if (TextUtils.isEmpty(getUsername)) {
                    //mUsername.setError(getString(R.string.login_error_empty));
                    mUsernameView.setHint(Html.fromHtml("<font color='#FF0000'>" + getString(R.string.login_username_hint) + "</font>"));
                }

                // get password
                String getPassword = mPasswordView.getText().toString();
                if (TextUtils.isEmpty(getPassword)) {
                    mPasswordView.setHint(Html.fromHtml("<font color='#FF0000'>" + getString(R.string.login_password_hint) + "</font> "));
                    setLogin = false;
                }


                if (setLogin) {
                    Cloud.basicLogin(getApplicationContext(), getUsername, getPassword, mCloudLoginListener);
                    mProgress.setMessage(getString(R.string.dialog_progress_login));
                    mProgress.show();
                }
            }else{
                mAlertDialog.setMessage(R.string.alert_dialog_no_network);
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.show();
            }
		}
    	
    };
    
}
