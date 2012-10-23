package com.mindhelix.nwplyng;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

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
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class Home extends Activity {

	private TextView notificationText;
	private TabHost mTabHost;
	
	Intent feedIntent, profileIntent, playInputIntent;
	
	private static long DELAY = 0;
	private static long PERIOD = 1 * 60000;
	
	SharedPreferences preferences;
	
    Timer timer = new Timer();
	
	String since_id;
	String count = "0";
	
	boolean feedFlag = false;

	private void setUpIntents(){
		feedIntent = new Intent(Home.this, Feed.class);
		profileIntent = new Intent(Home.this, Profile.class);
		playInputIntent = new Intent(Home.this, PlaySearchInputScreen.class);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Bundle bundle = getIntent().getExtras();
        	boolean exitFlag = bundle.getBoolean("EXIT", false);
        	feedFlag = bundle.getBoolean("Done Button", false);
        	
        	if (exitFlag) {
        		Intent intent = new Intent(Home.this, Login.class);
        		startActivity(intent);
        		finish();
        	}
        	
        }catch (Exception e) {
			e.printStackTrace();
		}
        
        setContentView(R.layout.home);
        
        preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
        
        since_id = preferences.getString("since_id", "0");
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        notificationText = (TextView)findViewById(R.id.notification_number);
        
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				getNotificationNumber();
				
			}
		}, DELAY, PERIOD);
        
	    LocalActivityManager mLocalActivityManager = new LocalActivityManager(Home.this, false);
	    mLocalActivityManager.dispatchCreate(savedInstanceState); 
	    mTabHost.setup(mLocalActivityManager);
	    
		mTabHost.setup();
		
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		
		setUpIntents();
		
		setupTab(new TextView(this), "feed", feedIntent);
		setupTab(new TextView(this),"play", playInputIntent);
		setupTab(new TextView(this), "profile", profileIntent);
		
		if(feedFlag)
			mTabHost.setCurrentTab(0);
		else
			mTabHost.setCurrentTab(1);
		
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId)
            {
            	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            	imm.hideSoftInputFromWindow(mTabHost.getApplicationWindowToken(), 0);
            }
        });
		
		notificationText.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Notifications.class);
				notificationText.setText("");
				notificationText.setBackgroundResource(R.drawable.bub);
				startActivity(intent);
			}
		});
		
    }
    
    private void setupTab(final View view, final String tag, Intent intent) {
		
    	View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		ImageView im = (ImageView) view.findViewById(R.id.tabsImage);
		
		if(text.equals("feed"))
			im.setBackgroundResource(R.drawable.feedhi);
		else if(text.equals("play"))
			im.setBackgroundResource(R.drawable.cbhi);
		else
			im.setBackgroundResource(R.drawable.profilehi);
		
		if(text.equals("play"))
		{
			tv.setEnabled(false);
			tv.setVisibility(TextView.INVISIBLE);
			
		}
		else
		{
			tv.setText(text);
			tv.setVisibility(TextView.VISIBLE);
		}
		
		
		return view;
	}
	
	private void getNotificationNumber() {
		
		String resultString = "";
		HttpEntity entity;
		
		String lookupResponse = "", auth_token = "";
		
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
        	String completeURLString = "";
        	if(since_id.equals("0"))
        		completeURLString = ConstantURIs.GET_NOTIFICATION + "?auth_token=" + auth_token;
        	else
        		completeURLString = ConstantURIs.GET_NOTIFICATION + "?since_id="+since_id+"&auth_token=" + auth_token;
        	
			HttpGet request = new HttpGet(completeURLString);
			request.setURI(new URI(completeURLString));
			HttpResponse response = client.execute(request);
			entity = response.getEntity();
			resultString = EntityUtils.toString(entity);
			
			JSONObject jobject = new JSONObject(resultString);
			count = jobject.getString("count");
			JSONArray array = jobject.getJSONArray("results");
			JSONObject row = array.getJSONObject(0);
			
			//get the feed id of last array element
	    	since_id = row.getString("feed_id");
	    	
	    	SharedPreferences.Editor editor = preferences.edit();
	    	editor.putString("since_id", since_id);
	    	editor.commit();
			
			
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					
					if(!count.equals("0")) {
						notificationText.setText(count);
						notificationText.setBackgroundResource(R.drawable.bubred);
					}
				}
			});
			
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
		
	}
    
}
