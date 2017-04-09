package com.um.asn.i_spy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RegisterSlaveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_slave);

        Button registerSlaveButton = (Button) findViewById(R.id.register_slave_button);

        registerSlaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    /* Creation de l'url pour atteindre le serveur REST, a changer par calyxe.fr */
                    String url = "http://192.168.1.11:80/ispy-rest-service.php/phone/";

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
}
