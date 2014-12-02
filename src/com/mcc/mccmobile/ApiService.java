package com.mcc.mccmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;






import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ApiService extends IntentService {
	public static final String API_BASE_URL = "http://130.233.42.224:8080/api/contacts";
	
	public static final String GET_INTENT = "GET";
	public static final String POST_INTENT = "POST";
	public static final String DELETE_INTENT = "DELETE";
	
	public ApiService() {
		super("Apiservice");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String result = "No response";
		try {
			if (intent.getStringExtra("type").equalsIgnoreCase(GET_INTENT)) {
				result = downloadUrl(API_BASE_URL);
			} else if (intent.getStringExtra("type").equalsIgnoreCase(POST_INTENT)) {
				String name = intent.getStringExtra("name");
				String phone = intent.getStringExtra("phone");
				String mail = intent.getStringExtra("mail");
				result = postContact(name, phone, mail);
			}else if (intent.getStringExtra("type").equalsIgnoreCase(DELETE_INTENT)) {
				String id = intent.getStringExtra("id");
				result = deleteContact(id);
			} else {
				result = "Unknown intent type";
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("DATA", "The data is: " + result);
		
	}
	

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	private String downloadUrl(String myurl) throws IOException {
		
		Toast.makeText(getApplicationContext(), "Started get", 
				   Toast.LENGTH_LONG).show();
		
	    InputStream is = null;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d("RESPONSE", "The response is: " + response);
	        is = conn.getInputStream();

	        String contentAsString = convertStreamToString(is);
	        return contentAsString;

	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	private String postContact(String name, String phone, String mail) {
		
		Toast.makeText(getApplicationContext(), "Started post", 
				   Toast.LENGTH_LONG).show();
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(API_BASE_URL);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("phone",
					phone));
			nameValuePairs.add(new BasicNameValuePair("email", mail));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			int resp_code = response.getStatusLine().getStatusCode();
			
			Log.d("RESPONSE", "The response is: " + resp_code);
			
			String str_resp = convertStreamToString(response.getEntity().getContent());
			return str_resp;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
private String deleteContact(String id) {
		
		Toast.makeText(getApplicationContext(), "Started post", 
				   Toast.LENGTH_LONG).show();
		
		String url = API_BASE_URL + "/" + id;
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpDelete httpdelete = new HttpDelete(url);
		try {

			HttpResponse response = httpclient.execute(httpdelete);
			
			int resp_code = response.getStatusLine().getStatusCode();
			
			Log.d("RESPONSE", "The response is: " + resp_code);
			
			String str_resp = convertStreamToString(response.getEntity().getContent());
			return str_resp;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
