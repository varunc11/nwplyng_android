package com.mindhelix.nwplyng;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CareerProgression extends Activity {
	
	TextView name, description, title, time, nextLevel;
	ImageView pick;
	ProgressBar progressBar;
	Button back;
	
	SharedPreferences preferences;
	
	String user_id, auth_token;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.career_progression_layout);
		
		user_id = "";
		auth_token = "";
		
		Bundle extras = this.getIntent().getExtras();
		user_id = extras.getString("user_id");
		
		preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		auth_token = preferences.getString("auth_token","");
		
		pick = (ImageView)findViewById(R.id.bigCareerPick);
		name = (TextView)findViewById(R.id.careerPickName);
		description = (TextView)findViewById(R.id.careerPickDescription);
		title = (TextView)findViewById(R.id.careerPickUnlockTitle);
		time = (TextView)findViewById(R.id.carrerPickUnlockTime);
		nextLevel = (TextView)findViewById(R.id.nextCareerLevelDescription);
		
		progressBar = (ProgressBar)findViewById(R.id.carrerProgressBar);
		progressBar.setVisibility(ProgressBar.VISIBLE);
		
		back = (Button)findViewById(R.id.backButtonCareerProgression);
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				CareerProgression.this.finish();
			}
		});
		
		new RetriveCareer().execute();
		
	}
	
	public String generateCareerMessage(String currentRank, String description, boolean justPromoted, String nextRank, int fans, int plays) {
		String message = "";
		
		name.setText(currentRank);
		this.description.setText(description);
		setCareerProgressionBadge(currentRank);
		
		if(nextRank.equals("null")) {
			
		}
		else {
			if((fans > 0) && (plays > 0)) {
				if((fans == 1) && (plays == 1)) {
					message = plays + " play and " + fans +" follower away from progressing to " + nextRank + ".";
				}
				else if(fans == 1) {
					message = plays + " plays and " + fans +" follower away from progressing to " + nextRank + ".";
				}
				else if(plays == 1) {
					message = plays + " play and " + fans +" followers away from progressing to " + nextRank + ".";
				}
				else {
					message = plays + " plays and " + fans +" followers away from progressing to " + nextRank + ".";
				}
			}	
			else if(fans <= 0) {
				if(plays == 1) {
					message = plays + " play away from progressing to " + nextRank + ".";
				}
				else {
					message = plays + " plays away from progressing to " + nextRank + ".";
				}
			}
			else {
				if(fans == 1) {
					message = fans +" follower away from progressing to " + nextRank + ".";
				}
				else {
					message = fans +" followers away from progressing to " + nextRank + ".";
				}
			}
		}
		
			
		return message;
	}
	
	public void setCareerProgressionBadge(String badgeName) {
		
		if(badgeName.equals("Air Guitarist"))
			pick.setBackgroundResource(R.drawable.air_guitarist2x);
		else if(badgeName.equals("Street Performer"))
			pick.setBackgroundResource(R.drawable.street_performer2x);
		else if(badgeName.equals("Baby Band"))
			pick.setBackgroundResource(R.drawable.baby_band2x);
		else if(badgeName.equals("Club Act"))
			pick.setBackgroundResource(R.drawable.club_act2x);
		else if(badgeName.equals("Indie hit"))
			pick.setBackgroundResource(R.drawable.indie_hit2x);
		else if(badgeName.equals("Rockstar"))
			pick.setBackgroundResource(R.drawable.rockstar2x);
		else if(badgeName.equals("Legend"))
			pick.setBackgroundResource(R.drawable.legend2x);
		
	}
	
	public class RetriveCareer extends AsyncTask<Void, Void, Void> {
		
		String resultString = "";
		int code = 0;

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
				
				HttpClient client = new DefaultHttpClient();
				String completeURL = ConstantURIs.GET_CAREER + "?auth_token=" + auth_token;
				if(user_id.length() != 0)
					completeURL += "&user_id=" + user_id;
				HttpGet getRequest = new HttpGet(completeURL);
				getRequest.setParams(httpParameters);
				HttpResponse response = client.execute(getRequest);
				code = response.getStatusLine().getStatusCode();
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
			
			String unlockDate = "";
	      
			if(code == 200) {
				try {
					JSONObject mainObject = new JSONObject(resultString);
					JSONObject nextObject = mainObject.getJSONObject("next_rank");
					
					JSONObject currentObject = mainObject.getJSONObject("current_rank");
					nextLevel.setText(generateCareerMessage(
								currentObject.getString("name"), 
								currentObject.getString("description"), 
								Boolean.parseBoolean(currentObject.getString("just_promoted")), 
								nextObject.getString("name"), 
								Integer.parseInt(nextObject.getString("fans")), Integer.parseInt(nextObject.getString("plays"))));
					
					unlockDate = currentObject.getString("local_time");
					title.setText("Progressed with "+currentObject.getString("title"));
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = sdf.parse(unlockDate.substring(0, 10)+ " " + unlockDate.substring(11, 19));
					
					String timeHHMM = new SimpleDateFormat("hh:mm a").format(date);
					String dateString = new SimpleDateFormat("MMMM dd, yyyy").format(date);
					
					time.setText(timeHHMM + " on " + dateString); 
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
	    }
		
	}

}
