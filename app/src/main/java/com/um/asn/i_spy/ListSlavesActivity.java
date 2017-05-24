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
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;


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
