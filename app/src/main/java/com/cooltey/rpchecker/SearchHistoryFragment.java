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
import android.widget.ListView;
import android.widget.TextView;

import com.cooltey.rpchecker.data.SearchHistory;
import com.cooltey.rpchecker.util.Cloud;

import java.util.ArrayList;

public class SearchHistoryFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();

    private static final int PRELOAD_DISTANCE = 2;
    // view
    private ListView mListView;

    // data
    private ArrayList<SearchHistory> mSearchData = new ArrayList<SearchHistory>();
    private ListViewAdapter mListViewAdapter;
    private int mPage = 0;

	public static SearchHistoryFragment newInstance(int sectionNumber) {
		SearchHistoryFragment fragment = new SearchHistoryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}


	public SearchHistoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_search_history, container,
				false);

        mListView = (ListView) rootView.findViewById(R.id.search_list_view);

        setContentView();
        setSearchHistory();

        // remove subtitle
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(null);
		return rootView;
	}

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, CarpoolPriceFragment.newInstance(MainActivity.NAVIGATION_SEARCH_PRICE, 0, mSearchData.get(position).search_history_record_data, true))
                    //.addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    };

    private void setSearchHistory(){
        mProgress.setMessage(getString(R.string.dialog_progress_searching));
        mProgress.show();
        Cloud.getSearchHistory(getActivity(), mPage + "", mSearchPriceListener);
    }

    private Cloud.SearchHistoryListener mSearchPriceListener = new Cloud.SearchHistoryListener() {
        @Override
        public Handler getHandler() {
            mProgress.cancel();
            return mHandler;
        }

        @Override
        public void onSuccess(String status, String message, boolean is_end, ArrayList<SearchHistory> data) {
            if(is_end){
                return;
            }

            if(data.size() == 0){
                mAlertDialog.setMessage(getString(R.string.alert_dialog_no_result));
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mAlertDialog.show();
                return;
            }

            // show list
            mSearchData.addAll(data);
            mListViewAdapter.notifyDataSetChanged();

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

    private void setContentView(){
        mListViewAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    public class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater  mLayoutInflater;

        public ListViewAdapter(Context context) {
            mContext 		= context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {

            //LogFactory.set("mSearchData size", mSearchData.size());
            return mSearchData.size();
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
            listView = mLayoutInflater.inflate(R.layout.item_search_history_result, null);

                // set view
            TextView orgname 	= (TextView) listView.findViewById(R.id.org_name);
            TextView address 	= (TextView) listView.findViewById(R.id.address);
            TextView date 	    = (TextView) listView.findViewById(R.id.date);

            // download new content
            if (position > (getCount() - PRELOAD_DISTANCE)) {
                mPage++;
                setSearchHistory();
            }

            orgname.setText(mSearchData.get(position).search_history_org_name);
            address.setText(mSearchData.get(position).search_history_city + " " + mSearchData.get(position).search_history_address);
            date.setText(getString(R.string.search_history_date_prefix)+ " " + mSearchData.get(position).search_history_date);


            return listView;
        }

    }

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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));

        ((MainActivity) activity).setOnBackFunction(backRunnable);
	}
}
