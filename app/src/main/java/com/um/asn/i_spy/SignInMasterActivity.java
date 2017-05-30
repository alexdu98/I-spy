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
import com.um.asn.i_spy.websockets.MasterWS;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class SignInMasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_master);

        Button signInButton = (Button) findViewById(R.id.sign_in_master_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Creation d'un objet HttpPostTask, execution de la methode
                 * doInBackground et recuperation du resultat */
                v.setVisibility(View.INVISIBLE);
                findViewById(R.id.sign_in_master_progressBar).setVisibility(View.VISIBLE);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    String url = Config.SERVER_DOMAIN + "user";
                    TextView mailAddress = (TextView) findViewById(R.id.sign_in_master_login);
                    TextView password = (TextView) findViewById(R.id.sign_in_master_password);

                    if (!mailAddress.getText().toString().equals("") && !password.getText().toString().equals("")) {

                        // Construction de l'url REST
                        url += "?user[mail]=" + mailAddress.getText().toString() + "&user[password]=" + password.getText().toString();

                        try {

                            JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                                    .execute(new Object[]{url}).get());

                            System.out.println(replyFromServer.toString());

                            if (!((boolean) replyFromServer.get("success"))) {

                                // Afficher le bouton et cacher la barre de progression
                                v.setVisibility(View.VISIBLE);
                                findViewById(R.id.sign_in_master_progressBar).setVisibility(View.INVISIBLE);

                                if (replyFromServer.get("message").equals("wrongUser"))
                                    Toast.makeText(SignInMasterActivity.this,
                                            R.string.message_sign_in_failed, Toast.LENGTH_LONG).show();
                                else Toast.makeText(SignInMasterActivity.this,
                                        R.string.message_internal_server_error, Toast.LENGTH_LONG).show();

                            } else {

                                // le retour d'une requete select est du JSON
                                JSONObject userInfoJSON = (JSONObject) replyFromServer.get("data");
                                userInfoJSON.put("password", password.getText().toString());

                        /* Ajout dans le fichier user_info des informations du nouvel utilisateur
                           pour l'envoi de l'id et password dans les futures requetes qu'il passera
                        */
                                // Desactivation de la progress bar en cercle et apparition du bouton
                                v.setVisibility(View.VISIBLE);
                                findViewById(R.id.sign_in_master_progressBar).setVisibility(View.INVISIBLE);

                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url(Config.SERVER_WS).build();
                                MasterWS listener = new MasterWS(getApplicationContext(), userInfoJSON);
                                WebSocket ws = client.newWebSocket(request, listener);
                                client.dispatcher().executorService().shutdown();

                                deleteFile(Config.USER_INFO);
                                FileOutputStream userInfoStream = openFileOutput(Config.USER_INFO, Context.MODE_PRIVATE);
                                userInfoStream.write(userInfoJSON.toString().getBytes());

                                Intent intent = new Intent(SignInMasterActivity.this, ListSlavesActivity.class);
                                startActivity(intent);
                            }

                        } catch (JSONException | InterruptedException | ExecutionException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Afficher le bouton et cacher la barre de progression
                        v.setVisibility(View.VISIBLE);
                        findViewById(R.id.sign_in_master_progressBar).setVisibility(View.INVISIBLE);
                        Toast.makeText(SignInMasterActivity.this, R.string.message_missing_mandatory_fields, Toast.LENGTH_LONG).show();
                    }
                } else {

                    // Afficher le bouton et cacher la barre de progression
                    v.setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_in_master_progressBar).setVisibility(View.INVISIBLE);
                    Toast.makeText(SignInMasterActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

