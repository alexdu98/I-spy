package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected())
        {
            System.out.println("Connected !");
        }
        else{
            System.out.println("Disconnected !");
        }
    }

}
