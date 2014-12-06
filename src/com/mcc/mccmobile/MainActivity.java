package com.mcc.mccmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ContactListFragment.OnContactSelectListener,
        ContactImportFragment.OnContactImportListener,
        ContactExportFragment.OnContactExportListener,
        ContactCreateFragment.OnContactCreateListener,
        ContactDeleteFragment.OnContactDeleteListener{


    private static final int DETAIL_VIEW_IDX = 5;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactListFragment.newInstance())
                        .commit();
                mTitle = getString(R.string.get_contacts);
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactCreateFragment.newInstance())
                        .commit();
                mTitle = getString(R.string.create_contact);
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactDeleteFragment.newInstance())
                        .commit();
                mTitle = getString(R.string.delete_contact);
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactImportFragment.newInstance())
                        .commit();
                mTitle = getString(R.string.import_contact);
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactExportFragment.newInstance())
                        .commit();
                mTitle = getString(R.string.export_contact);
                break;
            case DETAIL_VIEW_IDX:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactShowFragment.newInstance())
                        .commit();
                mTitle = getString(R.string.show_contact);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          String data = intent.getStringExtra(ApiService.RESULT_DATA_KEY);
          Log.d("receiver", "Data is: " + data);
          try {
            Contact.parseContacts(new JSONArray(data));
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), ApiService.class));
    }

    @Override
    public void onContactCreate(Contact contact) {
        startService(Contact.createIntent(getBaseContext(), contact));
    }

    @Override
    public void onContactDelete(Contact contact) {
        startService(Contact.deleteIntent(getBaseContext(), contact));
    }

    @Override
    public void onContactExport(Contact contact) {
        startService(Contact.exportIntent(getBaseContext(), contact));
    }

    @Override
    public void onContactImport(Contact contact) {
        startService(Contact.importIntent(getBaseContext(), contact));
    }

    @Override
    public void onContactSelect(Contact contact) {
        startService(Contact.fetchIntent(getBaseContext(), contact));
        onNavigationDrawerItemSelected(DETAIL_VIEW_IDX);
    }
}
