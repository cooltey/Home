package com.cooltey.rpchecker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.cooltey.rpchecker.util.LogFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	private CharSequence mTitle;

	private ProgressDialog mProgress;
	private Builder mAlertDialog;
	private Handler mHandler = new Handler();
	// image loader
	private ImageLoader mImageLoader;

	// set navigation num
	public static final int NAVIGATION_SEARCH_PRICE 	= 0;
    public static final int NAVIGATION_FEEDBACK 		= 1;
    public static final int NAVIGATION_LOGOUT 		= 2;
    public static final int NAVIGATION_SEARCH_HISTORY = 3;

    private static Runnable mRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		

		// set dialog
		mProgress     = new ProgressDialog(this);
		mAlertDialog  = new AlertDialog.Builder(this);
		
		// initial image
		//mImageLoader = Cloud.initImageLoader(getApplicationContext());

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
        clearFragments();
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch(position){
			case NAVIGATION_SEARCH_PRICE:

				fragmentManager
						.beginTransaction()
						.replace(R.id.container, SearchPriceFragment.newInstance(NAVIGATION_SEARCH_PRICE))
                        //.addToBackStack(null)
                        .commitAllowingStateLoss();
			break;

			/*case NAVIGATION_SEARCH_HISTORY:

				fragmentManager
						.beginTransaction()
						.replace(R.id.container, SearchHistoryFragment.newInstance(position + 1))
                        //.addToBackStack("SearchHistoryFragment")
                        .commitAllowingStateLoss();
            break;*/

            case NAVIGATION_FEEDBACK:

                mAlertDialog.setMessage(getString(R.string.alert_dialog_msg_feedback));
                mAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse(getString(R.string.feedback_email)));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title));
                        intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.feedback_content));
                        startActivity(intent);
                    }
                });
                mAlertDialog.setNegativeButton(getString(R.string.alert_dialog_cancel_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing;
                    }
                });
                mAlertDialog.show();
                break;

			case NAVIGATION_LOGOUT:

				mAlertDialog.setMessage(getString(R.string.alert_dialog_msg_logout));
		   		mAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
		       		public void onClick(DialogInterface dialog, int which) {
		       			// do nothing;
		       			finish();
		       		}
		   		 });
		   		mAlertDialog.setNegativeButton(getString(R.string.alert_dialog_cancel_btn), new DialogInterface.OnClickListener(){
		       		public void onClick(DialogInterface dialog, int which) {
		       			// do nothing;
		       		}
		   		 });
		   		mAlertDialog.show();
			break;
		}
	}

	public void onSectionAttached(int number) {
        LogFactory.set("onSectionAttached", number);
		switch (number) {
		case NAVIGATION_SEARCH_PRICE:
			mTitle = getString(R.string.title_home);
			break;
		case NAVIGATION_SEARCH_HISTORY:
			mTitle = getString(R.string.title_search_history);
			break;
        case NAVIGATION_FEEDBACK:
            mTitle = getString(R.string.title_feedback);
            break;
		case NAVIGATION_LOGOUT:
			mTitle = getString(R.string.title_logout);
			break;
		}

        restoreActionBar();
    }

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}

    public void clearFragments(){
        FragmentManager fm = getSupportFragmentManager();
        LogFactory.set("clearFragments count", fm.getBackStackEntryCount());
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    public void setOnBackFunction(Runnable runnable){
        mRunnable = runnable;
    }

    @Override
    public void onBackPressed() {
        if(mRunnable == null) {
            mAlertDialog.setMessage(getString(R.string.alert_dialog_msg_logout));
            mAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing;
                    finish();
                }
            });
            mAlertDialog.setNegativeButton(getString(R.string.alert_dialog_cancel_btn), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing;
                }
            });
            mAlertDialog.show();
        }else{
            mRunnable.run();
            mRunnable = null;
        }
    }
}
