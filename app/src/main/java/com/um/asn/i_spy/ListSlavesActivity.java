package com.um.asn.i_spy;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.um.asn.i_spy.adapters.PhonesAdapter;
import com.um.asn.i_spy.http_methods.HttpGetTask;
import com.um.asn.i_spy.models.Phone;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

        String url = Config.SERVER_DOMAIN;

        try {

            InputStream userInfoIS = openFileInput(Config.USER_INFO);
            BufferedReader userInfoBR = new BufferedReader(new InputStreamReader(userInfoIS));
            JSONObject userInfoJSON = new JSONObject(userInfoBR.readLine());


            JSONObject replyFromServer = new JSONObject((String) new HttpGetTask()
                    .execute(new Object[]{url}).get());


        } catch (JSONException | InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }


        ArrayList<Phone> phones = new ArrayList<>();
        phones.add(new Phone("1", "0678295634", "secret"));
        phones.add(new Phone("1", "0678295634", "secret"));
        phones.add(new Phone("1", "0678295634", "secret"));

        myPhones = new PhonesAdapter(this, phones);

        slavesListView.setAdapter(myPhones);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.list_slaves_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ListSlavesActivity.this, "Bouton d'ajout", Toast.LENGTH_LONG).show();
            }
        });

    }

}
