package com.um.asn.i_spy.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;

import com.um.asn.i_spy.listeners.SlaveConnectionListener;

public class MasterService extends IntentService {

    public MasterService() {
        super("MasterService");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Pour enregistrer l'eventListner sur les changements de connexion (car deprecated dans Nougat)
        registerReceiver(new SlaveConnectionListener(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


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
