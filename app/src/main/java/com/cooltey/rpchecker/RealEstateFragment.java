package com.cooltey.rpchecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cooltey.rpchecker.data.RealEstate;
import com.cooltey.rpchecker.data.Record;
import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.LogFactory;
import com.cooltey.rpchecker.util.NumberFormatter;
import com.cooltey.rpchecker.util.RealEstateDetailContent;
import com.cooltey.rpchecker.util.TipDialog;
import com.cooltey.rpchecker.util.UnitPrice;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class RealEstateFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();
    // image loader
    private ImageLoader mImageLoader;


    // view
    private ImageView mTopImage;
    private TextView mTopText;
    private Spinner mDateRangeSelector;
    private TextView mDateRangeView;
    private TextView mTotalPriceView;
    private TextView mUnitPriceView;
    private TextView mResultNumView;
    private Button mTradeDetailBtn;
    private Button mGraphBtn;
    private Button mSearchBtn;
    private Button mCancelBtn;
    private Menu mMenu;

    // data
    private static int mRecordPosition;
    private static String mRecordId;
    private static String mRecordImg;
    private static boolean mIsFromHistory;
    private static ArrayList<Record> mRecord = new ArrayList<Record>();
    private static ArrayList<RealEstate> mRealEstateData = new ArrayList<RealEstate>();
    private static ArrayList<String> mCarpoolPrice = new ArrayList<String>();
    private static ArrayList<String> mCarpoolPriceSummary = new ArrayList<String>();
    private static int mCurrentDate = 0;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static RealEstateFragment newInstance(int sectionNumber,
                                                 int item_position,
                                                 ArrayList<Record> record,
                                                 boolean is_from_history,
                                                 ArrayList<String> carpool_price,
                                                 ArrayList<String> carpool_price_summary,
                                                 ArrayList<RealEstate> data) {
		RealEstateFragment fragment = new RealEstateFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

        mRecordPosition = item_position;
        mRecordId = record.get(item_position).record_id;
        mRecordImg = record.get(item_position).record_address_img;
        mIsFromHistory = is_from_history;
        mRecord = record;
        mRealEstateData = data;
        mCarpoolPrice = carpool_price;
        mCarpoolPriceSummary = carpool_price_summary;

		return fragment;
	}

	public RealEstateFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());
        mImageLoader = Cloud.initImageLoader(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_realestate, container, false);

        mTopImage           = (ImageView) rootView.findViewById(R.id.top_img);
        mTopText            = (TextView) rootView.findViewById(R.id.top_title);
        mDateRangeSelector   = (Spinner) rootView.findViewById(R.id.date_range_selector);
        mDateRangeView     = (TextView) rootView.findViewById(R.id.date_range);
        mTotalPriceView      = (TextView) rootView.findViewById(R.id.total_price);
        mUnitPriceView       = (TextView) rootView.findViewById(R.id.unit_price);
        mResultNumView     = (TextView) rootView.findViewById(R.id.result_num);
        mGraphBtn          = (Button) rootView.findViewById(R.id.realestate_trade_graph_btn);
        mTradeDetailBtn      = (Button) rootView.findViewById(R.id.realestate_trade_detail_btn);
        mSearchBtn          = (Button) rootView.findViewById(R.id.realestate_search_btn);
        mCancelBtn          = (Button) rootView.findViewById(R.id.realestate_cancel_btn);

        // set spinner
        // set spinner
        ArrayAdapter<CharSequence> spinnerRintonesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.realestate_date_range, android.R.layout.simple_spinner_item);
        spinnerRintonesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDateRangeSelector.setAdapter(spinnerRintonesAdapter);

        mDateRangeSelector.setOnItemSelectedListener(onItemSelectedListener);

        // set disable title first
        mTopText.setVisibility(View.GONE);

        // set listener
        mGraphBtn.setOnClickListener(mGraphListener);
        mTradeDetailBtn.setOnClickListener(mTradeDetailListener);
        mCancelBtn.setOnClickListener(mCancelListener);
        mSearchBtn.setOnClickListener(mSearchListener);

        // set tip
        //mTopText.setOnClickListener(mTipOrgListener);

        setHasOptionsMenu(true);
        // load data
        setViewTitle();

		return rootView;
	}

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrentDate = position;
            updateViewContent();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void setViewTitle(){
        // set title & image
        mTopText.setVisibility(View.GONE);
        mTopText.setText(getTrueOrgName() + getString(R.string.tip_question_mark));

        mImageLoader.displayImage(mRecordImg, mTopImage);

        mTopImage.setOnClickListener(mTipOrgListener);
        updateViewContent();
    }

    private String getTrueOrgName(){
        String returnVal = "";

        for(int i = 0; i < mRealEstateData.size(); i++){
            if(returnVal.length() < 1){
                returnVal = mRealEstateData.get(i).realestate_org_name;
            }else{
                break;
            }
        }


        return returnVal;
    }

    private void updateViewContent(){

        RealEstate currentData = mRealEstateData.get(mCurrentDate);

        String totalPriceString = NumberFormatter.get(getActivity(), currentData.realestate_min_price, 0) +getString(R.string.realestate_dash_symbol) + NumberFormatter.get(getActivity(), currentData.realestate_max_price, 0);
        mTotalPriceView.setText(totalPriceString);

        String unitPriceString = UnitPrice.get(getActivity(), currentData.realestate_min_price, mCarpoolPrice.get(mCurrentDate), currentData.realestate_total_size, currentData.realestate_carpool_size)
                                    + getString(R.string.realestate_dash_symbol)
                                    + UnitPrice.get(getActivity(), currentData.realestate_max_price, mCarpoolPrice.get(mCurrentDate), currentData.realestate_total_size, currentData.realestate_carpool_size);
        mUnitPriceView.setText(unitPriceString);

        mResultNumView.setText(currentData.realestate_trades_num);



        mDateRangeView.setText(getString(R.string.realestate_date_range_prefix) + "(" + currentData.realestate_price_date_text + ")");
    }


    private View.OnClickListener mTipOrgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RealEstateDetailContent.show(getActivity(), mRealEstateData.get(mCurrentDate), mCarpoolPriceSummary.get(mCurrentDate));
        }
    };


    private View.OnClickListener mGraphListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mRealEstateData.get(mCurrentDate).realestate_org_name.length() > 1) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, RealEstateGraphFragment.newInstance(getPosition, mCarpoolPrice.get(mCurrentDate), mRealEstateData.get(mCurrentDate), mCarpoolPriceSummary.get(mCurrentDate)))
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }else{
                TipDialog mTipDialog = new TipDialog(getActivity());
                mTipDialog.showPlain(getString(R.string.tip_date_range_empty));
            }
        }
    };

    private View.OnClickListener mTradeDetailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(mRealEstateData.get(mCurrentDate).realestate_org_name.length() > 1) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, RealEstateTradeDetailFragment.newInstance(getPosition, mCarpoolPrice.get(mCurrentDate), mRealEstateData.get(mCurrentDate), mCarpoolPriceSummary.get(mCurrentDate)))
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }else{
                TipDialog mTipDialog = new TipDialog(getActivity());
                mTipDialog.showPlain(getString(R.string.tip_date_range_empty));
            }
        }
    };

    private View.OnClickListener mCancelListener = new View.OnClickListener() {
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
        LogFactory.set("backToPreviousPage - mCarpoolPrice", mCarpoolPrice);
        if(mCarpoolPriceSummary.size() < 1){
            if(mIsFromHistory){
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, SearchHistoryFragment.newInstance(MainActivity.NAVIGATION_SEARCH_HISTORY))
                        //.addToBackStack(null)
                        .commitAllowingStateLoss();
            }else {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, SearchPriceResultFragment.newInstance(getPosition, mRecord))
                        //.addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        }else{
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, CarpoolPriceFragment.newInstance(getPosition, mRecordPosition, mRecord, mIsFromHistory))
                    //.addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    private Runnable backRunnable = new Runnable() {
        @Override
        public void run() {
            backToPreviousPage();
        }
    };

    private View.OnClickListener mSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, SearchPriceFragment.newInstance(getPosition))
                    //.addToBackStack(null)
                    .commitAllowingStateLoss();

        }
    };


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
        ((MainActivity) activity).setOnBackFunction(backRunnable);
	}

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        mMenu = menu;
        // set action bar
        setActionBarOrgName(getTrueOrgName());
        // set action bar
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == 0){
            RealEstateDetailContent.show(getActivity(), mRealEstateData.get(mCurrentDate), mCarpoolPriceSummary.get(mCurrentDate));
        }

        return super.onOptionsItemSelected(item);
    }

    private void setActionBarOrgName(String getOrgname){
        // set action bar
        //((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(getOrgname);
        MenuItem addMenu = mMenu.add(0, 0, 0, getOrgname);
        addMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        // set action bar
    }
}
