package com.cooltey.rpchecker.util;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.cooltey.rpchecker.data.CarpoolPrice;
import com.cooltey.rpchecker.data.Payment;
import com.cooltey.rpchecker.data.RealEstate;
import com.cooltey.rpchecker.data.Record;
import com.cooltey.rpchecker.data.SearchHistory;
import com.cooltey.rpchecker.data.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Cloud {
	public static String URL_PREFIX = "http://56rec.com/realestate/api/";
    private static final String TAG = "Cloud";  
    // set connect method
    private static HttpClient sHttpClient = new DefaultHttpClient();
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();

    private static User mUser;
    
    public interface LoginListener {
        Handler getHandler();
        void onSuccess(String status, String message);
        void onFail(String status, String message);
    }

    public interface SearchPriceListener {
        Handler getHandler();
        void onSuccess(String status, String message, ArrayList<Record> data);
        void onFail(String status, String message);
    }

    public interface CarpoolPriceListener {
        Handler getHandler();
        void onSuccess(String status, String message, ArrayList<CarpoolPrice> data);
        void onFail(String status, String message);
    }

    public interface PaymentListener {
        Handler getHandler();
        void onSuccess(String status, String message, ArrayList<Payment> data);
        void onFail(String status, String message);
    }

    public interface RealEstateListener {
        Handler getHandler();
        void onSuccess(String status, String message, ArrayList<RealEstate> data);
        void onFail(String status, String message);
    }

    public interface SearchHistoryListener {
        Handler getHandler();
        void onSuccess(String status, String message, boolean is_end, ArrayList<SearchHistory> data);
        void onFail(String status, String message);
    }

    // set basic login
    static class BasicLoginRunnable implements Runnable {
        LoginListener mListener;
        String mUsername;
        String mPassword;
        String mQueryStatus;
        String mQueryMsg;
        Context mContext;
        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mQueryStatus.equals("1")) {
                    mListener.onFail(mQueryStatus, mQueryMsg);
                }
                else {
                    mListener.onSuccess(mQueryStatus, mQueryMsg);
                }
            }
        };
        public BasicLoginRunnable(Context context, String username, String password, LoginListener listener) {
            mUsername = username;
            mPassword = password;
            mListener = listener;
            mContext = context;
            
        }
        @Override
        public void run() {

            HttpResponse response = null;
            InputStream in = null;
            
            final String url = URL_PREFIX + "login.php";
            LogFactory.set("URL", url);
        	HttpPost request = new HttpPost(url);            

            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("username", mUsername));
            nameValuePair.add(new BasicNameValuePair("password", mPassword));
            
            // Url Encoding the POST parameters
            try {
           	 // prevent string decode error
            	request.setEntity(new UrlEncodedFormEntity(nameValuePair,"UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }
            
            

            try {
                response = sHttpClient.execute(request);
            	//LogFactory.set("BasicLoginRunnable", "HttpUriRequest sHttpClient => " + url);
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            
            

            try {
                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }

            if (null == in) {
                mListener.getHandler().post(mCompleteRunnable);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while (null != (line = reader.readLine())) {
                    builder.append(line);
                    builder.append("\n");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
        	LogFactory.set("BasicLoginRunnable", "builder  => " + builder);
            
            try {
            	
                JSONObject jsonObject = new JSONObject(builder.toString());
				mQueryStatus = jsonObject.getString("status");
				mQueryMsg 	 = jsonObject.getString("msg");
				
				// Add data into user
				mUser = User.fromJSON(jsonObject.getString("data"));
				
                
            } catch (JSONException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            mListener.getHandler().post(mCompleteRunnable);
            
        }
    }

    // set search price
    static class SearchPriceRunnable implements Runnable {
        SearchPriceListener mListener;
        String mKeyword;
        String mQueryStatus;
        String mQueryMsg;
        Context mContext;
        ArrayList<Record> mData = new ArrayList<Record>();
        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mQueryStatus.equals("1")) {
                    mListener.onFail(mQueryStatus, mQueryMsg);
                }
                else {
                    mListener.onSuccess(mQueryStatus, mQueryMsg, mData);
                }
            }
        };
        public SearchPriceRunnable(Context context, String keyword, SearchPriceListener listener) {
            mListener   = listener;
            mContext   = context;
            mKeyword  = keyword;

        }
        @Override
        public void run() {

            HttpResponse response = null;
            InputStream in = null;

            final String url = URL_PREFIX + "get_search_result.php";
            HttpPost request = new HttpPost(url);

            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("user_id", mUser.user_email));
            nameValuePair.add(new BasicNameValuePair("token", mUser.token));
            nameValuePair.add(new BasicNameValuePair("keyword", mKeyword));

            // Url Encoding the POST parameters
            try {
                // prevent string decode error
                request.setEntity(new UrlEncodedFormEntity(nameValuePair,"UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }



            try {
                response = sHttpClient.execute(request);
                //LogFactory.set("BasicLoginRunnable", "HttpUriRequest sHttpClient => " + url);
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }



            try {
                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }

            if (null == in) {
                mListener.getHandler().post(mCompleteRunnable);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while (null != (line = reader.readLine())) {
                    builder.append(line);
                    builder.append("\n");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            //LogFactory.set("BasicLoginRunnable", "builder  => " + builder);

            try {

                JSONObject jsonObject = new JSONObject(builder.toString());
                mQueryStatus = jsonObject.getString("status");
                mQueryMsg 	 = jsonObject.getString("msg");

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                for(int i = 0; i < jsonArray.length(); i++){
                    Record tmpData = Record.fromJSON(jsonArray.getString(i));
                    mData.add(tmpData);
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get carpool price
    static class CarpoolPriceRunnable implements Runnable {
        CarpoolPriceListener mListener;
        String mCity;
        String mDistrict;
        String mQueryStatus;
        String mQueryMsg;
        Context mContext;
        ArrayList<CarpoolPrice> mData = new ArrayList<CarpoolPrice>();
        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mQueryStatus.equals("1")) {
                    mListener.onFail(mQueryStatus, mQueryMsg);
                }
                else {
                    mListener.onSuccess(mQueryStatus, mQueryMsg, mData);
                }
            }
        };
        public CarpoolPriceRunnable(Context context, String city, String district, CarpoolPriceListener listener) {
            mListener   = listener;
            mContext   = context;
            mCity       = city;
            mDistrict    = district;

        }
        @Override
        public void run() {

            HttpResponse response = null;
            InputStream in = null;

            final String url = URL_PREFIX + "get_carpool_price.php";
            HttpPost request = new HttpPost(url);

            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("user_id", mUser.user_email));
            nameValuePair.add(new BasicNameValuePair("token", mUser.token));
            nameValuePair.add(new BasicNameValuePair("city", mCity));
            nameValuePair.add(new BasicNameValuePair("district", mDistrict));

            // Url Encoding the POST parameters
            try {
                // prevent string decode error
                request.setEntity(new UrlEncodedFormEntity(nameValuePair,"UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }



            try {
                response = sHttpClient.execute(request);
                //LogFactory.set("BasicLoginRunnable", "HttpUriRequest sHttpClient => " + url);
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }



            try {
                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }

            if (null == in) {
                mListener.getHandler().post(mCompleteRunnable);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while (null != (line = reader.readLine())) {
                    builder.append(line);
                    builder.append("\n");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            //LogFactory.set("BasicLoginRunnable", "builder  => " + builder);

            try {

                JSONObject jsonObject = new JSONObject(builder.toString());
                mQueryStatus = jsonObject.getString("status");
                mQueryMsg 	 = jsonObject.getString("msg");

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                for(int i = 0; i < jsonArray.length(); i++){
                    CarpoolPrice tmpData = CarpoolPrice.fromJSON(jsonArray.getString(i));
                    mData.add(tmpData);
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // set payment
    static class PaymentRunnable implements Runnable {
        PaymentListener mListener;
        String mReceipt;
        String mDeviceType;  // 1 = android , 2 = ios
        String mDeviceID;
        String mPaymentType; // 1 = single ,  2 = 30 days, 3 = 90 days
        String mQueryStatus;
        String mQueryMsg;
        Context mContext;
        ArrayList<Payment> mData = new ArrayList<Payment>();
        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mQueryStatus.equals("1")) {
                    mListener.onFail(mQueryStatus, mQueryMsg);
                }
                else {
                    mListener.onSuccess(mQueryStatus, mQueryMsg, mData);
                }
            }
        };
        public PaymentRunnable(Context context, String receipt, String payment_type, PaymentListener listener) {
            mListener       = listener;
            mContext        = context;
            mReceipt        = receipt;
            mPaymentType   = payment_type;
            mDeviceType     = "1";
            mDeviceID        = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        }
        @Override
        public void run() {

            HttpResponse response = null;
            InputStream in = null;

            final String url = URL_PREFIX + "set_payment.php";
            HttpPost request = new HttpPost(url);

            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("user_id", mUser.user_email));
            nameValuePair.add(new BasicNameValuePair("token", mUser.token));
            nameValuePair.add(new BasicNameValuePair("receipt", mReceipt));
            nameValuePair.add(new BasicNameValuePair("device_type", mDeviceType));
            nameValuePair.add(new BasicNameValuePair("device_id", mDeviceID));
            nameValuePair.add(new BasicNameValuePair("payment_type", mPaymentType));

            LogFactory.set("payment receipt", mReceipt);
            LogFactory.set("payment device_type", mDeviceType);
            LogFactory.set("payment device_id", mDeviceID);
            LogFactory.set("payment payment_type", mPaymentType);

            // Url Encoding the POST parameters
            try {
                // prevent string decode error
                request.setEntity(new UrlEncodedFormEntity(nameValuePair,"UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }



            try {
                response = sHttpClient.execute(request);
                //LogFactory.set("BasicLoginRunnable", "HttpUriRequest sHttpClient => " + url);
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }



            try {
                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }

            if (null == in) {
                mListener.getHandler().post(mCompleteRunnable);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while (null != (line = reader.readLine())) {
                    builder.append(line);
                    builder.append("\n");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            //LogFactory.set("BasicLoginRunnable", "builder  => " + builder);

            try {

                JSONObject jsonObject = new JSONObject(builder.toString());
                mQueryStatus = jsonObject.getString("status");
                mQueryMsg 	 = jsonObject.getString("msg");

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                for(int i = 0; i < jsonArray.length(); i++){
                    Payment tmpData = Payment.fromJSON(jsonArray.getString(i));
                    mData.add(tmpData);
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get realestate price
    static class RealEstateRunnable implements Runnable {
        RealEstateListener mListener;
        String mRecordId;
        String mQueryStatus;
        String mQueryMsg;
        Context mContext;
        ArrayList<RealEstate> mData = new ArrayList<RealEstate>();
        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mQueryStatus.equals("1")) {
                    mListener.onFail(mQueryStatus, mQueryMsg);
                }
                else {
                    mListener.onSuccess(mQueryStatus, mQueryMsg, mData);
                }
            }
        };
        public RealEstateRunnable(Context context, String record_id, RealEstateListener listener) {
            mListener       = listener;
            mContext       = context;
            mRecordId      = record_id;
        }
        @Override
        public void run() {

            HttpResponse response = null;
            InputStream in = null;

            final String url = URL_PREFIX + "get_realestate_price.php";
            HttpPost request = new HttpPost(url);

            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("user_id", mUser.user_email));
            nameValuePair.add(new BasicNameValuePair("token", mUser.token));
            nameValuePair.add(new BasicNameValuePair("record_id", mRecordId));

            // Url Encoding the POST parameters
            try {
                // prevent string decode error
                request.setEntity(new UrlEncodedFormEntity(nameValuePair,"UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }



            try {
                response = sHttpClient.execute(request);
                //LogFactory.set("BasicLoginRunnable", "HttpUriRequest sHttpClient => " + url);
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }



            try {
                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }

            if (null == in) {
                mListener.getHandler().post(mCompleteRunnable);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while (null != (line = reader.readLine())) {
                    builder.append(line);
                    builder.append("\n");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            //LogFactory.set("BasicLoginRunnable", "builder  => " + builder);

            try {

                JSONObject jsonObject = new JSONObject(builder.toString());
                mQueryStatus = jsonObject.getString("status");
                mQueryMsg 	 = jsonObject.getString("msg");

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                for(int i = 0; i < jsonArray.length(); i++){
                    RealEstate tmpData = RealEstate.fromJSON(jsonArray.getString(i));
                    mData.add(tmpData);
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // get search history
    static class SearchHistoryRunnable implements Runnable {
        SearchHistoryListener mListener;
        String mPage;
        String mQueryStatus;
        String mQueryMsg;
        boolean mIsEnd = true;
        Context mContext;
        ArrayList<SearchHistory> mData = new ArrayList<SearchHistory>();
        Runnable mCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mQueryStatus.equals("1")) {
                    mListener.onFail(mQueryStatus, mQueryMsg);
                }
                else {
                    if(mData.size() > 0){
                        mIsEnd = false;
                    }
                    mListener.onSuccess(mQueryStatus, mQueryMsg, mIsEnd, mData);
                }
            }
        };
        public SearchHistoryRunnable(Context context, String page, SearchHistoryListener listener) {
            mListener       = listener;
            mPage          = page;
            mContext       = context;
        }
        @Override
        public void run() {

            HttpResponse response = null;
            InputStream in = null;

            final String url = URL_PREFIX + "get_search_history.php";
            HttpPost request = new HttpPost(url);

            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("user_id", mUser.user_email));
            nameValuePair.add(new BasicNameValuePair("token", mUser.token));
            nameValuePair.add(new BasicNameValuePair("page", mPage));

            // Url Encoding the POST parameters
            try {
                // prevent string decode error
                request.setEntity(new UrlEncodedFormEntity(nameValuePair,"UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }



            try {
                response = sHttpClient.execute(request);
                //LogFactory.set("BasicLoginRunnable", "HttpUriRequest sHttpClient => " + url);
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }



            try {
                in = response.getEntity().getContent();
            } catch (Exception e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }

            if (null == in) {
                mListener.getHandler().post(mCompleteRunnable);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while (null != (line = reader.readLine())) {
                    builder.append(line);
                    builder.append("\n");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            //LogFactory.set("BasicLoginRunnable", "builder  => " + builder);

            try {

                JSONObject jsonObject = new JSONObject(builder.toString());
                mQueryStatus = jsonObject.getString("status");
                mQueryMsg 	 = jsonObject.getString("msg");

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                for(int i = 0; i < jsonArray.length(); i++){
                    SearchHistory tmpData = SearchHistory.fromJSON(jsonArray.getString(i));
                    mData.add(tmpData);
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString() + ": " + e.getMessage());
            }
            mListener.getHandler().post(mCompleteRunnable);

        }
    }

    // universal imageloader
    public static ImageLoader initImageLoader(Context content){
    	ImageLoader imageLoader 	= ImageLoader.getInstance();
    	
    	DisplayImageOptions options = new DisplayImageOptions.Builder()
			/*.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)*/
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.build();
    	
    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(content)
        .defaultDisplayImageOptions(options)
        .build();
        imageLoader.init(config);
        
		return imageLoader;
    }
    
    // set basic login
    public static void basicLogin(Context context, String username, String password, LoginListener listener) {
        sExecutor.execute(new BasicLoginRunnable(context, username, password, listener));
    }

    // set search price
    public static void searchPrice(Context context, String keyword, SearchPriceListener listener) {
        sExecutor.execute(new SearchPriceRunnable(context, keyword, listener));
    }

    // set search price
    public static void getCarpoolPrice(Context context, String city, String district, CarpoolPriceListener listener) {
        sExecutor.execute(new CarpoolPriceRunnable(context, city, district, listener));
    }

    // set payment
    public static void setPayment(Context context, String receipt, String payment_type, PaymentListener listener) {
        sExecutor.execute(new PaymentRunnable(context, receipt, payment_type, listener));
    }

    // get realestate price
    public static void getRealEstatePrice(Context context, String record_id, RealEstateListener listener) {
        sExecutor.execute(new RealEstateRunnable(context, record_id, listener));
    }

    // get realestate price
    public static void getSearchHistory(Context context, String page, SearchHistoryListener listener) {
        sExecutor.execute(new SearchHistoryRunnable(context, page, listener));
    }
}
