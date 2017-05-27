package com.um.asn.i_spy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LocateSlaveActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final static int REQUEST_GPS_PERM = 0;
    Phone targetPhone;
    User currentUser;

    GoogleMap locateSlaveGoogleMap = null;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            // Recuperation des infos du telephone pour lequel le menu est affiche
            Intent slaveMenuIntent = getIntent();

            targetPhone = new Phone(Integer.parseInt(slaveMenuIntent.getStringExtra("id")), slaveMenuIntent.getStringExtra("login"));

            // Recuperation des infos utilisateur
            InputStream userInfoIS = openFileInput(Config.USER_INFO);
            BufferedReader userInfoBR = new BufferedReader(new InputStreamReader(userInfoIS));
            JSONObject userInfoJSON = new JSONObject(userInfoBR.readLine());

            currentUser = new User(Integer.valueOf(userInfoJSON.get("id").toString()),
                    userInfoJSON.get("mail").toString(),
                    userInfoJSON.get("password").toString());

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_locate_slave);
            setTitle(getResources().getString(R.string.locate_slave_label) + " " + targetPhone.getLogin());

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager()
                    .findFragmentById(R.id.locate_slave_map_fragment));

            mapFragment.getMapAsync(this);


            // Montre la derniere position du traqué lorsque cliqué
            Button locateSlaveLastPosButton = (Button) findViewById(R.id.locate_slave_show_last_button);
            locateSlaveLastPosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (locateSlaveGoogleMap == null) {
                        Toast.makeText(LocateSlaveActivity.this,
                                R.string.message_map_not_ready,
                                Toast.LENGTH_LONG).show();
                    } else showLastPosition();
                }
            });

            // Montre la derniere position du traqué lorsque cliqué
            Button locateSlaveLastRouteButton = (Button) findViewById(R.id.locate_slave_show_route_button);
            locateSlaveLastRouteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (locateSlaveGoogleMap == null) {
                        Toast.makeText(LocateSlaveActivity.this,
                                R.string.message_map_not_ready,
                                Toast.LENGTH_LONG).show();
                    } else showLastRoute();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        locateSlaveGoogleMap = map;
        showLastPosition();
    }


    /**
     * ACTION : Mise a jour de locateSlaveGoogleMap en affichant le marqueur
     * de la dernière position du traqué.
     */
    public void showLastPosition() {
        try {

            // Checking GPS options enabled
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_GPS_PERM);

            }

            locateSlaveGoogleMap.clear();

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                // Construction de l'url REST
                url = Config.SERVER_DOMAIN + "user/" + currentUser.getId() + "/phone/" + targetPhone.getId()
                        + "/position_gps" + "?user[mail]=" + currentUser.getMail()
                        + "&user[password]=" + currentUser.getPassword() + "&last=1";

                JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                        .execute(new Object[]{url}).get());

                System.out.println("Reply from server : " + replyFromServer.toString());

                // Si success est a false, c'est une erreur du serveur
                if (!((boolean) replyFromServer.get("success"))) {

                    Toast.makeText(LocateSlaveActivity.this, R.string.message_internal_server_error, Toast.LENGTH_LONG).show();

                } else {

                    // si data est a null, il n'y pas de derniere position connue pour le phone
                    // utiliser equals, == plante
                    if (replyFromServer.get("data").equals(null)) {
                        Toast.makeText(LocateSlaveActivity.this,
                                R.string.message_last_location_unknown,
                                Toast.LENGTH_LONG).show();
                    } else {

                        JSONObject targetPhoneGPSPosJSON = (JSONObject) replyFromServer.get("data");
                        LatLng targetPhoneLatLng = new LatLng((double) targetPhoneGPSPosJSON.get("latitude"),
                                (double) targetPhoneGPSPosJSON.get("longitude"));

                        locateSlaveGoogleMap.setMyLocationEnabled(false);
                        locateSlaveGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetPhoneLatLng, 13));

                        locateSlaveGoogleMap.addMarker(new MarkerOptions()
                                .title(targetPhoneGPSPosJSON.getString("adresse"))
                                .snippet(targetPhoneGPSPosJSON.getString("datePosition"))
                                .position(targetPhoneLatLng)).showInfoWindow();
                    }
                }

            } else {

                Toast.makeText(LocateSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * ACTION : Mise a jour de locateSlaveGoogleMap en affichant le dernier
     * itinéraire du traqué.
     */
    public void showLastRoute() {
        try {

            // Checking GPS options enabled
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_GPS_PERM);

            }

            locateSlaveGoogleMap.clear();

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                // Construction de l'url REST
                url = Config.SERVER_DOMAIN + "user/" + currentUser.getId() + "/phone/" + targetPhone.getId()
                        + "/position_gps" + "?user[mail]=" + currentUser.getMail()
                        + "&user[password]=" + currentUser.getPassword();

                JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                        .execute(new Object[]{url}).get());

                System.out.println("Reply from server : " + replyFromServer.toString());

                // Si success est a false, c'est une erreur du serveur
                if (!((boolean) replyFromServer.get("success"))) {

                    Toast.makeText(LocateSlaveActivity.this, R.string.message_internal_server_error, Toast.LENGTH_LONG).show();

                } else {

                    // si data est a null, il n'y pas de derniere position connue pour le phone
                    // utiliser equals, == plante
                    if (replyFromServer.get("data").equals(null)
                            || ((JSONArray) replyFromServer.get("data")).length() <= 0) {
                        Toast.makeText(LocateSlaveActivity.this,
                                R.string.message_last_location_unknown,
                                Toast.LENGTH_LONG).show();
                    } else {

                        JSONArray targetPhoneLastRouteJSON = (JSONArray) replyFromServer.get("data");

                        // Centrage de la caméra autour du dernier point de l'itinéraire
                        LatLng targetPhoneLatLng = new LatLng((double) targetPhoneLastRouteJSON.optJSONObject(0).get("latitude"),
                                (double) targetPhoneLastRouteJSON.optJSONObject(0).get("longitude"));

                        locateSlaveGoogleMap.setMyLocationEnabled(false);
                        locateSlaveGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetPhoneLatLng, 13));

                        // Construction du dernier itinéraire
                        PolylineOptions slaveLastRoute = new PolylineOptions();
                        slaveLastRoute.geodesic(true);

                        ArrayList<LatLng> slaveLastRouteCoordonnates = new ArrayList<>();
                        JSONObject gpsPos = null;

                        for (int i = 0; i < targetPhoneLastRouteJSON.length(); i++) {
                            gpsPos = targetPhoneLastRouteJSON.optJSONObject(i);
                            slaveLastRoute.add(new LatLng(gpsPos.getDouble("latitude"), gpsPos.getDouble("longitude")));
                        }

                        locateSlaveGoogleMap.addPolyline(slaveLastRoute);

                    }
                }

            } else {

                Toast.makeText(LocateSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_GPS_PERM:

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        return;
                }

                break;

        }
    }
}
