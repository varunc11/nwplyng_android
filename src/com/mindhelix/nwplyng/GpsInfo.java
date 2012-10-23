package com.mindhelix.nwplyng;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsInfo implements LocationListener{
	
	public static double lat=0;
	public static double lon=0;
	
	private LocationManager locationManager;

	private String bestProvider;

	public GpsInfo(Context context) {
		
		locationManager = (LocationManager)context.getSystemService("location");
		
		Criteria criteria = new Criteria();

		bestProvider = locationManager.getBestProvider(criteria, false);
		
		bestProvider = locationManager.getBestProvider(criteria, false);

		Location location = locationManager.getLastKnownLocation(bestProvider);
		if(location!= null){
			lat = location.getLatitude();
			lon = location.getLongitude();
		}

	}
	
	//Returns the current latitude
	public double getLatitude(){
		return lat;
	}
	
	//Returns the current longitude
	public double getLongitude(){
		return lon;
	}
	
	public void onLocationChanged(Location location) {
		
		if(location!=null){
		lat = location.getLatitude();
		lon = location.getLongitude();
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
