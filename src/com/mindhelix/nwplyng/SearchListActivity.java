package com.mindhelix.nwplyng;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SearchListActivity extends Activity {
	
	public static String searchString = ""; 
	private ListView searchList;
	public List<YouTubeData>  searchDataList;
	
	ProgressBar progressBar;
	Button back;
	
	int start_index = 1;
	public int pageCount = 1;
	public int jsonArrayLength = 1;
	
	public boolean isLoadingOver= false;
	
	View footerView, headerView;
	
	ProgressBar footerProgress;
	SearchListAdapter adapter;
	
	public boolean loadingMore = true;
	public int footerflag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list);
		
		searchList = (ListView)findViewById(R.id.searchList);
		progressBar = (ProgressBar)findViewById(R.id.progressBarSearchList);
		back = (Button)findViewById(R.id.backButtonYT);
		
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
		searchDataList = new ArrayList<YouTubeData>();
		
		headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_header, null, false);
		headerView.setMinimumHeight(10);
		searchList.addHeaderView(headerView);
		
		footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_list_footer, null, false);
		footerView.setMinimumHeight(110);
        footerProgress = (ProgressBar)footerView.findViewById(R.id.progress);
        
        footerflag = 0;
        footerView.setVisibility(View.INVISIBLE);
        searchList.addFooterView(footerView);
        
        adapter = new SearchListAdapter(SearchListActivity.this,SearchListActivity.this);
        searchList.setAdapter(adapter);
        
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				SearchListActivity.this.finish();
			}
		});
		
		jsonArrayLength = 0;
		new PopulateList().execute();
	}
	
	private OnScrollListener listScroll = new OnScrollListener() {
		
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
			if(isLoadingOver)
				return;
			
			int lastInScreen = firstVisibleItem + visibleItemCount;
			if((lastInScreen == totalItemCount) && !(loadingMore) && (jsonArrayLength >= 20))
				new PopulateList().execute();
		}
	};
	
	private OnScrollListener mockScroll = new OnScrollListener() {
		
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
		}
	};
	
	private class PopulateList extends AsyncTask<Void, Void, Void> {
		JSONObject json;
		JSONArray items;
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
			loadingMore = true;
			
			footerflag++;
			if(footerflag == 2)
				footerView.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			//Retreve search result 
			json = JSONfunctions
					.getJSONfromURL("http://gdata.youtube.com/feeds/api/videos?q="+searchString+"&start-index="+start_index+"&max-results=20&v=2&alt=jsonc");
			
			//parsing search result
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			try {
				JSONObject obj1 = json.getJSONObject("data");
				items = obj1.getJSONArray("items");
				int len = items.length();
				jsonArrayLength = len;
				
				for(int i =0; i<len ;i++){
					JSONObject newItem = (JSONObject) items.get(i);
					
					String title = newItem.getString("title");
					
					JSONObject thumbnailObject = newItem.getJSONObject("thumbnail");
					String thumbNailURL = thumbnailObject.getString("hqDefault");
					String videoId = newItem.getString("id");
					String uploadedTime = newItem.getString("uploaded");
					String duration = newItem.getString("duration");
					
					String rtspLink = "";
					
					YouTubeData data = new YouTubeData(title,thumbNailURL,rtspLink,	videoId,uploadedTime,duration);
					searchDataList.add(data);
				}
				
				if(len < 20){
					footerProgress.setVisibility(ProgressBar.INVISIBLE);
					searchList.removeFooterView(footerView);
					searchList.addFooterView(headerView);
					isLoadingOver = true;
				}
				adapter.setItems(searchDataList);
				if(searchDataList.isEmpty()){
					footerProgress.setVisibility(ProgressBar.INVISIBLE);
					searchList.removeFooterView(footerView);
					searchList.addFooterView(headerView);
				}
				adapter.notifyDataSetChanged();
			
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			loadingMore = false;
			pageCount++;
			start_index += 20;
			
			if(!(start_index >= 100)){
	        	searchList.setOnScrollListener(listScroll);
	        }
	        else {
	        	searchList.setOnScrollListener(mockScroll);
	        	footerProgress.setVisibility(ProgressBar.INVISIBLE);
				searchList.removeFooterView(footerView);
				searchList.addFooterView(headerView);
	        }
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
		
	}
}