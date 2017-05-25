package com.um.asn.i_spy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class SlaveMenuActivity extends AppCompatActivity {

    // Menu Items ids
    final static int LOCATE_PHONE_ITEM = 0;
    final static int GET_CONTACTS_ITEM = 1;
    // Menu Items labels
    static ArrayList<String> menuItems = new ArrayList<>();

    static {
        menuItems.add("Localiser le téléphone");
        menuItems.add("Voir les contacts");
    }

    ArrayAdapter<String> menuItemsAdapter;
    String phoneId;
    String phoneLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuperation des infos du telephone pour lequel le menu est affiche
        Intent slaveMenuIntent = getIntent();

        phoneId = slaveMenuIntent.getStringExtra("id");
        phoneLogin = slaveMenuIntent.getStringExtra("login");

        setContentView(R.layout.activity_slave_menu);
        setTitle(getResources().getString(R.string.slave_menu_label) + " " + phoneLogin);

        menuItemsAdapter = new ArrayAdapter<String>(this, R.layout.slave_menu_item, menuItems);

        ListView slaveMenuLW = (ListView) findViewById(R.id.slave_menu_list_view);

        slaveMenuLW.setAdapter(menuItemsAdapter);

        slaveMenuLW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent menuItemIntent;

                switch (position) {

                    // Lancement de l'activite de localisation du telephone
                    case LOCATE_PHONE_ITEM:

                        menuItemIntent = new Intent(SlaveMenuActivity.this, LocateSlaveActivity.class);

                        menuItemIntent.putExtra("id", phoneId);
                        menuItemIntent.putExtra("login", phoneLogin);

                        startActivity(menuItemIntent);

                        break;

                    case GET_CONTACTS_ITEM:
                        break;

                    default:
                        break;
                }
            }
        });
    }
}
