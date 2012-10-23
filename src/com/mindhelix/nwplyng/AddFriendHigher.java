package com.mindhelix.nwplyng;

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
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

@TargetApi(14)
public class AddFriendHigher extends Activity implements SearchView.OnQueryTextListener {
	
	ListView friendsList;
	Button back;
	
	View footerView, headerView;
	ProgressBar footerProgress;
	
	SearchManager searchManager;
	
	private SearchView mSearchView;
	
	public int pageCount = 1;
	public int jsonArrayLength = 1;
	
	public boolean loadingMore = true;
	public boolean isLoadingOver = false;
	public boolean isFirstTime = false;
	
	FollowListAdapter friendListAdapter;
	List<User> friends;
	
	String name = "";
	
	SharedPreferences preferences;
	String auth_token = "";
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	     setContentView(R.layout.add_friend_higher);
	     
//	     preferences = PreferenceManager.getDefaultSharedPreferences(AddFriendHigher.this);
	     preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
	     auth_token = preferences.getString("auth_token", "");
	     
	     back = (Button)findViewById(R.id.addFriendHigherBackButton);
	     friendsList = (ListView)findViewById(R.id.friendSearchListHigher);
	     
	     headerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_list_header, null, false);
		 headerView.setMinimumHeight(10);
		 friendsList.addHeaderView(headerView);
	     
	     footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.feed_list_footer, null, false);
	     footerView.setMinimumHeight(110);
	     footerProgress = (ProgressBar)footerView.findViewById(R.id.progress);
	     
	     footerProgress.setVisibility(ProgressBar.INVISIBLE);
	     
	     if(friendListAdapter == null) {
	    	 friendListAdapter = new FollowListAdapter(AddFriendHigher.this, AddFriendHigher.this);
	     }
	     
	     friendsList.addFooterView(footerView);
	     try {
	    	 friendsList.removeFooterView(headerView);
	        }catch (Exception e) {}
	     
		 friendsList.setAdapter(friendListAdapter);
		 
	     friendsList.setOnScrollListener(new OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if((lastInScreen == totalItemCount) && !(loadingMore) && !(isLoadingOver) && (jsonArrayLength >= 20))
					new GetUserList().execute();
			}
		});
	     
	     back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AddFriendHigher.this.finish();
			}
		});
	     
	     onSearchRequested();
	     
	}
	 
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	     
	      isFirstTime = true;
			
	      footerProgress.setVisibility(ProgressBar.VISIBLE); 
	              
	      name = query;
	      				
	      pageCount = 1;
	      			
	      friends = new ArrayList<User>();
//	      friendListAdapter.setItems(friends);

	      isLoadingOver = false;
	      
	      jsonArrayLength = 0;
	      new GetUserList().execute();
	      
	      onSearchRequested();
	      
	    }
	}
	 
	@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.play_menu, menu);

	     MenuItem searchItem = menu.findItem(R.id.action_search);
	     mSearchView = (SearchView) searchItem.getActionView();
	     setupSearchView(searchItem);
	     return true;
	 }
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {

				onSearchRequested();

	 	return true;
	 }
	
	 private void setupSearchView(MenuItem searchItem) {
		 if (isAlwaysExpanded()) {
			 mSearchView.setIconifiedByDefault(false);
	     } 
		 else {
	         searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
	                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	     }
	     searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
	     
//	     searchManager.onDismiss(new DialogInterface() {
//			
//			public void dismiss() {
//				AddFriendHigher.this.finish();
//			}
//			
//			public void cancel() {
//				
//			}
//		});
	     
	     if (searchManager != null) {
	    	 List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
	         SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
	         for (SearchableInfo inf : searchables) {
	        	 if (inf.getSuggestAuthority() != null && inf.getSuggestAuthority().startsWith("applications")) {
	        		 info = inf;
	             }
	         }
	         mSearchView.setSearchableInfo(info);
	    }
	    mSearchView.setOnQueryTextListener(this);
	 }
	 
	 public boolean onQueryTextChange(String newText) {
		 return false;
	 }
	 
	 public boolean onQueryTextSubmit(String query) {
		 return false;
	 }
	 
	 public boolean onClose() {
	     return false;
	 }
	 
	 protected boolean isAlwaysExpanded() {
		 return false;
	 }
	 
	 @Override
	    public boolean onKeyUp(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	            AddFriendHigher.this.finish();
	            return true;
	        }
	        return super.onKeyUp(keyCode, event);
	    }
	 
	 public class GetUserList extends AsyncTask<Void, Void, Void> {
		 
		 String resultString = "";
		 int code = 0;
		 
		 @Override
		protected void onPreExecute() {
					loadingMore = true;
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				
				HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	
				HttpClient client = new DefaultHttpClient();
				String competeUri = ConstantURIs.SEARCH_USERS + "?rpp=20&page="+pageCount+"&name="+name+"&auth_token="+auth_token; 
				
				HttpGet getRequest = new HttpGet(competeUri);
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
			
			if(code == 200) {
				try {
					JSONObject jobject = new JSONObject(resultString);
					JSONArray array = null;
					
					if(jobject.length() != 0)
					{
						array = jobject.getJSONArray("results");
						
						if(array.length() == 0)
				    	{
				    		footerProgress.setVisibility(ProgressBar.INVISIBLE);
				    	}
				    	
				    	int len = array.length();
				    	jsonArrayLength = len;
				    	
				    	for(int i=0 ; i<len ; i++)
						{
							JSONObject row = array.getJSONObject(i);
							
							User user = new User(
									row.getString("user_id"), 
									row.getString("name"), 
									row.getString("profile_image"), 
									row.getString("follow_status"), 
									"",
									row.getString("full_name"));
							
							friends.add(user);
						}
				    	
//				    	friendListAdapter.setItems(friends);
				    	
				    	if(len < 20) {
							footerProgress.setVisibility(ProgressBar.INVISIBLE);
							friendsList.removeFooterView(footerView);
							friendsList.addFooterView(headerView);
							isLoadingOver = true;
							loadingMore = false;
						}
						
					}
					else
					{
						friendsList.invalidateViews();
						footerProgress.setVisibility(ProgressBar.INVISIBLE);
						friendsList.removeFooterView(footerView);
						friendsList.addFooterView(headerView);
						isLoadingOver = true;
						loadingMore = false;
					}
					
					if(friends.isEmpty()) {
						footerProgress.setVisibility(ProgressBar.INVISIBLE);
						friendsList.removeFooterView(footerView);
						friendsList.addFooterView(headerView);
					}
			    	
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				friendListAdapter.notifyDataSetChanged();
				
				loadingMore = false;
				pageCount++;
			}
			else {
				loadingMore = false;
			}
		}
		 
	 }

}