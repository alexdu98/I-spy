package com.um.asn.i_spy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    /* Creation de l'url pour atteindre le serveur REST, a changer par calyxe.fr */
                    String url = "https://ispy.calyxe.fr/index.php/phone/";

                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number();
                    System.out.println("NUMERO : " + mPhoneNumber);

                    /* Objet JSON test pour l'envoi dans la BDD */
                    String input = "{\"login\":\"mylogin\"," +
                            "\"password\":\"mypassword\"," +
                            "\"phone_number\":\"myphonenumber\"," +
                            "\"phone_model\":\"myphonemodel\"}";

                    /* Creation d'un objet HttpPostTask et execution de la methode
                     * doInBackground */
                    new HttpPostTask().execute(new Object[]{url, input});

                    Toast.makeText(RegisterSlaveActivity.this, "Ca marche !", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(RegisterSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
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
