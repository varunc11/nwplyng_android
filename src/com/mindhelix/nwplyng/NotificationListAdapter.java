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

public class NotificationListAdapter extends BaseAdapter {
	
	private Context context;
	private Activity activity;
	private List<NotificationData> notification = Collections.emptyList();
	public ImageLoader imageLoader;
	private static LayoutInflater inflater = null;

	public NotificationListAdapter(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return notification.size();
	}

	public Object getItem(int position) {
		return notification.get(position);
	}

	public long getItemId(int position) {
		return notification.get(position).hashCode();
	}
	
	public void setItems(List<NotificationData> notification) {
		this.notification = notification;
//		notifyDataSetChanged();
	}

	public View getView(final int index, View view, ViewGroup parent) {
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.notification_list_item, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		
		ImageView imageView = (ImageView)row.findViewById(R.id.notificationImage);
		TextView message = (TextView)row.findViewById(R.id.notificationMessage);
		TextView time = (TextView)row.findViewById(R.id.notificationTime);
		
		//show the notification message
		message.setText(notification.get(index).message);
		
		//convert and set the time
		time.setText(CalculateTime.compareAndConvertDate(notification.get(index).created_at));
		
		//load the images of notification
		imageLoader.DisplayImage(notification.get(index).profile_image_url, imageView);
		
		
		return (row);
	}
	
	

}
