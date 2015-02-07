package com.cooltey.rpchecker;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends ActionBarActivity {

    private TextView mStartText;
    private Button   mRegisterBtn;
	private Button   mLoginBtn;
	private ProgressDialog mProgress;
	private Builder mAlertDialog;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		// set dialog
		mProgress     = new ProgressDialog(this);
		mAlertDialog  = new Builder(this);
        mStartText    = (TextView) findViewById(R.id.start_text);
        mRegisterBtn     = (Button)   findViewById(R.id.register_btn);
		mLoginBtn     = (Button)   findViewById(R.id.login_btn);
        mStartText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bRUSS.otf"));
		
		// set onclick
		mLoginBtn.setOnClickListener(mLoginListener);
        mRegisterBtn.setOnClickListener(mRegisterListener);
		getSupportActionBar().hide();
	}

    // set basic login
    private OnClickListener mLoginListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
            // show feed list
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
		}
    	
    };

    private OnClickListener mRegisterListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            // show feed list
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    };

}
