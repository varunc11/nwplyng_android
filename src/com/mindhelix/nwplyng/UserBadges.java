package com.mindhelix.nwplyng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class UserBadges extends Activity {
	
	SharedPreferences preferences;
	
	ImageButton nwplyng, beatle, bestOf, billiboard, busted, dj, eargasm;
	ImageButton encore, fan, grammy, groupie, hbd, imusic, junkie, executive;
	ImageButton nightOwl, road, punk, single, top40, vip, wmd;
	
	Button back;
	ProgressBar progressBar;
	
	Intent intent;
	
	String auth_token = "";
	
	String description= "", name = "", time = "", title = "";
	
	String userId = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_records_badges);
		try{
			userId =getIntent().getExtras().getString("user_id");
		}catch(Exception e){
			
		}
		
//		preferences = PreferenceManager.getDefaultSharedPreferences(UserBadges.this);
		preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		auth_token = preferences.getString("auth_token", "");
		
		InitializeWidgets();
		
		progressBar.setVisibility(ProgressBar.VISIBLE);
		
		intent = new Intent(UserBadges.this, UserRecordDescription.class);
		
		new GetRecordsFromServer().execute();
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				UserBadges.this.finish();
			}
		});
		
	}
	
	public class GetRecordsFromServer extends AsyncTask<Void, Void, Void> {
		
		String responseString = "";

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				HttpClient client = new DefaultHttpClient();
				String completeUri = ConstantURIs.GET_USER_RECORDS + "?auth_token="+auth_token;
				
				if(userId.length()!=0){
					completeUri += "&user_id="+userId;
				}
				HttpGet getRequest = new HttpGet(completeUri);
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
				JSONArray jArray = new JSONArray(responseString);
				
				for(int i = 0; i < jArray.length(); i++) {
					
					final JSONObject row = jArray.getJSONObject(i);
					
					if(row.getString("name").equals("#nwplyng")) {
						
						nwplyng.setBackgroundResource(R.drawable.nwplyng_small);
						nwplyng.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
						
					}
					else if(row.getString("name").equals("Beatlemania")) {
						
						beatle.setBackgroundResource(R.drawable.beatlemania_small);
						beatle.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
						
					}
					else if(row.getString("name").equals("Best of")) {
						
						bestOf.setBackgroundResource(R.drawable.best_of_small);
						bestOf.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
						
					}
					else if(row.getString("name").equals("BillBoard")) {
						
						billiboard.setBackgroundResource(R.drawable.billboard_small);
						billiboard.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Busted")) {

						busted.setBackgroundResource(R.drawable.busted_small);
						busted.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("DJ")) {
						
						dj.setBackgroundResource(R.drawable.dj_small);
						dj.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Eargasm")) {
						
						eargasm.setBackgroundResource(R.drawable.eargasm_small);
						eargasm.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Encore")) {
						
						encore.setBackgroundResource(R.drawable.encore_small);
						encore.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Fan")) {
						
						fan.setBackgroundResource(R.drawable.fan_small);
						fan.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Grammy")) {
						
						grammy.setBackgroundResource(R.drawable.grammy_small);
						grammy.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Groupie")) {
						
						groupie.setBackgroundResource(R.drawable.groupie_small);
						groupie.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("HBD")) {
						
						hbd.setBackgroundResource(R.drawable.hbd_small);
						hbd.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("iMusic")) {
						
						imusic.setBackgroundResource(R.drawable.imusic_small);
						imusic.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Junkie")) {
						
						junkie.setBackgroundResource(R.drawable.junkie_small);
						junkie.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Music Executive")) {
						
						executive.setBackgroundResource(R.drawable.music_executive_small);
						executive.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Night Owl")) {
						
						nightOwl.setBackgroundResource(R.drawable.night_owl_small);
						nightOwl.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("On The Road")) {
						
						road.setBackgroundResource(R.drawable.on_the_road_small);
						road.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Punk")) {
						
						punk.setBackgroundResource(R.drawable.punk_small);
						punk.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Single")) {
						
						single.setBackgroundResource(R.drawable.single_small);
						
						single.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("Top 40")) {
						
						top40.setBackgroundResource(R.drawable.top40_small);
						top40.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("VIP Access")) {
						
						vip.setBackgroundResource(R.drawable.vip_access_small);
						vip.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					else if(row.getString("name").equals("WMD")) {
						
						wmd.setBackgroundResource(R.drawable.wmd_small);
						wmd.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {

								attachBundleToIntent(row);
								
							}
						});
	
					}
					
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
			
		}
		
		private void attachBundleToIntent(JSONObject row) {
			try {
				intent.putExtra("description", row.getString("description"));
				intent.putExtra("name", row.getString("name"));
				intent.putExtra("url", row.getString("public_url"));
				intent.putExtra("title", row.getString("title"));
				intent.putExtra("time", row.getString("local_time"));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			startActivity(intent);
		}
		
	}

	public void InitializeWidgets() {
		
		nwplyng = (ImageButton)findViewById(R.id.nwplyngBadge);
		beatle = (ImageButton)findViewById(R.id.beatleManiaBadge);
		bestOf = (ImageButton)findViewById(R.id.bestOfBadge);
		billiboard = (ImageButton)findViewById(R.id.billiboardBadge);
		busted = (ImageButton)findViewById(R.id.bustedBadge);
		dj = (ImageButton)findViewById(R.id.djBadge);
		eargasm = (ImageButton)findViewById(R.id.eargasmBadge);
		encore = (ImageButton)findViewById(R.id.encoreBadge);
		fan = (ImageButton)findViewById(R.id.fanBadge);
		grammy = (ImageButton)findViewById(R.id.grammyBadge);
		groupie = (ImageButton)findViewById(R.id.groupieBadge);
		hbd = (ImageButton)findViewById(R.id.hbdBadge);
		imusic = (ImageButton)findViewById(R.id.imusicBadge);
		junkie = (ImageButton)findViewById(R.id.junkieBadge);
		executive = (ImageButton)findViewById(R.id.executiveBadge);
		nightOwl = (ImageButton)findViewById(R.id.nightOwlBadge);
		road = (ImageButton)findViewById(R.id.onRoadBadge);
		punk = (ImageButton)findViewById(R.id.punkBadge);
		single = (ImageButton)findViewById(R.id.singleBadge);
		top40 = (ImageButton)findViewById(R.id.top40Badge);
		vip = (ImageButton)findViewById(R.id.vipBadge);
		wmd = (ImageButton)findViewById(R.id.wmdBadge);
		
		progressBar = (ProgressBar)findViewById(R.id.progressBarBadges);
		back = (Button)findViewById(R.id.backButtonBadges);
		
	}

}
