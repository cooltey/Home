package com.cooltey.rpchecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltey.rpchecker.data.CarpoolPrice;
import com.cooltey.rpchecker.data.RealEstate;
import com.cooltey.rpchecker.data.Record;
import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.LogFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class CarpoolPriceFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();
    // image loader
    private ImageLoader mImageLoader;


    // view
    private ImageView mTopImage;
    private TextView mTopText;
    private LinearLayout mCarpoolContainer;
    private Button mNextBtn;
    private Button mCancelBtn;
    private Menu mMenu;

    // data
    private static int mRecordPosition;
    private static String mRecordId;
    private static String mRecordImg;
    private static boolean mIsFromHistory;
    private static ArrayList<Record> mRecord = new ArrayList<Record>();
    private ArrayList<RealEstate> mRealEstateData = new ArrayList<RealEstate>();
    private ArrayList<CarpoolPrice> mCarpoolPriceData = new ArrayList<CarpoolPrice>();
    private ArrayList<String> mCarpoolFinalPriceSummary = new ArrayList<String>();
    private ArrayList<String> mCarpoolFinalPrice = new ArrayList<String>();
    private String mCarpoolPrice;
    private double mFinalPrice = 0;

    private int mChosenPosition = 0;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CarpoolPriceFragment newInstance(int sectionNumber, int item_position, ArrayList<Record> data, boolean is_from_history) {
		CarpoolPriceFragment fragment = new CarpoolPriceFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

        mRecordPosition = item_position;
        mRecordId = data.get(item_position).record_id;
        mRecordImg = data.get(item_position).record_address_img;
        mRecord = data;
        mIsFromHistory = is_from_history;

		return fragment;
	}

	public CarpoolPriceFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());
        mImageLoader = Cloud.initImageLoader(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_carpool_price, container, false);

        mTopImage           = (ImageView) rootView.findViewById(R.id.top_img);
        mTopText            = (TextView) rootView.findViewById(R.id.top_title);
        mCarpoolContainer   = (LinearLayout) rootView.findViewById(R.id.carpool_price_content);
        mNextBtn            = (Button) rootView.findViewById(R.id.carpool_next_btn);
        mCancelBtn          = (Button) rootView.findViewById(R.id.carpool_cancel_btn);

        // set disable title first
        mTopText.setVisibility(View.GONE);

        // set listener
        mCancelBtn.setOnClickListener(mCancelListener);
        mNextBtn.setOnClickListener(mNextListener);

        getRealEstateData();
        setHasOptionsMenu(true);
		return rootView;
	}

    private void getRealEstateData(){
        mProgress.setMessage(getString(R.string.dialog_progress));
        mProgress.show();
        Cloud.getRealEstatePrice(getActivity(), mRecordId, mRealEstateListener);
    }

    private void getCarpoolPriceData(String city, String district){
        mProgress.setMessage(getString(R.string.dialog_progress));
        mProgress.show();
        Cloud.getCarpoolPrice(getActivity(), city, district, mCarpoolPriceListener);
    }

    private Cloud.RealEstateListener mRealEstateListener = new Cloud.RealEstateListener() {
        @Override
        public Handler getHandler() {
            mProgress.cancel();
            return mHandler;
        }

        @Override
        public void onSuccess(String status, String message, ArrayList<RealEstate> data) {
            // no item
            if(data.size() == 0){
                mAlertDialog.setMessage(getString(R.string.alert_dialog_no_result));
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        backToPreviousPage();
                    }
                });
                mAlertDialog.show();
                return;
            }else if(data.get(0).realestate_org_name.length() <= 1 && data.get(1).realestate_org_name.length() <= 1 && data.get(2).realestate_org_name.length() <= 1 && data.get(3).realestate_org_name.length() <= 1){
                mAlertDialog = new AlertDialog.Builder(getActivity());
                mAlertDialog.setMessage(getString(R.string.carpool_price_item_price_no_result));
                mAlertDialog.setNeutralButton(getString(R.string.carpool_price_item_price_no_result_feedback_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity activity = (MainActivity) getActivity();
                        activity.onNavigationDrawerItemSelected(MainActivity.NAVIGATION_FEEDBACK);
                        backToPreviousPage();

                    }
                });
                mAlertDialog.setPositiveButton(getString(R.string.carpool_price_item_price_no_result_search_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity activity = (MainActivity) getActivity();
                        activity.onNavigationDrawerItemSelected(MainActivity.NAVIGATION_SEARCH_PRICE);
                       // backToPreviousPage();
                    }
                });
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();
                return;
            }

            mRealEstateData = data;

            // check the most number of carpool type
            int chosenCarpoolSize = 0;
            for(int t = 0; t < mRealEstateData.size(); t++){
                RealEstate tpData = mRealEstateData.get(t);
                if(tpData.realestate_carpool_types.length() > 0) {
                    String[] carpooltypes = tpData.realestate_carpool_types.split(",");
                    if(carpooltypes.length > chosenCarpoolSize){
                        chosenCarpoolSize = carpooltypes.length;
                        mChosenPosition = t;

                    }
                }
            }


            RealEstate tmpData = mRealEstateData.get(mChosenPosition);
            if(tmpData.realestate_carpool_types.length() <= 1){
                // to the detail page
                goToRealEstatePage();
                LogFactory.set("mRealEstateData", "to the detail page");
            }else{
                // set title & image
                mTopText.setVisibility(View.GONE);
                mTopText.setText(tmpData.realestate_org_name);
                mImageLoader.displayImage(mRecordImg, mTopImage);

                String getOrgname = tmpData.realestate_org_name;

                setActionBarOrgName(getOrgname);

                // get carpool types
                getCarpoolPriceData(tmpData.realestate_city_id, tmpData.realestate_district);
            }
        }

        @Override
        public void onFail(String status, String message) {
            if(message != null){
                mProgress.cancel();
                mAlertDialog.setMessage(message);
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        backToPreviousPage();
                    }
                });
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();
            }
        }
    };


    private Cloud.CarpoolPriceListener mCarpoolPriceListener = new Cloud.CarpoolPriceListener() {
        @Override
        public Handler getHandler() {
            mProgress.cancel();
            return mHandler;
        }

        @Override
        public void onSuccess(String status, String message, ArrayList<CarpoolPrice> data) {
            mCarpoolPriceData = data;

            // set view
            setViewContent();
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

    private void setViewContent(){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        RealEstate tmpData = mRealEstateData.get(mChosenPosition);
        String[] carTypes = tmpData.realestate_carpool_types.split(",");

        // for loop
        for(int i = 0; i < mCarpoolPriceData.size(); i++){
            for(int j = 0; j < carTypes.length; j++){
                if(mCarpoolPriceData.get(i).carpool_type_name.contains(carTypes[j])){
                    // add view
                    View rootView = layoutInflater.inflate(R.layout.item_carpool_price, null);
                    TextView text = (TextView) rootView.findViewById(R.id.text);
                    EditText edit = (EditText) rootView.findViewById(R.id.price);

                    text.setText(mCarpoolPriceData.get(i).carpool_type_name + getString(R.string.carpool_price_item_lastfix));
                    edit.setText(mCarpoolPriceData.get(i).carpool_price + "");
                    edit.setTag(mCarpoolPriceData.get(i).carpool_type_name);

                    mCarpoolContainer.addView(rootView);
                }
            }
        }
    }

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
        int getPosition = MainActivity.NAVIGATION_SEARCH_PRICE;
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
    }

    private Runnable backRunnable = new Runnable() {
        @Override
        public void run() {
            backToPreviousPage();
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            for(int a = 0; a < mRealEstateData.size(); a++){
                // chec k carpool size
                RealEstate tmpData = mRealEstateData.get(a);


                mCarpoolPrice = "";
                mFinalPrice    = 0;
                // get edittext's value
                for(int i = 0; i < mCarpoolContainer.getChildCount(); i++){
                    // get editor
                    View itemView = mCarpoolContainer.getChildAt(i);
                    EditText editView = (EditText) itemView.findViewById(R.id.price);

                    String getTag = editView.getTag().toString();

                    if(tmpData.realestate_carpool_types.contains(getTag)){
                        mFinalPrice = mFinalPrice + (Double.parseDouble(editView.getText().toString()) * 10000);

                        if(mCarpoolPrice.equals("")){
                            mCarpoolPrice = (Double.parseDouble(editView.getText().toString()) * 10000) + "";
                        }else {
                            mCarpoolPrice = mCarpoolPrice + "," + (Double.parseDouble(editView.getText().toString()) * 10000);
                        }
                    }
                }


                LogFactory.set("Total Price of Car", mFinalPrice);
                LogFactory.set("Total Price of Car mCarpoolPrice", mCarpoolPrice);

                // add into arraylist
                mCarpoolFinalPriceSummary.add(mCarpoolPrice);
                mCarpoolFinalPrice.add(mFinalPrice + "");

            }





            goToRealEstatePage();
        }
    };


    private void goToRealEstatePage(){
        int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, RealEstateFragment.newInstance(getPosition, mRecordPosition, mRecord, mIsFromHistory, mCarpoolFinalPrice, mCarpoolFinalPriceSummary, mRealEstateData))
                //.addToBackStack(null)
                .commitAllowingStateLoss();
    }

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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
