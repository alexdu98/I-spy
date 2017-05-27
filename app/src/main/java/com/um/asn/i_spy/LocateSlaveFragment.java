package com.um.asn.i_spy;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LocateSlaveFragment extends Fragment implements OnMapReadyCallback {

    private final static int REQUEST_GPS_PERM = 0;
    private Phone targetPhone;
    private User currentUser;

    private GoogleMap locateSlaveGoogleMap = null;
    private MapView locateSlaveMapView = null;
    private String url = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_locate_slave, container, false);

        locateSlaveMapView = (MapView) v.findViewById(R.id.locate_slave_map_view);
        locateSlaveMapView.onCreate(savedInstanceState);
        locateSlaveMapView.getMapAsync(this);

        // Montre la derniere position du traqué lorsque cliqué
        Button locateSlaveLastPosButton = (Button) v.findViewById(R.id.locate_slave_show_last_button);
        locateSlaveLastPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locateSlaveGoogleMap == null) {
                    Toast.makeText(getActivity(),
                            R.string.message_map_not_ready,
                            Toast.LENGTH_LONG).show();
                } else showLastPosition();
            }
        });

        // Montre la derniere position du traqué lorsque cliqué
        Button locateSlaveLastRouteButton = (Button) v.findViewById(R.id.locate_slave_show_route_button);
        locateSlaveLastRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locateSlaveGoogleMap == null) {
                    Toast.makeText(getActivity(),
                            R.string.message_map_not_ready,
                            Toast.LENGTH_LONG).show();
                } else showLastRoute();
            }
        });

        return v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle infosFromActivity = getArguments();

        // Recuperation du telephone
        targetPhone = new Phone(infosFromActivity.getInt("phone_id"),
                infosFromActivity.getString("phone_login"));

        // Recuperation des infos utilisateur
        currentUser = new User(infosFromActivity.getInt("user_id"),
                infosFromActivity.getString("user_mail"),
                infosFromActivity.getString("user_password"));

        getActivity().setTitle(getResources().getString(R.string.locate_slave_fragment_label) + " " + targetPhone.getLogin());

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
            if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_GPS_PERM);

            }

            locateSlaveGoogleMap.clear();

            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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

                    Toast.makeText(getActivity(), R.string.message_internal_server_error, Toast.LENGTH_LONG).show();

                } else {

                    // si data est a null, il n'y pas de derniere position connue pour le phone
                    // utiliser equals, == plante
                    if (replyFromServer.get("data").equals(null)) {
                        Toast.makeText(getActivity(),
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

                Toast.makeText(getActivity(), R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, REQUEST_GPS_PERM);

            }

            locateSlaveGoogleMap.clear();

            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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

                    Toast.makeText(getActivity(), R.string.message_internal_server_error, Toast.LENGTH_LONG).show();

                } else {

                    // si data est a null, il n'y pas de derniere position connue pour le phone
                    // utiliser equals, == plante
                    if (replyFromServer.get("data").equals(null)
                            || ((JSONArray) replyFromServer.get("data")).length() <= 0) {
                        Toast.makeText(getActivity(),
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

                Toast.makeText(getActivity(), R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
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

    @Override
    public void onResume() {
        super.onResume();
        locateSlaveMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        locateSlaveMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locateSlaveMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        locateSlaveMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        locateSlaveMapView.onLowMemory();
    }

}
