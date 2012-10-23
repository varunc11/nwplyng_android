package com.mindhelix.nwplyng;

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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;
import com.mindhelix.twitter.TwitterApp;

public class PostPlayActivity extends Activity {
	
	ImageLoader imageLoader;
	
	Button doneButton;
	TextView messageToUser;
	TextView managerText;
	TextView careerPromoted;
	TextView careerText;
	TextView careerTag;
	ImageView managerImage;
	ImageView careerImage;
	
	RelativeLayout managerLayout;
	RelativeLayout careerLayout;
	RelativeLayout baseLayout;
	
	public String responseString = "";
	public String careerResponseString = "";
	public String artistName = "";
	
	String auth_token = "";
	SharedPreferences preferences;
	
	public RecordUnlockAdapter recordAdapter;
	public List<RecordUnlockData> records;
	
	TwitterApp twitter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_play_screen2);
		
		Bundle extras = this.getIntent().getExtras();
		
		responseString = extras.getString("responseString");
		careerResponseString = extras.getString("careerResponseString"); 
		artistName = extras.getString("artist_name");
		
//		careerResponseString =  "{\"current_rank\":{\"description\":\"You are already a legend.\",\"name\":\"Legend\",\"public_url\":\"http://nwp.fm/biDLiK\",\"title\":\"Switchfoot - Your Love Is a Song\",\"created_at\":\"2012-08-04 04:46:17 UTC\",\"just_promoted\":false,\"check_in_local_time\":\"2012-07-06 22:16:31 +0000\"},\"next_rank\":{\"name\":\"null\",\"fans\":0,\"plays\":0}}"; 
		preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		auth_token = preferences.getString("auth_token", "");
		
		initializePostPlayWidgets();
		
		if(CheckNetworkConnection.isNetworkAvailable(PostPlayActivity.this)) {
			new CheckBadgeUnlock().execute();
		}
		else {
			NetworkConnectionErrorMessage.showNetworkError(PostPlayActivity.this, PostPlayActivity.this).show();
		}
		
		JSONObject nextObject = null;
		
		try {

			JSONObject jobject = new JSONObject(responseString);

			messageToUser.setText(jobject.getString("message"));

			managerText.setText(generateManagerMessage(
					Integer.parseInt(jobject.getString("manager_distance")),
					jobject.getString("current_manager"),
					Boolean.parseBoolean(jobject.getString("ousted_manager")),
					artistName,
					jobject.getString("current_manager_profile_image")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			
//			careerResponseString = "{\"current_rank\":null,\"next_rank\":{\"name\":\"Air Guitarist\",\"fans\":0,\"plays\":1}}";
			
			JSONObject mainObject = new JSONObject(careerResponseString);
			nextObject = mainObject.getJSONObject("next_rank");
			
			JSONObject currentObject = mainObject.getJSONObject("current_rank");
			careerText.setText(generateCareerMessage(
						currentObject.getString("name"), 
						currentObject.getString("description"), 
						Boolean.parseBoolean(currentObject.getString("just_promoted")), 
						nextObject.getString("name"), 
						Integer.parseInt(nextObject.getString("fans")), Integer.parseInt(nextObject.getString("plays"))));
			
		} 
		catch (JSONException e) {
			
			try {
			careerText.setText(generateCareerMessage(
					"", 
					"", 
					false, 
					nextObject.getString("name"), 
					Integer.parseInt(nextObject.getString("fans")), Integer.parseInt(nextObject.getString("plays"))));
			}catch(Exception exception) {}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		doneButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(PostPlayActivity.this, Itunes_searchlistActivity.class);
				intent.putExtra("back_home", true);
				startActivity(intent);
				PostPlayActivity.this.finish();
			}
		});
		
	}
	
	//get the career progression details from the server
		
	//check if any badge is unlocked
	public class CheckBadgeUnlock extends AsyncTask<Void, Void, Void> {
		
		String resultString = "";
		
		@Override
		protected void onPreExecute() {
			records = new ArrayList<RecordUnlockData>();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				HttpClient client = new DefaultHttpClient();
				String completeUri = ConstantURIs.GET_USER_RECORDS + "?auth_token="+auth_token +"&filter=true";
				HttpGet getRequest = new HttpGet(completeUri);
				HttpResponse response = client.execute(getRequest);
				HttpEntity entity = response.getEntity();
				resultString = EntityUtils.toString(entity);
//				resultString = "[{\"description\":\"Woohoo! You unlocked the iMusic plectrum for using the iPhone\",\"name\":\"iMusic\",\"title\":null,\"public_url\":null,\"created_at\":\"2012-06-26 12:22:01 UTC\"},{\"description\":\"Your 1st play! Hope you're not a one hit wonder.\",\"name\":\"Single\",\"title\":null,\"public_url\":null,\"created_at\":\"2012-06-26 12:22:04 UTC\"}, {\"description\":\"Woohoo! You unlocked the iMusic plectrum for using the iPhone\",\"name\":\"iMusic\",\"title\":null,\"public_url\":null,\"created_at\":\"2012-06-26 12:22:01 UTC\"},{\"description\":\"Your 1st play! Hope you're not a one hit wonder.\",\"name\":\"Single\",\"title\":null,\"public_url\":null,\"created_at\":\"2012-06-26 12:22:04 UTC\"} ]";
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			final Dialog dialog = new Dialog(PostPlayActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.record_unlock);
						
			LayoutParams params = dialog.getWindow().getAttributes();
			params.height = params.FILL_PARENT;
			params.width = params.FILL_PARENT;
			dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
			dialog.getWindow().setLayout(650, 1000);
			dialog.setCancelable(false);
								
			TextView heading = (TextView)dialog.findViewById(R.id.recordDialogTitleText);
			ListView listView = (ListView)dialog.findViewById(R.id.record_unlock_listview);
			Button continueBtn = (Button)dialog.findViewById(R.id.recordDialogButton);
			
			if(recordAdapter == null)
			{
				recordAdapter = new RecordUnlockAdapter(PostPlayActivity.this, PostPlayActivity.this);
				
			}
			
			listView.setAdapter(recordAdapter);
			
			continueBtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					dialog.dismiss();
									
				}
			});
			
			try {
				JSONArray jArray = new JSONArray(resultString);
				
				if(jArray.length() != 0) {
					
					if(jArray.length() == 1) {
						heading.setText("YOU UNLOCKED A PICK!");
					}
					else {
						heading.setText("YOU UNLOCKED "+jArray.length()+" PICKS!");
					}
					
					String badgesAquired = "";
					
					for(int i=0; i<jArray.length(); i++) {
						
						JSONObject row = jArray.getJSONObject(i);
						
						if(i == (jArray.length() - 2)) {
							badgesAquired = badgesAquired + "\""+row.getString("name") + "\"" + " and ";
						}
						else if(i == (jArray.length() - 1)){
							badgesAquired = badgesAquired + "\""+row.getString("name") + "\"";
						}
						else {
							badgesAquired = badgesAquired + "\""+row.getString("name") + "\"" + ", ";
						}
						
						RecordUnlockData data = new RecordUnlockData(row.getString("name"), row.getString("description"));
						records.add(data);
					}
					
					if(preferences.getString("fb_button_status", "off").equals("on")) {
						if(jArray.length() == 1) {
							JSONObject row = jArray.getJSONObject(0);
							new PostBadgeToFB().execute(row.getString("name"), "single");
						}
						else {
							new PostBadgeToFB().execute(badgesAquired, "many");
						}
					}
					
					if(preferences.getString("twitter_button_status", "off").equals("on")) {
						twitter = new TwitterApp(PostPlayActivity.this, Login.CONSUMER_KEY, Login.CONSUMER_SECRET);
						
						if(jArray.length() == 1) {
							JSONObject row = jArray.getJSONObject(0);
							new PostBadgeToTwitter().execute(row.getString("name"), "single");
						}
						else {
							new PostBadgeToTwitter().execute(badgesAquired, "many");
						}
					}
					
					recordAdapter.setItems(records);
					recordAdapter.notifyDataSetChanged();
					
					dialog.show();
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	//generate the texts under manager
	public String generateManagerMessage(int distance, String currentManager, boolean ousted, String artist, String imageURL) {
		String message = "";
		
		if(distance <= 0) {
			if(ousted) {
				message = "Booyah! You just got hired as the Manager of " + artist + ".";
				String messageString = "Booyah! I just got hired as the Manager of " + artist;
				
				if(preferences.getString("fb_button_status", "off").equals("on")) {
					new PostManagerToFB().execute(messageString);
				}
				
				if(preferences.getString("twitter_button_status", "off").equals("on")) {
					twitter = new TwitterApp(PostPlayActivity.this, Login.CONSUMER_KEY, Login.CONSUMER_SECRET);
					new PostManagerToTwitter().execute(messageString);
				}
				
			}
			else {
				message = "You are the Manager of " + artist +".";
			}
			
			managerImage.setBackgroundResource(R.drawable.manager2x);
			
		}
		else {
			if((distance > 20) && !currentManager.equals("")) {
				message = currentManager + " is the Manager of " + artist;
				imageLoader.DisplayImage(imageURL, managerImage);
			}
			else {
				if(distance == 1) {
					message = "You are " + (distance) + " play away from becoming the Manager of " + artist +".";
				}
				else {
					message = "You are " + (distance) + " plays away from becoming the Manager of " + artist +".";
				}
			}
		}
		
		if(!currentManager.equals("")) {
			if(!imageURL.equals("null")) {
				if(distance > 0)
					imageLoader.DisplayImage(imageURL, managerImage);
			}
			else {
				managerLayout.removeView(managerImage);
			}
		}
		else {
			managerLayout.removeView(managerImage);
		}
			
		return message;
	}
	
	//generate the texts under career progression
	public String generateCareerMessage(String currentRank, String description, boolean justPromoted, String nextRank, int fans, int plays) {
		String message = "";
		
		if(justPromoted) {
			careerPromoted.setText("Congrats! You just progressed to " + currentRank +".");
			message = description;
			setCareerProgressionBadge(currentRank);
		}
		else {
			
			if(nextRank.equals("null")) {
				baseLayout.removeView(careerLayout);
				baseLayout.removeView(careerTag);
			}
			else {
				if((fans > 0) && (plays > 0)) {
					if((fans == 1) && (plays == 1)) {
						message = "You are " + plays + " play and " + fans +" follower away from progressing to " + nextRank +".";
					}
					else if(fans == 1) {
						message = "You are " + plays + " plays and " + fans +" follower away from progressing to " + nextRank +".";
					}
					else if(plays == 1) {
						message = "You are " + plays + " play and " + fans +" followers away from progressing to " + nextRank +".";
					}
					else {
						message = "You are " + plays + " plays and " + fans +" followers away from progressing to " + nextRank +".";
					}
				}
				else if(fans <= 0) {
					if(plays == 1) {
						message = "You are " + plays + " play away from progressing to " + nextRank +".";
					}
					else {
						message = "You are " + plays + " plays away from progressing to " + nextRank +".";
					}
				}
				else {
					if(fans == 1) {
						message = "You are " + fans +" follower away from progressing to " + nextRank +".";
					}
					else {
						message = "You are " + fans +" followers away from progressing to " + nextRank +".";
					}
				}
				
				careerLayout.removeView(careerImage);
				careerLayout.removeView(careerPromoted);
			}
			
		}
		
		return message;
	}
	
	//post badge to FB
	public class PostBadgeToFB extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... message) {
			
			String startMessage =  "Woohoo! I just unlocked the ";
			String endMessage1 = " pick on #nwplyng";
			String endMessage2 = " picks on #nwplyng ";
			
			Facebook fb = new Facebook(Login.APP_ID);
			if(SessionStore.restore(fb, PostPlayActivity.this)){
				Bundle bun = new Bundle();
				if(message[1].equals("single"))
					bun.putString("message",startMessage+ message[0] + endMessage1);
				else
					bun.putString("message",startMessage+ message[0] + endMessage2);
				
				try {
					fb.request("me/feed", bun, "POST");
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			return null;
		}
		
	}
	
	//post badge to twitter
	public class PostBadgeToTwitter extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... message) {
			
			String startMessage =  "Woohoo! I just unlocked the ";
			String endMessage1 = " pick on @nwplyng";
			String endMessage2 = " picks on @nwplyng";
			
			try {
				if(message[1].equals("single"))
					twitter.updateStatus(startMessage + message[0] + endMessage1);
				else
					twitter.updateStatus(startMessage + message[0] + endMessage2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}	
	
	//post manager to FB
	public class PostManagerToFB extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... message) {
			Facebook fb = new Facebook(Login.APP_ID);
			if(SessionStore.restore(fb, PostPlayActivity.this)){
				Bundle bun = new Bundle();
				bun.putString("message", message[0] + " on #nwplyng.");
				try {
					fb.request("me/feed", bun, "POST");
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			return null;
		}
		
	}
	
	//post manager to twitter
	public class PostManagerToTwitter extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... message) {
			
			try {
				twitter.updateStatus(message[0] + " on @nwplyng.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	//set career badge
	public void setCareerProgressionBadge(String badgeName) {
		
		if(badgeName.equals("Air Guitarist"))
			careerImage.setBackgroundResource(R.drawable.air_guitarist2x);
		else if(badgeName.equals("Street Performer"))
			careerImage.setBackgroundResource(R.drawable.street_performer2x);
		else if(badgeName.equals("Baby Band"))
			careerImage.setBackgroundResource(R.drawable.baby_band2x);
		else if(badgeName.equals("Club Act"))
			careerImage.setBackgroundResource(R.drawable.club_act2x);
		else if(badgeName.equals("Indie hit"))
			careerImage.setBackgroundResource(R.drawable.indie_hit2x);
		else if(badgeName.equals("Rockstar"))
			careerImage.setBackgroundResource(R.drawable.rockstar2x);
		else if(badgeName.equals("Legend"))
			careerImage.setBackgroundResource(R.drawable.legend2x);
		
	}
		
	public void initializePostPlayWidgets() {
		
		managerLayout = (RelativeLayout)findViewById(R.id.manager_layout);
		careerLayout = (RelativeLayout)findViewById(R.id.career_layout);
		baseLayout = (RelativeLayout)findViewById(R.id.post_dialog_root);
			
		messageToUser = (TextView)findViewById(R.id.message_to_user);
		managerText = (TextView)findViewById(R.id.managerText);
		careerText = (TextView)findViewById(R.id.careerText);
		careerTag = (TextView)findViewById(R.id.careerTag);
		careerPromoted = (TextView)findViewById(R.id.promotedText);
		managerImage = (ImageView)findViewById(R.id.managerImage);
		careerImage = (ImageView)findViewById(R.id.careerImage);
		
		doneButton = (Button)findViewById(R.id.donebutton);
		
		imageLoader = new ImageLoader(PostPlayActivity.this);
			
	}

}
