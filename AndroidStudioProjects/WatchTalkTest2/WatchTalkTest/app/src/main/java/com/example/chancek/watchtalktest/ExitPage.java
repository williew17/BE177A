package com.example.chancek.watchtalktest;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExitPage extends WearableActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_page);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

       //Display date finished
        TextView textExit = findViewById(R.id.textExit);

        Bundle extras = getIntent().getExtras();
        String dateFinished = extras.getString("Date");

        textExit.setText("Your survey is complete:" + dateFinished);


    }
}
