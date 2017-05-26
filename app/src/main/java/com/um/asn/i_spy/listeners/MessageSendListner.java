package com.um.asn.i_spy.listeners;

import android.database.ContentObserver;
import android.os.Handler;

public class MessageSendListner extends ContentObserver {

    public MessageSendListner(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }
}
