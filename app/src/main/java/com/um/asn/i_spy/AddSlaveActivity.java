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

import com.um.asn.i_spy.http_methods.HttpPostTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;


public class AddSlaveActivity extends AppCompatActivity {

    String url = Config.SERVER_DOMAIN + "user/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slave);

        Button addSlaveButton = (Button) findViewById(R.id.add_slave_button);

        addSlaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                try {
                    
                    v.setVisibility(View.INVISIBLE);
                    findViewById(R.id.add_slave_progressBar).setVisibility(View.VISIBLE);

                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        // Recuperation des infos utilisateur
                        InputStream userInfoIS = openFileInput(Config.USER_INFO);
                        BufferedReader userInfoBR = new BufferedReader(new InputStreamReader(userInfoIS));
                        JSONObject userInfoJSON = new JSONObject(userInfoBR.readLine());

                        String userId = userInfoJSON.get("id").toString();
                        String userMail = userInfoJSON.get("mail").toString();
                        String userPassword = userInfoJSON.get("password").toString();

                        // Recuperation des infos sur le phone a ajouter
                        TextView phonePassword = (TextView) findViewById(R.id.add_slave_password);
                        TextView phoneLogin = (TextView) findViewById(R.id.add_slave_login);

                        JSONObject phoneJSON = new JSONObject();
                        phoneJSON.put("login", phoneLogin.getText().toString());
                        phoneJSON.put("password", phonePassword.getText().toString());

                        if (!phoneLogin.getText().toString().equals("") && !phonePassword.getText().toString().equals("")) {

                            // Construction de l'url REST
                            url += userId + "/phones?user[mail]=" + userMail + "&user[password]=" + userPassword;

                            /* Creation d'un objet HttpPostTask, execution de la methode
                            * doInBackground et recuperation du resultat */
                            JSONObject replyFromServer = new JSONObject((String) new HttpPostTask()
                                    .execute(new Object[]{url, phoneJSON.toString()}).get());

                            System.out.println(replyFromServer.toString());

                            if (!((boolean) replyFromServer.get("success"))) {

                                /* Handling error message from server*/
                                String message = (String) replyFromServer.get("message");
                                int toastMessage;

                                switch (message) {
                                    case "uniquePhone":
                                        toastMessage = R.string.message_phone_already_in_list;
                                        break;
                                    case "wrongPhone":
                                        toastMessage = R.string.message_phone_doesnt_exists;
                                        break;
                                    default:
                                        toastMessage = R.string.message_internal_server_error;
                                        break;
                                }

                                Toast.makeText(AddSlaveActivity.this,
                                        toastMessage, Toast.LENGTH_LONG).show();

                                // Desactivation de la progress bar en cercle et apparition du bouton
                                v.setVisibility(View.VISIBLE);
                                findViewById(R.id.add_slave_progressBar).setVisibility(View.INVISIBLE);

                            } else {

                                JSONObject addedPhone = (JSONObject) replyFromServer.get("data");

                                Intent intent = new Intent(AddSlaveActivity.this, SlaveMenuActivity.class);

                                intent.putExtra("id", (String) addedPhone.get("id"));
                                intent.putExtra("login", (String) addedPhone.get("login"));

                                startActivity(intent);
                            }


                        } else {
                            // Afficher le bouton et cacher la barre de progression
                            v.setVisibility(View.VISIBLE);
                            findViewById(R.id.add_slave_progressBar).setVisibility(View.INVISIBLE);
                            Toast.makeText(AddSlaveActivity.this, R.string.message_missing_mandatory_fields, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddSlaveActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException | InterruptedException | ExecutionException |
                        IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
