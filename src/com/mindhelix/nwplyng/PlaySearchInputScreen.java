package com.mindhelix.nwplyng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class PlaySearchInputScreen extends Activity {
	
	EditText searchInput;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.play_input);
	        
	        RelativeLayout parentLay = (RelativeLayout)findViewById(R.id.inputBackgroundLay);
	        searchInput = (EditText)findViewById(R.id.trackSearchInputBox);
	        
	        searchInput.setInputType(searchInput.getInputType()
	        	    | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
	        	    | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
	        
	        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        parentLay.setOnTouchListener(new View.OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
					return false;
				}
			});
	        
	        searchInput.setOnEditorActionListener(new OnEditorActionListener() {
				
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
			            
			            if(CheckNetworkConnection.isNetworkAvailable(PlaySearchInputScreen.this)) {
			            	try {
								SearchListActivity.searchString = URLEncoder.encode(searchInput.getText().toString(), "UTF-8");
								Itunes_searchlistActivity.searchString = URLEncoder.encode(searchInput.getText().toString(), "UTF-8");
							} 
							catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							
							SharedPreferences preferences = getSharedPreferences(ConstantValues.NWPLYNG_SHARED_PREFERENCE, Context.MODE_WORLD_WRITEABLE);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString("Itunes Json Array", "");
							editor.commit();
							
							Intent intent = new Intent(PlaySearchInputScreen.this, Itunes_searchlistActivity.class);
							intent.putExtra("for_result", false);
							startActivity(intent);
							searchInput.setText("");
			            }
			            else {
			            	NetworkConnectionErrorMessage.showNetworkError(PlaySearchInputScreen.this, PlaySearchInputScreen.this).show();
			            }
						return true;
					}
					return false;
				}
			});
	        
	        
	        
	 }

}
