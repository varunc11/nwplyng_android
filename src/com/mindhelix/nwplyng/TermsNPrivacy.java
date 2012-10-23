package com.mindhelix.nwplyng;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class TermsNPrivacy extends Activity {
	
	String flag = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_n_policy);
		
		Bundle extras = getIntent().getExtras();
		flag = extras.getString("flag");
		
		WebView webview = (WebView)findViewById(R.id.webview);
		Button back = (Button)findViewById(R.id.backButtonTerms);
		TextView headline = (TextView)findViewById(R.id.termsHeadline);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				TermsNPrivacy.this.finish();				
			}
		});
		
		if(flag.equals("policy"))
		{
			headline.setText("Privacy Policy");
			webview.loadUrl("file:///android_asset/policy.html");
		}
		
		if(flag.equals("terms"))
		{
			headline.setText("Terms of Service");
			webview.loadUrl("file:///android_asset/terms.html");
		}
		
		
	}

}
