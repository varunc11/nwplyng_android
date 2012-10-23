package com.mindhelix.nwplyng;

import net.londatiga.fsq.FoursquareApp;
import net.londatiga.fsq.FoursquareApp.FsqAuthListener;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;
import com.mindhelix.twitter.TwitterApp;
import com.mindhelix.twitter.TwitterApp.TwDialogListener;

public class Login extends Activity{
	
	
	EditText message;
	
	private Facebook mFacebook;
	private RelativeLayout mFacebookBtn;
	private ProgressDialog mfbProgress;
	private FoursquareApp mFsqApp;
	private ProgressDialog mProgress;
	private TwitterApp mTwitter;
	boolean isSuccess = false;
	
	
	//-----------------------------------------------------Facebook------------------------------------------------------------------------
	public static final String CLIENT_ID = "XF5SZAHOXOXGRIHUWTH20PE2YBSZXHDEWJQATBYITYFSMEDP";
	public static final String CLIENT_SECRET = "Z4H1LNUTJFMRVOCHH5T2EU3RXTN2P01W1TF4CZSH0KHAS2QW";
	public static final String[] PERMISSIONS = new String[] {"user_birthday", "user_about_me", "publish_stream", "user_photos", "email"};
	public static final String APP_ID = "379326712094413";
	
	//-----------------------------------------twitter-------------------------------------------------------------------------------------
	public static final String CONSUMER_KEY = "2CB7yALmETWriKYnu5OiyQ";
	public static final String CONSUMER_SECRET ="UiUkOhm9uRIQWtufuhWmYvPvR3xVeOIlHOEjPD6E";
	
	final Runnable mUpdateTwitterNotification = new Runnable() 
    {
        public void run() 
        {
        	Toast.makeText(getBaseContext(), "Tweet sent !", Toast.LENGTH_LONG).show();
        }
    };
	
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        RelativeLayout twitterLoginButton=(RelativeLayout)findViewById(R.id.twitter_login_layout);
	        RelativeLayout fourSqureButton = (RelativeLayout) findViewById(R.id.foursquare_login_layout);
	        
//	        try {
//	        	   PackageInfo info = getPackageManager().getPackageInfo("com.mindhelix.nwplyng", PackageManager.GET_SIGNATURES);
//	        	   for (Signature signature : info.signatures) {
//	        	        MessageDigest md = MessageDigest.getInstance("SHA");
//	        	        md.update(signature.toByteArray());
//	        	        Log.i("PXR", Base64.encodeBytes(md.digest()));
//	        	   }
//	        	}
//	        	catch (NameNotFoundException e) {}
//	        	catch (NoSuchAlgorithmException e) {}
	        
	        mTwitter = new TwitterApp(this, CONSUMER_KEY, CONSUMER_SECRET);
	        mFacebookBtn =(RelativeLayout)findViewById(R.id.facebook_login_layout);
	   //--------------------------------------------------foursquare----------------------------------------
	        mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);
	       
	        FsqAuthListener listener = new FsqAuthListener() {
	        	
	        	public void onSuccess() {
	        		if(!isSuccess){
	        			new checkFoursqureUserExits().execute();
	        			isSuccess = true;
	        		}
	        		return;
	        	}
	        	
	        	public void onFail(String error) {
	        		
	        	}
	        };
	        mFsqApp.setListener(listener);
	        
	        mProgress = new ProgressDialog(this);
	        mProgress.setMessage("Connecting...");
	        
	        //get access token and user name from foursquare
	        fourSqureButton.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		
	        		if(CheckNetworkConnection.isNetworkAvailable(Login.this)) {
	        			CookieSyncManager.createInstance(Login.this); 
				    	CookieManager cookieManager = CookieManager.getInstance();
				    	cookieManager.removeAllCookie();
				    	
		        		mFsqApp.authorize();
					}
					else {
						NetworkConnectionErrorMessage.showNetworkError(Login.this, Login.this).show();
					}
	        		
	        	}
	        });  
	        
	        mfbProgress	= new ProgressDialog(this);
	        mFacebook = new Facebook(APP_ID);
	        
	        SessionStore.restore(mFacebook, this);
	        
	        if (mFacebook.isSessionValid()) {
				//mFacebookBtn.setChecked(true);
				String name = SessionStore.getName(this);
				name		= (name.equals("")) ? "Unknown" : name;
			}
	        
	        mFacebookBtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					
					if(CheckNetworkConnection.isNetworkAvailable(Login.this))
						onFacebookClick();
					else
						NetworkConnectionErrorMessage.showNetworkError(Login.this, Login.this).show();
				}
			});
	        
	        twitterLoginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					
					if(CheckNetworkConnection.isNetworkAvailable(Login.this)) {
						mTwitter.setListener(mTwLoginDialogListener);
					    
						if (mTwitter.hasAccessToken() == true) {
					         new checkTwitterUserExits().execute();
					     }else{
					            mTwitter.authorize();
					     }
					}
					else {
						NetworkConnectionErrorMessage.showNetworkError(Login.this, Login.this).show();
					}
					
				}
			});
	    }
	 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebook.authorizeCallback(requestCode, resultCode, data);
        
    }
	
    @Override
	protected void onResume() {
		super.onResume();
	}
		
