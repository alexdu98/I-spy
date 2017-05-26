package com.um.asn.i_spy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class RegisterOrSignInSlaveActivity extends AppCompatActivity {

    public final static int REQUEST_ALL_PERM = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_connect_activity);

        // Demande les permissions nécessaires
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.LOCATION_HARDWARE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
        }, REQUEST_ALL_PERM);

        // Bouton pour se diriger vers l'inscription
        Button chooseRegisterButton = (Button) findViewById(R.id.choose_register_button);
        chooseRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerSlaveIntent = new Intent(RegisterOrSignInSlaveActivity.this, RegisterSlaveActivity.class);
                startActivity(registerSlaveIntent);
            }
        });

        // Bouton pour se diriger vers la connexion
        Button chooseConnectButton = (Button) findViewById(R.id.choose_connect_button);
        chooseConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userSignInIntent = new Intent(RegisterOrSignInSlaveActivity.this, SignInSlaveActivity.class);
                startActivity(userSignInIntent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL_PERM: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(RegisterOrSignInSlaveActivity.this, "PERM OK !", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(RegisterOrSignInSlaveActivity.this, "PERM NOT OK !", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
