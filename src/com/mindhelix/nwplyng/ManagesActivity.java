package com.mindhelix.nwplyng;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ManagesActivity extends Activity{
	
	String from ="";
	String userId ="";
	String authToken ="";
	
	SharedPreferences preferences;
	ProgressBar progressBar;
	ListView listView;
	Button back;
	
	View headerView;
	
	ManagesListAdapter adapter;
	public List<ManagesData>  managesDataList = new ArrayList<ManagesData>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manages_layout);
		
		userId = getIntent().getExtras().getString("user_id");
		
//		authToken = PreferenceManager.getDefaultSharedPreferences(ManagesActivity.this).getString("auth_token", "");
		authToken = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE).getString("auth_token", "");
		
		listView = (ListView)findViewById(R.id.managesList);
		progressBar = (ProgressBar)findViewById(R.id.progressBarManages);
		back = (Button)findViewById(R.id.backButtonManages);
		
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
//		headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.itunes_list_footer, null, false);
//		headerView.setMinimumHeight(15);
//		listView.addFooterView(headerView);
//		listView.addHeaderView(headerView);
		
		adapter = new ManagesListAdapter(ManagesActivity.this, ManagesActivity.this);
		listView.setAdapter(adapter);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				ManagesActivity.this.finish();				
			}
		});
		
		new RetreveManagesInfo().execute();
		
	}
	
	private class RetreveManagesInfo extends AsyncTask<Void, Void, Void>{
		
		JSONArray resultArray = new JSONArray();
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				String url = ConstantURIs.GET_ARTIST_MANAGERS+"?auth_token="+
						authToken;
				if(userId.length()!=0){
					url = url +"&user_id="+userId;
				}
				
				resultArray = JSONfunctions.getJSONArrayfromURL(url); 
				
			}catch(Exception e){
				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			try{
				for(int i =0 ; i<resultArray.length(); i++) {
					
					JSONObject obj = resultArray.getJSONObject(i);
					String manageName = obj.getString("name");
					
					ManagesData data = new ManagesData(manageName);
					managesDataList.add(data);
				}
				
				adapter.setItems(managesDataList);
				
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
			}catch(Exception e){
				
			}
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
		
	}

}
