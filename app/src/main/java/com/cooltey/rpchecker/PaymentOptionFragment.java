package com.cooltey.rpchecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cooltey.rpchecker.data.Payment;
import com.cooltey.rpchecker.data.Record;
import com.cooltey.rpchecker.util.Cloud;

import java.util.ArrayList;

public class PaymentOptionFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog mProgress;
    private AlertDialog.Builder mAlertDialog;
    private Handler mHandler = new Handler();

    // record id
    private static ArrayList<Record> mRecord = new ArrayList<Record>();
    private static int mRecordPosition;
    private static String mRecordId;
    private static String mRecordImg;

    // data
    private String mPaymentType;
    private String mPaymentOption;
    private String mReceipt;

    // view
    private RadioGroup mOptionGroup;
    private RadioButton mOption_1;
    private RadioButton mOption_2;
    private RadioButton mOption_3;
    private Button mCancelBtn;
    private Button mBuyBtn;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PaymentOptionFragment newInstance(int sectionNumber, int item_position, ArrayList<Record> data) {
		PaymentOptionFragment fragment = new PaymentOptionFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

        mRecordPosition = item_position;
        mRecordId = data.get(item_position).record_id;
        mRecordImg = data.get(item_position).record_address_img;
        mRecord = data;

		return fragment;
	}

	public PaymentOptionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // set dialog
        mProgress     = new ProgressDialog(getActivity());
        mAlertDialog  = new AlertDialog.Builder(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_payment_option, container,
				false);

        mOptionGroup = (RadioGroup) rootView.findViewById(R.id.payment_option_group);
        mOption_1    = (RadioButton) rootView.findViewById(R.id.payment_option_1);
        mOption_2    = (RadioButton) rootView.findViewById(R.id.payment_option_2);
        mOption_3    = (RadioButton) rootView.findViewById(R.id.payment_option_3);
        mCancelBtn   = (Button) rootView.findViewById(R.id.payment_cancel_btn);
        mBuyBtn      = (Button) rootView.findViewById(R.id.payment_buy_btn);

        mOptionGroup.setOnCheckedChangeListener(mOptionGroupListener);
        mCancelBtn.setOnClickListener(mCancelBtnListener);
        mBuyBtn.setOnClickListener(mBuyBtnListener);

        // remove subtitle
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(null);
		return rootView;
	}

    private RadioGroup.OnCheckedChangeListener mOptionGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            // doing payment function

            switch(checkedId){
                case R.id.payment_option_1:
                    //  single payment
                    mPaymentOption = "SINGLE_PAYMENT";
                    mPaymentType = "1";
                    break;

                case R.id.payment_option_2:
                    mPaymentOption = "30_DAYS_PAYMENT";
                    mPaymentType = "2";
                    break;

                case R.id.payment_option_3:
                    mPaymentOption = "90_DAYS_PAYMENT";
                    mPaymentType = "3";
                    break;
            }

        }
    };

    private void callInAppPurchasing(){
        // doing Google Play iap
        // doing Google Play iap

        // finish
        mReceipt = mPaymentOption;

        setPayment();
    }

    private void setPayment(){

        mProgress.setMessage(getString(R.string.dialog_progress));
        mProgress.show();
        Cloud.setPayment(getActivity(), mReceipt, mPaymentType, mPaymentListener);
    }

    private Cloud.PaymentListener mPaymentListener = new Cloud.PaymentListener() {
        @Override
        public Handler getHandler() {
            mProgress.cancel();
            return mHandler;
        }

        @Override
        public void onSuccess(String status, String message, ArrayList<Payment> data) {
            if(message != null){
                mAlertDialog.setMessage(message);
                mAlertDialog.setNeutralButton(getString(R.string.alert_dialog_ok_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager
                                .beginTransaction()
                                .replace(R.id.container, CarpoolPriceFragment.newInstance(getPosition, mRecordPosition, mRecord, false))
                                //.addToBackStack(null)
                                .commitAllowingStateLoss();
                    }
                });
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();
            }
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

    private View.OnClickListener mBuyBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callInAppPurchasing();
        }
    };

    private View.OnClickListener mCancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // getFragmentManager().popBackStack();
            backRunnable.run();
        }
    };


    private void backToPreviousPage(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int getPosition = getArguments().getInt(ARG_SECTION_NUMBER);
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, SearchPriceResultFragment.newInstance(getPosition, mRecord))
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
