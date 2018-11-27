package com.example.chancek.watchtalktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;



public class MainActivity extends WearableActivity {

    //SurveyResponse Responses = new SurveyResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();
    }

    public void startSurvey(View view)
    {
        Intent intent = new Intent(this, PinPage.class);
        //intent.putExtra("Serialized_Responses", Responses);
        startActivity(intent);
    }


}
