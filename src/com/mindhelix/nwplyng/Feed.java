package com.mindhelix.nwplyng;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mindhelix.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mindhelix.pulltorefresh.library.PullToRefreshListView;

public class Feed extends ListActivity {
	
	public int pageCount = 1;
	public int jsonArrayLength = 0;
	
	public String SINCE_ID = "0";
	public String MAX_ID = "0";
	
	public boolean loadingMore = false;
	public boolean loadingListTop = false;
	public boolean loadingListBottom = false;
	public boolean firstTimeLoad = true;
	
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	public FeedListAdapter feedAdapter;
	public List<FeedListItemData> feeds = new ArrayList<FeedListItemData>();
	
	View footerView, headerView;
	
	ProgressBar footerProgress, centerProgress;
	
	SharedPreferences preferences;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.feed_layout);
	        
	        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
	        firstTimeLoad = preferences.getBoolean("First Time", true);
	        
	        if(feedAdapter == null) {
				feedAdapter = new FeedListAdapter(Feed.this, Feed.this);
			}
	        
	        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
	        mPullRefreshListView.setDisableScrollingWhileRefreshing(false);
	        centerProgress = (ProgressBar)findViewById(R.id.progressBar_Feed);
	        centerProgress.setVisibility(ProgressBar.VISIBLE);
	        
	        headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_header, null, false);
			headerView.setMinimumHeight(10);
	        
	        footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_list_footer, null, false);
	        footerView.setMinimumHeight(110);
	        footerProgress = (ProgressBar)footerView.findViewById(R.id.progress);
	        
	        actualListView = mPullRefreshListView.getRefreshableView();
	        
	        // Set a listener to be invoked when the list should be refreshed.
			mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
				public void onRefresh() {
					
					if(pageCount <= 2) {
						footerProgress.setVisibility(ProgressBar.INVISIBLE);
						try {
						actualListView.removeFooterView(footerView);
						} catch (Exception e) {	}
					}
					pageCount = 1;
					loadingListTop = true;
					loadingListBottom = false;
					try {
						actualListView.removeHeaderView(headerView);
					} catch (Exception e) {}
					
					try {
						SINCE_ID = feeds.get(0).feed_id;
					}
					catch(Exception e) {
						firstTimeLoad = true;
						loadingListTop = false;
						loadingListBottom = false;
						try {
							actualListView.removeFooterView(footerView);
							} catch (Exception e1) {}
					}
					if(CheckNetworkConnection.isNetworkAvailable(Feed.this)) {
						new RetrieveFeedFromServer().execute();
					}
					else {
						NetworkConnectionErrorMessage.showNetworkError(Feed.this, Feed.this).show();
						mPullRefreshListView.onRefreshComplete();
					}
				}
			});
			
			actualListView.addFooterView(headerView);
	        actualListView.setAdapter(feedAdapter);
	        
	        actualListView.setOnScrollListener(new OnScrollListener() {
				
				public void onScrollStateChanged(AbsListView view, int scrollState) {}
				
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					
					int lastInScreen = firstVisibleItem + visibleItemCount;
					if((lastInScreen == totalItemCount)	&& (!loadingMore) && (jsonArrayLength >= 40)) {
						
						if(!loadingListTop) {
							loadingListBottom = true;
							
							try {
								actualListView.removeFooterView(headerView);
							}catch(Exception e) {}
							try {
								MAX_ID = feeds.get((feeds.size()-1)).feed_id;
								if(CheckNetworkConnection.isNetworkAvailable(Feed.this)) {
									new RetrieveFeedFromServer().execute();
								}
								else {
									NetworkConnectionErrorMessage.showNetworkError(Feed.this, Feed.this).show();
								}
							} catch(Exception e) {}
						}
					}
				}
			});
	        
	        actualListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					
					try {
						Intent intent = new Intent(Feed.this, FeedDescription.class);
						
						
						if(!loadingListTop)
							FeedListItemData.feed_list_item_data = feeds.get((position-2));
						else
							FeedListItemData.feed_list_item_data = feeds.get((position-1));
						
						intent.putExtra("title", FeedListItemData.feed_list_item_data.title);
						intent.putExtra("comment", FeedListItemData.feed_list_item_data.comment);
						intent.putExtra("itunes_preview_url", FeedListItemData.feed_list_item_data.itunes_preview_url);
						intent.putExtra("itunes_download_url", FeedListItemData.feed_list_item_data.itunes_download_url);
						intent.putExtra("track_name", FeedListItemData.feed_list_item_data.track_name);
						intent.putExtra("artist_name", FeedListItemData.feed_list_item_data.artist_name);
						intent.putExtra("youtube_id", FeedListItemData.feed_list_item_data.youtube_id);
						intent.putExtra("snapshot_filename", FeedListItemData.feed_list_item_data.snapshot_filename);
						intent.putExtra("user_name", FeedListItemData.feed_list_item_data.username);
						intent.putExtra("created_at", FeedListItemData.feed_list_item_data.created_at);
						intent.putExtra("place", FeedListItemData.feed_list_item_data.place);
						intent.putExtra("thumbnail_url", FeedListItemData.feed_list_item_data.thumbnail_url);
						intent.putExtra("profile_image_url", FeedListItemData.feed_list_item_data.profile_image_url);
						intent.putExtra("career_progression", FeedListItemData.feed_list_item_data.career_progression);
						intent.putExtra("user_id", FeedListItemData.feed_list_item_data.user_id);
						intent.putExtra("unique_id", FeedListItemData.feed_list_item_data.unique_id);
						intent.putExtra("feed_id", FeedListItemData.feed_list_item_data.feed_id);
						intent.putExtra("full_name", FeedListItemData.feed_list_item_data.full_name);
						intent.putExtra("upload_time", FeedListItemData.feed_list_item_data.video_uploaded_On);
						intent.putExtra("duration", FeedListItemData.feed_list_item_data.duration);
						intent.putExtra("last_played", "false");
						intent.putExtra("video_name", FeedListItemData.feed_list_item_data.video_name);
						
						startActivity(intent);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	        
	       	jsonArrayLength = 0;
	       	loadingListTop = true;
	       	firstTimeLoad = true;
	       	loadingListBottom = false;
	       	
	       	if(CheckNetworkConnection.isNetworkAvailable(Feed.this)) {
	       		new RetrieveFeedFromServer().execute();
	       	}
	       	else {
	       		centerProgress.setVisibility(ProgressBar.INVISIBLE);
	       	}
	       	
	 }
	 
	//AsynTask used to retrive the feeds from the server
	 public class RetrieveFeedFromServer extends AsyncTask<Void, Void, Void> {
		 
		String resultString = "";
		String lookupResponse = "", auth_token = "";
		int code = 0;
			
		HttpEntity entity;
		 
	    @Override
		protected void onPreExecute() {
			
	    	loadingMore = true;
	    	
	    	if(!loadingListTop) {
				footerProgress.setVisibility(ProgressBar.VISIBLE);
				try {
						actualListView.addFooterView(footerView);
					} catch (Exception e) {	}
						
			}
	    	
	    	if(firstTimeLoad) {
	    		try {
					actualListView.removeFooterView(footerView);
					} catch (Exception e1) {}
	    	}
			
			if(loadingListBottom) {
				mPullRefreshListView.setPullToRefreshEnabled(false);
			}
	    	
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			//Retrieve auth_token from preference or JSON String
			auth_token = preferences.getString("auth_token", "");
		    if(auth_token.equals("")) {
		    	lookupResponse = preferences.getString("lookupresponse", "");
		        try {
		        		JSONObject mainObj = new JSONObject(lookupResponse);
						JSONObject innerObj = mainObj.getJSONObject("user");
						auth_token = innerObj.getString("auth_token");
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString("auth_token",auth_token);
						editor.commit();
					} catch (JSONException e) {
						e.printStackTrace();
					}	
		    }
		    
		    //Connect to Server to retrieve the feeds
		    try {
		    	
		    	HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	
		    	HttpClient client = new DefaultHttpClient();
				String completeURLString = "";
		    	if(firstTimeLoad) {
		    		completeURLString = ConstantURIs.FEEDSURL + "?rpp=40&page="+pageCount+"&auth_token=" + auth_token;
		    	}
		    	else {
		    		if(loadingListTop) {
		    			completeURLString = ConstantURIs.FEEDSURL + "?rpp=40&since_id="+SINCE_ID+"&auth_token=" + auth_token;
		    		}
		    		else if(loadingListBottom) {
		    			completeURLString = ConstantURIs.FEEDSURL + "?rpp=40&max_id="+MAX_ID+"&auth_token=" + auth_token;
		    		}
		    	}
		    	
		    	HttpGet request = new HttpGet(completeURLString);
		    	request.setParams(httpParameters);
				request.setURI(new URI(completeURLString));
				HttpResponse response = client.execute(request);
				entity = response.getEntity();
				code = response.getStatusLine().getStatusCode();
				resultString = EntityUtils.toString(entity);
				
				pageCount++;
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    }
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(code == 200)
				setJSONtoList(resultString);
			else {
				mPullRefreshListView.onRefreshComplete();
				loadingMore = false;
				
				if(loadingListTop) {
					loadingListTop = false;
					jsonArrayLength = 40;
				}
				
				if(loadingListBottom) {
					loadingListBottom = false;
					mPullRefreshListView.setPullToRefreshEnabled(true);
				}
				
				centerProgress.setVisibility(ProgressBar.INVISIBLE);
			}
			
		}
		 
	 }
	 
	 public void setJSONtoList(String resultString) {
		 try {
				JSONObject jobject = new JSONObject(resultString);
		    	JSONArray array = jobject.getJSONArray("results");
		    	jsonArrayLength = array.length();
		    	JSONObject row = null;
		    	
		    	if(array.length() < 40)
		    	{
		    		footerProgress.setVisibility(ProgressBar.INVISIBLE);
		    		
		    		try {
						actualListView.removeFooterView(footerView);
					} catch (Exception e) {}
		    	}		    	
		    	else {
					footerProgress.setVisibility(ProgressBar.INVISIBLE);
					
					try {
						actualListView.removeFooterView(footerView);
					} catch (Exception e) {
					}
					
				}
		    	
		    	if(firstTimeLoad || loadingListBottom) {
		    		
		    		if(firstTimeLoad) {
		    			loadingListTop = false;
		    			SharedPreferences.Editor editor = preferences.edit();
		    			editor.putBoolean("First Time", false);
		    			editor.commit();
		    		}
		    		
		    		if(loadingListBottom) {
		    			footerProgress.setVisibility(ProgressBar.VISIBLE);
		    			actualListView.addFooterView(footerView);
		    		}
		    		
		    		for(int i=0 ; i<array.length() ; i++)
					{
						row = array.getJSONObject(i);
						
						FeedListItemData data = new FeedListItemData(
													row.getString("title"), 
								            		row.getString("comment"), 
								            		row.getString("youtube_id"), 
								            		row.getString("live"), 
								            		row.getString("snapshot_file_name"),
								            		row.getString("user_name"),
								            		row.getString("created_at"),
								            		row.getString("place"),
								            		row.getString("artist_name"),
								            		row.getString("thumbnail_url"),
								            		row.getString("itunes_preview_url"),
								            		row.getString("itunes_download_url"),
								            		row.getString("profile_image_url"),
								            		row.getString("track_name"),
								            		row.getString("career_progression"),
								            		row.getString("user_id"),
								            		row.getString("unique_id"),
								            		row.getString("feed_id"),
								            		row.getString("user_full_name"),
								            		row.getString("youtube_video_duration"),
								            		row.getString("youtube_video_uploaded_on"),
								            		row.getString("video_title"));
						
							feeds.add(data);
							
					}
		    		
		    	}
		    	else if(loadingListTop) {
		    		
		    		int length = jsonArrayLength - 1;
		    		
		    		for(int i= length ; i>=0 ; i--)
					{
						row = array.getJSONObject(i);
						   
						FeedListItemData data = new FeedListItemData(
													row.getString("title"), 
								            		row.getString("comment"), 
								            		row.getString("youtube_id"), 
								            		row.getString("live"), 
								            		row.getString("snapshot_file_name"),
								            		row.getString("user_name"),
								            		row.getString("created_at"),
								            		row.getString("place"),
								            		row.getString("artist_name"),
								            		row.getString("thumbnail_url"),
								            		row.getString("itunes_preview_url"),
								            		row.getString("itunes_download_url"),
								            		row.getString("profile_image_url"),
								            		row.getString("track_name"),
								            		row.getString("career_progression"),
								            		row.getString("user_id"),
								            		row.getString("unique_id"),
								            		row.getString("feed_id"),
								            		row.getString("user_full_name"),
								            		row.getString("youtube_video_duration"),
								            		row.getString("youtube_video_uploaded_on"),
								            		row.getString("video_title"));
						
							feeds.add(0, data);
							
					}
		    		
		    	}
		    	
		    	if(jsonArrayLength != 0) {
		    		feedAdapter.setItems(feeds);
					feedAdapter.notifyDataSetChanged();
		    	}
				
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				loadingMore = false;
				
				if(firstTimeLoad) {
	    			firstTimeLoad = false;
//	    			jsonArrayLength = 40;
	    			actualListView.addHeaderView(headerView);

				}
				
				if(feeds.size() >= 6) {
					actualListView.setOnTouchListener(scrolltheLV);
					
					try {
						actualListView.removeFooterView(footerView);
					}catch(Exception e) {}
				}
				else {
					actualListView.setOnTouchListener(donotscrollLV);
					
					try {
						actualListView.removeFooterView(footerView);
					}catch(Exception e) {}
				}
				
				if(loadingListTop) {
					loadingListTop = false;
					jsonArrayLength = 40;
					actualListView.addHeaderView(headerView);
				}
				
				if(loadingListBottom) {
					loadingListBottom = false;
					actualListView.addFooterView(headerView);
					mPullRefreshListView.setPullToRefreshEnabled(true);
				}
				
				centerProgress.setVisibility(ProgressBar.INVISIBLE);
				
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
	 }
	 
	 public OnTouchListener donotscrollLV = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
	            return true;
	        }
			return false;
		}
	 }; 
	
	 public OnTouchListener scrolltheLV = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			return false;
		}
	 };
	 
}