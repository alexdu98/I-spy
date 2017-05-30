package com.um.asn.i_spy.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;

import com.um.asn.i_spy.listeners.MasterConnectionListener;

public class MasterService extends IntentService {

    public MasterService() {
        super("MasterService");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service Master...");
        // Pour enregistrer l'eventListner sur les changements de connexion (car deprecated dans Nougat)
        registerReceiver(new MasterConnectionListener(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        return;
    }
}
