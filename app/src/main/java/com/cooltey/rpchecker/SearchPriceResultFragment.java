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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cooltey.rpchecker.data.Record;
import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.Keywords;

import java.util.ArrayList;

public class SearchPriceResultFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();

    // view
    private ListView mListView;
    private Button   mSearchAgainView;
    private EditText mKeywordView;
    private Button   mSearchView;

    // data
    private String mOldKeyword;
    private String mKeyword;
    private static ArrayList<Record> mRecordData = new ArrayList<Record>();
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SearchPriceResultFragment newInstance(int sectionNumber, ArrayList<Record> data) {
		SearchPriceResultFragment fragment = new SearchPriceResultFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

        mRecordData = data;

		return fragment;
	}

	public SearchPriceResultFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_search_price_result, container,
				false);

        mKeywordView    = (EditText) rootView.findViewById(R.id.search_keyword);
        mSearchView      = (Button) rootView.findViewById(R.id.search_btn);
        mListView        = (ListView) rootView.findViewById(R.id.search_list_view);
        mSearchAgainView  = (Button) rootView.findViewById(R.id.search_again_btn);

        // set default text
        mOldKeyword = Keywords.get(getActivity());
        mKeywordView.setText(mOldKeyword);

        // set adapter
        ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(listViewAdapter);
        mListView.setOnItemClickListener(mListItemClickListener);


        mSearchView.setOnClickListener(mSearchBtn);
        mSearchAgainView.setOnClickListener(mSearchAgainBtn);

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


    private void setSearchPrice(String keyword){
        mProgress.setMessage(getString(R.string.dialog_progress_searching));
        mProgress.show();
        Cloud.searchPrice(getActivity(), keyword, mSearchPriceListener);
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

    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);

            // check the availability
            Record rData = mRecordData.get(position);

            // available
            if(rData.record_is_available == 1){
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, CarpoolPriceFragment.newInstance(getPosition, position, mRecordData, false))
                        //.addToBackStack(null)
                        .commitAllowingStateLoss();
            }else{
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, PaymentOptionFragment.newInstance(getPosition, position, mRecordData))
                        //.addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        }
    };

    private View.OnClickListener mSearchAgainBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //getFragmentManager().popBackStack();
            //backToPreviousPage();
            backRunnable.run();
        }
    };


    private void backToPreviousPage(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, SearchPriceFragment.newInstance(getPosition))
                //.addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private Runnable backRunnable = new Runnable() {
        @Override
        public void run() {
            backToPreviousPage();
        }
    };

    public class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater  mLayoutInflater;

        public ListViewAdapter(Context context) {
            mContext 		= context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return mRecordData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View listView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes

                listView = mLayoutInflater.inflate(R.layout.item_search_price_result, null);

                // set view
                TextView imgText 	= (TextView) listView.findViewById(R.id.text);
                // org_name - address
                String combineResultString = mRecordData.get(position).record_org_name + " - " + mRecordData.get(position).record_city + mRecordData.get(position).record_address;

                imgText.setText(combineResultString);

            } else {
                listView = convertView;
            }

            return listView;
        }

    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));

        ((MainActivity) activity).setOnBackFunction(backRunnable);

	}

}
