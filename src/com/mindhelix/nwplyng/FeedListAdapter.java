package com.mindhelix.nwplyng;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedListAdapter extends BaseAdapter {
		
	private Context context;
	private Activity activity;
	private List<FeedListItemData> feedList = Collections.emptyList();
	public ImageLoader imageLoader;
	private static LayoutInflater inflater = null;
	
	private String timeDuration = "";
	
	public FeedListAdapter(Context context, Activity activity)
	{
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
	
	public void setItems(List<FeedListItemData> feedList){
		this.feedList = feedList;
//		notifyDataSetChanged();
	}

	public View getView(final int index, View view, ViewGroup parent) {
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.feed_list_item, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		
		ImageView friendUserImage = (ImageView)row.findViewById(R.id.friendProfilePic);
		TextView friendUserName = (TextView)row.findViewById(R.id.friendUserName);
		ImageView videoIcon = (ImageView)row.findViewById(R.id.videoIcon);
		ImageView photoIcon = (ImageView)row.findViewById(R.id.photoIcon);
		TextView songNArtist = (TextView)row.findViewById(R.id.song_n_Artist);
		TextView timeNLocation = (TextView)row.findViewById(R.id.time_n_Location);
		TextView trackComment = (TextView)row.findViewById(R.id.commentForTrack);
		
//		show photo n video icon if value is present
		if(feedList.get(index).youtube_id.equals("null"))
		{
			videoIcon.setVisibility(ImageView.INVISIBLE);
		}
		else
		{
			videoIcon.setVisibility(ImageView.VISIBLE);
		}
		
		if(feedList.get(index).snapshot_filename.equals(ConstantURIs.MISSING_IMAGE))	
		{
			photoIcon.setVisibility(ImageView.INVISIBLE);
		}
		else
		{
			photoIcon.setVisibility(ImageView.VISIBLE);
		}
		
		//show friend's name
		friendUserName.setText(feedList.get(index).username);
		//show artist and track name
		songNArtist.setText(feedList.get(index).title);
		
		if(feedList.get(index).comment != "null")
			trackComment.setText(feedList.get(index).comment);
		else
			trackComment.setText("");
		
		//Convert timestamp to actual time and concatenate with location string if given
		timeDuration = CalculateTime.compareAndConvertDate(feedList.get(index).created_at);
		
		//Check if location is null before setting the text
		if(feedList.get(index).place.equals("null"))
			timeNLocation.setText(timeDuration);
		else
			timeNLocation.setText(timeDuration + " @ " + feedList.get(index).place);
		
		//Call method to load images from the url
		imageLoader.DisplayImage(feedList.get(index).profile_image_url, friendUserImage);
		
		return (row);
	}

}
