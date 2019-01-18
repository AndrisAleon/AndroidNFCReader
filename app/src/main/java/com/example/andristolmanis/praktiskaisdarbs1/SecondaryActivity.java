package com.example.andristolmanis.praktiskaisdarbs1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SecondaryActivity extends AppCompatActivity {

    Button buttonActivityOne;

    Button btnRead;
    SharedPreferences sp;
    EditText txtWriteBox;
    public static final String USER_PREF = "USER_PREF" ;
    public static final String KEY_TEXT = "KEY_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //PD2
        buttonActivityOne = (Button) findViewById(R.id.buttonActivity1);
        btnRead = (Button) findViewById(R.id.btnRead);
        // PD3
        sp = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        txtWriteBox = (EditText) findViewById(R.id.txtWriteBox);
        //---
        // PD2
        buttonActivityOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondaryActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        // PD3
        btnRead.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StringBuilder str = new StringBuilder();
                if (sp.contains(KEY_TEXT)) {
                    txtWriteBox.setText(sp.getString(KEY_TEXT, ""));
                }

                Toast.makeText(SecondaryActivity.this, "Teksts parādīts!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
