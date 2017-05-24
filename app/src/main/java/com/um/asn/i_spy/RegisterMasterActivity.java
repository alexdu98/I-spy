package com.um.asn.i_spy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.um.asn.i_spy.http_methods.HttpPostTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegisterMasterActivity extends AppCompatActivity {

    public final static String SERVER_DOMAIN = "https://ispy.calyxe.fr/index.php/";
    public final static String USER_INFO = "user_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_master);

        Button registerMasterButton = (Button) findViewById(R.id.register_master_button);

        registerMasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Creation d'un objet HttpPostTask, execution de la methode
                 * doInBackground et recuperation du resultat */
                try {

                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        String url = SERVER_DOMAIN + "user";
                        TextView mailAddress = (TextView) findViewById(R.id.register_master_login);
                        TextView password = (TextView) findViewById(R.id.register_master_password);

                        if (!mailAddress.getText().toString().equals("") && !password.getText().toString().equals("")) {

                            // Construction de l'objet json
                            JSONObject user = new JSONObject();
                            user.put("mail", mailAddress.getText().toString());
                            user.put("password", password.getText().toString());

                            JSONObject replyFromServer = new JSONObject((String) new HttpPostTask()
                                    .execute(new Object[]{url, user.toString()}).get());

                            if (((boolean)replyFromServer.get("success")) == false) {

                                Toast.makeText(RegisterMasterActivity.this,
                                        R.string.message_register_master_error, Toast.LENGTH_LONG).show();
                            } else {

                                JSONObject insertedId = (JSONObject) replyFromServer.get("data");

                        /* Ajout dans le fichier user_info des informations du nouvel utilisateur
                           pour l'envoi de l'id et password dans les futures requetes qu'il passera
                        */

                                JSONObject userInfoJSON = new JSONObject();
                                userInfoJSON.put("user_id", insertedId.get("id"));
                                userInfoJSON.put("mail", mailAddress.getText().toString());
                                userInfoJSON.put("password", password.getText().toString()); // Penser a encrypter le mot de passe

                                FileOutputStream userInfoStream = openFileOutput(USER_INFO, Context.MODE_PRIVATE);
                                userInfoStream.write(userInfoJSON.toString().getBytes());

                                Intent intent = new Intent(RegisterMasterActivity.this, ListSlavesActivity.class);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Toast.makeText(RegisterMasterActivity.this, R.string.message_internet_connection_error, Toast.LENGTH_LONG).show();
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
