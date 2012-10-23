package com.mindhelix.nwplyng;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FoursquareSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;
import com.mindhelix.twitter.TwitterApp;
import com.mindhelix.twitter.TwitterSession;

public class PlayItems extends Activity {
	
	Button postPlay;
	Button changeTrack;
	ImageView photoBtn;
	ToggleButton postFB, postFS, postTW;
	Button goiTunes;
	
	TextView ituneTrackName;
	TextView ituneArtistName;
	TextView youtubeVideoName;
	TextView locationName;
	
	EditText comment;
	
	SharedPreferences preferences;
	
	RelativeLayout youtubeLayout, locationLayout;
	RelativeLayout youtubeOverlay, locationOverlay;
	
	public static final int FACEBOOK_SELECT = 1;
	public static final int TRACK_SELECT = 2;
	public static final int LOCATION_SELECT = 3;
	public static final int CAMERA_SELECT = 4;
	public static final int GALLERY_SELECT = 5;
	public static final int VIDEO_SELECT = 6;
	
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public static final int DIALOG_CONNECT = 1;
	
	public String itunes_track_name = "", itunes_censoredName = "", itunes_artist, itunes_preview_url = "", itunes_download_url = "", itunes_trackID = "", itunes_artistID = "";
	public String video_title = "", video_thumbnail_url = "", videoID = "",videoDuration="",videoUploadedOn="";
	public String lattitude_string = "", longitude_string = "", places = "", place_id = "", files = "";
	public String selectedImagePath = "";
	
	String auth_token = "";
	
	static Uri capturedImageUri = null;
	File file = null;
	
	final CharSequence[] photo_dialog_options1 = {"Take Photo", "Select Image from Gallery", "Cancel"};
	final CharSequence[] photo_dialog_options2 = {"Take Photo", "Select Image from Gallery", "Remove Photo", "Cancel"};
	
	boolean photoTaken = false;
	boolean isLocationSelected = false;
	boolean isVideoSelected = false;
	
	Bitmap bitmap;
	
	AlertDialog.Builder builder, usedAccountAlertBuilder;
	AlertDialog alert, usedAccountAlert;
	
	private ProgressDialog progressDialog;
	
	Facebook fb;
	TwitterApp twitter;
	FoursquareApp fsqApp;
	
	boolean isCalled = false;
	
