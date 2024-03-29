package com.mindhelix.nwplyng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url, Context context){
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences preferences = context.getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		
		//http post
		try
		{
			HttpClient httpclient=new DefaultHttpClient();
			HttpGet httppost=new HttpGet(url);
			HttpResponse response=httpclient.execute(httppost);
			HttpEntity entity=response.getEntity();
			is=entity.getContent();
		}
		catch (Exception e) {
			 Log.e("log_tag", "Error in http connection "+e.toString());
		}
		//convert response to string
	    try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	            
	            SharedPreferences.Editor editor = preferences.edit();
	            editor.putString("Itunes Json Array", result);
	            editor.commit();
	            
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    
	    try{
	    	
            jArray = new JSONObject(result);       
	    }catch(JSONException e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }
    
		return jArray;
		
		
	}
	
	public static JSONObject getJSONfromURL(String url){
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		
		//http post
		try
		{
			HttpClient httpclient=new DefaultHttpClient();
			HttpGet httppost=new HttpGet(url);
			HttpResponse response=httpclient.execute(httppost);
			HttpEntity entity=response.getEntity();
			is=entity.getContent();
		}
		catch (Exception e) {
			 Log.e("log_tag", "Error in http connection "+e.toString());
		}
		//convert response to string
	    try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	            
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    
	    try{
	    	
            jArray = new JSONObject(result);       
	    }catch(JSONException e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }
    
		return jArray;
		
		
	}
	
	public static JSONArray getJSONArrayfromURL(String url){
		InputStream is = null;
		String result = "";
		JSONArray jArray = null;
		//http post
		try
		{
			HttpClient httpclient=new DefaultHttpClient();
			HttpGet httppost=new HttpGet(url);
			HttpResponse response=httpclient.execute(httppost);
			HttpEntity entity=response.getEntity();
			is=entity.getContent();
		}
		catch (Exception e) {
			 Log.e("log_tag", "Error in http connection "+e.toString());
		}
		//convert response to string
	    try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    
	    try{
	    	
            jArray = new JSONArray(result);            
	    }catch(JSONException e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }
    
		return jArray;
		
		
	}

}
