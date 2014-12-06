package com.mcc.mccmobile;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiService extends IntentService {
	public static final String API_BASE_URL = "http://130.233.42.224:8080/api/contacts";

    public static final String RESULT_DATA_KEY = "RESULT_DATA";

	public static final String GET_INTENT = "GET";
	public static final String CREATE_INTENT = "POST";
	public static final String DELETE_INTENT = "DELETE";
    public static final String EXPORT_INTENT = "EXPORT";
    public static final String IMPORT_INTENT = "IMPORT";
    public static final String GET_ALL_INTENT = "GET_ALL" ;
    public static final String GET_RESULT_INTENT = "GET_RESULT";
    public static final String GET_ALL_RESULT_INTENT = "GET_ALL_RESULT";
    public static final String CREATE_RESULT_INTENT = "CREATE_RESULT";
    public static final String DELETE_RESULT_INTENT = "DELETE_RESULT";
    public static final String EXPORT_RESULT_INTENT = "EXPORT_RESULT";
    public static final String IMPORT_RESULT_INTENT = "IMPORT_RESULT";

    public ApiService() {
		super("Apiservice");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String result = "No response";
		try {

            if (intent.getStringExtra("type").equalsIgnoreCase(GET_ALL_INTENT)) {
                result = downloadUrl(API_BASE_URL);
                broadcastResultIntent(GET_ALL_RESULT_INTENT, result);

            } else if (intent.getStringExtra("type").equalsIgnoreCase(GET_INTENT)) {
                result = downloadUrl(API_BASE_URL + "/" + intent.getStringExtra("id"));
                broadcastResultIntent(GET_RESULT_INTENT, result);

            } else if (intent.getStringExtra("type").equalsIgnoreCase(CREATE_INTENT) ||
                    intent.getStringExtra("type").equalsIgnoreCase(IMPORT_INTENT)) {
				String name = intent.getStringExtra("name");
				String phone = intent.getStringExtra("phone");
				String email = intent.getStringExtra("email");
				result = postContact(name, phone, email);
                if (intent.getStringExtra("type").equalsIgnoreCase(CREATE_INTENT)){
                    broadcastResultIntent(CREATE_RESULT_INTENT, result);
                }else {
                    broadcastResultIntent(IMPORT_RESULT_INTENT, result);
                }
				
			} else if (intent.getStringExtra("type").equalsIgnoreCase(DELETE_INTENT)) {
                String id = intent.getStringExtra("id");
                result = deleteContact(id);
                broadcastResultIntent(DELETE_RESULT_INTENT, result);

            } else if (intent.getStringExtra("type").equalsIgnoreCase(EXPORT_INTENT)) {
                String id = intent.getStringExtra("id");
                String name = intent.getStringExtra("name");
                String phone = intent.getStringExtra("phone");
                String email = intent.getStringExtra("email");
                result = exportContact(id, name, phone, email);
                broadcastResultIntent(EXPORT_RESULT_INTENT, result);
			} else {
				result = "Unknown intent type";
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("DATA", "The data is: " + result);
		
	}

    private void broadcastResultIntent(String intentKey, String result) {
        Intent resultIntent = new Intent(intentKey);
        resultIntent.putExtra(RESULT_DATA_KEY, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
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
            Log.d("RESPONSE DATA", contentAsString);
	        return contentAsString;

	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	private String postContact(String name, String phone, String mail) {
		
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
		
		String url = API_BASE_URL + "/" + id;
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpDelete httpdelete = new HttpDelete(url);
		try {

			HttpResponse response = httpclient.execute(httpdelete);
			
			int resp_code = response.getStatusLine().getStatusCode();
			
			Log.d("RESPONSE", "The response is: " + resp_code);
			
			convertStreamToString(response.getEntity().getContent());
			return id;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    private String exportContact(String id, String name, String phone, String email){
        String[] parts = name.split(" ");
        String gName = "";
        String fName = "";
        if (parts.length > 1){
            gName = parts[0];
            fName = parts[1];
        } else if(parts.length > 0){
            gName = parts[0];
        }

        ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
        op_list.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        // .withValue(RawContacts.AGGREGATION_MODE,
                        // RawContacts.AGGREGATION_MODE_DEFAULT)
                .build());
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, gName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, fName).build());

        op_list.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        op_list.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, op_list);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to export contact";
        }
    }

}
