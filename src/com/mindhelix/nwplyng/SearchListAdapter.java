package com.mindhelix.nwplyng;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter{
	private Context context;
	private Activity activity;
	private List<YouTubeData> feedList = Collections.emptyList();
	public ImageLoader imageLoader;
	private static LayoutInflater inflater = null;
	
	public SearchListAdapter(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return feedList.size();
	}

	public Object getItem(int position) {
		return feedList.get(position);
	}

	public long getItemId(int position) {
		return feedList.get(position).hashCode();
	}

	public void setItems(List<YouTubeData> feedList){
		this.feedList = feedList;
//		notifyDataSetChanged();
	}
	public View getView(final int index, View view, ViewGroup parent) {
		
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.list_row, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		
		TextView titleText =((TextView)row.findViewById(R.id.title)); 
		titleText.setText(feedList.get(index).title);
		
		final RelativeLayout mainLayout = (RelativeLayout)row.findViewById(R.id.youtube_list_row);
		RelativeLayout textLayout = (RelativeLayout)row.findViewById(R.id.youtubeTextArea);
		
		textLayout.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					 mainLayout.setBackgroundResource(R.drawable.pressed_state);
				 }
				 else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					 mainLayout.setBackgroundResource(R.drawable.not_pressed_state);
				 }
				 
				return false;
			}
		});
		
		textLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				
				returnIntent.putExtra("SelectedVideo", feedList.get(index).title);
				returnIntent.putExtra("thumbNailUrl", feedList.get(index).thumbnailURL);
				returnIntent.putExtra("videoId", feedList.get(index).videoId);
				returnIntent.putExtra("videoDuration", feedList.get(index).seconds);
				returnIntent.putExtra("uploadedOn", feedList.get(index).uploadedOn);
				
				activity.setResult(Activity.RESULT_OK,returnIntent);        
				activity.finish();
			}
		});
		
		TextView durationView =((TextView)row.findViewById(R.id.duration));
		durationView.setText(feedList.get(index).duration);
		
		TextView uploadTimeView =((TextView)row.findViewById(R.id.uploadTime));
		uploadTimeView.setText("("+feedList.get(index).uploadedTime+")");
		
		ImageView thumbnailView=((ImageView)row.findViewById(R.id.thumbNail));
		
		thumbnailView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					 mainLayout.setBackgroundResource(R.drawable.pressed_state);
				 }
				 else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					 mainLayout.setBackgroundResource(R.drawable.not_pressed_state);
				 }
				 
				return false;
			}
		});

		thumbnailView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.youtube.com/watch?v="+feedList.get(index).videoId));
				
				if(isAppInstalled("com.google.android.youtube",activity)) {
                    intent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                }
				activity.startActivity(intent);
			}
		});
		
		imageLoader.DisplayImage(feedList.get(index).thumbnailURL, thumbnailView);	
		
		return row;
	}
	
	public static boolean isAppInstalled(String uri, Context context) {
	    PackageManager pm = context.getPackageManager();
	    boolean installed = false;
	    try {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	        installed = false;
	    }
	    return installed;
	}
}
