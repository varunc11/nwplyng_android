package com.mindhelix.nwplyng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

public class Itunes_searchlistActivity extends Activity {
	
	public static String searchString = ""; 
	private ListView searchList;
	ArrayList<Itunes_data> ItunessearchDataList = new ArrayList<Itunes_data>();
	ArrayList<Itunes_data> currentSearchDataList = new ArrayList<Itunes_data>();
	
	EditText searchBox;
	Button back;
	
	View headerView;
	
	boolean for_result_flag;
	boolean finish_flag;
	boolean isLoadingOver = false;
	
	AlertDialog.Builder builder;
	AlertDialog alert;
	
	ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itunes_search_list);
		
		Bundle extras = this.getIntent().getExtras();
		
		for_result_flag = extras.getBoolean("for_result", false);
		finish_flag = extras.getBoolean("back_home", false);
		
		searchList = (ListView)findViewById(R.id.itunesSearchList);
		back = (Button)findViewById(R.id.backButtoniTunes);
		progressBar = (ProgressBar)findViewById(R.id.ituneSearchProgressBar);
		
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
		headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.itunes_list_footer, null, false);
		headerView.setMinimumHeight(20);
		searchList.addFooterView(headerView);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		searchBox = (EditText)findViewById(R.id.itunesSearchBox);
		
		searchBox.setInputType(searchBox.getInputType()
			    | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
			    | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
		
		
		searchBox.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			public void afterTextChanged(Editable text) {
				ArrayList<Itunes_data> newData = new ArrayList<Itunes_data>();
				String searchWord  = text.toString().toLowerCase();
				
				int len = ItunessearchDataList.size();
				
				for(int i= 0;i<len; i++){
					if(ItunessearchDataList.get(i).artistName.toLowerCase().contains(searchWord)||ItunessearchDataList.get(i).censoredName.toLowerCase().contains(searchWord)){
						newData.add(ItunessearchDataList.get(i));
					}
				}
				
				Itunes_searchlistAdapter adapter = new Itunes_searchlistAdapter(Itunes_searchlistActivity.this, Itunes_searchlistActivity.this);
				currentSearchDataList = newData;
				adapter.setItems(newData);
				searchList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});
		
		//call background process to populate serach list
		searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				Intent intent = null;
				if(for_result_flag)
					intent = new Intent();
				else
					intent = new Intent(Itunes_searchlistActivity.this,PlayItems.class);
				
				String trackName = currentSearchDataList.get(position).tracknames;
				String artist = currentSearchDataList.get(position).artistName;
				String previewURL = currentSearchDataList.get(position).preview_tracks;
				String downloadURL = currentSearchDataList.get(position).downloadUrl;
				String  trackID = currentSearchDataList.get(position).trackID;
				String artistID = currentSearchDataList.get(position).artistID;
				String censoredName = currentSearchDataList.get(position).censoredName;
				
				intent.putExtra("trackName", trackName);
				intent.putExtra("artist", artist);
				intent.putExtra("censoredName", censoredName);
				intent.putExtra("previewUrl", previewURL);
				intent.putExtra("downloadUrl", downloadURL);
				intent.putExtra("trackId", trackID);
				intent.putExtra("artistId", artistID);
				
				try {
                    	SearchListActivity.searchString = URLEncoder.encode(currentSearchDataList.get(position).artistName+" "+ currentSearchDataList.get(position).tracknames,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				
				if(for_result_flag)
					setResult(RESULT_OK, intent);
				else
					startActivity(intent);
				
				Itunes_searchlistActivity.this.finish();
			}
		});
		
		if(finish_flag) {
			Itunes_searchlistActivity.this.finish();
		}
		else {
			if(CheckNetworkConnection.isNetworkAvailable(Itunes_searchlistActivity.this)) {
				new PopulateList().execute();
			}
			else {
				NetworkConnectionErrorMessage.showNetworkError(Itunes_searchlistActivity.this, Itunes_searchlistActivity.this).show();
			}
		}
		
		builder = new AlertDialog.Builder(this);
		builder.setMessage("Sorry, we were not able to find any artist or song by that name. Please try again.")
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		        	   Intent intent = new Intent(Itunes_searchlistActivity.this, Home.class);
		        	   intent.putExtra("Done Button", false);
		        	   startActivity(intent);
		               Itunes_searchlistActivity.this.finish();
		           }
		       });
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!for_result_flag) {
	        		startActivity(new  Intent(Itunes_searchlistActivity.this, Home.class));
	        	}
	            Itunes_searchlistActivity.this.finish();
			}
		});
	}
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(!for_result_flag) {
        		startActivity(new  Intent(Itunes_searchlistActivity.this, Home.class));
        	}
            Itunes_searchlistActivity.this.finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
	
	private class PopulateList extends AsyncTask<Void, Void, Void> {
		
		JSONArray items;
		
		@Override
		protected void onPreExecute() {
			
			progressBar.setVisibility(ProgressBar.VISIBLE);
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			SharedPreferences preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
			String jArrayString = preferences.getString("Itunes Json Array", "");
			
			JSONObject json = null;
			
			if(jArrayString.length() == 0){
				//Retreve search result 
				json = JSONfunctions
						.getJSONfromURL("http://itunes.apple.com/search?term="+searchString+"&limit=200&entity=song", Itunes_searchlistActivity.this);//+searchString);
			}
			else {
				try {
					json = new JSONObject(jArrayString);
				} catch (JSONException e) {}
			}
			
			
			//parsing search result
			
			try {

				items = json.getJSONArray("results");
				
				for (int i = 0; i < items.length(); i++) {
					JSONObject newItem = (JSONObject) items.get(i);
					
					String trackName = newItem.getString("trackName");
					String previewTrack = newItem.getString("previewUrl");
					String artistName =  newItem.getString("artistName");
					String downloadURL = newItem.getString("collectionViewUrl");
					String artistID = newItem.getString("artistId");
					String trackID = newItem.getString("trackId");
					String censoredName = newItem.getString("trackCensoredName");
					
					Itunes_data data = new Itunes_data(trackName,previewTrack,artistName,downloadURL,
							trackID,artistID, censoredName);
					ItunessearchDataList.add(data);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			isLoadingOver = true;
			Itunes_searchlistAdapter adapter = new Itunes_searchlistAdapter(Itunes_searchlistActivity.this, Itunes_searchlistActivity.this);
			currentSearchDataList =ItunessearchDataList;
			adapter.setItems(ItunessearchDataList);
			searchList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			
			try{
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			if(items.length() == 0)
			{
				alert = builder.create();
				alert.show();
			}
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
	}
	
	
}