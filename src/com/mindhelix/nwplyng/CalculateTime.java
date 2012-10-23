package com.mindhelix.nwplyng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CalculateTime {
	
	
	private static double MILLIS_IN_MONTH = 1000 * 60 * 60 * 24 * 7 * 4.348;
	private static double MILLIS_IN_WEEK = 1000 * 60 * 60 * 24 * 7;
	private static long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
	private static long MILLIS_IN_HOUR = 1000 * 60 * 60;
	private static long MILLIS_IN_MINUTE = 1000 * 60;
	private static long MILLIS_IN_SEC = 1000;
	
	
	public static String compareAndConvertDate(String postedDate) {
//		2012-08-01T10:11:50Z
		
		long currentTimeStamp = System.currentTimeMillis();
		
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		long feedTimeStamp = 0;
		String timeDuration = "";
		
		try {  
//			format.setTimeZone(TimeZone.getTimeZone("GMT"));
		    Date date = format.parse(postedDate);  
		    System.out.println(date);
		    feedTimeStamp = date.getTime();
		} 
		catch (Exception e) {  
		    e.printStackTrace();  
		}
		
		int temp;
		
		if((currentTimeStamp-feedTimeStamp) > MILLIS_IN_MONTH)
		{
			temp = (int)((currentTimeStamp - feedTimeStamp)/MILLIS_IN_MONTH);
			if(temp == 1)
				timeDuration = temp + " month ago";
			else
			timeDuration = temp + " months ago";
		}
		else if((currentTimeStamp-feedTimeStamp) > MILLIS_IN_WEEK)
		{
			temp =  (int) ((currentTimeStamp - feedTimeStamp)/MILLIS_IN_WEEK);
			if(temp == 1)
				timeDuration = temp + "week ago";
			else
				timeDuration = temp + " weeks ago";
		}
		else if((currentTimeStamp-feedTimeStamp) > MILLIS_IN_DAY)
		{
			if((currentTimeStamp - feedTimeStamp)/MILLIS_IN_DAY == 1)
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_DAY + " day ago";
			else
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_DAY + " days ago";
				
		}
		else if((currentTimeStamp-feedTimeStamp) > MILLIS_IN_HOUR)
		{
			if((currentTimeStamp - feedTimeStamp)/MILLIS_IN_HOUR == 1)
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_HOUR + " hour ago";
			else
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_HOUR + " hours ago";
		}
		else if((currentTimeStamp-feedTimeStamp) > MILLIS_IN_MINUTE)
		{
			if((currentTimeStamp - feedTimeStamp)/MILLIS_IN_MINUTE == 1)
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_MINUTE + " minute ago";
			else
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_MINUTE + " minutes ago";
		}
		else
		{
			if((currentTimeStamp - feedTimeStamp)/MILLIS_IN_SEC == 1)
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_SEC + " second ago";
			else
				timeDuration = (currentTimeStamp - feedTimeStamp)/MILLIS_IN_SEC + " seconds ago";
		}
		
		return timeDuration;
	}
	
	public static String ConvertDate(String postedDate) {
		
		Date date;
		String timeDuration = "";
		try {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(postedDate);
			timeDuration = "uploaded on " + new SimpleDateFormat("MM/dd/yyyy").format(date);
		} 
		catch (ParseException e) {
			timeDuration = ConvertDateFeeds(postedDate);
			e.printStackTrace();
		}
		
		return timeDuration;
	}
	
	
	public static String ConvertDateFeeds(String postedDate) {
		
		Date date;
		String timeDuration = "";
		try {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(postedDate);
			timeDuration = "uploaded on " + new SimpleDateFormat("MM/dd/yyyy").format(date);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return timeDuration;
	}

}
