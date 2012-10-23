package com.mindhelix.nwplyng;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Profile extends Activity{
	
	TextView nameView;
	ImageView profileImage;
	TextView careerProgression;
	TextView trackName;
	TextView artistName;
	
	TextView lpStaticText;
	
	private RelativeLayout playsLayout;
	private RelativeLayout followersLayout;
	private RelativeLayout followingLayout;
	private RelativeLayout recordsLayout;
	private RelativeLayout manageslayout;
	private RelativeLayout lastPlayedLayout;
	private RelativeLayout careerLayout;
	private RelativeLayout settingsButton;
	private RelativeLayout addFriendButton;
	
	private String title,comment,itunes_preview_url,itunes_download_url,duration;
	private String track_name,artist_name,youtube_id,snapshot_filename,user_name;
	private String created_at,place,thumbnail_url,profile_image_url,career_progression;
	private String user_id,full_name, video_name;
	
	YouTubeData lpData;
	int manages = 0;
	
	SharedPreferences preferences;
	public String auth_token = ""; 
	
	boolean isImageLoaded = false;
	
	private static long DELAY = 0;
	private static long PERIOD = 1 * 10000;
	Timer timer = new Timer();
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.profile_layout);
	      
	      preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
	      auth_token = preferences.getString("auth_token","");
	      
	      nameView = (TextView)findViewById(R.id.profileName);
	      profileImage = (ImageView)findViewById(R.id.profileImage);
	      careerProgression = (TextView)findViewById(R.id.career);
	      trackName = (TextView)findViewById(R.id.lastPlayedSong);
	      artistName = (TextView)findViewById(R.id.lastPlayedArtist);
	      lpStaticText = (TextView)findViewById(R.id.lpStatic);
	      
	      playsLayout = (RelativeLayout)findViewById(R.id.playsLayout);
	      followersLayout =(RelativeLayout)findViewById(R.id.followersLayout);
	      followingLayout =(RelativeLayout)findViewById(R.id.followingLayout);
	      recordsLayout = (RelativeLayout)findViewById(R.id.recordsLayout);
	      manageslayout = (RelativeLayout)findViewById(R.id.managesLayout);
	      careerLayout = (RelativeLayout)findViewById(R.id.careerLayout);
	      lastPlayedLayout =(RelativeLayout)findViewById(R.id.lastPlayedLayout);
	      settingsButton = (RelativeLayout)findViewById(R.id.settingsButton);
	      addFriendButton = (RelativeLayout)findViewById(R.id.addFriendButton);
	      
	      this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	      
	      if(!preferences.getString("Profile_JSON_Array", "").equals(""))
	    	  setValuesToFields(preferences.getString("Profile_JSON_Array", ""));
	      
	      timer.scheduleAtFixedRate(new TimerTask() {
				
				@Override
				public void run() {
					
					 if(CheckNetworkConnection.isNetworkAvailable(Profile.this)) {
				    	  new RetriveProfileInfoFromServer().execute();
				      }
					
				}
			}, DELAY, PERIOD);
	      
	      settingsButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent intent = new Intent(Profile.this, UserSettings.class);
				startActivity(intent);
			}
	      });
	      
	      addFriendButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					Intent intent = new Intent(Profile.this, AddFriendHigher.class);
