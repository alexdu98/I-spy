package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.um.asn.i_spy.services.SlaveService;

public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, SlaveService.class);
        context.startService(myIntent);
    }

}