	Button resetButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_play_screen1);
		
		preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		final Editor preferenceEditor = preferences.edit();
		
		auth_token = preferences.getString("auth_token", "");
		
		Bundle extras = this.getIntent().getExtras();
		
		itunes_track_name = extras.getString("trackName");
		itunes_artist = extras.getString("artist");
		itunes_preview_url = extras.getString("previewUrl");
		itunes_download_url = extras.getString("downloadUrl");
		itunes_trackID = extras.getString("trackId");
		itunes_artistID = extras.getString("artistId");
		itunes_censoredName = extras.getString("censoredName");
		
		fb = new Facebook(Login.APP_ID);
		SessionStore.restore(fb, this);
		
		if (fb.isSessionValid()) {
			//mFacebookBtn.setChecked(true);
			String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;
		}
		
		twitter = new TwitterApp(PlayItems.this, Login.CONSUMER_KEY, Login.CONSUMER_SECRET);
		
		fsqApp = new FoursquareApp(PlayItems.this, Login.CLIENT_ID, Login.CLIENT_SECRET);
		
		initializeUsedWidgets();
		
		if(preferences.getString("fb_button_status", "off").equals("on")){
			postFB.setChecked(true);
		}
		
		if(preferences.getString("twitter_button_status", "off").equals("on")){
			postTW.setChecked(true);
		}
		
		if(preferences.getString("fsq_button_status", "off").equals("on")){
			postFS.setChecked(true);
		}
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Select");
		
		//set the itunes track name in the first cell
		ituneTrackName.setText(itunes_censoredName);
		ituneArtistName.setText(itunes_artist);
		resetButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("Itunes Json Array", "");
				editor.commit();
				
				Intent intent = new Intent(PlayItems.this, Itunes_searchlistActivity.class);
				intent.putExtra("for_result", false);
				intent.putExtra("back_home", true);
				startActivity(intent);
				PlayItems.this.finish();
			}
		});
		
		//enable or disable facebook post option
		postFB.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(postFB.isChecked()){
					postFB.setChecked(false);
					if(SessionStore.restore(fb, PlayItems.this)){
						postFB.setChecked(true);
						preferenceEditor.putString("fb_button_status", "on");
						preferenceEditor.commit();
					}
					else{
						fb.authorize(PlayItems.this, Login.PERMISSIONS, 1, new FbLoginDialogListener()); //-1
					}
				}
				else {
					preferenceEditor.putString("fb_button_status", "off");
					preferenceEditor.commit();
				}
			}
		});
		
		//enable or disable twitter post option
		postTW.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if (postTW.isChecked()) {
					postTW.setChecked(false);

					if (twitter.hasAccessToken()) {
						postTW.setChecked(true);
						preferenceEditor.putString("twitter_button_status", "on");
						preferenceEditor.commit();
					} else {
						twitter.setListener(new TwitterApp.TwDialogListener() {
							public void onError(String value) {

							}

							public void onComplete(String value) {
								new checkValidTwitterUser().execute();
							}
						});
						twitter.authorize();
					}
				}else{
					preferenceEditor.putString("twitter_button_status", "off");
					preferenceEditor.commit();
				}
			}
		});
		
		//enable or disable fsq button
		
		postFS.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if(postFS.isChecked()){
					postFS.setChecked(false);
					if(fsqApp.hasAccessToken()){
						postFS.setChecked(true);
						preferenceEditor.putString("fsq_button_status", "on");
						preferenceEditor.commit();
					}else{
						fsqApp.setListener(new FoursquareApp.FsqAuthListener() {

							public void onSuccess() {
								if(!isCalled){
									new checkValidFsqUser().execute();
									isCalled = true;
								}
							}

							public void onFail(String error) {

							}
						});
						fsqApp.authorize();
					}
				}else{
					preferenceEditor.putString("fsq_button_status", "off");
					preferenceEditor.commit();
				}
			}
		});
		
		//click to select the youtube video
		youtubeLayout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				Intent i = new Intent(PlayItems.this, SearchListActivity.class);
				startActivityForResult(i, VIDEO_SELECT);
				
			}
		});
		
		youtubeOverlay.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				Intent i = new Intent(PlayItems.this, SearchListActivity.class);
				startActivityForResult(i, VIDEO_SELECT);
				
			}
		});
		
		//click to change the track from itunes list
		changeTrack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				hideSoftKeyInput();
				
				Intent intent = new Intent(PlayItems.this, Itunes_searchlistActivity.class);
				intent.putExtra("for_result", true);
				intent.putExtra("back_home", false);
				startActivityForResult(intent, TRACK_SELECT);
				
			}
		});
		
		comment.setOnEditorActionListener(new OnEditorActionListener() {
			
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					hideSoftKeyInput();
				}
				return false;
			}
		});
		
		postPlay.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				hideSoftKeyInput();
				
				if(CheckNetworkConnection.isNetworkAvailable(PlayItems.this)) {
					new PostPlayDetailsTask().execute();
				}
				else {
					NetworkConnectionErrorMessage.showNetworkError(PlayItems.this, PlayItems.this).show();
				}
				
			}
		});
		
		//click to open the itunes download link in a browser
		goiTunes.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				hideSoftKeyInput();
				
				if(!itunes_download_url.equals("")) {
					final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(itunes_download_url));
					startActivity(intent);
				}
				
			}
		});
		
		locationLayout.setOnClickListener(locationClickListener);
		locationOverlay.setOnClickListener(locationClickListener);
		
		photoBtn.setOnClickListener(photoButtonClickListener);
		
		LayoutInflater inflater = getLayoutInflater();
		View customTitle = inflater.inflate(R.layout.nwpyng_dialog_title, null);
		
		usedAccountAlertBuilder = new AlertDialog.Builder(PlayItems.this);
		usedAccountAlertBuilder.setCustomTitle(customTitle)
			   					.setMessage("Account mis-match. Please try with another account.")
			   					.setCancelable(false)
			   					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			   						public void onClick(DialogInterface dialog, int id) {
			   							dialog.dismiss();
			   						}
			   					});
		
		usedAccountAlert = usedAccountAlertBuilder.create();
		
	}
	
	//take photo and set the thumbnail
	private OnClickListener photoButtonClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			
			if(photoTaken)
			{
				builder.setItems(photo_dialog_options2, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if(item == 0)
				    	{
				    		takePhotoUsingCAM();
				    	}
				    	else if (item == 1)
				    	{
				    		selectImageFromGallery();
				    	}
				    	else if(item == 2) {
				    		photoTaken = false;
				    		photoBtn.setImageResource(R.drawable.take_photo);
				    	}
				    	else
				    		dialog.dismiss();
				    }
				});
				alert = builder.create();
			}
			else
			{
				builder.setItems(photo_dialog_options1, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if(item == 0)
				    		takePhotoUsingCAM();
				    	else if (item == 1)
				    		selectImageFromGallery();
				    	else 
				    		dialog.dismiss();
				    	
				    }
				});
				alert = builder.create();
			}
			alert.show();
			
		}
	};
	
	//get locations foursqaure 
	private OnClickListener locationClickListener = new OnClickListener() {
	    public void onClick(View v) {
	    	Intent i = new Intent(PlayItems.this, NearLocationList.class);
	    	startActivityForResult(i, LOCATION_SELECT);
		}
	};
	    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK) {
			switch (requestCode) {
			case FACEBOOK_SELECT:
				fb.authorizeCallback(requestCode, resultCode, data);
				break;
			
			case VIDEO_SELECT:
				
				Bundle extras = data.getExtras();
				
				video_title = extras.getString("SelectedVideo");
				video_thumbnail_url = extras.getString("thumbNailUrl");
				videoID = extras.getString("videoId");
				videoDuration = extras.getString("videoDuration");
				videoUploadedOn = extras.getString("uploadedOn");
				
				youtubeVideoName.setText(video_title);
				isVideoSelected = true;
				youtubeLayout.setClickable(true);
				youtubeLayout.setVisibility(RelativeLayout.VISIBLE);
				youtubeOverlay.setClickable(false);
				youtubeOverlay.setVisibility(RelativeLayout.INVISIBLE);
				
				break;
			
			case TRACK_SELECT:
				
				Bundle extras1 = data.getExtras();
				
				itunes_track_name = extras1.getString("trackName");
				itunes_artist = extras1.getString("artist");
				itunes_preview_url = extras1.getString("previewUrl");
				itunes_download_url = extras1.getString("downloadUrl");
				itunes_trackID = extras1.getString("trackId");
				itunes_artistID = extras1.getString("artistId");
				
				ituneTrackName.setText(itunes_track_name);
				ituneArtistName.setText(itunes_artist);
				break;
				
			case LOCATION_SELECT:
				places = data.getStringExtra("location");
				lattitude_string = data.getStringExtra("latitude");
				longitude_string = data.getStringExtra("longitude");
				place_id = data.getStringExtra("venue_id");
				locationName.setText(places);
				isLocationSelected = true;
				
				locationLayout.setClickable(true);
				locationLayout.setVisibility(RelativeLayout.VISIBLE);
				locationOverlay.setClickable(false);
				locationOverlay.setVisibility(RelativeLayout.INVISIBLE);
				
				break;
				
			case CAMERA_SELECT:
				try {
						selectedImagePath = Environment.getExternalStorageDirectory() +  "/nwplyng/play_image.jpg";
						
				    	BitmapFactory.Options options = new BitmapFactory.Options();
				    	options.inSampleSize = 5;
				    	
				    	bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(selectedImagePath, options));
				    	
				    	Bitmap rotatedBMP = null;
				    	
				    	ExifInterface exif = new ExifInterface(selectedImagePath);
				    	int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				    	
				    	switch (orientation) {
				         case ExifInterface.ORIENTATION_ROTATE_270:
				             rotatedBMP = rotate(bitmap, 270);
				             break;
				         case ExifInterface.ORIENTATION_ROTATE_180:
				        	 rotatedBMP = rotate(bitmap, 180);
				             break;
				         case ExifInterface.ORIENTATION_ROTATE_90:
				        	 rotatedBMP = rotate(bitmap, 90);
				             break;
				         default:
				             rotatedBMP = bitmap;
				        	 break;
				         }
				    	
				    	photoTaken = true;
				    	photoBtn.setImageBitmap(rotatedBMP);
				} 
				catch (Exception e) {
				    e.printStackTrace();
				}
				
				break;
				
			case GALLERY_SELECT:
				try {
					capturedImageUri = data.getData();
					selectedImagePath = getPath(capturedImageUri);
					
					selectedImagePath = URLDecoder.decode(selectedImagePath);
					
					BitmapFactory.Options options = new BitmapFactory.Options();
			    	options.inSampleSize = 5;
			    	
			    	bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(selectedImagePath, options));
			    	
			    	Bitmap rotatedBMP = null;
			    	
			    	ExifInterface exif = new ExifInterface(selectedImagePath);
			    	int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			    	
			    	switch (orientation) {
			         case ExifInterface.ORIENTATION_ROTATE_270:
			             rotatedBMP = rotate(bitmap, 270);
			             break;
			         case ExifInterface.ORIENTATION_ROTATE_180:
			        	 rotatedBMP = rotate(bitmap, 180);
			             break;
			         case ExifInterface.ORIENTATION_ROTATE_90:
			        	 rotatedBMP = rotate(bitmap, 90);
			             break;
			         default:
			        	 rotatedBMP = bitmap;
			        	 break;
			         }
			    	
			    	photoTaken = true;
			    	photoBtn.setImageBitmap(rotatedBMP);
					
//			    	if(file != null)
//						file.delete();
					file = new File(selectedImagePath);
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				break;

			default:
				break;
			}
			
		}
		
	}
	
	private final class FbLoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
