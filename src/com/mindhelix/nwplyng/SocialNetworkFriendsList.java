package com.mindhelix.nwplyng;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SocialNetworkFriendsList extends Activity {
	
	ListView followList;
	SocialNetworkFriendsListAdapter adapter;
	
	Button back;
	
	View footerView, headerView;
	
	ProgressBar progressBar;
	ProgressBar footerProgress;
	public boolean loadingMore = true;
	
	public int pageCount = 1;
	public int jsonArrayLength = 1;
	public int footerflag = 0;
	
	String auth_token = "";
	
	SharedPreferences preferences;
	public List<User>  followDataList;
	public List<Boolean> followInitialList;
	public List<Boolean> followFinalList;
	
	public boolean isLoadingOver = false;
	
	public boolean isUpdate = false;
	
	String app_id;
	String targetID;
	String type;
	String key;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.add_friend_social_networks);
	     
	     Bundle bundle = getIntent().getExtras();
	     
	     type = bundle.getString("type");
	     
	     if(type.equals("1")) {
	    	 app_id = bundle.getString("facebook_id");
	    	 key = bundle.getString("access_token");
	     }
	     else if(type.equals("2")) {
	    	 app_id = bundle.getString("twitter_id");
	    	 key = "";
	     }
	     else if(type.equals("3")) {
	    	 app_id = bundle.getString("foursquare_id");
	    	 key = bundle.getString("oauth_token");
	     }
	     
		 preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		 auth_token = preferences.getString("auth_token", "");
		 
		 followList = (ListView)findViewById(R.id.socialFriendSearchList);
		 followDataList = new ArrayList<User>();
		 followInitialList = new ArrayList<Boolean>();
		 followFinalList = new ArrayList<Boolean>();
		 
		 headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_header, null, false);
		 headerView.setMinimumHeight(10);
		 followList.addHeaderView(headerView);
		 
		 footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_list_footer, null, false);
		 footerView.setMinimumHeight(110);
		 footerProgress = (ProgressBar)footerView.findViewById(R.id.progress);
		 
		 progressBar = (ProgressBar)findViewById(R.id.activityIndicator_social);
		 back = (Button)findViewById(R.id.addFriendSocialBackButton);
		 
		 progressBar.setVisibility(ProgressBar.INVISIBLE);
		 
		 footerflag = 0;
		 footerView.setVisibility(View.INVISIBLE);
		 followList.addFooterView(footerView);
		 try {
			 followList.removeFooterView(headerView);
	        }	catch (Exception e) {}
				
		 adapter = new SocialNetworkFriendsListAdapter(SocialNetworkFriendsList.this,SocialNetworkFriendsList.this, SocialNetworkFriendsList.this);
		 followList.setAdapter(adapter);
			
		 followList.setOnScrollListener(new OnScrollListener() {
			 
			 public void onScrollStateChanged(AbsListView view, int scrollState) {
				 
			 }
				
			 public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
						
				 if(isLoadingOver)
					 return;
				 int lastInScreen = firstVisibleItem + visibleItemCount;
				 if((lastInScreen == totalItemCount) && !(loadingMore) && (jsonArrayLength >= 20))
					 
					 if(CheckNetworkConnection.isNetworkAvailable(SocialNetworkFriendsList.this)) {
						 new PopulateList().execute();
					 }
					 else {
						 NetworkConnectionErrorMessage.showNetworkError(SocialNetworkFriendsList.this, SocialNetworkFriendsList.this).show();
					 }
						
				}
			});
			
			followList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					try{
						isUpdate = true;
						Intent i = new Intent(SocialNetworkFriendsList.this,FriendsProfile.class);
						i.putExtra("user_id", followDataList.get(position).id);
						startActivity(i);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}
			});
			
			jsonArrayLength = 0;
			
			if(CheckNetworkConnection.isNetworkAvailable(SocialNetworkFriendsList.this)) {
				new PopulateList().execute();
			}
			else {
				NetworkConnectionErrorMessage.showNetworkError(SocialNetworkFriendsList.this, SocialNetworkFriendsList.this).show();
			}
			
			back.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					
					SocialNetworkFriendsList.this.finish();
					
				}
			});
			
		}
		
		@Override
		public void onStop() {
			super.onStop();
			
			followFinalList = adapter.getFinalFollowList();
			int sizeOfList = followInitialList.size();
			for(int i=0; i<sizeOfList; i++) {
				
				if(!followInitialList.get(i).equals(followFinalList.get(i))) {
					targetID = followDataList.get(i).id;
					if(followFinalList.get(i)) {
						new FollowUser().execute(targetID);
					}
					else {
						new UnfollowUser().execute(targetID);
					}
				}
			}
			
		}
		
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			super.onWindowFocusChanged(hasFocus);
			if(hasFocus && !loadingMore && isUpdate) {
				pageCount = 1;
				followDataList = new ArrayList<User>();
				followInitialList = new ArrayList<Boolean>();
				followFinalList = new ArrayList<Boolean>();
				isLoadingOver = false;
				
				if(CheckNetworkConnection.isNetworkAvailable(SocialNetworkFriendsList.this)) {
					new PopulateList().execute();
				}
				else {
					NetworkConnectionErrorMessage.showNetworkError(SocialNetworkFriendsList.this, SocialNetworkFriendsList.this).show();
				}
				
				isUpdate = false;
			}
		}
		
		private class PopulateList extends AsyncTask<Void, Void, Void> {

			JSONObject obj;
			
			@Override
			protected void onPreExecute() {
				progressBar.setVisibility(ProgressBar.VISIBLE);
				loadingMore = true;
				footerflag++;
				if(footerflag == 2)
					footerView.setVisibility(View.VISIBLE);
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				
				try {
					HttpParams httpParameters = new BasicHttpParams();
			    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
			    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
					
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(ConstantURIs.LOOK_UP_FRIENDS);
					post.setParams(httpParameters);
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("auth_token", auth_token));
					nameValuePairs.add(new BasicNameValuePair("type", type));
					
					if(type.equals("1")) {
						nameValuePairs.add(new BasicNameValuePair("facebook_id", app_id));
						nameValuePairs.add(new BasicNameValuePair("access_token", key));
					}
					else if(type.equals("2")) {
						nameValuePairs.add(new BasicNameValuePair("twitter_id", app_id));
					}
					else if(type.equals("3")) {
						nameValuePairs.add(new BasicNameValuePair("foursquare_id", app_id));
						nameValuePairs.add(new BasicNameValuePair("oauth_token", key));
					}
					
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(post);
					HttpEntity entity = response.getEntity();
					String resultString = EntityUtils.toString(entity);
					obj = new JSONObject(resultString);
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				try {
					
					//do things here
					JSONArray array = obj.getJSONArray("results");
					int len = array.length();
					jsonArrayLength = len;
					for(int i =0; i<len ;i++){
//						String id= "";
						JSONObject user = array.getJSONObject(i);
						
						User userDetails = new User(
								user.getString("user_id"),
								user.getString("name"), 
								user.getString("profile_image"),
								user.getString("follow_status"),
								"",
								"");
						
						followInitialList.add(Boolean.parseBoolean(user.getString("follow_status")));
						followFinalList.add(Boolean.parseBoolean(user.getString("follow_status")));
						followDataList.add(userDetails);
					}
					if(len < 20){
						footerProgress.setVisibility(ProgressBar.INVISIBLE);
						followList.removeFooterView(footerView);
						followList.addFooterView(headerView);
						isLoadingOver = true;
					}
					
					adapter.setItems(followDataList, followFinalList);
					
					if(followDataList.isEmpty()){
						footerProgress.setVisibility(ProgressBar.INVISIBLE);
						followList.removeFooterView(footerView);
						followList.addFooterView(headerView);
					}
					adapter.notifyDataSetChanged();
				
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				loadingMore = false;
				pageCount++;
				progressBar.setVisibility(ProgressBar.INVISIBLE);
			}
		}
		
		public class FollowUser extends AsyncTask<String, Void, Void> {

			@Override
			protected Void doInBackground(String... values) {
				
				String profileID = values[0];
				
				try{
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(ConstantURIs.FOLLOW_USER);
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("target_id", profileID));
					nameValuePairs.add(new BasicNameValuePair("auth_token", auth_token));
					
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					HttpResponse response = client.execute(post);
					HttpEntity entity = response.getEntity();
					EntityUtils.toString(entity);
					
				}catch(Exception e){
					e.printStackTrace();
				}
				return null;
			}
			
		}
		
		public class UnfollowUser extends AsyncTask<String, Void, Void> {

			@Override
			protected Void doInBackground(String... values) {
				
				String profileID = values[0];
				
				try{
					URL url = new URL(ConstantURIs.FOLLOW_USER+"1?auth_token="+auth_token+"&target_id="+profileID);
					HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
					httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
					httpCon.setRequestMethod("DELETE");
					httpCon.getResponseCode();
				}catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
		}
}
