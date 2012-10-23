package com.mindhelix.nwplyng;

public class FeedListItemData {
	
	public String title;
	public String comment;
	public String youtube_id;
	public String live;
	public String snapshot_filename;
	public String username;
	public String created_at;
	public String place;
	public String artist_name;
	public String thumbnail_url;
	public String itunes_preview_url;
	public String itunes_download_url;
	public String profile_image_url;
	public String track_name;
	public String career_progression;
	public String user_id;
	public String unique_id;
	public String feed_id;
	public String full_name;
	public String upload_time;
	public String duration;
	public String video_uploaded_On;
	public String video_name;
	
	public static FeedListItemData feed_list_item_data;
	
	
	public FeedListItemData(
			String title, String comment, String youtube_id, String live, String snapshot_filename, String username, String created_at,
			String place, String artist_name, String thumbnail_url, String itunes_preview_url, String itunes_download_url, String profile_image_url,
			String track_name, String career_progression, String user_id, String unique_id, String feed_id, String full_name,String duration,
			String uploadedOn, String videoName) {
		this.title = title;
		this.comment = comment;
		this.youtube_id = youtube_id;
		this.live = live;
		this.snapshot_filename = snapshot_filename;
		this.username = username;
		this.created_at = created_at;
		this.upload_time = CalculateTime.ConvertDateFeeds(created_at);
		this.video_uploaded_On = CalculateTime.ConvertDate(uploadedOn);
		this.place = place;
		this.artist_name = artist_name;
		this.thumbnail_url = thumbnail_url;
		this.itunes_preview_url = itunes_preview_url;
		this.itunes_download_url = itunes_download_url;
		this.profile_image_url = profile_image_url;
		this.track_name = track_name;
		this.career_progression = career_progression;
		this.user_id = user_id;
		this.unique_id = unique_id;
		this.feed_id = feed_id;
		this.full_name = full_name;
		this.video_name = videoName;
		
		if(!duration.equals("null")){
		int totalSecs = Integer.parseInt(duration);
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
		}else{
			this.duration  ="";
		}
	}
	
}
