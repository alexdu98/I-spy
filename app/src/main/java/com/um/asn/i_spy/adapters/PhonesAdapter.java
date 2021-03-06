package com.um.asn.i_spy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.um.asn.i_spy.R;
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;

public class PhonesAdapter extends ArrayAdapter<Phone> {

    public PhonesAdapter(Context context, ArrayList<Phone> phones) {
        super(context, 0, phones);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Phone phone = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.slave_item, parent, false);
        }

        TextView phoneLoginTW = (TextView) convertView.findViewById(R.id.slave_item_login);
        TextView phoneIdTW = (TextView) convertView.findViewById(R.id.slave_item_id);

        phoneLoginTW.setText(phone.getLogin());
        phoneIdTW.setText(String.valueOf(phone.getId()));

        return convertView;
    }

}
