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
import org.apache.http.client.methods.HttpGet;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddFriend extends Activity {
	
	ListView followList;
	AddFriendAdapter adapter;
	
	Button back;
	TextView headline;
	EditText friendSearchInput;
	
	View footerView, headerView;
	
	ProgressBar progressBar;
	ProgressBar footerProgress;
	public boolean loadingMore = true;
	
	public int pageCount = 1;
	public int jsonArrayLength = 1;
	public int footerflag = 0;
	
	String auth_token = "";
	String targetID;
	String name = "";
	
	SharedPreferences preferences;
	public List<User>  followDataList;
	public List<Boolean> followInitialList;
	public List<Boolean> followFinalList;
	
	public boolean isLoadingOver = false;
	public boolean isUpdate = false;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.add_friend);
	     
	     preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
	     auth_token = preferences.getString("auth_token", "");
	     back = (Button)findViewById(R.id.addFriendBackButton);
	     followList = (ListView)findViewById(R.id.friendSearchList);
	     progressBar = (ProgressBar)findViewById(R.id.activityIndicator_add_friend);
	     friendSearchInput = (EditText)findViewById(R.id.friendSearchInputBox);
	     
	     progressBar.setVisibility(ProgressBar.INVISIBLE);
	     friendSearchInput.setInputType(friendSearchInput.getInputType()
	    		    | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
	    		    | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
	     
	     followDataList = new ArrayList<User>();
		 followInitialList = new ArrayList<Boolean>();
		 followFinalList = new ArrayList<Boolean>();
			
		 headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_header, null, false);
		 headerView.setMinimumHeight(10);
		 
		 footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_list_footer, null, false);
		 footerView.setMinimumHeight(110);
		 footerProgress = (ProgressBar)footerView.findViewById(R.id.progress);
		 
		 footerflag = 0;
		 footerView.setVisibility(View.INVISIBLE);
		 followList.addFooterView(footerView);
		 try {
			 followList.removeFooterView(headerView);
	     }catch (Exception e) {}
			
		 adapter = new AddFriendAdapter(AddFriend.this,AddFriend.this,AddFriend.this);
		 followList.setAdapter(adapter);
		 
		 friendSearchInput.setOnEditorActionListener(new OnEditorActionListener() {
				
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						
					    footerProgress.setVisibility(ProgressBar.VISIBLE); 
						
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			            imm.hideSoftInputFromWindow(friendSearchInput.getWindowToken(), 0);
			            
						name = friendSearchInput.getText().toString();
						pageCount = 1;
						
						followDataList = new ArrayList<User>();
						followInitialList = new ArrayList<Boolean>();
						followFinalList = new ArrayList<Boolean>();
						
						friendSearchInput.clearFocus();
						isLoadingOver = false;
						jsonArrayLength = 0;
											
						if(CheckNetworkConnection.isNetworkAvailable(AddFriend.this)) {
							new PopulateList().execute();
						}
						else {
							NetworkConnectionErrorMessage.showNetworkError(AddFriend.this, AddFriend.this).show();
						}
						return true;
					}
					return false;
				}
			});
		 
		 followList.setOnScrollListener(new OnScrollListener() {
				
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					
				}
				
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					
					if(isLoadingOver)
						return;
					int lastInScreen = firstVisibleItem + visibleItemCount;
					if((lastInScreen == totalItemCount) && !(loadingMore) && (jsonArrayLength >= 20))
						
						if(CheckNetworkConnection.isNetworkAvailable(AddFriend.this)) {
							new PopulateList().execute();
						}
						else {
							NetworkConnectionErrorMessage.showNetworkError(AddFriend.this, AddFriend.this).show();
						}
						
				}
		 });
		 
		 followList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View view, int position,
						long arg3) {
					try{
						isUpdate = true;
						Intent i = new Intent(AddFriend.this,FriendsProfile.class);
						i.putExtra("user_id", followDataList.get(position).id);
						startActivity(i);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}
		 });
		 
		 back.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					
					AddFriend.this.finish();
					
				}
		 });
	}
	
	@Override
	public void onStop() {
		super.onStop();
		try {
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
		}catch (Exception e) {}
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
			
			if(CheckNetworkConnection.isNetworkAvailable(AddFriend.this)) {
				new PopulateList().execute();
			}
			else {
				NetworkConnectionErrorMessage.showNetworkError(AddFriend.this, AddFriend.this).show();
			}
			
			isUpdate = false;
		}
	}
	
	private class PopulateList extends AsyncTask<Void, Void, Void> {

		String resultString = "";
		int len = 0;
		 
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
				String competeUri = ConstantURIs.SEARCH_USERS + "?rpp=20&page="+pageCount+"&name="+name+"&auth_token="+auth_token; 
				HttpGet getRequest = new HttpGet(competeUri);
				getRequest.setParams(httpParameters);
				HttpResponse response = client.execute(getRequest);
				HttpEntity entity = response.getEntity();
				resultString = EntityUtils.toString(entity);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			try {
				
				JSONObject jobject = new JSONObject(resultString);
				JSONArray array = null;
				
				if(jobject.length() != 0)
				{
					array = jobject.getJSONArray("results");
					
					if(array.length() == 0)
			    	{
			    		footerProgress.setVisibility(ProgressBar.INVISIBLE);
			    	}
			    	
			    	len = array.length();
			    	jsonArrayLength = len;
			    	
			    	for(int i=0 ; i<len ; i++)
					{
						JSONObject row = array.getJSONObject(i);
						
						User user = new User(
								row.getString("user_id"), 
								row.getString("name"), 
								row.getString("profile_image"), 
								row.getString("follow_status"), 
								"",
								row.getString("full_name"));
						
						followInitialList.add(Boolean.parseBoolean(row.getString("follow_status")));
						followFinalList.add(Boolean.parseBoolean(row.getString("follow_status")));
						followDataList.add(user);
					}
				}
				
				//do here
				if(len < 20) {
					footerProgress.setVisibility(ProgressBar.INVISIBLE);
					try {
						followList.removeFooterView(footerView);
					}catch(Exception e) {}
					followList.addFooterView(headerView);
					isLoadingOver = true;
				}
				
				adapter.setItems(followDataList, followFinalList);
				
				if(followDataList.isEmpty()){
					footerProgress.setVisibility(ProgressBar.INVISIBLE);
					try {
						followList.removeFooterView(footerView);
					}catch(Exception e) {}
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
