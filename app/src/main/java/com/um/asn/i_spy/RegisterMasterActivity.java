package com.um.asn.i_spy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.um.asn.i_spy.http_methods.HttpPostTask;
import com.um.asn.i_spy.websockets.MasterWS;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class RegisterMasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_master);

        final Button registerMasterButton = (Button) findViewById(R.id.register_master_button);

        registerMasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Recuperation de la progress bar
                ProgressBar registerMasterProgressBar = (ProgressBar) findViewById(R.id.register_master_progressBar);

                // Activation de la progress bar en cercle et effacement du bouton
                v.setVisibility(View.INVISIBLE);
                registerMasterProgressBar.setVisibility(View.VISIBLE);

                try {

                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        String url = Config.SERVER_DOMAIN + "/users";
                        TextView mailAddress = (TextView) findViewById(R.id.register_master_login);
                        TextView password = (TextView) findViewById(R.id.register_master_password);

                        if (!mailAddress.getText().toString().equals("") && !password.getText().toString().equals("")) {

                            // Construction de l'objet json
                            JSONObject user = new JSONObject();
                            user.put("mail", mailAddress.getText().toString());
                            user.put("password", password.getText().toString());

                             /* Creation d'un objet HttpPostTask, execution de la methode
                            * doInBackground et recuperation du resultat */
                            JSONObject replyFromServer = new JSONObject((String) new HttpPostTask()
                                    .execute(new Object[]{url, user.toString()}).get());

                            if (!((boolean) replyFromServer.get("success"))) {

                                /* Handling error message from server*/
                                String message = (String) replyFromServer.get("message");
                                int toastMessage;

                                switch (message) {
                                    case "uniqueMail":
                                        toastMessage = R.string.message_mail_already_exists;
                                        break;
                                    default:
                                        toastMessage = R.string.message_internal_server_error;
                                        break;
                                }

                                Toast.makeText(RegisterMasterActivity.this,
                                        toastMessage, Toast.LENGTH_LONG).show();

                                // Desactivation de la progress bar en cercle et apparition du bouton
                                v.setVisibility(View.VISIBLE);
                                registerMasterProgressBar.setVisibility(View.INVISIBLE);

                            } else {

                                JSONObject insertedId = (JSONObject) replyFromServer.get("data");

                                /* Ajout dans le fichier user_info des informations du nouvel utilisateur
                                   pour l'envoi de l'id et password dans les futures requetes qu'il passera
                                */

                                JSONObject userInfoJSON = new JSONObject();
                                userInfoJSON.put("id", insertedId.get("id"));
                                userInfoJSON.put("mail", mailAddress.getText().toString());
                                userInfoJSON.put("password", password.getText().toString()); // Penser a encrypter le mot de passe

                                // Desactivation de la progress bar en cercle et apparition du bouton
                                v.setVisibility(View.VISIBLE);
                                registerMasterProgressBar.setVisibility(View.INVISIBLE);

                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url(Config.SERVER_WS).build();
                                MasterWS listener = new MasterWS(getApplicationContext());
                                WebSocket ws = client.newWebSocket(request, listener);
                                client.dispatcher().executorService().shutdown();

                                // Supprime le fichier user info si il existe
                                deleteFile(Config.USER_INFO);

                                FileOutputStream userInfoStream = openFileOutput(Config.USER_INFO, Context.MODE_PRIVATE);
                                userInfoStream.write(userInfoJSON.toString().getBytes());

                                Intent intent = new Intent(RegisterMasterActivity.this, ListSlavesActivity.class);
                                startActivity(intent);
                            }

                        } else {

                            Toast.makeText(RegisterMasterActivity.this,
                                    R.string.message_missing_mandatory_fields, Toast.LENGTH_LONG).show();

                            // Desactivation de la progress bar en cercle et apparition du bouton
                            v.setVisibility(View.VISIBLE);
                            registerMasterProgressBar.setVisibility(View.INVISIBLE);
                        }
                    } else {

                        Toast.makeText(RegisterMasterActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
                        // Desactivation de la progress bar en cercle et apparition du bouton
                        v.setVisibility(View.VISIBLE);
                        registerMasterProgressBar.setVisibility(View.INVISIBLE);
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

}
