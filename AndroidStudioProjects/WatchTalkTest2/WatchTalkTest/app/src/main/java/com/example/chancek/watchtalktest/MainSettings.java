package com.example.chancek.watchtalktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

public class MainSettings extends WearableActivity {

    private TextView mTextView;
    String surveyOID;
    Map<String, String> testMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        // Enables Always-on
        setAmbientEnabled();

        Bundle extras = getIntent().getExtras();
        surveyOID = extras.getString("surveyOID");

        Spinner spin = (Spinner) findViewById(R.id.spinner);

    }

    public void onClickOK(View view)
    {
        Intent intent;
        intent = new Intent(this, MainMenu.class);

        intent.putExtra("surveyOID", surveyOID);

        startActivity(intent);
    }
}
