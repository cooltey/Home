package com.cooltey.rpchecker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cooltey.rpchecker.data.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class Cloud {
	public static String URL_PREFIX = "";
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
        	//LogFactory.set("BasicLoginRunnable", "builder  => " + builder);
            
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
    
}
