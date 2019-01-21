package com.example.chancek.watchtalktest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;



public class MainMenu extends WearableActivity {

    boolean useSecure = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Enables Always-on
        setAmbientEnabled();

    }

    public void startSurvey(View view)
    {
        Intent intent;
        if (useSecure)
            intent = new Intent(this, PinPage.class);
        else
            intent = new Intent(this, SurveyQuestion.class);

        startActivity(intent);
    }


}
