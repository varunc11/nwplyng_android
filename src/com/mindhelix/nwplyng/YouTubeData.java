package com.mindhelix.nwplyng;

/*
 * Created By MindHelix Technologies on 11/07/12
 * 
 * Class to store data from the youtube search details
 * 
 */

public class YouTubeData {
	
	String title;
	String thumbnailURL;
	String rtspLink;
	String videoId;
	String uploadedTime;
	String duration;
	String seconds;
	String uploadedOn;

	public YouTubeData(String title,String thumbnailURL,String rtspLink, String videoId,String uploaded,String duration) {
		
		this.title = title;
		this.thumbnailURL = thumbnailURL;
		this.rtspLink = rtspLink;
		this.videoId = videoId;
		this.uploadedTime = CalculateTime.ConvertDate(uploaded);
		this.seconds = duration;
		
		this.uploadedOn = uploaded;
		int totalSecs = 0;
		try{
			totalSecs = Integer.parseInt(duration);
		}catch(Exception e){
			this.duration = "";
			return;
		}
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;
		if(hours == 0)
		{
			if(minutes < 10)
			{
				if(seconds < 10)
					this.duration = "0" + minutes + ":0" + seconds;
				else
					this.duration  = "0" + minutes + ":" + seconds;
			}
			else
			{
				if(seconds < 10)
					this.duration = minutes + ":0" + seconds;
				else
					this.duration  = minutes + ":" + seconds;
			}
		}
		else
			this.duration  = hours + ":" + minutes + ":" + seconds;
	}
}
