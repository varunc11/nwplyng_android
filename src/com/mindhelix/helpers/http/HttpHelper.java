/*
 * HTTP Helper functions
 * 
 * Author : Thomas Antony
 * Company : MindHelix Technosol Pvt. Ltd
 * Last Updated : 01-Apr-2012
 * 
 */
package com.mindhelix.helpers.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class HttpHelper {
	public static boolean CheckNetworkConnectivity(Context c)
	{
	    boolean HaveConnectedWifi = false;
	    boolean HaveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo)
	    {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                HaveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                HaveConnectedMobile = true;
	    }
	    return HaveConnectedWifi || HaveConnectedMobile;
	}
	
	/*
	 * 
	 * Synchronous POST functions
	 * 
	 */
	public static HttpStringResult PostJson(String url,Map<String,?> data) throws IOException, UnsupportedEncodingException
	{
		return PostJson(url,data,null);
	}
	public static HttpStringResult PostJson(String url,Map<String,?> data,Map<String, String> headers) throws IOException, UnsupportedEncodingException
	{
		if(headers == null)
		{
			headers = new HashMap<String,String>();
		}
		JSONObject jo = new JSONObject(data);
		headers.put("Accepts", "application/json");
		headers.put("Content-type", "application/json");
		return Post(url,jo.toString(),headers);
	}
	public static HttpStringResult Post(String url,String data,Map<String, String> headers) throws IOException, UnsupportedEncodingException
	{
		HttpPost post = new HttpPost(url);
		
		StringEntity sEntity;
		sEntity = new StringEntity(data);
		post.setEntity(sEntity);
		
		return doRequest(post, headers);
	}
	
	
	/*
	 * 
	 * Asynchronous POST functions
	 * 
	 */
	public static void PostJsonAsync(String url, Map<String,String> data, Map<String, String> headers, IHttpStringListener listener)
	{
		PostJsonAsync(url,data, headers, listener, null,null);
	}
	public static void PostJsonAsync(String url, Map<String,String> data, Map<String, String> headers, IHttpStringListener listener, Context c, String progressMessage)
	{
		if(headers == null)
		{
			headers = new HashMap<String,String>();
		}
		JSONObject jo = new JSONObject(data);
		headers.put("Accepts", "application/json");
		headers.put("Content-type", "application/json");
		PostAsync(url, jo.toString(), headers, listener,c,progressMessage);
	}
	public static void PostAsync(String url, String data, Map<String, String> headers, IHttpStringListener listener)
	{
		PostAsync(url,data,headers,listener,null,null);
	}
	public static void PostAsync(String url, String data, Map<String, String> headers, IHttpStringListener listener, Context c, String progressMessage)
	{
		HttpAsyncTask task = new HttpAsyncTask(url,HttpPost.METHOD_NAME,data,headers,listener);
		task.setProgressOptions(c,progressMessage);
		task.execute();
	}
	
	
	/*
	 * 
	 * Synchronous GET functions
	 * 
	 */
	public static HttpResult<JSONObject> GetJsonObject(String url) throws IOException, JSONException
	{
		return GetJsonObject(url,null);
	}
	public static HttpResult<JSONObject> GetJsonObject(String url, Map<String, String> headers) throws IOException, JSONException
	{
		HttpStringResult out = Get(url, headers);
		HttpResult<JSONObject> o= new HttpResult<JSONObject>();
		o.code = out.code;
		if(out.code == 200)
		{
			JSONObject obj = new JSONObject(out.response);
			o.response = obj;
			o.code = out.code;
		}
		return o;
	}
	public static HttpResult<JSONArray> GetJsonArray(String url) throws IOException, JSONException
	{
		return GetJsonArray(url,null);
	}
	public static HttpResult<JSONArray> GetJsonArray(String url,Map<String, String> headers) throws IOException, JSONException
	{
		HttpStringResult out = Get(url, headers);
		HttpResult<JSONArray> o= new HttpResult<JSONArray>();
		o.code = out.code;
		if(out.code == 200)
		{
			JSONArray obj = new JSONArray(out.response);
			
			o.response = obj;
			o.code = out.code;
		}
		return o;
	}
	public static HttpStringResult Get(String url) throws IOException
	{
		return Get(url, null);
	}
	public static HttpStringResult Get(String url, Map<String, String> headers) throws IOException
	{
		HttpGet get = new HttpGet(url);
		return doRequest(get, headers);
	}
	
	/*
	 * 
	 * Asynchronous GET functions
	 * 
	 * 
	 */
	public static void GetAsync(String url, Map<String, String> headers,IHttpStringListener listener)
	{
		GetAsync(url, headers, listener, null, null);
	}
	public static void GetAsync(String url, Map<String, String> headers,IHttpStringListener listener, Context c, String progressMessage)
	{
		HttpAsyncTask task = new HttpAsyncTask(url,HttpGet.METHOD_NAME,null,headers,listener);
		task.setProgressOptions(c, progressMessage);
		task.execute();
	}
	
	
	/*
	 * 
	 * Internal functions and classes used by the above functions
	 * 
	 */
	private static HttpStringResult doRequest(HttpUriRequest req, Map<String, String> headers) throws IOException
	{
		HttpClient client = new DefaultHttpClient();
		
		if(headers == null)
		{
			headers = new HashMap<String,String>();
		}
		
		for (Map.Entry<String, String> entry : headers.entrySet()) {
		    String k = entry.getKey();
		    String v = entry.getValue();
		    req.addHeader(k, v);
		}
		HttpResponse response = client.execute(req);

		HttpEntity outputEntity = response.getEntity();
		String result = EntityUtils.toString(outputEntity);
		
		HttpStringResult o = new HttpStringResult();
		o.response = result;
		o.code = response.getStatusLine().getStatusCode();
		return o;

	}
	static class HttpAsyncTask extends AsyncTask<Void, Void, HttpStringResult>
	{
		IHttpStringListener listener = null;
		protected String url = "";
		protected String data = "";
		protected Map<String, String> headers = null;
		protected ProgressDialog waitDialog = null;
		protected String method = HttpGet.METHOD_NAME;
		

		public HttpAsyncTask(String url, String method, String data, Map<String,String> headers, IHttpStringListener listener) {
			if(headers == null)
			{
				headers = new HashMap<String,String>();
			}
			this.headers = headers;
			this.data = data;
			this.url = url;
			this.listener = listener;
			this.method = method;
		}

		public void setMethod(String method)
		{
			this.method = method;
		}
		@Override
		protected void onPreExecute() {
			if(waitDialog!=null)
			{
				waitDialog.show();
			}
			super.onPreExecute();
		}

		public void setProgressOptions(Context c, String progressMessage) {
			if(c!=null)
			{
				if(progressMessage == null)
				{
					progressMessage = "Please wait";
				}
				waitDialog = new ProgressDialog(c);
				waitDialog.setCancelable(true);
				waitDialog.setMessage(progressMessage);
				waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			}
		}

		@Override
		protected void onPostExecute(HttpStringResult result) {
			if(waitDialog != null)
			{
				waitDialog.dismiss();
			}
			if(listener==null)
			{
				Log.i("Resultlisten", "");
			}
			if(listener!=null)
			{
				Log.i("Listener value", listener.toString());
				listener.RequestComplete(result);
			}
			
			super.onPostExecute(result);
		}

		@Override
		protected HttpStringResult doInBackground(Void... params) {
			try {
				if(method.equals(HttpGet.METHOD_NAME))
				{
					return HttpHelper.Get(url, headers);
				}else if(method.equals(HttpPost.METHOD_NAME))
				{
					return HttpHelper.Post(url, data, headers);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
