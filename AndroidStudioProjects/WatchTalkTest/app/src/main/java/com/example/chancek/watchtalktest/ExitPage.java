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

        FileInputStream in = null;
        String inString = "Nothing was found.";
        try {
            in = openFileInput("WatchTalkTest_Results.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            //inString = "AAA";

            //TODO: the app never exits this loop, need to figure out why.
            while ((inString = bufferedReader.readLine()) != null) {
                sb.append(inString);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            inString = "Exception thrown";
        }

        mTextView.setText(inString);


    }
}
