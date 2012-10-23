package com.mindhelix.nwplyng;

public class RecordUnlockData {
	
	public String pickName;
	public String pickDescription;
	
	public static RecordUnlockData record_unlock_data;
	
	public RecordUnlockData(String name, String description) {
		this.pickName = name;
		this.pickDescription = description;
	}

}
