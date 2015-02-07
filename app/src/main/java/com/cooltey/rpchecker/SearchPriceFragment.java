package com.cooltey.rpchecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.cooltey.rpchecker.data.Record;
import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.Keywords;

import java.util.ArrayList;

public class SearchPriceFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();


    // data
    private String mKeyword;
    private EditText mKeywordView;
    private Button   mSearchView;
    private Button   mSearchHistoryView;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SearchPriceFragment newInstance(int sectionNumber) {
		SearchPriceFragment fragment = new SearchPriceFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SearchPriceFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_search_price, container,
				false);

        mKeywordView    = (EditText) rootView.findViewById(R.id.search_keyword);
        mSearchView      = (Button) rootView.findViewById(R.id.search_btn);
        mSearchHistoryView      = (Button) rootView.findViewById(R.id.search_history_btn);


        mSearchView.setOnClickListener(mSearchBtn);
        mSearchHistoryView.setOnClickListener(mSearchHistoryBtn);

        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mKeywordView.getWindowToken(), 0);


        // remove subtitle
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(null);

		return rootView;
	}


    private View.OnClickListener mSearchBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mKeyword = mKeywordView.getText().toString();

            // set keyword into tmp data
            Keywords.set(getActivity(), mKeyword);

            setSearchPrice(mKeyword);
        }
    };

    private View.OnClickListener mSearchHistoryBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goSearchHistory();
        }
    };

    private void setSearchPrice(String keyword){
        mProgress.setMessage(getString(R.string.dialog_progress_searching));
        mProgress.show();
        Cloud.searchPrice(getActivity(), keyword, mSearchPriceListener);
    }

    private void goSearchHistory(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager
                .beginTransaction()
                .replace(R.id.container, SearchHistoryFragment.newInstance(MainActivity.NAVIGATION_SEARCH_HISTORY))
                //.addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private Cloud.SearchPriceListener mSearchPriceListener = new Cloud.SearchPriceListener() {
        @Override
        public Handler getHandler() {
            mProgress.cancel();
            return mHandler;
        }

        @Override
        public void onSuccess(String status, String message, ArrayList<Record> data) {
            if(data.size() == 0){
                mAlertDialog.setMessage(getString(R.string.alert_dialog_no_result));
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mAlertDialog.show();
                return;
            }
            int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,SearchPriceResultFragment.newInstance(getPosition, data))
                    //.addToBackStack(null)
                    .commitAllowingStateLoss();
        }

        @Override
        public void onFail(String status, String message) {
            if(message != null){
                mAlertDialog.setMessage(message);
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mAlertDialog.show();
            }
        }
    };


    private Runnable backRunnable = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));

        ((MainActivity) activity).setOnBackFunction(backRunnable);
	}

}
