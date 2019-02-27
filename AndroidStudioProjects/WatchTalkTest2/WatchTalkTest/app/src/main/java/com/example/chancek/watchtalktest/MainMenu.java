package com.example.chancek.watchtalktest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import java.util.Locale;


public class MainMenu extends WearableActivity {
    String surveyOID;
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    public static final String TTS_DONE = "TTSDone";
    String TAG = "WATCH_TALK_TEST";

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Enables Always-on
        setAmbientEnabled();
        Bundle extras = getIntent().getExtras();
        try {
            surveyOID = extras.getString("surveyOID");
        } catch (NullPointerException nEx){
            Log.e(TAG, "Null pointer exception!");
            nEx.printStackTrace();
            surveyOID = "80C5D4A3-FC1F-4C1B-B07E-10B796CF8105"; //PROMIS Physical Function
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainMenu.this, new String[]{Manifest.permission
                    .RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // Ask a question
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);

                    //Whenever TTS finishes speaking a line with "utteranceID" =
                    // ANSWERS_DONE, vibrate and trigger voice
                    // recognition
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG, "TTS finished");

                            if (utteranceId.equals(TTS_DONE)){
                                //TODO: react to what the user says
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                        }
                    });
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }

                }
                else
                    Log.e("error", "Initialization Failed!");
            }
        });

        tts.speak("Welcome! Press Start to begin your survey.", TextToSpeech.QUEUE_FLUSH,null,
                null);
    }

    public void startSurvey(View view)
    {
        tts.stop();
        tts.shutdown();
        Intent intent;
        intent = new Intent(this, SurveyQuestion.class);
        intent.putExtra("surveyOID", surveyOID);

        startActivity(intent);
    }

    public void startSurvey()
    {
        Intent intent;
        intent = new Intent(this, SurveyQuestion.class);
        intent.putExtra("surveyOID", surveyOID);

        startActivity(intent);
    }

    public void goToSettings(View view)
    {
        tts.stop();
        tts.shutdown();
        Intent intent;
        intent = new Intent(this, MainSettings.class);

        startActivity(intent);
    }

    public void goToSettings()
    {
        Intent intent;
        intent = new Intent(this, MainSettings.class);

        startActivity(intent);
    }
}
