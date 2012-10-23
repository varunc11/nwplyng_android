package com.mindhelix.nwplyng;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendsProfile extends Activity{

	TextView nameView;
	ImageView profileImage;
	TextView careerProgression;
	TextView trackName;
	TextView artistName;
	Button followStatusButton;
	Button back;
	TextView headline;
	
	ProgressBar activityIndicator;
	
	String userId = "";
	
	SharedPreferences preferences; 
	
	public String auth_token = "";
	
	private RelativeLayout playsLayout;
	private RelativeLayout followersLayout;
	private RelativeLayout followingLayout;
	private RelativeLayout managesLayout;
	private RelativeLayout lastPlayedLayout;
	private RelativeLayout recordsLayout;
	private RelativeLayout careerLayout;
	
	private String title,comment,itunes_preview_url,itunes_download_url;
	private String track_name,artist_name,youtube_id,snapshot_filename,user_name;
	private String created_at,place,thumbnail_url,profile_image_url,career_progression,duration;
	private String user_id,full_name, video_name;
	
	YouTubeData lpData;
	int manages = 0;
	
	boolean isImageLoaded = false;
	
	private static long DELAY = 0;
	private static long PERIOD = 1 * 5000;
	Timer timer = new Timer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_profile_layout);
		 
		preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
	    auth_token = preferences.getString("auth_token","");
		userId = getIntent().getExtras().getString("user_id");
		
		nameView = (TextView)findViewById(R.id.friendProfileName);
	    profileImage = (ImageView)findViewById(R.id.friendProfileImage);
	    careerProgression = (TextView)findViewById(R.id.friendCareer);
	    trackName = (TextView)findViewById(R.id.friendlastPlayedSong);
	    artistName = (TextView)findViewById(R.id.friendlastPlayedArtist);
	    followStatusButton = (Button)findViewById(R.id.FollowStatusButton);
	    activityIndicator = (ProgressBar)findViewById(R.id.activityIndicator_friendProfile);
	    headline = (TextView)findViewById(R.id.friendProfileHeadline);
	    back = (Button)findViewById(R.id.backButtonFriendProfile);
	    
	    playsLayout = (RelativeLayout)findViewById(R.id.friendPlaysLayout);
	    followersLayout = (RelativeLayout)findViewById(R.id.friendFollowersLayout);
	    followingLayout =(RelativeLayout)findViewById(R.id.friendFollowingLayout);
	    managesLayout = (RelativeLayout)findViewById(R.id.friendManagesLayout);
	    lastPlayedLayout = (RelativeLayout)findViewById(R.id.friendLastPlayedLayout);
	    recordsLayout = (RelativeLayout)findViewById(R.id.friendRecordsLayout);
	    careerLayout = (RelativeLayout)findViewById(R.id.friendCareerLayout);
	    
	    activityIndicator.setVisibility(ProgressBar.VISIBLE);
	    followStatusButton.setEnabled(false);
		followStatusButton.setVisibility(Button.INVISIBLE);
	    
	    lastPlayedLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if(trackName.getText().toString().length()==0||trackName.getText().toString().equals("No plays Recorded")){
					return;
				}
				
				Intent intent = new Intent(FriendsProfile.this, FeedDescription.class);
				
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
				intent.putExtra("profile_image_url", profile_image_url);
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
			
			public void onClick(View arg0) {
				
				Intent i = new Intent(FriendsProfile.this,PlaysListActivity.class);
				i.putExtra("from","friend");
				i.putExtra("user_id",userId);
				startActivity(i);
			}
		});
	    
		recordsLayout.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(FriendsProfile.this, UserBadges.class);
				intent.putExtra("user_id",userId);
				startActivity(intent);

			}
		});	    
	    
	    followStatusButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if(followStatusButton.getTag().equals("Follow")){
					
					new FollowUser().execute();
					
				}else if(followStatusButton.getTag().equals("Unfollow")){
					
					AlertDialog.Builder builder = new AlertDialog.Builder(FriendsProfile.this);
					builder.setMessage("Are you sure you want to Unfollow "+nameView.getText()+"?")
					       .setCancelable(true)
					       .setPositiveButton("Unfollow", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   new UnfollowUser().execute();
					        	   dialog.cancel();
					        	}
					       })
					       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
	    
	    followersLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent i = new Intent(FriendsProfile.this,FollowListActivity.class);
				i.putExtra("from","followers");
				i.putExtra("user_id",userId);
				startActivity(i);
			}
		});
	    
	    followingLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				Intent i = new Intent(FriendsProfile.this,FollowListActivity.class);
				i.putExtra("from","following");
				i.putExtra("user_id",userId);
				startActivity(i);
			}
		});
	    
	   managesLayout.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View arg0) {
			
			Intent i = new Intent(FriendsProfile.this,ManagesActivity.class);
			i.putExtra("user_id",userId);
			startActivity(i);
		}
	   });
	   
	   careerLayout.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
			
			Intent intent = new Intent(FriendsProfile.this, CareerProgression.class);
			intent.putExtra("user_id",userId);
			startActivity(intent);
			
		}
	   });
	   
	   timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				 if(CheckNetworkConnection.isNetworkAvailable(FriendsProfile.this)) {
					 new RetriveProfileInfoFromServer().execute();
			      }
				
			}
		}, DELAY, PERIOD);
	   
	    back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				FriendsProfile.this.finish();
			}
		});
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			if(CheckNetworkConnection.isNetworkAvailable(FriendsProfile.this)) {
		    	  new RetriveProfileInfoFromServer().execute();
		      }
		}
	}
	
	private class UnfollowUser extends AsyncTask<Void, Void, Void>{
		int responseCode=0;
		@Override
		protected void onPreExecute() {
			followStatusButton.setTag("Follow");
			followStatusButton.setBackgroundResource(R.drawable.follow);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try{
				
				HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
				
				URL url = new URL(ConstantURIs.FOLLOW_USER+"1?auth_token="+auth_token+"&target_id="+userId);
				HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
				httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
				httpCon.setRequestMethod("DELETE");
				httpCon.setConnectTimeout(ConstantValues.TIME_OUT_VALUE);
				httpCon.setReadTimeout(ConstantValues.TIME_OUT_VALUE);
				responseCode = httpCon.getResponseCode();
				
				System.out.print("Response ="+responseCode);
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(responseCode == 200){
				if(CheckNetworkConnection.isNetworkAvailable(FriendsProfile.this)) {
					new RetriveProfileInfoFromServer().execute();
				}
			}
			
		}
	}
	
	private class FollowUser extends AsyncTask<Void, Void, Void>{
		
		int code = 0;
		
		@Override
		protected void onPreExecute() {
			followStatusButton.setTag("Unfollow");
			followStatusButton.setBackgroundResource(R.drawable.unfollow);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
				
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(ConstantURIs.FOLLOW_USER);
				post.setParams(httpParameters);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("target_id", userId));
				nameValuePairs.add(new BasicNameValuePair("auth_token", auth_token));
				
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = client.execute(post);
				code = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				EntityUtils.toString(entity);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(code == 200)
				if(CheckNetworkConnection.isNetworkAvailable(FriendsProfile.this)) {
					new RetriveProfileInfoFromServer().execute();
				}
		}
		
	}
	
	 public class RetriveProfileInfoFromServer extends AsyncTask<Void, Void, Void>{
			
			JSONArray array;
			
			@Override
			protected void onPreExecute() {
				
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				
				array = JSONfunctions.getJSONArrayfromURL(ConstantURIs.GET_USER_PROFILE_DATA+"?user_id="+userId+"&auth_token="+auth_token);
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				//process array 
				try {
					
					JSONObject obj = array.getJSONObject(0);
					
					//set user details
					user_name = obj.getString("name");
					
					career_progression = obj.getString("career_progression");
					profile_image_url = obj.getString("profile_image");
					full_name = obj.getString("fullname");
					
					nameView.setText(user_name);
					
					if(full_name.equals("null"))
					{
						full_name = "";
					}
					((TextView)findViewById(R.id.fullName)).setText(full_name);
					headline.setText(user_name);
					
					if(career_progression.equals("null"))
						career_progression = "-";
					FriendsProfile.this.careerProgression.setText(career_progression);
					ImageLoader img = new ImageLoader(FriendsProfile.this);
					
					if(!profile_image_url.equals("null")){
						if(!isImageLoaded){
							img.DisplayImage(profile_image_url, profileImage);
							isImageLoaded = true;
						}
						
					}
					
					//set last played song details
					JSONObject lastPlayed = obj.getJSONObject("last_played");
					title = lastPlayed.getString("title");
					video_name = lastPlayed.getString("video_title");
					comment = lastPlayed.getString("comment");
					itunes_preview_url = lastPlayed.getString("previewUrl");
					itunes_download_url =lastPlayed.getString("itunes_download_url");
					track_name = lastPlayed.getString("trackName");
					artist_name = lastPlayed.getString("artistName");
					duration = lastPlayed.getString("youtube_video_duration");
					
					if(track_name.equals("null")){
						FriendsProfile.this.trackName.setText("");
					}else{
						FriendsProfile.this.trackName.setText(track_name);
					}
					
					artist_name = lastPlayed.getString("artistName");
					if(artist_name.equals("null")) {
						FriendsProfile.this.artistName.setText("");
					}
					else {
						FriendsProfile.this.artistName.setText(artist_name);
					}
					
					youtube_id = lastPlayed.getString("youtube_id");
					snapshot_filename = lastPlayed.getString("snapshot_file_name");
					created_at =lastPlayed.getString("created_at");
					
					lpData = new YouTubeData(title, snapshot_filename, "", youtube_id, created_at, duration);
					try{
					place = lastPlayed.getJSONObject("place").getString("title");
					}catch(Exception e){
						place ="null";
					}
					thumbnail_url = lastPlayed.getString("thumbnailUrl");
					
					
					user_id = obj.getString("user_id");
					
					//set profile records
					String plays = obj.getString("plays");
					String manages = obj.getString("manages");
					String records = obj.getString("records");
					String followingStatus = obj.getString("following_status");
					try{
						FriendsProfile.this.manages = Integer.parseInt(manages);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					
					if(followingStatus.equals("notfollowing")){
						followStatusButton.setTag("Follow");
						followStatusButton.setBackgroundResource(R.drawable.follow);
						followStatusButton.setVisibility(Button.VISIBLE);
						followStatusButton.setEnabled(true);
					}else if(followingStatus.equals("current_user")){
						followStatusButton.setVisibility(View.INVISIBLE);
						followStatusButton.setEnabled(false);
					}else{
						followStatusButton.setTag("Unfollow");
						followStatusButton.setBackgroundResource(R.drawable.unfollow);
						followStatusButton.setVisibility(Button.VISIBLE);
						followStatusButton.setEnabled(true);
					}
					
					((TextView)findViewById(R.id.numberOfPlays)).setText(plays);
					((TextView)findViewById(R.id.numberOfManages)).setText(manages);
					((TextView)findViewById(R.id.numberOfRecords)).setText(records);
					
					//set following and followers details
					String followers = obj.getString("followers");
					String following = obj.getString("following");
					
					((TextView)findViewById(R.id.numberOfFollowers)).setText(followers);
					((TextView)findViewById(R.id.numberOfFollowing)).setText(following);
					
					activityIndicator.setVisibility(ProgressBar.INVISIBLE);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}
			 
		 }
}
