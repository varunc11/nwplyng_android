package com.mindhelix.nwplyng;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.fsq.FoursquareSession;

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
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;
import com.mindhelix.twitter.TwitterSession;

public class UserSettings extends PreferenceActivity {
	
	Preference serviceTerms, privacyPolicy, logout;
	CheckBoxPreference  career, following, record, manager, ousted;
	
	String auth_token = "";
	
	List<NameValuePair> nameValuePairs;
	
	SharedPreferences preferences;
	
	boolean careerFlag = true, 
			followingFlag = true, 
			recordFlag = true, 
			managerFlag = true, 
			oustedFlag = true;
	
	int careerInt = 0,
		followingInt = 0,
		recordInt = 0,
		managerInt = 0,
		oustedInt = 0;
	
	String nameTobePassed = "";
	int valueTobePassed = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_settings);
		
		preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		auth_token = preferences.getString("auth_token", "");
		
		serviceTerms = (Preference)findPreference("terms_of_service");
		privacyPolicy = (Preference)findPreference("privacy_policy");
		logout = (Preference)findPreference("logout_app");
		
		career = (CheckBoxPreference)findPreference("career_check");
		following = (CheckBoxPreference)findPreference("following_check");
		record = (CheckBoxPreference)findPreference("record_check");
		manager = (CheckBoxPreference)findPreference("manager_check");
		ousted = (CheckBoxPreference)findPreference("ousted_check");
		
		new RetrivePreferenceFromServer().execute();
		
		career.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				careerFlag = preferences.getBoolean("career_check", false);
				
				if(careerFlag)
					careerInt = 1;
				else
					careerInt = 0;
				
				nameTobePassed = "career_progression_email";
				valueTobePassed = careerInt;
				
				new SavePreferencesToServer().execute();
				
				return false;
			}
		});
		
		following.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				followingFlag = preferences.getBoolean("following_check", false);
				
				if(followingFlag)
					followingInt = 1;
				else
					followingInt = 0;
				
				nameTobePassed = "following_email";
				valueTobePassed = followingInt;
				
				new SavePreferencesToServer().execute();
				
				return false;
			}
		});
		
		record.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {

				recordFlag = preferences.getBoolean("record_check", false);
				
				if(recordFlag)
					recordInt = 1;
				else
					recordInt = 0;
				
				nameTobePassed = "record_unlock_email";
				valueTobePassed = recordInt;
				
				new SavePreferencesToServer().execute();
				
				return false;
			}
		});
		
		manager.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				managerFlag = preferences.getBoolean("manager_check", false);
				
				if(managerFlag)
					managerInt = 1;
				else
					managerInt = 0;
				
				nameTobePassed = "manager_email";
				valueTobePassed = managerInt;
				
				new SavePreferencesToServer().execute();
				
				return false;
			}
		});
		
		ousted.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				oustedFlag = preferences.getBoolean("ousted_check", false);
				
				if(oustedFlag)
					oustedInt = 1;
				else
					oustedInt = 0;
				
				nameTobePassed = "manager_ousted_email";
				valueTobePassed = oustedInt;
				
				new SavePreferencesToServer().execute();
				
				return false;
			}
		});
		
		
		serviceTerms.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				Intent intent = new Intent(UserSettings.this, TermsNPrivacy.class);
				intent.putExtra("flag", "terms");
				startActivity(intent);
				
				return false;
			}
		});
		
		privacyPolicy.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				Intent intent = new Intent(UserSettings.this, TermsNPrivacy.class);
				intent.putExtra("flag", "policy");
				startActivity(intent);
				
				return false;
			}
		});
		
		logout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(UserSettings.this);
				builder.setMessage("Are you sure you want to Logout?")
				       .setCancelable(true)
				       .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               
				        	    SharedPreferences pref = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
				                SharedPreferences.Editor editor = pref.edit();
				                editor.clear();
				                editor.remove("Profile_JSON_Array");
				                editor.commit();
				        	   
				                SessionStore.clear(UserSettings.this);
				                Facebook mFacebook = new Facebook(Login.APP_ID);
				                
				                try {
									mFacebook.logout(UserSettings.this);
								} catch (Exception e) {
									e.printStackTrace();
								}
				                
				                TwitterSession tsession = new TwitterSession(UserSettings.this);
				                tsession.resetAccessToken();
				                
				                FoursquareSession fsession = new FoursquareSession(UserSettings.this);
				                fsession.resetAccessToken();
				        	   
				                Intent intent = new Intent(getApplicationContext(), Home.class);
				                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				                intent.putExtra("EXIT", true);
				                startActivity(intent);
				           }
				       })
				       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				
				
				return false;
			}
		});
		
		
	}
	
	
	//get the user settings from server and assign to checkboxes
		public class RetrivePreferenceFromServer extends AsyncTask<Void, Void, Void> {
			
			String responseString = "";
			
			@Override
			protected void onPreExecute() {
				
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					HttpClient client = new DefaultHttpClient();
					String completeURL = ConstantURIs.USER_PREF_DUMMY + "?auth_token=" + auth_token;
					HttpGet getRequest = new HttpGet(completeURL);
					HttpResponse response = client.execute(getRequest);
					HttpEntity entity = response.getEntity();
					responseString = EntityUtils.toString(entity);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				try {
					
					JSONObject mainObject = new JSONObject(responseString);
					careerFlag = mainObject.getBoolean("career_progression_email");
					followingFlag = mainObject.getBoolean("following_email");
					managerFlag = mainObject.getBoolean("manager_email");
					oustedFlag = mainObject.getBoolean("manager_ousted_email");
					recordFlag = mainObject.getBoolean("record_unlock_email");
					
					career.setChecked(careerFlag);
					following.setChecked(followingFlag);
					manager.setChecked(managerFlag);
					ousted.setChecked(oustedFlag);
					record.setChecked(recordFlag);
					
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		//save the user preferences to the server
		public class SavePreferencesToServer extends AsyncTask<Void, Void, Void> {
			
			String responseString = "";
			
			@Override
			protected void onPreExecute() {

			}

			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(ConstantURIs.USER_PREFERENCE);
					
					nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("auth_token", auth_token));
					nameValuePairs.add(new BasicNameValuePair(nameTobePassed, ""+valueTobePassed));

			        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        
			        HttpResponse response = client.execute(post);
					HttpEntity entity = response.getEntity();
					responseString = EntityUtils.toString(entity);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				
				nameValuePairs.clear();
				
			}
			
		}
	
}