///------------------------------------------------------- FACEBOOK ---------------------------------------------------------
		 	private void onFacebookClick() {
		 			mFacebook.authorize(Login.this, PERMISSIONS, 1, new FbLoginDialogListener());
			}
		    
		    private final class FbLoginDialogListener implements DialogListener {
		        public void onComplete(Bundle values) {
		            SessionStore.save(mFacebook, Login.this);
		            if(CheckNetworkConnection.isNetworkAvailable(Login.this)) {
		            	new checkFacebookUserExits().execute();
		            }
		            else {
		            	NetworkConnectionErrorMessage.showNetworkError(Login.this, Login.this).show();
		            }
		            
		        }
		        public void onFacebookError(FacebookError error) {
		        	Toast.makeText(Login.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();
		        }
		        public void onError(DialogError error) {
		        	Toast.makeText(Login.this, "Facebook connection failed", Toast.LENGTH_SHORT).show(); 
		        }
		        public void onCancel() {

		        }
		    }
			  
				// Twitter Dialog Listner
			    private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
			    	public void onError(String value) {
			            Log.e("TWITTER", value);
			            mTwitter.resetAccessToken();
			        }
			    	public void onComplete(String value) {
			            new checkTwitterUserExits().execute();
			        }
			    };
			    
			    /*
			     * The background tasks to check the Facebook,Twitter  
			     * and Foursqure user already registered with nwplaying 
			     * or not.
			     * 
			     */
			    
			    private class checkFacebookUserExits extends AsyncTask<Void, Void, Void>{
			    	
			    	HttpService service = new HttpService();
			    	String name = "";
			    	String userId ="";
			    	boolean isValidUser = false;
			    	
		        	@Override
		        	protected void onPreExecute() {
		        		mfbProgress.setMessage("Connecting...");
		        		mfbProgress.show();
		        	}

					@Override
					protected Void doInBackground(Void... arg0) {
						try {
							JSONObject obj = Util.parseJson(mFacebook.request("me"));
							name = obj.getString("name");
							userId = obj.getString("id");
							isValidUser = service.isValidUser(userId,"facebook_id", "login");
						} catch (Exception e){
							e.printStackTrace();
						} catch (FacebookError e) {
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						SharedPreferences mp1 = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor=mp1.edit();
						if(isValidUser){
							//push feeds screen
							service.saveUserIds(Login.this);
							editor.putString("lookupresponse", service.getLookUpResponse());
							editor.putBoolean("mnwplayinglogin", true);
							editor.putBoolean("mfblogin", true);
							editor.commit();
							startHomeActivity();
							
						}else{
							//push unique screen
							editor.putString("facebookUserId",userId);
							editor.putString("fullname",name);
							editor.putString("social_type", "facebook");
							editor.commit();
							startUniqueNameActivity();
						}
						mfbProgress.dismiss();
					}
		        	
		        }
			    
			    private class checkTwitterUserExits extends AsyncTask<Void, Void, Void>{
			    	HttpService service = new HttpService();
			    	boolean isValidUser;
			    	long userId;
			    	String userName="";
			    	
			    	@Override
			    	protected void onPreExecute() {
			    		mfbProgress.setMessage("Connecting...");
			    		mfbProgress.show();
			    	}
					@Override
					protected Void doInBackground(Void... arg0) {
						
						try {
							userId = mTwitter.mTwitter.getId();
							userName=mTwitter.mSession.getUsername();
							isValidUser = service.isValidUser(Long.toString(userId),"twitter_id", "login");
							SharedPreferences preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString("twitter_user_id", ""+userId);
							editor.commit();
							
						} catch (Exception e){
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						SharedPreferences mp1 = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor=mp1.edit();
						if(isValidUser){
							//push feeds screen
							service.saveUserIds(Login.this);
							editor.putString("lookupresponse", service.getLookUpResponse());
							editor.putBoolean("mnwplayinglogin", true);
							editor.putBoolean("mtwitterlogin", true);
							editor.commit();
							startHomeActivity();
						}else{
							//push unique screen
							editor.putString("twitterUserId",Long.toString(userId));
							editor.putString("fullname",userName);
							editor.putString("social_type", "twitter");
							editor.commit();
							startUniqueNameActivity();
						}
						mfbProgress.dismiss();
					}
			    }
			    
			    private class checkFoursqureUserExits extends AsyncTask<Void, Void, Void>{
			    	HttpService service = new HttpService();
			    	String userName = "";
			    	String userId ="";
			    	String userProfilePicUrl="";
			    	boolean isValidUser = false;
			    	
			    	@Override
			    	protected void onPreExecute() {
			    		mfbProgress.setMessage("Connecting...");
			    		mfbProgress.show();
			    	}
			    	
					@Override
					protected Void doInBackground(Void... params) {
						userName = mFsqApp.getUserName();
						userId = mFsqApp.getUserId();
						userProfilePicUrl = mFsqApp.getPhotoUrl();
						isValidUser = service.isValidUser(userId, "foursquare_id", "login");
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						SharedPreferences mp1 = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor=mp1.edit();
						if(isValidUser){
							service.saveUserIds(Login.this);
							editor.putString("lookupresponse", service.getLookUpResponse());
							editor.putBoolean("mnwplayinglogin", true);
							editor.putBoolean("mfsqlogin", true);
							editor.commit();
							startHomeActivity();
						}else{
							editor.putString("foursqureUserId",userId);
							editor.putString("foursqureUserPhoto",userProfilePicUrl);
							editor.putString("fullname",userName);
							editor.putString("social_type", "foursquare");
							editor.commit();
							startUniqueNameActivity();
						}
						mfbProgress.dismiss();
					}
			    }
			    
			    private void startHomeActivity(){
			    	Intent intent = new Intent(Login.this, Home.class);
			    	intent.putExtra("Done Button", false);
					startActivity(intent);
					Login.this.finish();
			    }
			    
			    private void startUniqueNameActivity(){
			    	 Intent intent = new Intent(Login.this, UniqueNameInput.class);
					 startActivity(intent);
					 Login.this.finish();
			    }
}