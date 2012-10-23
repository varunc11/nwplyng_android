package net.londatiga.fsq;

import java.util.ArrayList;

import net.londatiga.fsq.FoursquareApp.FsqAuthListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mindhelix.nwplyng.R;

public class Main extends Activity {
	private FoursquareApp mFsqApp;
	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<FsqVenue> mNearbyList;
	private ProgressDialog mProgress;
	
	public static final String CLIENT_ID = "XF5SZAHOXOXGRIHUWTH20PE2YBSZXHDEWJQATBYITYFSMEDP";
	public static final String CLIENT_SECRET = "Z4H1LNUTJFMRVOCHH5T2EU3RXTN2P01W1TF4CZSH0KHAS2QW";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
//        final TextView nameTv 		= (TextView) findViewById(R.id.tv_name);
        Button connectBtn 			= (Button) findViewById(R.id.btn_foursquare);
       /* final EditText latitudeEt	= (EditText) findViewById(R.id.et_latitude);
        final EditText longitudeEt	= (EditText) findViewById(R.id.et_longitude);
        Button goBtn				= (Button) findViewById(R.id.b_go);
        mListView					= (ListView) findViewById(R.id.lv_places);*/
        
        mFsqApp 		= new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);
        
        mAdapter 		= new NearbyAdapter(this);
        mNearbyList		= new ArrayList<FsqVenue>();
        mProgress		= new ProgressDialog(this);
        
        mProgress.setMessage("Connecting...");
        
        
//        if (mFsqApp.hasAccessToken()) nameTv.setText("Connected as " + mFsqApp.getUserName());
        
        FsqAuthListener listener = new FsqAuthListener() {
        	
        	public void onSuccess() {
        		Toast.makeText(Main.this, "Connected as " + mFsqApp.getUserName(), Toast.LENGTH_SHORT).show();
//        		nameTv.setText("Connected as " + mFsqApp.getUserName());
        	}
        	
        	
        	public void onFail(String error) {
        		Toast.makeText(Main.this, error, Toast.LENGTH_SHORT).show();
        	}
        };
        
        mFsqApp.setListener(listener);
        
        //get access token and user name from foursquare
        connectBtn.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		mFsqApp.authorize();
        	}
        });  
        
        //use access token to get nearby places
       /* goBtn.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		String latitude  = latitudeEt.getText().toString();
        		String longitude = longitudeEt.getText().toString();
        		
        		if (latitude.equals("") || longitude.equals("")) {
        			Toast.makeText(Main.this, "Latitude or longitude is empty", Toast.LENGTH_SHORT).show();
        			return;
        		}
        		
    			double lat	= Double.valueOf(latitude);
    			double lon	= Double.valueOf(longitude);
    			
        		loadNearbyPlaces(lat, lon);
        	}
        });*/
    }
    
    private void loadNearbyPlaces(final double latitude, final double longitude) {
    	mProgress.show();
    	
    	new Thread() {
    		@Override
    		public void run() {
    			int what = 0;
    			
    			try {
    				
    				mNearbyList = mFsqApp.getNearby(latitude, longitude);
    			} catch (Exception e) {
    				what = 1;
    				e.printStackTrace();
    			}
    			
    			mHandler.sendMessage(mHandler.obtainMessage(what));
    		}
    	}.start();
    }
    
    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		mProgress.dismiss();
    		
    		if (msg.what == 0) {
    			if (mNearbyList.size() == 0) {
    				Toast.makeText(Main.this, "No nearby places available", Toast.LENGTH_SHORT).show();
    				return;
    			}
    			
    			mAdapter.setData(mNearbyList);    			
    			mListView.setAdapter(mAdapter);
    		} else {
    			Toast.makeText(Main.this, "Failed to load nearby places", Toast.LENGTH_SHORT).show();
    		}
    	}
    };
}