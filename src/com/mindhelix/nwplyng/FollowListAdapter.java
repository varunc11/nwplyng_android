package com.mindhelix.nwplyng;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FollowListAdapter extends BaseAdapter {
	
	private Context context;
	private Activity activity;
	private List<User> followList = Collections.emptyList();
	private List<Boolean> followFinalList = Collections.emptyList();
	public ImageLoader imageLoader;
	private static LayoutInflater inflater = null;
	String auth_token = "";
	FollowListActivity listActivity;
	
	public FollowListAdapter(Context context,Activity activity) {
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		SharedPreferences preferences = context.getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		auth_token = preferences.getString("auth_token", "");
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}
	
	public FollowListAdapter(Context context,Activity activity,FollowListActivity listActivity) {
		this(context,activity);	
		this.listActivity = listActivity;
	}

	public int getCount() {
		return followList.size();
	}

	public Object getItem(int position) {
		return followList.get(position);
	}

	public long getItemId(int position) {
		return followList.get(position).hashCode();
	}
	
	public void setItems(List<User> feedList, List<Boolean> followFinalList){
		this.followList = feedList;
		this.followFinalList = followFinalList;
	}
	
	public List<Boolean> getFinalFollowList() {
		return this.followFinalList;
	}

	public View getView(final int index, View view, ViewGroup parent) {
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.follow_row, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		
		String name = followList.get(index).name;
		String full_name = followList.get(index).fullName;
		String imageurl = followList.get(index).profileImageURL;
		String followStatus = followList.get(index).followStatus;
		String iscurrentUser = followList.get(index).isCurrentUser;
		
		TextView nameView =(TextView)row.findViewById(R.id.profileName);
		TextView fullNameView = (TextView)row.findViewById(R.id.fullNameofUser);
		nameView.setText(name);
		if(!full_name.equals("null"))
			fullNameView.setText(full_name);
		
		RelativeLayout rel = (RelativeLayout)row.findViewById(R.id.mainLayout);
		
		rel.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				try{
					listActivity.isUpdate = true;
				}catch (Exception e) {
					e.printStackTrace();
				}
				Intent i = new Intent(activity, FriendsProfile.class);
				i.putExtra("user_id", followList.get(index).id);
				activity.startActivity(i);
			}
		});
		
		ImageView imageView = (ImageView)row.findViewById(R.id.listProfileImage);
		imageLoader.DisplayImage(imageurl, imageView);
		
		final Button followButton = (Button)row.findViewById(R.id.FollowButton);
		
		followButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if(followButton.getTag().equals("Follow")){
					
					new AsyncTask<Void,Void,Void>() {
						
						protected void onPreExecute() {
							followButton.setTag("Unfollow");
							followButton.setBackgroundResource(R.drawable.unfollow);
						};

						@Override
						protected Void doInBackground(Void... params) {
							followFinalList.remove(index);
							followFinalList.add(index, true);
							return null;
						}
						
						protected void onPostExecute(Void result) {
								followButton.invalidate();
						};
					}.execute();
				}else if(followButton.getTag().equals("Unfollow")){
					
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setMessage("Are you sure you want to Unfollow "+followList.get(index).name+"?")
					       .setCancelable(true)
					       .setPositiveButton("Unfollow", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   new AsyncTask<Void,Void, Void>() {
										
										protected void onPreExecute() {
											followButton.setTag("Follow");
											followButton.setBackgroundResource(R.drawable.follow);
										};

										@Override
										protected Void doInBackground(Void... params) {
											followFinalList.remove(index);
											followFinalList.add(index, false);
											return null;
										}
										
										protected void onPostExecute(Void result) {
											followButton.invalidate();
										};
									}.execute();
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
		
		if(followStatus.equals("false")){
			followButton.setTag("Follow");
			followButton.setBackgroundResource(R.drawable.follow);
		}else{
			followButton.setTag("Unfollow");
			followButton.setBackgroundResource(R.drawable.unfollow);
		}
		if(iscurrentUser.equals("true")){
			followButton.setVisibility(View.INVISIBLE);
		}else{
			followButton.setVisibility(View.VISIBLE);
		}
		
		return row;
	}
}