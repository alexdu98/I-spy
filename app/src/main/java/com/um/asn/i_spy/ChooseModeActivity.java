package com.um.asn.i_spy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ChooseModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        Button slaveModeButton = (Button) findViewById(R.id.slave_mode_button);
        Button masterModeButton = (Button) findViewById(R.id.master_mode_button);

        slaveModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseModeActivity.this, RegisterOrSignInSlaveActivity.class);
                startActivity(intent);
            }
        });

        masterModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseModeActivity.this, RegisterOrSignInMasterActivity.class);
                startActivity(intent);
            }
        });
    }
}