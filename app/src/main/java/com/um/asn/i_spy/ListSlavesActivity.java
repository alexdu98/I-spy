package com.um.asn.i_spy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.um.asn.i_spy.adapters.PhonesAdapter;
import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.services.SlaveService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ListSlavesActivity extends AppCompatActivity {

    PhonesAdapter myPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_slaves);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.list_slaves_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addSlaveIntent = new Intent(ListSlavesActivity.this, AddSlaveActivity.class);
                startActivity(addSlaveIntent);
            }
        });

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);

        ListView slavesListView = (ListView)findViewById(R.id.slaves_list_view);
        slavesListView.setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(R.id.activity_list_slaves_id);
        root.addView(progressBar);

        try {

            InputStream userInfoIS = openFileInput(Config.USER_INFO);
            BufferedReader userInfoBR = new BufferedReader(new InputStreamReader(userInfoIS));
            JSONObject userInfoJSON = new JSONObject(userInfoBR.readLine());

            String id = userInfoJSON.get("id").toString();
            String mail = userInfoJSON.get("mail").toString();
            String password = userInfoJSON.get("password").toString();

            String url = Config.SERVER_DOMAIN + "user/" + id + "/phones";

            // Construction de l'url REST
            url += "?user[mail]=" + mail + "&user[password]=" + password;

            JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                    .execute(new Object[]{url}).get());

            if (!((boolean) replyFromServer.get("success"))) {

                if (replyFromServer.get("message").equals("wrongUser"))
                    Toast.makeText(ListSlavesActivity.this,
                            R.string.message_sign_in_failed, Toast.LENGTH_LONG).show();
                else Toast.makeText(ListSlavesActivity.this,
                        R.string.message_internal_server_error, Toast.LENGTH_LONG).show();
            } else {

                // le retour d'une requete select est un tableau JSON
                JSONArray phonesArray = (JSONArray) replyFromServer.get("data");

                if (phonesArray.length() == 0) {

                    TextView listSlavesMessage = (TextView) findViewById(R.id.list_slaves_message);
                    listSlavesMessage.setText(R.string.message_no_phones_registered);
                    listSlavesMessage.setVisibility(View.VISIBLE);

                } else {

                    // Boucle sur tous les objets JSON renvoye par la requete
                    ArrayList<Phone> phones = new ArrayList<>();
                    JSONObject phoneJSON;

                    for(int i = 0; i < phonesArray.length(); i++) {
                        phoneJSON = phonesArray.getJSONObject(i);
                        phones.add(new Phone(String.valueOf(phoneJSON.get("id")),
                                (String)phoneJSON.get("login")));
                    }

                    myPhones = new PhonesAdapter(this, phones);

                    slavesListView.setAdapter(myPhones);

                    slavesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });
                }


            }

        } catch (JSONException | InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }




    }

}
