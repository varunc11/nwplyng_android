package com.mindhelix.nwplyng;

public class User {
	
	public String id;
	public String name;
	public String profileImageURL;
	public String followStatus;
	public String isCurrentUser;
	public String fullName;
	
	public User(String id, String name, String profileImageURL, String followStatus, String isCurrentUser, String fullName) {
		this.id = id;
		this.name = name;
		this.profileImageURL = profileImageURL;
		this.followStatus = followStatus;
		this.isCurrentUser = isCurrentUser;
		this.fullName = fullName;
	}
}