//            SessionStore.save(fb, PlayItems.this);
            new checkValidFacebookUser().execute();
        }
        public void onFacebookError(FacebookError error) {
        	Toast.makeText(PlayItems.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();
        }
        public void onError(DialogError error) {
        	Toast.makeText(PlayItems.this, "Facebook connection failed", Toast.LENGTH_SHORT).show(); 
        }
        public void onCancel() {

        }
    }
	
	//post all the details to server using httppost
	public class PostPlayDetailsTask extends AsyncTask<Void, Void, Void> {
		
		String responseString = "";
		String careerResponseString = "";
		
		int code = 0;
		int careercode = 0;
		
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			Calendar calendar = Calendar.getInstance();
//			2012-08-01T10:11:50Z
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			TimeZone timeZone = calendar.getTimeZone();
			
			// Get the number of hours from GMT
			int rawOffset = timeZone.getRawOffset();
			int hour = rawOffset / (60*60*1000);
			int minute = Math.abs(rawOffset / (60*1000)) % 60;
			
			String currentDate = dateformat.format(calendar.getTime());
			String currentDateWithGMTVariation = currentDate;
			
			if((hour < 10) && (minute < 10)) {
				currentDateWithGMTVariation = currentDate + " GMT+0" +hour+ ":0" + minute;
			}
			else if((hour < 10) && (minute > 10)) {
				currentDateWithGMTVariation = currentDate + " GMT+0" +hour+ ":" + minute;
			}
			else if((hour > 10) && (minute < 10)) {
				currentDateWithGMTVariation = currentDate + " GMT+" +hour+ ":0" + minute;
			}
			else {
				currentDateWithGMTVariation = currentDate + " GMT+" +hour+ ":" + minute;
			}
			
			try {
				HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_PLAY);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_PLAY);
				
				HttpClient client = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(ConstantURIs.CREATE_CHECKIN);
				postRequest.setParams(httpParameters);
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("track_title", itunes_track_name));
	        	nameValuePairs.add(new BasicNameValuePair("itunes_track_id", itunes_trackID));
	        	nameValuePairs.add(new BasicNameValuePair("itunes_artist_id", itunes_artistID));
	        	nameValuePairs.add(new BasicNameValuePair("itunes_download_url", itunes_download_url));
	        	nameValuePairs.add(new BasicNameValuePair("artist", itunes_artist));
	        	nameValuePairs.add(new BasicNameValuePair("itunes_preview_url", itunes_preview_url));
	        	nameValuePairs.add(new BasicNameValuePair("auth_token", auth_token));
		        
		        if(!videoID.equals("")) {
		        	nameValuePairs.add(new BasicNameValuePair("youtube_id", videoID));
		        	nameValuePairs.add(new BasicNameValuePair("video_thumbnail_url", video_thumbnail_url));
		        	nameValuePairs.add(new BasicNameValuePair("video_title", video_title));
		        	nameValuePairs.add(new BasicNameValuePair("youtube_video_duration", videoDuration));
		        	nameValuePairs.add(new BasicNameValuePair("youtube_video_uploaded_on", videoUploadedOn));
		        }
		        
		        if(!places.equals("")) {
		        	nameValuePairs.add(new BasicNameValuePair("venue_lat", lattitude_string));
			        nameValuePairs.add(new BasicNameValuePair("venue_lng", longitude_string));
			        nameValuePairs.add(new BasicNameValuePair("venue_name", places));
			        nameValuePairs.add(new BasicNameValuePair("venue_id", place_id));
		        }
		        
		        if(comment.getText().length() != 0) {
		        	nameValuePairs.add(new BasicNameValuePair("comment", comment.getText().toString()));
		        }
		        
		        if(photoTaken) {
		        	try {
		        		
		        		nameValuePairs.add(new BasicNameValuePair("files", selectedImagePath));
						
		        	}
		        	catch(Exception e) {
		        		e.printStackTrace();
		        	}
		        	
		        }
		        
		        nameValuePairs.add(new BasicNameValuePair("local_time", currentDateWithGMTVariation));
		        
		        if(!photoTaken)
		        	postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        else{
		        	  MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		              for(int index=0; index < nameValuePairs.size(); index++) {
		                  if(nameValuePairs.get(index).getName().equalsIgnoreCase("files")) {
		                      // If the key equals to "image", we use FileBody to transfer the data
		                      entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
		                  } else {
		                      // Normal string data
		                      entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
		                  }
		              }
		              
		              postRequest.setEntity(entity);

		        }
		        
		      //post to facebook
		        HttpResponse response = client.execute(postRequest);
		        code = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				responseString = EntityUtils.toString(entity);
				
				JSONObject jobject = new JSONObject(responseString);
				if(jobject.getString("success").equals("true")) {
				//	if(jobject.getString("valid_play").equals("true")){
						String tiny_url = jobject.getString("tiny_url");
						String Snap_Shot_url =  jobject.getJSONObject("snapshot").getString("original");
						String urlToPost = "http://nwp.fm/"+tiny_url;
						//if facebook button is active
						if(postFB.isChecked()){
							if(SessionStore.restore(fb, PlayItems.this)){
								Bundle bun = new Bundle();
								
								String messageString = "#nwplyng "+itunes_artist+" - "+itunes_track_name;
								if(isLocationSelected)
									messageString += " (@ "+locationName.getText().toString()+") ";
								if(isVideoSelected){
									if(photoTaken){
									 messageString +="[pic]";
									 postPhoto(Snap_Shot_url,messageString+" \'"+comment.getText().toString()+"\' "+urlToPost);
									}
									messageString += " \'"+comment.getText().toString();
									messageString += "\' "+urlToPost;
									bun.putString("message", messageString);
									bun.putString("link", "http://www.youtube.com/watch?v="+videoID);
									fb.request("me/feed", bun, "POST");
								}else if(photoTaken){
									messageString += " \'"+comment.getText().toString()+"\' ";
									
									messageString += " "+urlToPost;
									
									bun.putString("message", messageString);
									if(photoTaken)
										bun.putString("url", Snap_Shot_url);
									fb.request("me/photos", bun, "POST");
								}else{
									if(comment.getText().length() > 0)
										messageString += " \'"+comment.getText().toString()+"\'";
									messageString += " "+urlToPost;
									bun.putString("message", messageString);
									String res =fb.request("me/feed", bun, "POST");
									System.out.print(res);
								}
							}
						}
						
						//if twitter button is active
						if(postTW.isChecked()){
							String tweetMessage = getTruncateMessage(urlToPost,false);
							twitter.updateStatus(tweetMessage);
						}
						
						//if fsq button is active
						if(postFS.isChecked()){
							if(isLocationSelected){
								// create checkin
								String fsqMessage = getTruncateMessage(urlToPost,true);
								fsqApp.checkin(place_id, fsqMessage);
							}
						}
						
					//}
				}
				
				//retreve career progression
				
				try {
					
					HttpParams httpParameters1 = new BasicHttpParams();
			    	HttpConnectionParams.setConnectionTimeout(httpParameters1, ConstantValues.TIME_OUT_VALUE);
			    	HttpConnectionParams.setSoTimeout(httpParameters1, ConstantValues.TIME_OUT_VALUE);
					
					HttpClient client2 = new DefaultHttpClient();
					String completeURL = ConstantURIs.GET_CAREER + "?auth_token=" + auth_token;
					HttpGet getRequest = new HttpGet(completeURL);
					getRequest.setParams(httpParameters1);
					HttpResponse responseSting = client2.execute(getRequest);
					careercode = responseSting.getStatusLine().getStatusCode();
					HttpEntity entity2 = responseSting.getEntity();
					careerResponseString = EntityUtils.toString(entity2);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if((code == 200) && (careercode == 200)) {
				try {
					JSONObject jobject = new JSONObject(responseString);
					if(jobject.getString("success").equals("true")) {
						
						if(photoTaken) {
							photoTaken = false;
//							file.delete();
						}
						
						dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
						
						Intent intent = new Intent(PlayItems.this, PostPlayActivity.class);
						intent.putExtra("responseString", responseString);
						intent.putExtra("careerResponseString", careerResponseString);
						intent.putExtra("artist_name", itunes_artist);
						
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString("Itunes Json Array", "");
						editor.commit();
						
						startActivity(intent);
						PlayItems.this.finish();
					}
					else {
						dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
						Toast.makeText(PlayItems.this, "Failed to post", Toast.LENGTH_SHORT).show();
					}
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public void postPhoto(String snapShotUrl,String message){
		Bundle bun = new Bundle();
		bun.putString("url", snapShotUrl);
		
		bun.putString("message", message);
		bun.putString("no_story","1");
		try {
			String res = fb.request("me/photos", bun, "POST");
			System.out.print(res);
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
        {
			case DIALOG_DOWNLOAD_PROGRESS:
				progressDialog = new ProgressDialog(PlayItems.this);
				progressDialog.setMessage("Posting...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();
				return progressDialog;
			
			case DIALOG_CONNECT:
				progressDialog = new ProgressDialog(PlayItems.this);
				progressDialog.setMessage("Connecting...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();
				return progressDialog;
				
			default:
				return null;
        }
	}
	
	public void takePhotoUsingCAM() {
		file = new File(Environment.getExternalStorageDirectory(),  "/nwplyng/play_image.jpg");
		if(!file.exists()) {
		    try {
		        file.createNewFile();
		    } 
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		else {
			file.delete();
		    try {
		       file.createNewFile();
		    }
		    catch (IOException e) {
		       e.printStackTrace();
		    }
		}
		capturedImageUri = Uri.fromFile(file);
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
		startActivityForResult(i, CAMERA_SELECT);
	}
	
	public void selectImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.setType("image/*");
		startActivityForResult(intent, GALLERY_SELECT);
	}
	
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	
	public void initializeUsedWidgets() {
		
		postPlay = (Button)findViewById(R.id.post_the_play);
		changeTrack = (Button)findViewById(R.id.change_track_btn);
		photoBtn = (ImageView)findViewById(R.id.photo_btn);
		postFB = (ToggleButton)findViewById(R.id.post_facebook);
		postFS = (ToggleButton)findViewById(R.id.post_foursqaure);
		postTW = (ToggleButton)findViewById(R.id.post_twitter);
		goiTunes = (Button)findViewById(R.id.go_itunes);
		
		comment = (EditText)findViewById(R.id.comment_text);
		
		ituneTrackName = (TextView)findViewById(R.id.itunes_track_name);
		ituneArtistName = (TextView)findViewById(R.id.itunes_artist_name);
		youtubeVideoName = (TextView)findViewById(R.id.youtube_video_name);
		locationName =(TextView)findViewById(R.id.location_name);
		
		youtubeLayout = (RelativeLayout)findViewById(R.id.youtubeLayout);
		locationLayout = (RelativeLayout)findViewById(R.id.locationLayout);
		
		youtubeOverlay = (RelativeLayout)findViewById(R.id.youtubeLayoutOverlay);
		locationOverlay = (RelativeLayout)findViewById(R.id.locationLayoutOverlay);
		
		resetButton = (Button)findViewById(R.id.resetButton);
		
	}
	
	private String getTruncateMessage(String tinyUrl, boolean isFsq){
		
		
		int TOTAL_MESSAGE_LENGTH;
		
		if(!isFsq)
			TOTAL_MESSAGE_LENGTH = 140;
		else
			TOTAL_MESSAGE_LENGTH = 200;
		
		String message = comment.getText().toString();
		int lenghtOfTinyUrl=21;
        int lenghtOfYoutube;
        
        if(!isFsq)
        		lenghtOfYoutube = isVideoSelected?21:0;
        else
        		lenghtOfYoutube = isVideoSelected?43:0;

        int lenghtOfPic = photoTaken?6:0;
        int lenghtOfComment=0;

        if(message.length()!=0)
        {   
			lenghtOfComment= message.length()+3;
		}

        int lenghtOflocation=0;

        if(isLocationSelected&&!(isFsq)){      
			lenghtOflocation=places.length()+5;
		}

        int lenghtOfSong = itunes_track_name.length();
        int lenghtOfArtist = itunes_artist.length()+1;
        int lenghtOfsongandArtistSeperator = 3;
        int lenghtOfsign=8;

        int totalLenght = lenghtOfsign + lenghtOfArtist + lenghtOfsongandArtistSeperator + lenghtOfSong + 
        		lenghtOflocation + lenghtOfComment + lenghtOfPic + lenghtOfYoutube + lenghtOfTinyUrl;

        String theTweetMessage=" "+tinyUrl;

        if(message.length()!=0){   
		
			String comment;

            if(totalLenght<=TOTAL_MESSAGE_LENGTH){   
				comment=" \'"+message+"\'";
				theTweetMessage=comment+theTweetMessage;
			}else{   
			
				int difference=totalLenght-TOTAL_MESSAGE_LENGTH;
				int conditionaldifference=(message.length()-(difference+3));

                if(conditionaldifference>10){

                    comment=" \'";
					comment += message.substring(0, message.length()-(difference+5));
					comment += "...\'";
					lenghtOfComment=comment.length();
					theTweetMessage=comment+theTweetMessage;
				}else{
					lenghtOfComment=0;
				}
			}
		}
		totalLenght=lenghtOfsign+lenghtOfArtist+lenghtOfsongandArtistSeperator+lenghtOfSong+lenghtOflocation+lenghtOfComment+lenghtOfPic+lenghtOfYoutube+lenghtOfTinyUrl;

        if(photoTaken){
			theTweetMessage=" [pic]"+theTweetMessage;
		}
		if(isVideoSelected){   
			String youtubeurl= " http://www.youtube.com/watch?v="+videoID;
			theTweetMessage=youtubeurl + theTweetMessage;
		}
		if(isLocationSelected && !(isFsq)){
			String locationname;
			if(totalLenght<=TOTAL_MESSAGE_LENGTH){   
				locationname=" (@ "+places+")";
				theTweetMessage=locationname +theTweetMessage;
			}else{
				int difference=totalLenght-TOTAL_MESSAGE_LENGTH;
				int conditionaldifference=(places.length()-(difference+5));
				if(conditionaldifference>10){
					locationname=" (@ ";
					locationname = locationname+ places.substring(0,(places.length()-(difference+4)));
					locationname = locationname +"...)";
					lenghtOflocation = locationname.length();
					theTweetMessage = locationname+theTweetMessage;
				}else{
					lenghtOflocation=0;
				}
			}
		}
		totalLenght=lenghtOfsign+lenghtOfArtist+lenghtOfsongandArtistSeperator+lenghtOfSong+lenghtOflocation+lenghtOfComment+lenghtOfPic+lenghtOfYoutube+lenghtOfTinyUrl;

        String songname;

        if(totalLenght<=TOTAL_MESSAGE_LENGTH){   
			songname=" - "+itunes_track_name;
			theTweetMessage=songname+theTweetMessage;
		}else{   
			int difference=totalLenght-TOTAL_MESSAGE_LENGTH;
			int conditionaldifference=itunes_track_name.length()-(difference+3);
			if(conditionaldifference>10){
				songname=" - ";
				songname = songname+ itunes_track_name.substring(0,itunes_track_name.length()-(difference+3));
				songname=songname +"...";
				lenghtOfSong=songname.length()-3;
				theTweetMessage=songname + theTweetMessage;
			}else{
				lenghtOfSong=0;
				lenghtOfsongandArtistSeperator=0;
			}

        }
		totalLenght=lenghtOfsign+lenghtOfArtist+lenghtOfsongandArtistSeperator+lenghtOfSong+lenghtOflocation+lenghtOfComment+lenghtOfPic+lenghtOfYoutube+lenghtOfTinyUrl;
		
		String  artist;
		if(totalLenght<=TOTAL_MESSAGE_LENGTH){   
			artist=" "+itunes_artist;
			theTweetMessage=artist+theTweetMessage;
		}else{   
			int difference=totalLenght-TOTAL_MESSAGE_LENGTH;
			int conditionaldifference=(itunes_artist.length()-(difference+4));
			if(conditionaldifference>10){
				artist=" ";
				artist=artist +itunes_artist.substring(0,itunes_artist.length()-(difference+4));
				artist=artist +"...";
				lenghtOfArtist=artist.length();
				theTweetMessage=artist+theTweetMessage;
				}else{
					lenghtOfArtist=0;
				}
		}
		
		theTweetMessage="#nwplyng"+theTweetMessage;
		totalLenght=lenghtOfsign+lenghtOfArtist+lenghtOfsongandArtistSeperator+lenghtOfSong+lenghtOflocation+lenghtOfComment+lenghtOfPic+lenghtOfYoutube+lenghtOfTinyUrl;     
		
		return theTweetMessage;
	}
	
	class checkValidFacebookUser extends AsyncTask<Void, Void, Void> {
		SharedPreferences sp = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		Editor editor = sp.edit();
		boolean isValidLogin = false;
		String current_id = "";
		
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_CONNECT);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String facebook_id = sp.getString("facebook_user_id", "null");
			String fullname = sp.getString("current user fullname", "");
			try{
				JSONObject obj = Util.parseJson(fb.request("me"));
				current_id = obj.getString("id");
				if(current_id.equals(facebook_id)){
					isValidLogin = true;
				}else if(facebook_id.equals("null")){
					HttpService service = new HttpService();
					if(service.isValidUser(current_id, "facebook_id", fullname)){
						isValidLogin = true;
						//update id 
						service.updateSocialId(auth_token, current_id, "facebook_id");
					}else{
						SessionStore.clear(PlayItems.this);
						fb.logout(PlayItems.this);
					}
				}else{
					SessionStore.clear(PlayItems.this);
					fb.logout(PlayItems.this);
				}
			}catch(Exception e){
				
			} catch (FacebookError e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			dismissDialog(DIALOG_CONNECT);
			
			if(isValidLogin){
				postFB.setChecked(true);
				editor.putString("fb_button_status", "on");
				editor.commit();
				SessionStore.save(fb, PlayItems.this);
				
			}else{
				usedAccountAlert.show();
				postFB.setChecked(false);
				editor.putString("fb_button_status", "off");
				editor.commit();
				
			}
		}
	}
	
	class checkValidTwitterUser extends AsyncTask<Void, Void, Void>{
		
		SharedPreferences sp = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		Editor editor = sp.edit();
		boolean isValidLogin = false;
		String current_id = "";
		
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_CONNECT);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String twitter_id = sp.getString("twitter_user_id", "null");
			String fullname = sp.getString("current user fullname", "");
			
			try{
				current_id = Integer.toString(twitter.mTwitter.getId());
				
				if(current_id.equals(twitter_id)){
					isValidLogin = true;
				}else if(twitter_id.equals("null")){
					HttpService service = new HttpService();
					if(service.isValidUser(current_id, "twitter_id", fullname)){
						isValidLogin = true;
						//update
						service.updateSocialId(auth_token, current_id, "twitter_id");
					}else{
						TwitterSession session = new TwitterSession(PlayItems.this);
						session.resetAccessToken();
					}
				}else{
					TwitterSession session = new TwitterSession(PlayItems.this);
					session.resetAccessToken();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissDialog(DIALOG_CONNECT);
			if(isValidLogin){
				postTW.setChecked(true);
				editor.putString("twitter_button_status", "on");
				editor.commit();
				
			}else{
				usedAccountAlert.show();
				CookieSyncManager.createInstance(PlayItems.this); 
		    	CookieManager cookieManager = CookieManager.getInstance();
		    	cookieManager.removeAllCookie();
				
				twitter = new TwitterApp(PlayItems.this, Login.CONSUMER_KEY, Login.CONSUMER_SECRET);
			}
		}
	}
	
	class checkValidFsqUser extends AsyncTask<Void, Void, Void>{

		SharedPreferences sp = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
		Editor editor = sp.edit();
		boolean isValidLogin = false;
		boolean isException = false;
		String current_id = "";
		
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_CONNECT);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
			String fsq_id = sp.getString("forsqure_user_id", "null");
			String fullname = sp.getString("current user fullname", "");
			current_id = fsqApp.getUserId();
			if(fsq_id.equals(current_id)){
				isValidLogin = true;
			}else if(fsq_id.equals("null")){
				HttpService service = new HttpService();
				if(service.isValidUser(current_id, "foursquare_id", fullname)){
					isValidLogin = true;
					service.setPhotoUrl(fsqApp.getPhotoUrl());
					service.updateSocialId(auth_token, current_id, "foursquare_id");
				}else{
					FoursquareSession session = new FoursquareSession(PlayItems.this);
					session.resetAccessToken();
				}
			}else{
				FoursquareSession session = new FoursquareSession(PlayItems.this);
				session.resetAccessToken();
				
				
			}
			}catch(Exception e){
				e.printStackTrace();
				isException = true;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissDialog(DIALOG_CONNECT);
			if(isException){
				return;
			}
			if(isValidLogin){
				postFS.setChecked(true);
				editor.putString("fsq_button_status", "on");
				editor.commit();
			}else{
				usedAccountAlert.show();
				isCalled = false;
				CookieManager cookieManager = CookieManager.getInstance();
		    	cookieManager.removeAllCookie();
				fsqApp = new FoursquareApp(PlayItems.this, Login.CLIENT_ID, Login.CLIENT_SECRET);
			}
		}
		
	}
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent intent = new  Intent(PlayItems.this, Itunes_searchlistActivity.class);
        	intent.putExtra("for_result", false);
            startActivity(intent);
            PlayItems.this.finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
	
	public void hideSoftKeyInput() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
	}
	
  public static Bitmap rotate(Bitmap b, int degrees) {
  if (degrees != 0 && b != null) {
      Matrix m = new Matrix();
      m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
      try {
          Bitmap b2 = Bitmap.createBitmap(
                  b, 0, 0, b.getWidth(), b.getHeight(), m, true);
          if (b != b2) {
              b.recycle();
              b = b2;
          }
      } catch (OutOfMemoryError ex) {
         throw ex;
      }
  }
  return b;
}
	
}
