package com.example.chancek.watchtalktest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;


public class MainMenu extends WearableActivity {
    String surveyOID;
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    public static final String TTS_DONE = "TTSDone";
    String TAG = "WATCH_TALK_TEST";

    TextToSpeech tts;
    private SpeechRecognizer mySR;

    int numErrors;
    int MAX_ERRORS = 5;

    public static Handler handler;

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

        handler = new Handler(getMainLooper());

        mySR = SpeechRecognizer.createSpeechRecognizer(this);
        mySR.setRecognitionListener(new listener());

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
                                getVoiceInput_noUI();
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

                tts.speak("Welcome!  Say start after the vibration prompt, or press the start" +
                                " button to begin your survey.",
                        TextToSpeech.QUEUE_FLUSH,null, TTS_DONE);
            }
        });
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
        }
        public void onError(int error) {
            Log.d(TAG,  "error " +  error);
            numErrors++;
            if (numErrors > MAX_ERRORS)
                QuitApp();
            else
                rePrompt();
        }
        public void onResults(Bundle results)
        {
            // Once user has finished speaking, respond appropriately
            String str = "";

            // DO NOT CHANGE promptQuit HERE
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                //Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            String mString = str.toLowerCase();

            // Valid answer received
            if (mString.contains("start"))
            {
                startSurvey();
            }
            else if (mString.contains("quit"))
            {
                QuitApp();
            }
            else
            {
                // Invalid input
                numErrors++;
                if (numErrors > MAX_ERRORS)
                    QuitApp();
                else
                    rePrompt();
            }

        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void startSurvey(View view)
    {
        tts.stop();
        tts.shutdown();
        mySR.cancel();
        mySR.destroy();
        Intent intent;
        intent = new Intent(this, SurveyQuestion.class);
        intent.putExtra("surveyOID", surveyOID);

        startActivity(intent);
    }

    public void startSurvey()
    {
        tts.stop();
        tts.shutdown();
        mySR.cancel();
        mySR.destroy();
        Intent intent;
        intent = new Intent(this, SurveyQuestion.class);
        intent.putExtra("surveyOID", surveyOID);

        startActivity(intent);
    }

    public void goToSettings(View view)
    {
        tts.stop();
        tts.shutdown();
        mySR.cancel();
        mySR.destroy();
        Intent intent;
        intent = new Intent(this, MainSettings.class);

        startActivity(intent);
    }

    // Get voice input without displaying the default pop-up window.
    // Code = 0 means default behavior.  Code = 1 means say the startup line again first.
    public void getVoiceInput_noUI()
    {
        if (!SpeechRecognizer.isRecognitionAvailable(this))
            Log.d(TAG, "No voice recognition available.");
        else {
            MainMenu.handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    mySR.startListening(intent);
                    Log.d(TAG, "started listening");
                }
            });
        }
    }

    public void rePrompt(){
        MainMenu.handler.post(new Runnable() {
            @Override
            public void run() {
                tts.speak("Please say start after the vibration prompt, or press the start" +
                                " button to begin your survey.",
                        TextToSpeech.QUEUE_FLUSH,null, TTS_DONE);
            }
        });
    }

    public void QuitApp(){
        tts.stop();
        tts.shutdown();
        mySR.cancel();
        mySR.destroy();

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

        this.finishAffinity();
    }
}
