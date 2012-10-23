package com.mindhelix.nwplyng;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class Notifications extends Activity {
	
	public NotificationListAdapter notificationAdapter;
	public List<NotificationData> notifications = new ArrayList<NotificationData>();
	
	private ListView notificationListView;
	Button back;
	
	ProgressBar progressBar;
	
	View headerView;
	
	SharedPreferences preferences;
	
	String since_id;

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.notification_list);
	        
	        notificationListView = (ListView)findViewById(R.id.notification_list);
	        back = (Button)findViewById(R.id.backButtonNotifications);
	        preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
	        
	        since_id = "0";
	        
	        progressBar = (ProgressBar)findViewById(R.id.progressBarNotifications);
	        progressBar.setVisibility(ProgressBar.INVISIBLE);
	        
	        headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_header, null, false);
			headerView.setMinimumHeight(10);
			notificationListView.addHeaderView(headerView);
			notificationListView.addFooterView(headerView);
	        
	        new RetriveNotificationsFromServer().execute();
	        
	        if(notificationAdapter == null) {
	        	notificationAdapter = new NotificationListAdapter(Notifications.this, Notifications.this);
	        }
	        
	        if(notifications != null)
	        	notificationAdapter.setItems(notifications);
	        
	        notificationListView.setAdapter(notificationAdapter);
	        
	        back.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Notifications.this.finish();
				}
			});
	        
	        notificationListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					NotificationData.notificationData = notifications.get(position-1);
					
					Intent i = new Intent(Notifications.this, FriendsProfile.class);
					i.putExtra("user_id", NotificationData.notificationData.action_user_id);
					startActivity(i);	
				}
			});
	        
	 }
	 
	 public class RetriveNotificationsFromServer extends AsyncTask<Void, Void, Void> {
		 
		String resultString = "";
		HttpEntity entity;
		
		String lookupResponse = "", auth_token = "";
		
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			auth_token = preferences.getString("auth_token", "");
	        if(auth_token.equals("")) {
	        	lookupResponse = preferences.getString("lookupresponse", "");
	        	try {
					JSONObject mainObj = new JSONObject(lookupResponse);
					JSONObject innerObj = mainObj.getJSONObject("user");
					auth_token = innerObj.getString("auth_token");
				} 
	        	catch (JSONException e) {
					e.printStackTrace();
				}	
	        }
	        
	        try {
	        	HttpClient client = new DefaultHttpClient();
				String completeURLString = ConstantURIs.GET_NOTIFICATION + "?since_id="+since_id+"&auth_token=" + auth_token;
				HttpGet request = new HttpGet(completeURLString);
				request.setURI(new URI(completeURLString));
				HttpResponse response = client.execute(request);
				entity = response.getEntity();
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
		    	JSONArray array = jobject.getJSONArray("results");
		    	
		    	JSONObject row = array.getJSONObject(0);
		    	
		    	//get the feed id of first array element
		    	since_id = row.getString("feed_id");
		    	
		    	SharedPreferences.Editor editor = preferences.edit();
		    	editor.putString("since_id", since_id);
		    	editor.commit();
		    	
		    	for(int i=0 ; i<array.length() ; i++)
				{
					row = array.getJSONObject(i);
					
					NotificationData data = new NotificationData(
							row.getString("message"), 
							row.getString("created_at"), 
							row.getString("status"), 
							row.getString("type"),
							row.getString("action_user_id"),
							row.getString("profile_image_url"),
							row.getString("feed_id"));
					
					notifications.add(data);
				}
		    	
		    	notificationAdapter.setItems(notifications);
		    	notificationAdapter.notifyDataSetChanged();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
	 
	 }
	
}
