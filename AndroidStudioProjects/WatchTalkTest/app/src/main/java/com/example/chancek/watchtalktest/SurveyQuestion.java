package com.example.chancek.watchtalktest;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.Locale;

public class SurveyQuestion extends WearableActivity {

    TextToSpeech tts;
    Spinner spinner;
    /*SurveyResponse Responses = (SurveyResponse) getIntent().getSerializableExtra
            ("Serialized_Responses");*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_question);

        // Enables Always-on
        setAmbientEnabled();

        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        tts=new TextToSpeech(SurveyQuestion.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // Ask a question
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.UK);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{
                        String message = "<speak>In the past 7 days, how much did pain interfere with your day to day activities?</speak>";
                        tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                }
                else
                    Log.e("error", "Initialization Failed!");
            }
        });
    }

    public void submitAnswer(View view){
        int answerID = spinner.getSelectedItemPosition();
        String questionID = "Q1";
        String filename = "WatchTalkTest_Results.txt";
        String fileString = "";
        FileOutputStream outputStream;
        //Responses.record("1", String.valueOf(pos));

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(questionID,answerID);

            fileString = jsonObj.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        try {
            // save fileString to a json file
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, ExitPage.class);
        startActivity(intent);
    }
}
