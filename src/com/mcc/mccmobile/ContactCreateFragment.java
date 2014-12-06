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
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.mcc.mccmobile.ContactCreateFragment.OnContactCreateListener} interface
 * to handle interaction events.
 * Use the {@link com.mcc.mccmobile.ContactCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactCreateFragment extends Fragment {
    private OnContactCreateListener mListener;
    private BroadcastReceiver mCreateReceiver;
    private EditText mNameInput;
    private EditText mPhoneInput;
    private EditText mEmailInput;
    private TextView mInfoText;
    private Button mCreateAction;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactCreateFragment.
     */
    public static ContactCreateFragment newInstance() {
        return new ContactCreateFragment();
    }

    public ContactCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra(ApiService.RESULT_DATA_KEY);
                Contact contact = Contact.parseContact(data);
                Log.d("CREATE/mCreateReceiver", "Created contact: " + contact.getId());
                mInfoText.setText(getString(R.string.created) + ": " + contact.getName());
                mCreateAction.setEnabled(true);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_save, container, false);

        mNameInput = (EditText) view.findViewById(R.id.name_input);
        mPhoneInput = (EditText) view.findViewById(R.id.phone_input);
        mEmailInput = (EditText) view.findViewById(R.id.email_input);
        mInfoText = (TextView) view.findViewById(R.id.info_view);

        mCreateAction = (Button) view.findViewById(R.id.save_action);
        mCreateAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreateAction.setEnabled(false);
                mInfoText.setText(getString(R.string.creating));
                String name  = mNameInput.getText().toString();
                String phone = mPhoneInput.getText().toString();
                String email = mEmailInput.getText().toString();
                Log.d("CREATE/mCreateAction", "Creating new contact: " + name+ "|" + phone + "|" + email);
                mListener.onContactCreate(new Contact(name, phone, email));
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnContactCreateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactCreateListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(mCreateReceiver, new IntentFilter(ApiService.CREATE_RESULT_INTENT));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager
                .getInstance(getActivity())
                .unregisterReceiver(mCreateReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnContactCreateListener {
        public void onContactCreate(Contact contact);
    }

}
