package com.example.chancek.watchtalktest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;



public class MainActivity extends WearableActivity {

    //SurveyResponse Responses = new SurveyResponse();
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    public boolean useSecure = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission
                    .RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }

    public void startSurvey(View view)
    {
        Intent intent;
        if (useSecure)
            intent = new Intent(this, PinPage.class);
        else
            intent = new Intent(this, SurveyQuestion.class);
        //intent.putExtra("Serialized_Responses", Responses);
        startActivity(intent);
    }


}
