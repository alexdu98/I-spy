package com.um.asn.i_spy.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.um.asn.i_spy.Config;
import com.um.asn.i_spy.entities.PositionGPSEntity;
import com.um.asn.i_spy.http_methods.HttpPostTask;
import com.um.asn.i_spy.listeners.GPSListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PositionGPS {

    public Context context;
    public Location location;
    public HashMap<String, String> address;
    public String id_phone;
    public String login;
    public String password;

    public PositionGPS(Context context, String id_phone, String login, String password) {
        this.context = context;
        this.address = new HashMap<String, String>();
        this.id_phone = id_phone;
        this.login = login;
        this.password = password;
        this.location = new Location("");
    }

    public void setLocation() {
        GPSListener gps = new GPSListener(this.context);
        this.location = gps.getLocation();
    }

    public void setAddress() {
        Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(this.location.getLatitude(), this.location.getLongitude(), 1);
            Address obj = addresses.get(0);
            this.address.put(PositionGPSEntity.POSITION_GPS_COLUMN_ADRESSE, obj.getAddressLine(0));
            this.address.put(PositionGPSEntity.POSITION_GPS_COLUMN_PAYS, obj.getCountryName());
            this.address.put(PositionGPSEntity.POSITION_GPS_COLUMN_CODE_POSTAL, obj.getPostalCode());
            this.address.put(PositionGPSEntity.POSITION_GPS_COLUMN_VILLE, obj.getLocality());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLocation(ConnectivityManager cm) {
        setLocation();

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (this.location != null) {
            System.out.println("Latitude : " + location.getLatitude() + " / Longitude : " + location.getLongitude());
            if (networkInfo != null && networkInfo.isConnected()) {
                insertDist(true, null);
            } else {
                insertLocal();
            }
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            insertDistFromLocal(cm);
        }
    }

    public boolean insertDist(boolean isFirstTry, String date) {
        setAddress();

        JSONObject jo = new JSONObject();
        try {
            jo.put(PositionGPSEntity.POSITION_GPS_COLUMN_LATITUDE, this.location.getLatitude());
            jo.put(PositionGPSEntity.POSITION_GPS_COLUMN_LONGITUDE, this.location.getLongitude());
            for (Map.Entry<String, String> field : this.address.entrySet()) {
                jo.put(field.getKey(), field.getValue());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (isFirstTry)
                jo.put(PositionGPSEntity.POSITION_GPS_COLUMN_DATE_POSITION, sdf.format(new Date()));
            else
                jo.put(PositionGPSEntity.POSITION_GPS_COLUMN_DATE_POSITION, sdf.format(sdf.parse(date)));
            jo.put(PositionGPSEntity.POSITION_GPS_COLUMN_PHONE, this.id_phone);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject result = null;
        try {
            String url = Config.SERVER_DOMAIN + "positionsGPS";
            url += "?phone[login]=" + login + "&phone[password]=" + password;
            result = new JSONObject((String) new HttpPostTask().execute(new Object[]{url, jo.toString()}).get());

            if (!(boolean) result.get("success")) {
                if (isFirstTry)
                    insertLocal();
                return false;
            }
            System.out.println("Position saved dist");
            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertLocal() {
        PositionGPSEntity myBD = new PositionGPSEntity(this.context, PositionGPSEntity.POSITION_GPS_COLUMN_ID, null, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        myBD.insert(
                location.getLatitude(),
                location.getLongitude(),
                address.get(PositionGPSEntity.POSITION_GPS_COLUMN_PAYS),
                address.get(PositionGPSEntity.POSITION_GPS_COLUMN_VILLE),
                address.get(PositionGPSEntity.POSITION_GPS_COLUMN_ADRESSE),
                address.get(PositionGPSEntity.POSITION_GPS_COLUMN_CODE_POSTAL),
                sdf.format(new Date()),
                id_phone
        );
        System.out.println("Position saved local (" + myBD.getAll().size() + ")");
    }

    public void insertDistFromLocal(ConnectivityManager cm) {
        PositionGPSEntity myBD = new PositionGPSEntity(this.context, PositionGPSEntity.POSITION_GPS_COLUMN_ID, null, 1);
        ArrayList<HashMap<String, String>> positions = myBD.getAll();
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        System.out.println("Size : " + positions.size());
        for (HashMap<String, String> position : positions) {
            System.out.println(
                    "id : " + position.get(PositionGPSEntity.POSITION_GPS_COLUMN_ID) +
                            " / lat : " + position.get(PositionGPSEntity.POSITION_GPS_COLUMN_LATITUDE) +
                            " / lon : " + position.get(PositionGPSEntity.POSITION_GPS_COLUMN_LONGITUDE) +
                            " / date : " + position.get(PositionGPSEntity.POSITION_GPS_COLUMN_DATE_POSITION)
            );
            if (networkInfo != null && networkInfo.isConnected()) {
                PositionGPS pos = new PositionGPS(context, id_phone, login, password);
                pos.location.setLatitude(Double.parseDouble(position.get(PositionGPSEntity.POSITION_GPS_COLUMN_LATITUDE)));
                pos.location.setLongitude(Double.parseDouble(position.get(PositionGPSEntity.POSITION_GPS_COLUMN_LONGITUDE)));
                if (pos.insertDist(false, position.get(PositionGPSEntity.POSITION_GPS_COLUMN_DATE_POSITION))) {
                    myBD.delete(Integer.parseInt(position.get(PositionGPSEntity.POSITION_GPS_COLUMN_ID)));
                }
            } else {
                return;
            }
        }
    }

}