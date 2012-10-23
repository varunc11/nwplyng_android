package com.mindhelix.nwplyng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Splash extends Activity
{
    protected static final int MIN_SPLASH_DURATION = 1000;
    protected boolean waitSplashFinished = false;
   
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.splash);
    	
    	
       	Thread splashThread=new Thread()
		{
			public void run()
			{
				try
				{
				    Thread.sleep(MIN_SPLASH_DURATION);
				} catch(Exception e)
				{
					
				}
				finally
				{
//					SharedPreferences mp1=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences mp1 = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
        	        boolean firsttime=mp1.getBoolean("mnwplayinglogin", false);
        	        if(!firsttime)
        	        {
        	        	Intent intent=new Intent(Splash.this,Login.class);
        	     
        	        	startActivity(intent);
        	        }
        	       else
        	       {
        	    	   Intent intent=new Intent(Splash.this,Home.class);
        	    	   startActivity(intent);
        	       }
        	        finish();
				}
			}
		};
		splashThread.start();
	}
    
    
    
}