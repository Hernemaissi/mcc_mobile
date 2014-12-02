package com.mcc.mccmobile;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	public static final String GET_INTENT = "GET";
	public static final String POST_INTENT = "POST";
	public static final String DELETE_INTENT = "DELETE";
	public static final int PICK_CONTACT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Method to start the service
    public void startService(View view) {
       Intent i = new Intent(getBaseContext(), ApiService.class);
       i.putExtra("type", GET_INTENT);
       startService(i);
    }

    // Method to stop the service
    public void stopService(View view) {
       stopService(new Intent(getBaseContext(), ApiService.class));
    }
    
    public void postService(View view) {
        Intent i = new Intent(getBaseContext(), ApiService.class);
        i.putExtra("type", POST_INTENT);
        i.putExtra("name", "Android User");
        i.putExtra("phone", "123-765");
        i.putExtra("mail", "android@android.com");
        startService(i);
     }
    
    public void deleteService(View view) {
    	Intent i = new Intent(getBaseContext(), ApiService.class);
    	i.putExtra("type", DELETE_INTENT);
    	i.putExtra("id", "547b260284f0cf8c1d2df4af");
    	startService(i);
    }
    
    public void importContact(View view) {
    	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    	startActivityForResult(intent, PICK_CONTACT);
    }
    
    public void exportContact(View view) {
    	String name = "Test Guy1";
    	String phone = "999-999";
    	String email = "test1@test.com";
    	export(name, phone, email);
    }
    
	private void export(String name, String phone, String email) {
		
		String gName = name.split(" ")[0];
		String fName = name.split(" ")[1];

		ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
		op_list.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				// .withValue(RawContacts.AGGREGATION_MODE,
				// RawContacts.AGGREGATION_MODE_DEFAULT)
				.build());
		op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.GIVEN_NAME, gName)
				.withValue(StructuredName.FAMILY_NAME, fName).build());

		op_list.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						Phone.TYPE_MOBILE).build());

		op_list.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    
    
    
    
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
      super.onActivityResult(reqCode, resultCode, data);

      switch (reqCode) {
        case (PICK_CONTACT) :
          if (resultCode == Activity.RESULT_OK) {
        	
        	String name = "";
        	String phone = "";
        	String mail = "";
        	  
            Uri contactData = data.getData();
            Cursor c =  getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
              String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
              name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
              
              if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            	  Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                          null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
            	  while (pCur.moveToNext()) {
                      phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            	  }
            	  pCur.close();
              }
              
              Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                      ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null); 
              while (emailCur.moveToNext()) {
            	  mail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        	  }
              emailCur.close();
              
              String result = name + " / " + phone + " / " + mail;
              Toast.makeText(getApplicationContext(), result, 
   				   Toast.LENGTH_LONG).show();
            }
            
            if (name.length() > 0 && phone.length() > 0 && mail.length() > 0) {
            	Intent i = new Intent(getBaseContext(), ApiService.class);
                i.putExtra("type", POST_INTENT);
                i.putExtra("name", name);
                i.putExtra("phone", phone);
                i.putExtra("mail", mail);
                startService(i);
            }
          }
          break;
      }
    }
}
