package com.example.chancek.watchtalktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class PinPage extends WearableActivity {


    String pincode;

    EditText codeInput;

    Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode_page);

        // Enables Always-on
        setAmbientEnabled();

        codeInput = findViewById(R.id.editTextCode);

        submitButton = findViewById(R.id.buttonSubmit);


    }

    public void submitPin(View view) {

        String correctPin = "1234";
        pincode = codeInput.getText().toString();

       if (pincode.equals(correctPin)) {
            Intent intent = new Intent(this, SurveyQuestion.class);
            startActivity(intent);
        }

    }

}
