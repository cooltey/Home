package com.cooltey.rpchecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltey.rpchecker.data.RealEstate;
import com.cooltey.rpchecker.data.RealEstateTradeDetail;
import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.NumberFormatter;
import com.cooltey.rpchecker.util.RealEstateDetailContent;
import com.cooltey.rpchecker.util.TipDialog;
import com.cooltey.rpchecker.util.UnitPrice;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RealEstateTradeDetailFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();
    // image loader
    private ImageLoader mImageLoader;


    // view
    private TextView mTradeDetailView;
    private LinearLayout mTradeDetailContainerView;
    private Button mCancelBtn;
    private Menu mMenu;

    //data
    private static RealEstate mRealEstateData;
    private static String mCarpoolPrice;
    private static String mCarpoolPriceSummary;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static RealEstateTradeDetailFragment newInstance(int sectionNumber,
                                                            String carpool_price,
                                                            RealEstate data,
                                                            String carpool_price_summary) {
		RealEstateTradeDetailFragment fragment = new RealEstateTradeDetailFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

        mRealEstateData = data;
        mCarpoolPrice = carpool_price;
        mCarpoolPriceSummary = carpool_price_summary;

		return fragment;
	}

	public RealEstateTradeDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());
        mImageLoader = Cloud.initImageLoader(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_realestate_trade_detail, container, false);

        mTradeDetailView              = (TextView) rootView.findViewById(R.id.trade_detail_title);
        mTradeDetailContainerView     = (LinearLayout) rootView.findViewById(R.id.trade_detail_content);
        mCancelBtn                    = (Button) rootView.findViewById(R.id.trade_detail_cancel_btn);

        setContent();
        setHasOptionsMenu(true);

        mCancelBtn.setOnClickListener(mCancelListener);

		return rootView;
	}

    private void setContent(){

        // set daa
        mTradeDetailView.setText(getString(R.string.realestate_trade_detail_desc_prefix) + mRealEstateData.realestate_price_date + getString(R.string.realestate_trade_detail_desc_lastfix));

        // get detail data
        ArrayList<RealEstateTradeDetail> tradeDetail = mRealEstateData.realestate_trades_detail;

        // get content
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        for(int i = 0; i < tradeDetail.size(); i++){

            RealEstateTradeDetail tmpData = tradeDetail.get(i);

            if(i == 0){
                // add title
                LinearLayout rootView = (LinearLayout) layoutInflater.inflate(R.layout.item_realestate_trade_detail_horizontal, null);

                TextView textView_1 = (TextView) rootView.findViewById(R.id.text1);
                TextView textView_2 = (TextView) rootView.findViewById(R.id.text2);
                TextView textView_3 = (TextView) rootView.findViewById(R.id.text3);
                TextView textView_4 = (TextView) rootView.findViewById(R.id.text4);
                TextView textView_5 = (TextView) rootView.findViewById(R.id.text5);
                TextView textView_6 = (TextView) rootView.findViewById(R.id.text6);
                TextView textView_7 = (TextView) rootView.findViewById(R.id.text7);
                TextView textView_8 = (TextView) rootView.findViewById(R.id.text8);
                //ImageView imageView_1 = (ImageView) rootView.findViewById(R.id.img1);
                //ImageView imageView_2 = (ImageView) rootView.findViewById(R.id.img2);
                //ImageView imageView_3 = (ImageView) rootView.findViewById(R.id.img3);
                ImageView imageView_4 = (ImageView) rootView.findViewById(R.id.img4);
                ImageView imageView_5 = (ImageView) rootView.findViewById(R.id.img5);
                ImageView imageView_6 = (ImageView) rootView.findViewById(R.id.img6);
                //ImageView imageView_7 = (ImageView) rootView.findViewById(R.id.img7);
                ImageView imageView_8 = (ImageView) rootView.findViewById(R.id.img8);


                textView_1.setText(getString(R.string.realestate_trade_detail_column_1));
                textView_2.setText(getString(R.string.realestate_trade_detail_column_2));
                textView_3.setText(getString(R.string.realestate_trade_detail_column_3));
                textView_4.setText(getString(R.string.realestate_trade_detail_column_4));
                textView_5.setText(getString(R.string.realestate_trade_detail_column_5));
                textView_6.setText(getString(R.string.realestate_trade_detail_column_6));
                textView_7.setText(getString(R.string.realestate_trade_detail_column_7));
                textView_8.setText(getString(R.string.realestate_trade_detail_column_8));

                // set listener
                textView_1.setTag(1);
                textView_2.setTag(2);
                textView_3.setTag(3);
                textView_4.setTag(4);
                textView_5.setTag(5);
                textView_6.setTag(6);
                textView_7.setTag(7);
                textView_8.setTag(8);

                //textView_7.setTag(4);
                textView_4.setOnClickListener(mTipListener);
                textView_5.setOnClickListener(mTipListener);
                textView_6.setOnClickListener(mTipListener);
                textView_8.setOnClickListener(mTipListener);
                imageView_4.setVisibility(View.VISIBLE);
                imageView_5.setVisibility(View.VISIBLE);
                imageView_6.setVisibility(View.VISIBLE);
                imageView_8.setVisibility(View.VISIBLE);
                imageView_4.setTag(4);
                imageView_5.setTag(5);
                imageView_6.setTag(6);
                imageView_8.setTag(8);
                imageView_4.setOnClickListener(mTipListener);
                imageView_5.setOnClickListener(mTipListener);
                imageView_6.setOnClickListener(mTipListener);
                imageView_8.setOnClickListener(mTipListener);
                //textView_7.setOnClickListener(mTipListener);


                rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mTradeDetailContainerView.addView(rootView);
            }


            // add title
            LinearLayout childView = (LinearLayout) layoutInflater.inflate(R.layout.item_realestate_trade_detail_horizontal, null);

            TextView textView_1 = (TextView) childView.findViewById(R.id.text1);
            TextView textView_2 = (TextView) childView.findViewById(R.id.text2);
            TextView textView_3 = (TextView) childView.findViewById(R.id.text3);
            TextView textView_4 = (TextView) childView.findViewById(R.id.text4);
            TextView textView_5 = (TextView) childView.findViewById(R.id.text5);
            TextView textView_6 = (TextView) childView.findViewById(R.id.text6);
            TextView textView_7 = (TextView) childView.findViewById(R.id.text7);
            TextView textView_8 = (TextView) childView.findViewById(R.id.text8);

            ImageView imageView_5 = (ImageView) childView.findViewById(R.id.img5);
            imageView_5.setVisibility(View.VISIBLE);

            // get date string
            String dateYear = tmpData.detail_date.substring(0, 3);
            String dateMonth = tmpData.detail_date.substring(3, 5);
            String dateString = dateYear + "/" + dateMonth;

            int currentNum = i + 1;
            textView_1.setText(currentNum + "");
            textView_2.setText(dateString);
            textView_3.setText(tmpData.detail_levels);

            textView_4.setText(NumberFormatter.get(getActivity(), tmpData.detail_total_price, 0));

            // get unit price
            String unitPrice = UnitPrice.get(getActivity(), tmpData.detail_total_price, mCarpoolPrice, tmpData.detail_total_size, tmpData.detail_carpool_size);

            final String unitPriceTip = UnitPrice.getTip(getActivity(), tmpData.detail_total_price, mCarpoolPrice, tmpData.detail_total_size, tmpData.detail_carpool_size);

            textView_5.setText(unitPrice);
            textView_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TipDialog tipDialog = new TipDialog(getActivity());
                    tipDialog.showPlain(unitPriceTip);
                }
            });
            imageView_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TipDialog tipDialog = new TipDialog(getActivity());
                    tipDialog.showPlain(unitPriceTip);
                }
            });

            // format the total size
            DecimalFormat formatter;
            formatter = new DecimalFormat("#,###,###,###.##");
            String totalSizeFormatter =  formatter.format(Float.parseFloat(tmpData.detail_total_size)*UnitPrice.SIZE_VALUE);

            textView_6.setText(totalSizeFormatter + "");
            textView_7.setText(tmpData.detail_trade_number);
            textView_8.setText(tmpData.detail_extra);


            if(i%2 == 0){
                childView.setBackgroundColor(Color.parseColor("#e3e3e3"));
            }

            if(i == tradeDetail.size()-1){
                // add end line
                LinearLayout endLine = new LinearLayout(getActivity());
                endLine.setLayoutParams(new LinearLayout.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));
                endLine.setBackgroundColor(Color.parseColor("#999999"));
                childView.addView(endLine);
            }

            childView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mTradeDetailContainerView.addView(childView);
        }


    }

    private View.OnClickListener mTipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int getPosition = (int) v.getTag();
            String getAlertMsg = "";
            switch(getPosition){
                case 4:
                    // total price
                    getAlertMsg = getString(R.string.tip_total_price);
                    break;

                case 5:
                    // unit price
                    getAlertMsg = getString(R.string.tip_unit_price);
                    break;

                case 6:
                    // total size
                    getAlertMsg = getString(R.string.tip_total_size);
                    break;

                case 8:
                    // extra
                    getAlertMsg = getString(R.string.tip_extra);
                    break;
            }

            TipDialog tipDialog = new TipDialog(getActivity());
            tipDialog.showPlain(getAlertMsg);
        }
    };

    private View.OnClickListener mCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            backRunnable.run();
        }
    };

    private void backToPreviousPage(){
        getFragmentManager().popBackStack();
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


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        mMenu = menu;
        // set action bar
        setActionBarOrgName(mRealEstateData.realestate_org_name);
        // set action bar
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == 0){
            RealEstateDetailContent.show(getActivity(), mRealEstateData, mCarpoolPriceSummary);
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
