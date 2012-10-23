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

public class ManagesListAdapter extends BaseAdapter {
	
	private Context context;
	private Activity activity;
	private List<ManagesData> managesList = Collections.emptyList();
	private static LayoutInflater inflater = null;
	
	public ManagesListAdapter(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return managesList.size();
	}

	public Object getItem(int position) {
		return managesList.get(position);
	}

	public long getItemId(int position) {
		return managesList.get(position).hashCode();
	}
	
	public void setItems(List<ManagesData> managesList){
		this.managesList = managesList;
	}

	public View getView(int index, View view, ViewGroup parent) {

		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.manages_row, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		
		final String name = managesList.get(index).name;
		
		TextView nameText =((TextView)row.findViewById(R.id.manage_title)); 
		nameText.setText(name);
		
		return row;
	}

}
