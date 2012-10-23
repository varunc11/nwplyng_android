package com.mindhelix.nwplyng;

import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FoursquareSession;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;
import com.mindhelix.twitter.TwitterApp;
import com.mindhelix.twitter.TwitterSession;

public class AddFriendsAdvanced extends Activity {
	
	Button back;
	TextView heading;
	
	RelativeLayout facebookLayout;
	RelativeLayout twitterLayout;
	RelativeLayout foursquareLayout;
	RelativeLayout searchLayout;
	
	Facebook fb;
	TwitterApp twitter;
	FoursquareApp fsqApp;
	
	ProgressDialog progressDialog;
	SharedPreferences preferences;
	
	String auth_token = "";
	public static final int DIALOG_CONNECT = 1;
	boolean isCalled = false;
	
	AlertDialog.Builder usedAccountAlertBuilder;
	AlertDialog usedAccountAlert;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.add_friend_select);
	     
	     Bundle extras = this.getIntent().getExtras();
	     String username = extras.getString("username");
	     
	     preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		 auth_token = preferences.getString("auth_token", "");
	     
	     back = (Button)findViewById(R.id.addFriendAdvancedBackButton);
	     facebookLayout = (RelativeLayout)findViewById(R.id.facebook_select_layout);
	     twitterLayout = (RelativeLayout)findViewById(R.id.twitter_select_layout);
	     foursquareLayout = (RelativeLayout)findViewById(R.id.foursquare_select_layout);
	     searchLayout = (RelativeLayout)findViewById(R.id.users_select_layout);
	     heading = (TextView)findViewById(R.id.add_friend_select_heading);
	     
	     heading.setText(username);
	     
	     
	     fb = new Facebook(Login.APP_ID);
	     SessionStore.restore(fb, this);
			
	     if (fb.isSessionValid()) {
	    	String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;
	     }
			
		 twitter = new TwitterApp(AddFriendsAdvanced.this, Login.CONSUMER_KEY, Login.CONSUMER_SECRET);
		 fsqApp = new FoursquareApp(AddFriendsAdvanced.this, Login.CLIENT_ID, Login.CLIENT_SECRET);
		 
		 LayoutInflater inflater = getLayoutInflater();
		 View customTitle = inflater.inflate(R.layout.nwpyng_dialog_title, null);
	     
		 usedAccountAlertBuilder = new AlertDialog.Builder(AddFriendsAdvanced.this);
		 usedAccountAlertBuilder.setCustomTitle(customTitle)
				   				.setMessage("Account mis-match. Please try with another account.")
				   				.setCancelable(false)
				   				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				   					public void onClick(DialogInterface dialog, int id) {
				   						dialog.dismiss();
				   					}
				   				});
			
		 usedAccountAlert = usedAccountAlertBuilder.create();
	     
	     facebookLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(SessionStore.restore(fb, AddFriendsAdvanced.this)){
					String facebook_id = preferences.getString("facebook_user_id", "null");
					Intent intent = new Intent(AddFriendsAdvanced.this, SocialNetworkFriendsList.class);
					intent.putExtra("type", "1");
					intent.putExtra("facebook_id", facebook_id);
					intent.putExtra("access_token", fb.getAccessToken());
					startActivity(intent);
					
				}
				else{
					fb.authorize(AddFriendsAdvanced.this, Login.PERMISSIONS, 1, new FbLoginDialogListener()); //-1
				}
				
			}
	     });
	     
	     twitterLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if (twitter.hasAccessToken() == true) {
					
					int twitter_id = 0;
					twitter_id = Integer.parseInt(preferences.getString("twitter_user_id", "0"));
					try {
						twitter_id = twitter.mTwitter.getId();
						if(twitter_id != 0) {
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString("twitter_user_id", ""+twitter_id);
							editor.commit();
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					Intent intent = new Intent(AddFriendsAdvanced.this, SocialNetworkFriendsList.class);
					intent.putExtra("type", "2");
					intent.putExtra("twitter_id", ""+twitter_id);
					startActivity(intent);
					
				} 
				else {
					twitter.setListener(new TwitterApp.TwDialogListener() {
						public void onError(String value) {

						}

						public void onComplete(String value) {
							new checkValidTwitterUser().execute();
						}
					});
					twitter.authorize();
				}
				
			}
	     });
	     
	     foursquareLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(fsqApp.hasAccessToken()){
					FoursquareSession mSession =  new FoursquareSession(AddFriendsAdvanced.this);
					String fsq_id = preferences.getString("forsqure_user_id", "null");
					String a = mSession.getAccessToken();
					Intent intent = new Intent(AddFriendsAdvanced.this, SocialNetworkFriendsList.class);
					intent.putExtra("type", "3");
					intent.putExtra("foursquare_id", fsq_id);
					intent.putExtra("oauth_token", mSession.getAccessToken());
					startActivity(intent);
					
				}
				else{
					fsqApp.setListener(new FoursquareApp.FsqAuthListener() {

						public void onSuccess() {
							if(!isCalled){
								new checkValidFsqUser().execute();
								isCalled = true;
							}
						}

						public void onFail(String error) {

						}
					});
					fsqApp.authorize();
				}
				
			}
	     });
	     
	     searchLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(AddFriendsAdvanced.this, AddFriend.class);
				startActivity(intent);
				
			}
	     });
	     
	     back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AddFriendsAdvanced.this.finish();
			}
		});
	     
	}
	
	private final class FbLoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionStore.save(fb, AddFriendsAdvanced.this);
            new checkValidFacebookUser().execute();
        }
        public void onFacebookError(FacebookError error) {
        	Toast.makeText(AddFriendsAdvanced.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();
        }
        public void onError(DialogError error) {
        	Toast.makeText(AddFriendsAdvanced.this, "Facebook connection failed", Toast.LENGTH_SHORT).show(); 
        }
        public void onCancel() {

        }
    }
	
	class checkValidFacebookUser extends AsyncTask<Void, Void, Void>{
		SharedPreferences sp = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		boolean isValidLogin = false;
		String facebook_id = "";
		
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_CONNECT);
		}

		@Override
		protected Void doInBackground(Void... params) {
			facebook_id = sp.getString("facebook_user_id", "null");
			String fullname = sp.getString("current user fullname", "");
			
			try{
				JSONObject obj = Util.parseJson(fb.request("me"));
				String current_id = obj.getString("id");
				if(current_id.equals(facebook_id)){
					isValidLogin = true;
				}else if(facebook_id.equals("null")){
					HttpService service = new HttpService();
					if(!service.isValidUser(current_id, "facebook_id", fullname)){
						isValidLogin = true;
						//update id 
						service.updateSocialId(auth_token, current_id, "facebook_id");
					}else{
						SessionStore.clear(AddFriendsAdvanced.this);
						fb.logout(AddFriendsAdvanced.this);
					}
				}else{
					SessionStore.clear(AddFriendsAdvanced.this);
					fb.logout(AddFriendsAdvanced.this);
				}
			}catch(Exception e){
				
			} catch (FacebookError e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			dismissDialog(DIALOG_CONNECT);
			
			if(isValidLogin){
				
				Intent intent = new Intent(AddFriendsAdvanced.this, SocialNetworkFriendsList.class);
				intent.putExtra("type", "1");
				intent.putExtra("facebok_id", facebook_id);
				intent.putExtra("access_token", fb.getAccessToken());
				startActivity(intent);
				
			}
			else{
				usedAccountAlert.show();
			}
		}
	}
	
	class checkValidTwitterUser extends AsyncTask<Void, Void, Void> {
		SharedPreferences sp = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		boolean isValidLogin = false;
		String twitter_id = "";
		
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_CONNECT);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			twitter_id = sp.getString("twitter_user_id", "null");
			String fullname = sp.getString("current user fullname", "");
			
			try{
				String current_id = Integer.toString(twitter.mTwitter.getId());
				
				if(current_id.equals(twitter_id)){
					isValidLogin = true;
				}else if(twitter_id.equals("null")){
					HttpService service = new HttpService();
					if(!service.isValidUser(current_id, "twitter_id", fullname)){
						isValidLogin = true;
						//update
						service.updateSocialId(auth_token, current_id, "twitter_id");
					}else{
						TwitterSession session = new TwitterSession(AddFriendsAdvanced.this);
						session.resetAccessToken();
					}
				}else{
					TwitterSession session = new TwitterSession(AddFriendsAdvanced.this);
					session.resetAccessToken();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissDialog(DIALOG_CONNECT);
			if(isValidLogin){
				
				int twitter_id = 0;
				try {
					twitter_id = twitter.mTwitter.getId();
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				Intent intent = new Intent(AddFriendsAdvanced.this, SocialNetworkFriendsList.class);
				intent.putExtra("type", "2");
				intent.putExtra("twitter_id", ""+twitter_id);
				startActivity(intent);
				
			}else{
				usedAccountAlert.show();
				CookieSyncManager.createInstance(AddFriendsAdvanced.this); 
		    	CookieManager cookieManager = CookieManager.getInstance();
		    	cookieManager.removeAllCookie();
				
				twitter = new TwitterApp(AddFriendsAdvanced.this, Login.CONSUMER_KEY, 
						Login.CONSUMER_SECRET);
			}
		}
	}

	class checkValidFsqUser extends AsyncTask<Void, Void, Void>{

		SharedPreferences sp = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		boolean isValidLogin = false;
		boolean isException = false;
		String fsq_id = "";
	
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_CONNECT);
		}
	
		@Override
		protected Void doInBackground(Void... params) {
			try{
				fsq_id = sp.getString("forsqure_user_id", "null");
				String fullname = sp.getString("current user fullname", "");
				
				String current_id = fsqApp.getUserId();
				if(fsq_id.equals(current_id)){
					isValidLogin = true;
				}else if(fsq_id.equals("null")){
					HttpService service = new HttpService();
					if(!service.isValidUser(current_id, "foursquare_id", fullname)){
						isValidLogin = true;
						service.setPhotoUrl(fsqApp.getPhotoUrl());
						service.updateSocialId(auth_token, current_id, "foursquare_id");
					}else{
						FoursquareSession session = new FoursquareSession(AddFriendsAdvanced.this);
						session.resetAccessToken();
					}
				}else{
					FoursquareSession session = new FoursquareSession(AddFriendsAdvanced.this);
					session.resetAccessToken();
					
				}
			}catch(Exception e){
				e.printStackTrace();
				isException = true;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissDialog(DIALOG_CONNECT);
			if(isException){
				return;
			}
			if(isValidLogin){
				FoursquareSession mSession =  new FoursquareSession(AddFriendsAdvanced.this);
				String fsq_id = preferences.getString("forsqure_user_id", "null");
				Intent intent = new Intent(AddFriendsAdvanced.this, SocialNetworkFriendsList.class);
				intent.putExtra("type", "3");
				intent.putExtra("foursquare_id", fsq_id);
				intent.putExtra("oauth_token", mSession.getAccessToken());
				startActivity(intent);
			}
			else{
				isCalled = false;
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				fsqApp = new FoursquareApp(AddFriendsAdvanced.this, Login.CLIENT_ID, Login.CLIENT_SECRET);
				usedAccountAlert.show();
			}
		}
	
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
        {
			case DIALOG_CONNECT:
				progressDialog = new ProgressDialog(AddFriendsAdvanced.this);
				progressDialog.setMessage("Connecting...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();
				return progressDialog;
				
			default:
				return null;
        }
	}
	
}
