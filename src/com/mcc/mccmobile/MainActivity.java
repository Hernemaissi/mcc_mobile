package com.mcc.mccmobile;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
	public static final String GET_INTENT = "GET";
	public static final String POST_INTENT = "POST";
	public static final String DELETE_INTENT = "DELETE";

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
}
