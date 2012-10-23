package com.mindhelix.nwplyng;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserRecordDescription extends Activity {
	
	TextView name, description, title, time;
	TextView pickName;
	ImageView pick;
	
	Button back;
	
	String nameString, descriptionString, titleString, timeString;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_record_description);
		
		Bundle extras = this.getIntent().getExtras();
		
		nameString = extras.getString("name");
		descriptionString = extras.getString("description");
		titleString = extras.getString("title");
		timeString = extras.getString("time");
		
		pick = (ImageView)findViewById(R.id.bigPick);
		name = (TextView)findViewById(R.id.pickName);
		description = (TextView)findViewById(R.id.pickDescription);
		title = (TextView)findViewById(R.id.pickUnlockTitle);
		time = (TextView)findViewById(R.id.pickUnlockTime);
		pickName = (TextView)findViewById(R.id.userPickNameDisplay);
		
		back = (Button)findViewById(R.id.backButtonPick);
		
		name.setText(nameString);
		pickName.setText(nameString);
		description.setText(descriptionString);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				UserRecordDescription.this.finish();
			}
		});
		
		if(nameString.equals("#nwplyng")) {
			
			pick.setBackgroundResource(R.drawable.nwplyng);
			
		}
		else if(nameString.equals("iMusic")) {
			
			pick.setBackgroundResource(R.drawable.imusic);
			
		}
		else if(nameString.equals("Single")) {
			
			pick.setBackgroundResource(R.drawable.single);
			
		}
		else if(nameString.equals("Best of")) {
			
			pick.setBackgroundResource(R.drawable.best_of);
			
		}
		else if(nameString.equals("Top 40")) {
			
			pick.setBackgroundResource(R.drawable.top40);
			
		}
		else if(nameString.equals("BillBoard")) {
			
			pick.setBackgroundResource(R.drawable.billboard);
			
		}
		else if(nameString.equals("Encore")) {
			
			pick.setBackgroundResource(R.drawable.encore);
			
		}
		else if(nameString.equals("Grammy")) {
			
			pick.setBackgroundResource(R.drawable.grammy);
			
		}
		else if(nameString.equals("Eargasm")) {
			
			pick.setBackgroundResource(R.drawable.eargasm);
			
		}
		else if(nameString.equals("Junkie")) {
			
			pick.setBackgroundResource(R.drawable.junkie);
			
		}
		else if(nameString.equals("Fan")) {
			
			pick.setBackgroundResource(R.drawable.fan);
			
		}
		else if(nameString.equals("Groupie")) {
			
			pick.setBackgroundResource(R.drawable.groupie);
			
		}
		else if(nameString.equals("Music Executive")) {
			
			pick.setBackgroundResource(R.drawable.music_executive);
			
		}
		else if(nameString.equals("Night Owl")) {
			
			pick.setBackgroundResource(R.drawable.night_owl);
			
		}
		else if(nameString.equals("DJ")) {
			
			pick.setBackgroundResource(R.drawable.dj);
			
		}
		else if(nameString.equals("Busted")) {
			
			pick.setBackgroundResource(R.drawable.busted);
			
		}
		else if(nameString.equals("HBD")) {
			
			pick.setBackgroundResource(R.drawable.hbd);
			
		}
		else if(nameString.equals("Beatlemania")) {
			
			pick.setBackgroundResource(R.drawable.beatlemania);
			
		}
		else if(nameString.equals("VIP Access")) {
			
			pick.setBackgroundResource(R.drawable.vip_access);
			
		}
		else if(nameString.equals("Punk")) {
			
			pick.setBackgroundResource(R.drawable.punk);
			
		}
		else if(nameString.equals("WMD")) {
			
			pick.setBackgroundResource(R.drawable.wmd);
			
		}
		else if(nameString.equals("On the Road")) {
			
			pick.setBackgroundResource(R.drawable.on_the_road);
			
		}
		
		if(!titleString.equals("null"))
			title.setText("Unlocked with "+titleString);
		
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date date =sdf.parse(timeString.substring(0, 10)+" "+timeString.substring(11, 19));
			
			String timeHHMM = new SimpleDateFormat("hh:mm a").format(date);
			String dateString = new SimpleDateFormat("MMMM dd, yyyy").format(date);
			
			time.setText(timeHHMM + " on " + dateString); 
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}

}
