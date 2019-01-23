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
        String filename = extras.getString("Filename");

        FileInputStream in;
        String fileString = "";

        // filename == "" if the file writer threw an exception in SurveyQuestion
        // If the filename is valid, read the file and display results.
        if (!filename.equals("")) {
            try {
                in = openFileInput(filename);
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                fileString = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                fileString = "Exception thrown";
            }
        }

        String totalText = "Your survey is complete:" + dateFinished + "\n" + fileString;

        textExit.setText(totalText);


    }
}
