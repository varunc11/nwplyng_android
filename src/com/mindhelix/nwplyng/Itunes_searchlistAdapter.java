package com.mindhelix.nwplyng;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Itunes_searchlistAdapter extends BaseAdapter {
	
	private Context context;
	private Activity activity;
	private List<Itunes_data> ItunesDataList = Collections.emptyList();
	private static LayoutInflater inflater = null;
	
	public Itunes_searchlistAdapter(Context context,Activity activity) {
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return ItunesDataList.size();
	}

	public Object getItem(int position) {
		return ItunesDataList.get(position);
	}

	public long getItemId(int position) {
		return ItunesDataList.get(position).hashCode();
	}
	
	public void setItems(List<Itunes_data> ItunesDataList){
		this.ItunesDataList = ItunesDataList;
	}
	

	public View getView(final int index, View view, ViewGroup parent) {
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.itunesadapter, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		final String text = ItunesDataList.get(index).censoredName;
		final String artist = ItunesDataList.get(index).artistName;
		
		
		TextView titleText =((TextView)row.findViewById(R.id.title)); 
		titleText.setText(text);
		
		TextView artistName =((TextView)row.findViewById(R.id.artist));
		artistName.setText(artist);
		
		return row;
	}
}
