package com.example.chancek.watchtalktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainSettings extends WearableActivity {

    private TextView mTextView;
    String surveyOID;
    HashMap<String, String> testMap = new HashMap<>();

    //ArrayList<String> OIDArray = new ArrayList<>();
    ArrayList<String> nameArray = new ArrayList<>();

    Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        // Enables Always-on
        setAmbientEnabled();

        //Bundle extras = getIntent().getExtras();
        //surveyOID = extras.getString("surveyOID");

        spin = (Spinner) findViewById(R.id.spinner);

        nameArray.add("PROMIS Pain Interference");
        testMap.put("PROMIS Pain Interference", "C1E44752-BCBD-4130-A307-67F6758F3891");

        nameArray.add("ASCQ-Me Social Functioning Impact CAT");
        testMap.put("ASCQ-Me Social Functioning Impact CAT", "042ED857-B664-4A22-B5FA" +
                "-6CF3CF15763F");

        nameArray.add("ASCQ-Me Pain Impact CAT");
        testMap.put("ASCQ-Me Pain Impact CAT", "A42E79AB-FE2B-4665-8089-BE1AA4C32D6B");

        LoadSpinner(nameArray);
    }

    public void LoadSpinner(ArrayList<String> optionsArray){
        //Spinner spinnerOptions = findViewById(R.id.spinnerOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, optionsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

    }

    public void onClickOK(View view)
    {
        int index = spin.getSelectedItemPosition();
        String name = nameArray.get(index);
        surveyOID = testMap.get(name);
        Intent intent;
        intent = new Intent(this, MainMenu.class);
        intent.putExtra("surveyOID", surveyOID);
        startActivity(intent);
    }
}
