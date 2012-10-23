package net.londatiga.fsq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.londatiga.fsq.FoursquareDialog.FsqDialogListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mindhelix.nwplyng.Login;


public class FoursquareApp {
	private FoursquareSession mSession;
	private FoursquareDialog mDialog;
	private FsqAuthListener mListener;
	private ProgressDialog mProgress;
	private String mTokenUrl;
	private String mAccessToken;
	private String userId="";
	private String photoUrl="";
	
	/**
	 * Callback url, as set in 'Manage OAuth Costumers' page (https://developer.foursquare.com/)
	 */
	public static final String CALLBACK_URL = "nwplyng://foursquare";
	private static final String AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
	private static final String TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";	
	private static final String API_URL = "https://api.foursquare.com/v2";
	
	private static final String TAG = "FoursquareApi";
	
	public FoursquareApp(Context context, String clientId, String clientSecret) {
		mSession		= new FoursquareSession(context);
		
		mAccessToken	= mSession.getAccessToken();
		
		mTokenUrl		= TOKEN_URL + "&client_id=" + clientId + "&client_secret=" + clientSecret
						+ "&redirect_uri=" + CALLBACK_URL;
		
		String url		= AUTH_URL + "&client_id=" + clientId + "&redirect_uri=" + CALLBACK_URL;
		
		FsqDialogListener listener = new FsqDialogListener() {
			public void onComplete(String code) {
				getAccessToken(code);
			}
			
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};
		
		mDialog			= new FoursquareDialog(context, url, listener);
		mProgress		= new ProgressDialog(context);
		
		mProgress.setCancelable(false);
	}
	
	private void getAccessToken(final String code) {
		mProgress.setMessage("Connecting ...");
		mProgress.show();
		
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				
				int what = 0;
				
				try {
					URL url = new URL(mTokenUrl + "&code=" + code);
					
					Log.i(TAG, "Opening URL " + url.toString());
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					
					urlConnection.connect();
					
					JSONObject jsonObj  = (JSONObject) new JSONTokener(streamToString(urlConnection.getInputStream())).nextValue();
		        	mAccessToken 		= jsonObj.getString("access_token");
		        	
		        	Log.i(TAG, "Got access token: " + mAccessToken);
				} catch (Exception ex) {
					what = 1;
					
					ex.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}
	
	private void fetchUserName() {
		mProgress.setMessage("Connecting...");
		
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user name");
				int what = 0;
		
				try {
					URL url = new URL(API_URL + "/users/self?oauth_token=" + mAccessToken);
					
										HttpClient httpclient = new DefaultHttpClient();
					// Prepare a request object
				    HttpGet httpget = new HttpGet(url.toString()); 

				    // Execute the request
				    String response = null;
				    HttpResponse response1;
				    try {
				        response1 = httpclient.execute(httpget);
				        // Get hold of the response entity
				        HttpEntity entity = response1.getEntity();
				        // If the response does not enclose an entity, there is no need
				        // to worry about connection release

				        if (entity != null) {

				            // A Simple JSON Response Read
				            InputStream instream = entity.getContent();
				            response= convertStreamToString(instream);
				        }
				    }catch(Exception e){
				    	
				    }
					JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		       
					JSONObject resp		= (JSONObject) jsonObj.get("response");
					JSONObject user		= (JSONObject) resp.get("user");
					
					String firstName 	= user.getString("firstName");
		        	String lastName		= user.getString("lastName");
		        	
		        	userId = user.getString("id");
		        	photoUrl = user.getString("photo");
		        	
		        	Log.i(TAG, "Got user name: " + firstName + " " + lastName);
		        	
		        	mSession.storeAccessToken(mAccessToken, firstName + " " + lastName);
				} catch (Exception ex) {
					what = 1;
					
					ex.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}
	
	public void checkin(String venueId, String message){
		
		String  url = API_URL +"/checkins/add?oauth_token="+mAccessToken;
		String result = null ;
		try{
			URL uri = new URL(url);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri.toString());
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        //nameValuePairs.add(new BasicNameValuePair("oauth_token", mAccessToken));
        	nameValuePairs.add(new BasicNameValuePair("venueId", venueId));
        	nameValuePairs.add(new BasicNameValuePair("shout", message));

        	post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			 if (entity != null) {
				 result= EntityUtils.toString(entity);
		     }
			
			
		}catch(Exception e){
			
		}
		System.out.print(result);
			
	}
	
	public String getUserId(){
		if(userId.length()!=0)
			return userId;
		else{
			fetchUserName();
			return userId;
		}
	}
	public String getPhotoUrl(){
		if(photoUrl.length()!=0)
			return photoUrl;
		else{
			fetchUserName();
			return photoUrl;
		}
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				if (msg.what == 0) {
					fetchUserName();
				} else {
					mProgress.dismiss();
					
					mListener.onFail("Failed to get access token");
				}
			} else {
				mProgress.dismiss();
				
				mListener.onSuccess();
			}
		}
	};
	
	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}
	
	public void setListener(FsqAuthListener listener) {
		mListener = listener;
	}
	
	public String getUserName() {
		return mSession.getUsername();
	}
	
	public void authorize() {
		mDialog.show();
	}
	/*
	 * RETURNS THE NEARBY VENUES
	 * 
	 */
	public static ArrayList<FsqVenue> getNearby(double latitude, double longitude) throws Exception {
		ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();

		try {
			
			Calendar c = Calendar.getInstance();
			Date d = c.getTime();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			String dateNow = sf.format(d);

			String ll = String.valueOf(latitude) + ","
					+ String.valueOf(longitude);
			URL url = new URL(API_URL + "/venues/search?ll=" + ll
					+ "&client_id=" + Login.CLIENT_ID + "&client_secret="
					+ Login.CLIENT_SECRET + "&v=" + dateNow);

			Log.d(TAG, "Opening URL " + url.toString());

			HttpClient httpclient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(url.toString());

			String response = null;

			HttpResponse response1;

			try {
				response1 = httpclient.execute(httpget);
				
				HttpEntity entity = response1.getEntity();
				
				if (entity != null) {
					InputStream instream = entity.getContent();
					response = convertStreamToString(instream);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			JSONArray groups = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONArray("venues");

			int length = groups.length();

			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject group = (JSONObject) groups.get(i);

					FsqVenue venue = new FsqVenue();

					venue.id = group.getString("id");
					venue.name = group.getString("name");

					JSONObject location = (JSONObject) group
							.getJSONObject("location");

					venue.latitude = location.getString("lat");
					venue.longitude = location.getString("lng");
					venue.distance = location.getInt("distance");

					if (venue.distance >= 1000) {
						float distaceinKm = (float) venue.distance / 1000;
						venue.distanceString = distaceinKm + " Km";
					} else
						venue.distanceString = venue.distance + " m";
					venueList.add(venue);
				}
			}

		} catch (Exception ex) {
			throw ex;
		}
		
		return venueList;
	}
	
	private String streamToString(InputStream is) throws IOException {
		String str  = "";
		
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));
				
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				
				reader.close();
			} finally {
				is.close();
			}
			
			str = sb.toString();
		}
		
		return str;
	}
	
	public interface FsqAuthListener {
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}
	
	 private static String convertStreamToString(InputStream is) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
		}
}