//					startActivity(intent);
//				}
//				else {
//					Intent intent = new Intent(Profile.this, AddFriend.class);
//					startActivity(intent);
//				}
				
				Intent intent = new Intent(Profile.this, AddFriendsAdvanced.class);
				intent.putExtra("username", user_name);
				startActivity(intent);
				
			}
	      });
	      
	      lastPlayedLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if(trackName.getText().toString().length() == 0 || trackName.getText().toString().equals("-")){
					return;
				}
				
				Intent intent = new Intent(Profile.this, FeedDescription.class);
				
				intent.putExtra("title",title);
				intent.putExtra("comment", comment);
				intent.putExtra("itunes_preview_url", itunes_preview_url);
				intent.putExtra("itunes_download_url", itunes_download_url);
				intent.putExtra("track_name", track_name);
				intent.putExtra("artist_name",artist_name);
				intent.putExtra("youtube_id", youtube_id);
				intent.putExtra("snapshot_filename", snapshot_filename);
				intent.putExtra("user_name", user_name);
				intent.putExtra("created_at", created_at);
				intent.putExtra("place", place);
				intent.putExtra("thumbnail_url", thumbnail_url);
				intent.putExtra("profile_image_url", preferences.getString("profile_image_url", "null"));
				intent.putExtra("career_progression", career_progression);
				intent.putExtra("user_id", user_id);
				intent.putExtra("unique_id", " ");
				intent.putExtra("feed_id", " ");
				intent.putExtra("full_name", full_name);
				intent.putExtra("upload_time", lpData.uploadedTime);
				intent.putExtra("duration", lpData.duration);
				intent.putExtra("last_played", "true");
				intent.putExtra("video_name", video_name);
				
				startActivity(intent);
			}
	      });
	      
	      playsLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Profile.this,PlaysListActivity.class);
				i.putExtra("from", "user");
				startActivity(i);
			}
		});
	      
	      followersLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(Profile.this,FollowListActivity.class);
					i.putExtra("from", "followers");
					startActivity(i);
				}
			});
	      
	      followingLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(Profile.this,FollowListActivity.class);
					i.putExtra("from", "following");
					startActivity(i);
				}
			});
	      
	      recordsLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(Profile.this, UserBadges.class);
				startActivity(intent);
				
			}
	      });
	      
	      manageslayout.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					
					Intent i = new Intent(Profile.this, ManagesActivity.class);
					i.putExtra("user_id", "");
					startActivity(i);
				}
			});
	      
	      careerLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Profile.this, CareerProgression.class);
				intent.putExtra("user_id", "");
				startActivity(intent);
				
			}
		});
	      
	}
	 
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			if(CheckNetworkConnection.isNetworkAvailable(Profile.this)) {
		    	  new RetriveProfileInfoFromServer().execute();
		      }
		}
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.profile_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		
		switch (item.getItemId()) {
		case R.id.settings:
			intent = new Intent(Profile.this, UserSettings.class);
			startActivity(intent);
			break;
		
		case R.id.add_friend:
			intent = new Intent(Profile.this, AddFriend.class);
			startActivity(intent);
			break;
		
		default:
			break;
		}
		return true;
		
	}
	
	public class RetriveProfileInfoFromServer extends AsyncTask<Void, Void, Void> {
		
		JSONArray array;
		
		@Override
		protected void onPreExecute() {
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			try {
				array = JSONfunctions.getJSONArrayfromURL(ConstantURIs.GET_USER_PROFILE_DATA+"?auth_token="+auth_token);
			
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("Profile_JSON_Array", array.toString());
				editor.commit();
			} catch(Exception e) {}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
//			//process array 
			setValuesToFields(array.toString());
			
		}
		 
	 }
	
	public void setValuesToFields(String string) {
		try {
			
			JSONArray array = new JSONArray(string);
			JSONObject obj = array.getJSONObject(0);
			
			//set user details
			
			profile_image_url = obj.getString("profile_image");
			
			if(!nameView.getText().toString().equals(obj.getString("name"))) {
				user_name = obj.getString("name");
				nameView.setText(user_name);
			}
			
			if((!Profile.this.careerProgression.getText().toString().equals(obj.getString("career_progression")))
						||(!Profile.this.careerProgression.getText().toString().equals("-"))) {
				career_progression = obj.getString("career_progression");
				
				if(career_progression.equals("null")) {
					Profile.this.careerProgression.setText("-");
				}
				else {
					Profile.this.careerProgression.setText(career_progression);
				}
			}
			
			//load previous user profile image
			ImageLoader img = new ImageLoader(Profile.this);
//			if(!preferences.getString("profile_image_url", "null").equals("null")){
				if(!isImageLoaded){
					img.DisplayImage(profile_image_url, profileImage);
					isImageLoaded = true;
				}
				
//			}
//			else {
				img.DisplayImage(profile_image_url, profileImage);
				isImageLoaded = true;
//			}
			
			//if profile image changed, then load new one
			if(!preferences.getString("profile_image_url", "null").equals(profile_image_url)) {
				profile_image_url = obj.getString("profile_image");
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("profile_image_url", profile_image_url);
				editor.commit();
				
				if(!profile_image_url.equals("null")){
					if(!isImageLoaded){
						img.DisplayImage(profile_image_url, profileImage);
						isImageLoaded = true;
					}
					
				}
			}
			
			if(!((TextView)findViewById(R.id.fullName)).getText().toString().equals(obj.getString("fullname"))) {
				full_name = obj.getString("fullname");
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("current user fullname", full_name);
				editor.commit();
				
				if(full_name.equals("null"))
					full_name = "";
				((TextView)findViewById(R.id.fullName)).setText(full_name);
			}
			
			//set last played song details
			JSONObject lastPlayed = obj.getJSONObject("last_played");
			title = lastPlayed.getString("title");
			comment = lastPlayed.getString("comment");
			itunes_preview_url = lastPlayed.getString("previewUrl");
			itunes_download_url =lastPlayed.getString("itunes_download_url");
			track_name = lastPlayed.getString("trackName");
			artist_name = lastPlayed.getString("artistName");
			duration = lastPlayed.getString("youtube_video_duration");
			
			
			if((!Profile.this.trackName.getText().toString().equals(obj.getString("career_progression")))
					||(!Profile.this.trackName.getText().toString().equals("-"))) {
				if(track_name.equals("null")) {
					Profile.this.trackName.setText("");
					lpStaticText.setText("lp: -");
					Profile.this.artistName.setText("");
				}else{
					lpStaticText.setText("lp:");
					Profile.this.trackName.setText(track_name);
					if((!Profile.this.artistName.getText().toString().equals(obj.getString("career_progression")))
							||(!Profile.this.artistName.getText().toString().equals(""))) {
						if(artist_name.equals("null")) {
							Profile.this.artistName.setText("");
						}
						else {
							Profile.this.artistName.setText(artist_name);
						}
					}
					
				}
			}
			
			video_name =  lastPlayed.getString("video_title");
			youtube_id = lastPlayed.getString("youtube_id");
			snapshot_filename = lastPlayed.getString("snapshot_file_name");
			created_at =lastPlayed.getString("created_at");
			lpData = new YouTubeData(title, snapshot_filename, "", youtube_id, created_at, duration);
			try{
			place = lastPlayed.getJSONObject("place").getString("title");
			}catch(Exception e){
				place ="";
			}
			thumbnail_url = lastPlayed.getString("thumbnailUrl");
			
			
			user_id = obj.getString("user_id");
			
			
			//set profile records
			String plays = obj.getString("plays");
			String manages = obj.getString("manages");
			String records = obj.getString("records");
			
			try{
				Profile.this.manages = Integer.parseInt(manages);
			} catch(Exception e){
				
			}
			
			if(!((TextView)findViewById(R.id.numberOfPlays)).getText().toString().equals(plays))
				((TextView)findViewById(R.id.numberOfPlays)).setText(plays);
			
			if(!((TextView)findViewById(R.id.numberOfManages)).getText().toString().equals(manages))
				((TextView)findViewById(R.id.numberOfManages)).setText(manages);
			
			if(!((TextView)findViewById(R.id.numberOfRecords)).getText().toString().equals(records))
				((TextView)findViewById(R.id.numberOfRecords)).setText(records);
			
			//set following and followers details
			String followers = obj.getString("followers");
			String following = obj.getString("following");
			
			if(!((TextView)findViewById(R.id.numberOfFollowers)).getText().toString().equals(followers))
				((TextView)findViewById(R.id.numberOfFollowers)).setText(followers);
			
			if(!((TextView)findViewById(R.id.numberOfFollowing)).getText().toString().equals(following))
				((TextView)findViewById(R.id.numberOfFollowing)).setText(following);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
