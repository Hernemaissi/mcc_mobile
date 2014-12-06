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
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView;


/**
 * A simple {@link android.app.Fragment} subclass.
 * Use the {@link com.mcc.mccmobile.ContactShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactShowFragment extends Fragment {
    private BroadcastReceiver mShowReceiver;
    private TextView mNameText;
    private TextView mPhoneText;
    private TextView mEmailText;
    private TextView mIdText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactCreateFragment.
     */
    public static ContactShowFragment newInstance() {
        return new ContactShowFragment();
    }

    public ContactShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShowReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra(ApiService.RESULT_DATA_KEY);
                Contact contact = Contact.parseContact(data);
                Log.d("SHOW/mShowReceiver", "Show contact: " + contact.getId());
                mIdText.setText(contact.getId());
                mNameText.setText(contact.getName());
                mPhoneText.setText(contact.getPhone());
                mEmailText.setText(contact.getEmail());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        mNameText = (TextView) view.findViewById(R.id.name_view);
        mPhoneText = (TextView) view.findViewById(R.id.phone_view);
        mEmailText = (TextView) view.findViewById(R.id.email_view);
        mIdText = (TextView) view.findViewById(R.id.id_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(mShowReceiver, new IntentFilter(ApiService.GET_RESULT_INTENT));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager
                .getInstance(getActivity())
                .unregisterReceiver(mShowReceiver);
    }

}
