package com.um.asn.i_spy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class RegisterOrSignInMasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_connect_activity);

        // Bouton pour se diriger vers l'inscription
        Button chooseRegisterButton = (Button)findViewById(R.id.choose_register_button);
        chooseRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerMasterIntent = new Intent(RegisterOrSignInMasterActivity.this, RegisterMasterActivity.class);
                startActivity(registerMasterIntent);
            }
        });

        // Bouton pour se diriger vers la connexion
        Button chooseConnectButton = (Button) findViewById(R.id.choose_connect_button);
        chooseConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userSignInIntent = new Intent(RegisterOrSignInMasterActivity.this, SignInMasterActivity.class);
                startActivity(userSignInIntent);
            }
        });
    }
}
