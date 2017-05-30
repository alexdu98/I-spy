package com.um.asn.i_spy.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.um.asn.i_spy.Config;
import com.um.asn.i_spy.websockets.MasterWS;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MasterConnectionListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            System.out.println("Master Connected !");

            // Démarre la WebSocket
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(Config.SERVER_WS).build();
            MasterWS listener = new MasterWS(context);
            WebSocket ws = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();

        } else {
            System.out.println("Master Disconnected !");
        }
    }

}
