package com.mindhelix.nwplyng;

public class Itunes_data {
	
	String tracknames;
	String preview_tracks;
	String artistName;
	String downloadUrl;
	String trackID;
	String artistID;
	String censoredName;
	
	public Itunes_data(String trackname,String preview_track,String artist,String downloadURL, String trackId,String artistID, String censoredName) {
		this.tracknames = trackname;
		this.preview_tracks = preview_track;
		this.artistName = artist;
		this.downloadUrl = downloadURL;
		this.trackID= trackId;
		this.artistID = artistID;
		this.censoredName = censoredName;
	}
}

