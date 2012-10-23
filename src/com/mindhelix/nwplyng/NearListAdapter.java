package com.mindhelix.nwplyng;

import java.util.Collections;
import java.util.List;

import net.londatiga.fsq.FsqVenue;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NearListAdapter extends BaseAdapter {
		
	private Context context;
	private Activity activity;
	private List<FsqVenue> nearestLocationList = Collections.emptyList();
	private static LayoutInflater inflater = null;
	
	public NearListAdapter(Context context, Activity activity)
	{
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return nearestLocationList.size();
	}

	public Object getItem(int position) {
		return nearestLocationList.get(position);
	}

	public long getItemId(int position) {
		return nearestLocationList.get(position).hashCode();
	}
	
	public void setItems(List<FsqVenue> nearestLocationList){
		this.nearestLocationList = nearestLocationList;
//		notifyDataSetChanged();
	}

	public View getView(final int index, View view, ViewGroup parent) {
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.nearlocationraw, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		TextView location =((TextView)row.findViewById(R.id.location));
		location.setText(nearestLocationList.get(index).name);
		return row;
		
	}
}
