package com.mindhelix.nwplyng;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class HttpService {
	
	public static String api_secret_key= "3485879cdd7b6656151473f5982d8abfc137cbb0";
	private String socialId="";
	private long unixTime;
	private String sha1String;
	private String lookUpResponse= "";
	private String url = "";
	
	public HttpService() {
		
	}
	
	public boolean isValidUser(String socialId,String socialMeadia, String currentUserName){
		boolean isValidUser = false;
		this.socialId = socialId;
		this.unixTime = System.currentTimeMillis();
		String hashSting = unixTime+api_secret_key+socialId;
		
		try {

			MessageDigest cript = MessageDigest.getInstance("SHA-1");
            cript.reset();
            cript.update(hashSting.getBytes("utf8"));
            this.sha1String = new BigInteger(1, cript.digest()).toString(16);
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair(socialMeadia, socialId));
            pairs.add(new BasicNameValuePair("timestamp", Long.toString(unixTime)));
            pairs.add(new BasicNameValuePair("digest", sha1String));
            
            HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ConstantURIs.USER_LOOKUP);
			post.setEntity(new UrlEncodedFormEntity(pairs));
			org.apache.http.HttpResponse response = client.execute(post);
			HttpEntity entity =response.getEntity();
			String result = EntityUtils.toString(entity);
			
			JSONObject obj = new JSONObject(result);
			JSONObject userExist = obj.getJSONObject("user");
			if(userExist == null) {
				isValidUser = false;
				return isValidUser;
			}
			String socialIDfromNwplyng = userExist.getString(socialMeadia);
			String usernameFromNwplyng = userExist.getString("fullname");
			
			if(socialIDfromNwplyng == null)
				isValidUser = true;
			else if(socialId.equals(socialIDfromNwplyng)) {
				if(currentUserName.equals("login"))
					isValidUser = true;
				else if(currentUserName.equals(usernameFromNwplyng)) {
					isValidUser = true;
				}
				else
					isValidUser = false;
			}
			else
				isValidUser = false;
			
			if(isValidUser)
				lookUpResponse = result;
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return isValidUser;
	}
	
	public String getLookUpResponse(){
		return lookUpResponse;
	}
	
	public void saveUserIds(Context context){
		SharedPreferences mp1 = context.getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);;
		SharedPreferences.Editor editor = mp1.edit();
		
		try{
			JSONObject obj = new JSONObject(getLookUpResponse()).getJSONObject("user");
			
			String facebook_id =obj.getString("facebook_id");
			String fsq_id =	obj.getString("foursquare_id");
			String twitter_id = obj.getString("twitter_id");
			
			editor.putString("facebook_user_id", facebook_id);
			editor.putString("twitter_user_id", twitter_id);
			editor.putString("forsqure_user_id", fsq_id);
			
			editor.commit();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	public void setPhotoUrl(String url){
		this.url = url;
	}
	
	public void updateSocialId(String auth_token,String user_id,String social_media){
		
		try{
			HttpClient  client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ConstantURIs.UPDATE_USER_SOCIAL_ID);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
	        pairs.add(new BasicNameValuePair("auth_token",auth_token));
			pairs.add(new BasicNameValuePair(social_media, user_id));
			
			if(social_media.equals("foursquare_id")){
				pairs.add(new BasicNameValuePair("foursquare_profile_image_url", url));
			}
			
			post.setEntity(new UrlEncodedFormEntity(pairs));
	        
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.print(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	 

}
