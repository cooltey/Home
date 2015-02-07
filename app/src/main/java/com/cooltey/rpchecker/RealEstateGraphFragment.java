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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooltey.rpchecker.data.RealEstate;
import com.cooltey.rpchecker.data.RealEstateTradeDetail;
import com.cooltey.rpchecker.util.Cloud;
import com.cooltey.rpchecker.util.RealEstateDetailContent;
import com.cooltey.rpchecker.util.UnitPrice;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class RealEstateGraphFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();
    // image loader
    private ImageLoader mImageLoader;


    // view
    private TextView mGraphDescView;
    private LinearLayout mGraphContainerView;
    private Button mCancelBtn;
    private Menu mMenu;

    //data
    private static RealEstate mRealEstateData;
    private static String mCarpoolPrice;
    private static String mCarpoolPriceSummary;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static RealEstateGraphFragment newInstance(int sectionNumber,
                                                      String carpool_price,
                                                      RealEstate data,
                                                      String carpool_price_summary) {
		RealEstateGraphFragment fragment = new RealEstateGraphFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

        mRealEstateData = data;
        mCarpoolPrice = carpool_price;
        mCarpoolPriceSummary = carpool_price_summary;

		return fragment;
	}

	public RealEstateGraphFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());
        mImageLoader = Cloud.initImageLoader(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_realestate_graph, container, false);

        mGraphDescView           = (TextView) rootView.findViewById(R.id.graph_desc);
        mGraphContainerView     = (LinearLayout) rootView.findViewById(R.id.graph_content);
        mCancelBtn               = (Button) rootView.findViewById(R.id.graph_cancel_btn);

        setContent();
        setHasOptionsMenu(true);

        mCancelBtn.setOnClickListener(mCancelListener);

		return rootView;
	}

    private void setContent(){
        // set daa
        mGraphDescView.setText(getString(R.string.realestate_graph_desc_prefix) + mRealEstateData.realestate_price_date + getString(R.string.realestate_graph_desc_lastfix));

        // get detail data
        ArrayList<RealEstateTradeDetail> tradeDetail = mRealEstateData.realestate_trades_detail;

        if(tradeDetail.size() > 0) {
           /*
            GraphView.GraphViewData[] gvData = new GraphView.GraphViewData[tradeDetail.size()];
            String[] dateData = new String[tradeDetail.size()];

            // insert data

            for (int i = 0; i < gvData.length; i++) {
                RealEstateTradeDetail tmpData = tradeDetail.get(i);
                //gvData[i] = new GraphView.GraphViewData(i, (Double.parseDouble(tmpData.detail_total_price)/10000));
                gvData[i] = new GraphView.GraphViewData(i, UnitPrice.getPure(getActivity(), tmpData.detail_total_price, mCarpoolPrice, tmpData.detail_total_size, tmpData.detail_carpool_size));
                // split string to date
                String dateYear = tmpData.detail_date.substring(0, 3);
                String dateMonth = tmpData.detail_date.substring(3, 5);
                dateData[i] = dateYear + "/" + dateMonth;
            }

            // init example series data
            GraphViewSeries dataSeries = new GraphViewSeries(gvData);

            GraphView graphView = new LineGraphView(
                    getActivity() // context
                    , "" // heading
            );


            graphView.addSeries(dataSeries); // data


            graphView.setHorizontalLabels(dateData); // x
            graphView.setScrollable(true);
            // optional - activate scaling / zooming
            graphView.setScalable(true);
            graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.realestate_graph_textsize));
*/

            GraphView graphView = new GraphView(getActivity());
            DataPoint[] dataPoint = new DataPoint[tradeDetail.size()+2];
            String[] dateData = new String[tradeDetail.size()+2];


            for (int i = 0; i < tradeDetail.size()+2; i++) {



                if( i == 0 || i == tradeDetail.size()+1){
                    dataPoint[i] = new DataPoint(i, 0);
                    dateData[i] = "";
                }else {
                    RealEstateTradeDetail tmpData = tradeDetail.get(i-1);
                    // split string to date
                    String dateYear = tmpData.detail_date.substring(0, 3);
                    String dateMonth = tmpData.detail_date.substring(3, 5);
                    dateData[i] = dateYear + "/" + dateMonth;
                    int chartVal = UnitPrice.getPure(getActivity(), tmpData.detail_total_price, mCarpoolPrice, tmpData.detail_total_size, tmpData.detail_carpool_size);
                    dataPoint[i] = new DataPoint(i, chartVal);
                    dateData[i] = dateYear + "/" + dateMonth;
                }


            }


            BarGraphSeries<DataPoint> dataSeries = new BarGraphSeries<DataPoint>(dataPoint);
            graphView.addSeries(dataSeries);

            // styling
            /*dataSeries.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });
                */
            dataSeries.setSpacing(60);
            // draw values on top
            dataSeries.setDrawValuesOnTop(true);
            dataSeries.setValuesOnTopColor(Color.parseColor("#ff4171"));
            dataSeries.setValuesOnTopSize(40);

            // use static labels for horizontal and vertical labels
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(dateData);
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graphView.getViewport().setScalable(true);
            graphView.getViewport().setScrollable(true);

            //series.setValuesOnTopSize(50);
            mGraphContainerView.addView(graphView);
        }
    }
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
