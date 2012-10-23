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

public class RecordUnlockAdapter extends BaseAdapter {
	
	private Context context;
	private Activity activity;
	private List<RecordUnlockData> recordList = Collections.emptyList();
	private static LayoutInflater inflater = null;
	
	public RecordUnlockAdapter(Context context, Activity activity)
	{
		this.context = context;
		this.activity = activity;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return recordList.size();
	}

	public Object getItem(int position) {
		return recordList.get(position);
	}

	public long getItemId(int position) {
		return recordList.get(position).hashCode();
	}
	
	public void setItems(List<RecordUnlockData> recordList){
		this.recordList = recordList;
	}
	
	public View getView(final int index, View view, ViewGroup parent) {
		RelativeLayout row;
		if(view == null)
		{
			row = new RelativeLayout(context);
			inflater.inflate(R.layout.record_unlock_item, row, true);
		}
		else
		{
			row = (RelativeLayout)view;
		}
		
		ImageView pick = (ImageView)row.findViewById(R.id.recordDialogPick);
		TextView name = (TextView)row.findViewById(R.id.recordDialogName);
		TextView description = (TextView)row.findViewById(R.id.recordDialogDescription);
		
		name.setText(recordList.get(index).pickName);
		description.setText(recordList.get(index).pickDescription);
		
		if(recordList.get(index).pickName.equals("#nwplyng")) {
			
			pick.setBackgroundResource(R.drawable.nwplyng);
			
		}
		else if(recordList.get(index).pickName.equals("Beatlemania")) {
			
			pick.setBackgroundResource(R.drawable.beatlemania);
			
		}
		else if(recordList.get(index).pickName.equals("Best of")) {
			
			pick.setBackgroundResource(R.drawable.best_of);
			
		}
		else if(recordList.get(index).pickName.equals("BillBoard")) {
			
			pick.setBackgroundResource(R.drawable.billboard);

		}
		else if(recordList.get(index).pickName.equals("Busted")) {

			pick.setBackgroundResource(R.drawable.busted);

		}
		else if(recordList.get(index).pickName.equals("DJ")) {
			
			pick.setBackgroundResource(R.drawable.dj);

		}
		else if(recordList.get(index).pickName.equals("Eargasm")) {
			
			pick.setBackgroundResource(R.drawable.eargasm);

		}
		else if(recordList.get(index).pickName.equals("Encore")) {
			
			pick.setBackgroundResource(R.drawable.encore);

		}
		else if(recordList.get(index).pickName.equals("Fan")) {
			
			pick.setBackgroundResource(R.drawable.fan);

		}
		else if(recordList.get(index).pickName.equals("Grammy")) {
			
			pick.setBackgroundResource(R.drawable.grammy);

		}
		else if(recordList.get(index).pickName.equals("Groupie")) {
			
			pick.setBackgroundResource(R.drawable.groupie);

		}
		else if(recordList.get(index).pickName.equals("HBD")) {
			
			pick.setBackgroundResource(R.drawable.hbd);

		}
		else if(recordList.get(index).pickName.equals("iMusic")) {
			
			pick.setBackgroundResource(R.drawable.imusic);

		}
		else if(recordList.get(index).pickName.equals("Junkie")) {
			
			pick.setBackgroundResource(R.drawable.junkie);

		}
		else if(recordList.get(index).pickName.equals("Music Executive")) {
			
			pick.setBackgroundResource(R.drawable.music_executive);

		}
		else if(recordList.get(index).pickName.equals("Night Owl")) {
			
			pick.setBackgroundResource(R.drawable.night_owl);

		}
		else if(recordList.get(index).pickName.equals("On The Road")) {
			
			pick.setBackgroundResource(R.drawable.on_the_road);

		}
		else if(recordList.get(index).pickName.equals("Punk")) {
			
			pick.setBackgroundResource(R.drawable.punk);

		}
		else if(recordList.get(index).pickName.equals("Single")) {
			
			pick.setBackgroundResource(R.drawable.single);

		}
		else if(recordList.get(index).pickName.equals("Top 40")) {
			
			pick.setBackgroundResource(R.drawable.top40);

		}
		else if(recordList.get(index).pickName.equals("VIP Access")) {
			
			pick.setBackgroundResource(R.drawable.vip_access);

		}
		else if(recordList.get(index).pickName.equals("WMD")) {
			
			pick.setBackgroundResource(R.drawable.wmd);
			
		}
		
		return (row);
	}

}
