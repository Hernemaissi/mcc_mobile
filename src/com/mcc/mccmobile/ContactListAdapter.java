package com.mcc.mccmobile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by sten on 12/6/14.
 */
public class ContactListAdapter extends ArrayAdapter<Contact>{

    public ContactListAdapter(Activity activity){
        super(activity, 0);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name_view);
        TextView phone = (TextView) convertView.findViewById(R.id.phone_view);
        TextView email = (TextView) convertView.findViewById(R.id.email_view);
        TextView status = (TextView) convertView.findViewById(R.id.status_view);
        name.setText(getContext().getString(R.string.name) + ":" + contact.getName());
        phone.setText(getContext().getString(R.string.phone) + ":" + contact.getPhone());
        email.setText(getContext().getString(R.string.email) + ":" + contact.getEmail());
        if (contact.getStatus() != Contact.NO_STATUS){
            status.setText(getContext().getString(R.string.status) + ":" + getContext().getString(contact.getStatus()));
        } else {
            status.setText("");
        }
        return convertView;
    }
}
