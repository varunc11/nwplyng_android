package com.mindhelix.nwplyng;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class UniqueNameInput extends Activity {
	
	String name = "";
	AlertDialog alertDialog;
	SharedPreferences preferences;
	
	String social_type = "";
	public String social_media = "";
	public String social_id = "";
	public String fullName = "";
	
	public String photoUrl;
	
	TextView availabilityText;
	EditText uniqueNameText;
	Button goButton;
	
	ProgressDialog progressDialog;
	ProgressBar progressBar;
	
	CountDownTimer countDown;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uniquename);
        
        preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
        
        fullName = preferences.getString("fullname", "");
        
        uniqueNameText = (EditText)findViewById(R.id.uniqueNameInputBox);
        availabilityText = (TextView)findViewById(R.id.availableText);
        goButton = (Button)findViewById(R.id.goButton);
        progressBar = (ProgressBar)findViewById(R.id.activityIndicator_uniquename);
        
        goButton.setVisibility(Button.INVISIBLE);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        goButton.setEnabled(false);
        
        uniqueNameText.setInputType(uniqueNameText.getInputType()
        	    | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        	    | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        social_type = preferences.getString("social_type", "");
        
        if(social_type.equals("facebook"))
        {
        	social_media = "facebook_id";
        	social_id = preferences.getString("facebookUserId", "");
        }	
        else if(social_type.equals("twitter"))
        {
        	social_media = "twitter_id";
        	social_id = preferences.getString("twitterUserId", "");
        }
        else
        {
        	social_media = "foursquare_id";
        	social_id = preferences.getString("foursqureUserId", "");
        	photoUrl = preferences.getString("foursqureUserPhoto", "");
        }
        
        countDown = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                //update the UI with the new count
            }

            public void onFinish() {
                	availabilityText.setText("");
           }
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(UniqueNameInput.this);
        builder.setTitle("Invalid Username");
        builder.setMessage("Please choose a valid username consisting of 1-12 characters from the following set [a-z 0-9]");
        builder.setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
        
        alertDialog = builder.create(); 
        
        uniqueNameText.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
					goButton.setVisibility(Button.INVISIBLE);
					goButton.setEnabled(false);
				return false;
			}
		});
        
        uniqueNameText.setOnEditorActionListener(new OnEditorActionListener() {
			
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					
					goButton.setVisibility(Button.INVISIBLE);
					goButton.setEnabled(false);
					
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(uniqueNameText.getWindowToken(), 0);
					
		            name = uniqueNameText.getText().toString().toLowerCase();
		            
		            //Check is special charecter there or length >12
		            
		            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		            Matcher m = p.matcher(name);
		            boolean b = m.find();
		            
		            if(name.length()>12||b||name.length() == 0){
		            	goButton.setVisibility(Button.INVISIBLE);
		            	goButton.setEnabled(false);
		            	alertDialog.show();
		            	return true;
		            }else{
		            	
		            	if(CheckNetworkConnection.isNetworkAvailable(UniqueNameInput.this)) {
		            		new LookUpUserName().execute();
		            	}
		            	else {
		            		NetworkConnectionErrorMessage.showNetworkError(UniqueNameInput.this, UniqueNameInput.this).show();
		            	}
		            }
					return true;
				}
				return false;
			}
		});
        
        goButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(CheckNetworkConnection.isNetworkAvailable(UniqueNameInput.this)) {
					new CreateNewUser().execute();
				}
				else {
					NetworkConnectionErrorMessage.showNetworkError(UniqueNameInput.this, UniqueNameInput.this).show();
				}
				
			}
		});
        
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent intent = new Intent(UniqueNameInput.this, Login.class);
        	startActivity(intent);
        	UniqueNameInput.this.finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    
    //used to check if username already exist
    private class LookUpUserName extends AsyncTask<Void, Void, Void>{
    	
    	String availability;
    	int responsecode = 0;

    	@Override
		protected void onPreExecute() {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			try{
				
				HttpParams httpParameters = new BasicHttpParams();
		    	HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
		    	HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
				
				HttpClient client = new DefaultHttpClient();
				String completeURLString = ConstantURIs.UNAME_LOOKUP + "?name=" + name;
				HttpGet request = new HttpGet(completeURLString);
				HttpResponse response = client.execute(request);
				responsecode = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				String resultString = EntityUtils.toString(entity);
				
				if(responsecode == 200) {
					JSONObject mainJObj = new JSONObject(resultString);
					availability = mainJObj.getString("availability");
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(responsecode == 200) {
				
				if((availability != null) && (availability.equals("yes"))) {
					
					availabilityText.setText("Available");
					availabilityText.setTextColor(Color.rgb(33, 55, 107)); //21376B
					goButton.setEnabled(true);
					goButton.setVisibility(Button.VISIBLE);
					countDown.start();
				}
				else if(availability != null && (availability.equals("no"))) {
					availabilityText.setText("Not Available. Please try again");
					availabilityText.setTextColor(Color.RED);
					goButton.setEnabled(false);
					goButton.setVisibility(Button.INVISIBLE);
					countDown.start();
				}
				else {
					
					Toast.makeText(UniqueNameInput.this, "Server Error. Please try again.", Toast.LENGTH_SHORT).show();
					goButton.setEnabled(false);
					goButton.setVisibility(Button.INVISIBLE);
				}
				
			}
			
			progressBar.setVisibility(ProgressBar.INVISIBLE);
			
		}
		
    }
    
    
    private class CreateNewUser extends AsyncTask<Void, Void, Void> {
    	
    	String responseMessage, auth_token;
    	boolean success;
    	int responsecode = 0;
    	
    	@Override
		protected void onPreExecute() {
    		progressDialog = ProgressDialog.show(UniqueNameInput.this, "", "Connecting...", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
					HttpParams httpParameters = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
					HttpConnectionParams.setSoTimeout(httpParameters, ConstantValues.TIME_OUT_VALUE);
				
					HttpClient getClient = new DefaultHttpClient();
					HttpPost getPost = new HttpPost(ConstantURIs.CREATE_USER);
					getPost.setParams(httpParameters);
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("name", name));
					nameValuePairs.add(new BasicNameValuePair(social_media, social_id));
					nameValuePairs.add(new BasicNameValuePair("fullname", fullName));
					
					if(social_media.equals("foursquare_id")){
						nameValuePairs.add(new BasicNameValuePair("foursquare_profile_image_url", photoUrl));
					}
					
					getPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = getClient.execute(getPost);
					responsecode = response.getStatusLine().getStatusCode();
					HttpEntity entity = response.getEntity();
					String resultString = EntityUtils.toString(entity);
					
					if(responsecode == 200) {
						
						JSONObject mainJObj = new JSONObject(resultString);
						success = mainJObj.getBoolean("success");
						responseMessage = mainJObj.getString("response");
						if(success)
							auth_token = mainJObj.getString("auth_token");
						
					}
					
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(responsecode == 200) {
				
				if(success) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("auth_token", auth_token);
					editor.putBoolean("mnwplayinglogin", true);
					if(social_type.equals("facebook")){
						 editor.putBoolean("mfblogin", true);
					 }else  if(social_type.equals("twitter")){
						 editor.putBoolean("mtwitterlogin", true);
					 }else{
						 editor.putBoolean("mfsqlogin", true);
					 }
					editor.commit();
					
					progressDialog.dismiss();
					
					Intent intent = new Intent(UniqueNameInput.this, Home.class);
					intent.putExtra("Done Button", false);
					startActivity(intent);
					UniqueNameInput.this.finish();
				}
				else {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(UniqueNameInput.this);
					builder.setTitle("Unsuccessful").setMessage(responseMessage)
					.setPositiveButton("OK", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
						}
					}).show();
				}
				
			}
			
		}
    	
    }
    
}