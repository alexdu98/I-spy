package com.um.asn.i_spy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.um.asn.i_spy.R;
import com.um.asn.i_spy.models.Contact;

import java.util.ArrayList;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Contact contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, parent, false);
        }

        TextView contactIdTextView = (TextView) convertView.findViewById(R.id.contact_id);
        TextView contactNameTextView = (TextView) convertView.findViewById(R.id.contact_name);
        TextView contactPhoneNumberTextView = (TextView) convertView.findViewById(R.id.contact_phone_number);

        contactIdTextView.setText(String.valueOf(contact.getId()));
        contactNameTextView.setText(contact.getNom());
        contactPhoneNumberTextView.setText(String.valueOf(contact.getNumero()));

        return convertView;
    }
}
