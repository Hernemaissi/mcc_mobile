package com.mcc.mccmobile;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnContactDeleteListener}
 * interface.
 */
public class ContactDeleteFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnContactDeleteListener mListener;


    private HashMap<String, Contact> removeContacts;
    private ContactListAdapter mAdapter;
    private BroadcastReceiver mDeleteReceiver;
    private BroadcastReceiver mGetAllReceiver;

    public static ContactDeleteFragment newInstance() {
        return new ContactDeleteFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactDeleteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ContactListAdapter(getActivity());
        removeContacts = new HashMap<String, Contact>();
        mDeleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String id = intent.getStringExtra(ApiService.RESULT_DATA_KEY);
                Log.d("DELETE/mDeleteReceiver", "Contact: " + id);
                if (removeContacts.containsKey(id)){
                    Log.d("DELETE/mDeleteReceiver", "Removing: " + id);
                    Contact contact = removeContacts.get(id);
                    contact.setStatus(R.string.deleted);
                    mAdapter.remove(contact);
                    removeContacts.remove(id);
                }
            }
        };
        mGetAllReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra(ApiService.RESULT_DATA_KEY);
                Log.d("DELETE/mGetAllReceiver", data);
                mAdapter.clear();
                mAdapter.addAll(Contact.parseContacts(data));
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // Set the adapter
        AbsListView mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnContactDeleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactDeleteListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(mDeleteReceiver, new IntentFilter(ApiService.DELETE_RESULT_INTENT));
        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(mGetAllReceiver, new IntentFilter(ApiService.GET_ALL_RESULT_INTENT));
        getActivity().startService(Contact.fetchAllIntent(getActivity().getBaseContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager
                .getInstance(getActivity())
                .unregisterReceiver(mDeleteReceiver);
        LocalBroadcastManager
                .getInstance(getActivity())
                .unregisterReceiver(mGetAllReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = mAdapter.getItem(position);
        if (null != mListener && !removeContacts.containsKey(contact.getId())) {
            Log.d("DELETE/mDeleteAction", "Deleting contact: " + contact.getId());
            contact.setStatus(R.string.deleting);
            removeContacts.put(contact.getId(), contact);
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onContactDelete(contact);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnContactDeleteListener {
        public void onContactDelete(Contact contact);
    }

}
