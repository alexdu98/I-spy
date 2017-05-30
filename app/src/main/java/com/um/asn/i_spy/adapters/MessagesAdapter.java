package com.um.asn.i_spy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.asn.i_spy.R;
import com.um.asn.i_spy.models.Message;

import java.util.ArrayList;


public class MessagesAdapter extends ArrayAdapter<Message> {

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        Message message = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.message_item_text);
        messageTextView.setText(message.getContenu());

        System.out.println(message.getType());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


        if (message.getType() == 1) {

            lp.addRule(RelativeLayout.ALIGN_PARENT_START);
            messageTextView.setBackgroundColor(getContext().getResources().getColor(R.color.greenAlex));
        } else {

            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            messageTextView.setBackgroundColor(getContext().getResources().getColor(R.color.yellowAlex));
        }

        lp.setMargins(18, 18, 18, 18);
        messageTextView.setLayoutParams(lp);

        return convertView;

    }
}
