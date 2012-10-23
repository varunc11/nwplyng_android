package com.mindhelix.nwplyng;

public class NotificationData {
	
	public String message;
	public String created_at;
	public String status;
	public String type;
	public String action_user_id;
	public String profile_image_url;
	public String feed_id;
	
	public static NotificationData notificationData;
	
	public NotificationData(String message, String created_at, String status, String type, String action_user_id, String url, String feed_id) {
		
		this.message = message;
		this.created_at = created_at;
		this.status = status;
		this.type = type;
		this.action_user_id = action_user_id;
		this.profile_image_url = url;
		this.feed_id = feed_id;
		
	}

}
