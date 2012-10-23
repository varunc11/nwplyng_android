package com.mindhelix.nwplyng;

import java.util.ArrayList;

import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FsqVenue;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

public class NearLocationList extends Activity {
	
	
	private ListView locationList;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog progressDialog;
	
	GpsInfo gpsInfo;
	ArrayList<FsqVenue> venues ;
	ArrayList<FsqVenue> currentVenues = new ArrayList<FsqVenue>() ;
	
	EditText searchBox;
	Button back;
	
	View headerView;
	
	ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_list);
		
		gpsInfo = new GpsInfo(NearLocationList.this);
		
		locationList = (ListView)findViewById(R.id.location_list);
		progressBar = (ProgressBar)findViewById(R.id.progressBarLocationSearch);
		
		back = (Button)findViewById(R.id.backButtonLocations);
		
		headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.itunes_list_footer, null, false);
		headerView.setMinimumHeight(20);
		locationList.addFooterView(headerView);
		
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
		NearLocationList.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		searchBox = (EditText)findViewById(R.id.location_search_box);
				
		searchBox.setInputType(searchBox.getInputType()
			    | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
			    | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
		
		searchBox.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			public void afterTextChanged(Editable text) {
				ArrayList<FsqVenue> newData = new ArrayList<FsqVenue>();
				String searchWord  = text.toString().toLowerCase();
				
				int len = venues.size();
				
				for(int i = 0; i<len; i++){
					if(venues.get(i).name.toLowerCase().contains(searchWord)){
						newData.add(venues.get(i));
					}
				}
				
				NearListAdapter adapter = new NearListAdapter(NearLocationList.this, NearLocationList.this);
				currentVenues = newData;
				adapter.setItems(newData);
				locationList.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});
		
		locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Intent returnIntent = new Intent();
				
				returnIntent.putExtra("location",currentVenues.get(pos).name);
				returnIntent.putExtra("latitude", currentVenues.get(pos).latitude);
				returnIntent.putExtra("longitude", currentVenues.get(pos).longitude);
				returnIntent.putExtra("venue_id", currentVenues.get(pos).id);
				setResult(RESULT_OK,returnIntent);        
				finish();
			}
		
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				NearLocationList.this.finish();				
			}
		});
		
		new populateLocations().execute();
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
        {
			case DIALOG_DOWNLOAD_PROGRESS:
				progressDialog = new ProgressDialog(NearLocationList.this);
				progressDialog.setMessage("Loading Locations");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(true);
				progressDialog.show();
				return progressDialog;
			default:
				return null;
        }
	}
	
	class populateLocations extends AsyncTask<Void, Void, Void>{
		 
		boolean isException = false;
		@Override
		protected void onPreExecute() {
//			showDialog(DIALOG_DOWNLOAD_PROGRESS);
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				venues =FoursquareApp.getNearby(gpsInfo.getLatitude(),gpsInfo.getLongitude());
			} catch (Exception e) {
				isException = true;
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(!isException){
			NearListAdapter adapter = new NearListAdapter(NearLocationList.this, NearLocationList.this);
			currentVenues = venues;
			adapter.setItems(venues);
			locationList.setAdapter(adapter);
			adapter.notifyDataSetChanged();		
			}
			try{
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
			
		}
		
	}
	

}
