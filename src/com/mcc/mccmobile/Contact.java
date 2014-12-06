package com.mcc.mccmobile;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sten on 12/6/14.
 */
public class Contact {

    public static final int NO_STATUS = -1;

    public static List<Contact> parseContacts(JSONArray data) throws JSONException {
        int length = data.length();
        LinkedList<Contact> contacts = new LinkedList<Contact>();
        
        for (int i = 0; i < length; i++) {
            contacts.push(parseContact(data.getJSONObject(i)));
        }
        
        return contacts;
    }

    public static List<Contact> parseContacts(String data) {
        try {
            return parseContacts(new JSONArray(data));
        } catch (JSONException e) {
            e.printStackTrace();
            return new LinkedList<Contact>();
        }
    }
    
    public static Contact parseContact(JSONObject data) throws JSONException {
        return new Contact(
                data.getString("_id"),
                data.getString("name"),
                data.getString("phone"),
                data.getString("email")     
        );
    }

    public static Contact parseContact(String data) {
        try {
            return parseContact(new JSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
            return new Contact(null,null,null);
        }
    }

    public static Intent createIntent(Context context, Contact contact){
        Intent i = new Intent(context, ApiService.class);
        i.putExtra("type", ApiService.CREATE_INTENT);
        i.putExtra("name", contact.getName());
        i.putExtra("phone", contact.getPhone());
        i.putExtra("email", contact.getEmail());
        return i;
    }

    public static Intent deleteIntent(Context context, Contact contact) {
        Intent i = new Intent(context, ApiService.class);
        i.putExtra("type", ApiService.DELETE_INTENT);
        i.putExtra("id", contact.getId());
        return i;
    }

    public static Intent fetchIntent(Context context, Contact contact) {
        Intent i = new Intent(context, ApiService.class);
        i.putExtra("type", ApiService.GET_INTENT);
        i.putExtra("id", contact.getId());
        return i;
    }

    public static Intent fetchAllIntent(Context context) {
        Intent i = new Intent(context, ApiService.class);
        i.putExtra("type", ApiService.GET_ALL_INTENT);
        return i;
    }

    public static Intent exportIntent(Context context, Contact contact) {
        Intent i = new Intent(context, ApiService.class);
        i.putExtra("type", ApiService.EXPORT_INTENT);
        i.putExtra("id", contact.getId());
        i.putExtra("name", contact.getName());
        i.putExtra("phone", contact.getPhone());
        i.putExtra("email", contact.getEmail());
        return i;
    }

    public static Intent importIntent(Context context, Contact contact){
        Intent i = new Intent(context, ApiService.class);
        i.putExtra("type", ApiService.IMPORT_INTENT);
        i.putExtra("name", contact.getName());
        i.putExtra("phone", contact.getPhone());
        i.putExtra("email", contact.getEmail());
        return i;
    }

    private final String id;
    private final String name;
    private final String phone;
    private final String email;

    private int status;

    public Contact(String name, String phone, String email) {
        this(null, name, phone, email);
    }

    public Contact(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.status = NO_STATUS;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }
}
