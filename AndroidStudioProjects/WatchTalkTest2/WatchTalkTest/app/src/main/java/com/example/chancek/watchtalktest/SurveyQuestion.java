package com.example.chancek.watchtalktest;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SurveyQuestion extends WearableActivity {

    // Global variables
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10;

    // PROMIS Pain interference non-CAT
    //String testOID = "C1E44752-BCBD-4130-A307-67F6758F3891";

    // ASCQ Me Social Functioning impact CAT
    String testOID = "042ED857-B664-4A22-B5FA-6CF3CF15763F";

    String filename = "WatchTalkTest_Results.txt";

    TextToSpeech tts;
    String gQuestion;
    Spinner spinnerOptions;
    Vibrator vibrator;
    Button submitButton;

    // UtteranceID strings for use with tts
    public static final String QUESTION_DONE = "QDone";
    public static final String ANSWERS_DONE = "ADone";

    //Private variables
    private SpeechRecognizer mySR;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    // Initialize variables that store registration ID and token
    String textID = "0ED7B052-FDB2-4EDF-9B4B-E732F69DDF7A";
    String textToken = "3171FF33-83C5-4221-9BB0-051DC747AEB9";
    String totalToken = textID + ":" + textToken;

    //TODO: define what type of ArrayList this is (i.e. ArrayList<String>)
    ArrayList responseIDArray = new ArrayList();

    // Make array for options
    ArrayList<String> optionsArray = new ArrayList<>();

    String gToken;
    String URL;
    String TAG = "WATCHTALKTEST";

    // Initialize handler for running tts, voice recognition in main thread
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_question);

        // Enables Always-on
        setAmbientEnabled();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(SurveyQuestion.this, new String[]{Manifest.permission
                    .RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        handler = new Handler(getMainLooper());

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setEnabled(false);

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
                            Log.d("MainMenu", "TTS finished");

                            if (utteranceId.equals(QUESTION_DONE)){
                                submitButton.setEnabled(true);
                            }
                            else if (utteranceId.equals(ANSWERS_DONE)){
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
            }
        });

        spinnerOptions = findViewById(R.id.spinnerOptions);


        // Generate participant token
        String tokenURL = "https://www.assessmentcenter.net/ac_api/2014-01/Assessments/" +
                testOID + ".json";
        getToken(tokenURL);


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
            //Log.d(TAG,  "error " +  error);
            getVoiceInput_noUI(1);
        }
        public void onResults(Bundle results)
        {
            // Once user has finished speaking, respond appropriately
            String str = "";
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                //Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            String mString = str.toLowerCase();

            int idx = getIndex(spinnerOptions, mString);
            if (idx != -1) {
                // Valid answer received
                spinnerOptions.setSelection(idx);
                submitButton.setEnabled(false);
                new CountDownTimer(500,100){
                    public void onTick(long millisUntilFinished) {
                        //do nothing
                    }

                    public void onFinish() {
                        submitAnswer();
                    }

                }.start();
            }
            else if (mString.contains("again") || mString.contains("repeat"))
            {
                // Repeat the question and answers, and prompt for voice input
                getVoiceInput_noUI(0);
            }
            else
            {
                // Invalid/empty input
                getVoiceInput_noUI(1);
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


    public void getQuestions (String itoken, String responseID, String valueID){
        // Make Request
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        //itoken is OID, update gToken accordingly
        gToken = itoken;

        if (responseID == null) {
            URL = "https://www.assessmentcenter.net/ac_api/2014-01/Participants/" + itoken + ".json";
        }
        else {
            //String URL = "https://www.assessmentcenter.net/ac_api/2014-01/Participants/" + itoken + ".json?ItemResponseOID=374D7D6F-7281-4E75-BB77-6ED5CCE479D3";
            URL = "https://www.assessmentcenter.net/ac_api/2014-01/Participants/" + itoken + ".json?ItemResponseOID=" + responseID + "&Response=" + valueID;
        }

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());
                        try {
                            String dateFinished = response.getString("DateFinished");

                            if (!dateFinished.equals("") && !dateFinished.equals(null)){
                                callExitPage(dateFinished, gToken);
                            }

                            JSONArray elementsArray = response.getJSONArray("Items").getJSONObject(0).getJSONArray("Elements");
                            String question = "";
                            for (int i = 0; i < elementsArray.length() - 1; i++) {
                                question = question + " " + response.getJSONArray("Items").getJSONObject(0).getJSONArray("Elements").getJSONObject(i).get("Description");
                            }

                            //get question from api
                            TextView textViewQuestion = findViewById(R.id.textViewQuestion);


                            //set question into text box only if date finished is null
                            textViewQuestion.setText(question);

                            //Speak question
                            gQuestion = question;
                            tts.speak(question,TextToSpeech.QUEUE_FLUSH,null, QUESTION_DONE);

                            // Make array for map
                            JSONArray mapArray = response.getJSONArray("Items").getJSONObject(0)
                                    .getJSONArray("Elements").getJSONObject(elementsArray.length()-1)
                                    .getJSONArray
                                            ("Map");

                            // clear optionsArray and responseIDArray
                            optionsArray.clear();
                            responseIDArray.clear();


                            for (int i = 0; i < mapArray.length(); i++) {
                                optionsArray.add(mapArray.getJSONObject(i).get("Description").toString());
                                responseIDArray.add(mapArray.getJSONObject(i).get("ItemResponseOID"));
                            }


                            //Load Spinner with Options
                            LoadSpinner(optionsArray);

                            //use tts to speak answers
                            String currentAnswer;
                            for (int i = 0; i < optionsArray.size(); i++)
                            {
                                if (i == optionsArray.size()-1)
                                {
                                    currentAnswer = "or " + optionsArray.get(i) + "?";
                                    tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,ANSWERS_DONE);
                                }
                                else {
                                    currentAnswer = optionsArray.get(i) + ", ";
                                    tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                                    tts.playSilentUtterance(100,TextToSpeech.QUEUE_ADD,null);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response Error", error.toString());
            }
        }
        ) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                byte[] data = null;
                try {
                    data = totalToken.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //String base64Token = Base64.encodeToString(dataToken, Base64.DEFAULT);
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                //              System.out.print(base64);


                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Basic " + base64);
                return params;
            }


        };


        requestQueue.add(objectRequest);

    }


    // make request for token
    public void getToken(String tokenURL){


        // Make Request
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JsonObjectRequest objectRequest;
        /*String tokenURL = "https://www.assessmentcenter" +
                ".net/ac_api/2014-01/Assessments/C1E44752-BCBD-4130-A307-67F6758F3891.json";*/



         objectRequest = new JsonObjectRequest(Request.Method.POST, tokenURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());

                        try {
                           String itoken = response.getString("OID");
                           Log.e("Key value", itoken);
                           getQuestions(itoken,null, null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response Error", error.toString());
            }
        }
        ) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                byte[] data = null;
                try {
                    data = totalToken.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //String base64Token = Base64.encodeToString(dataToken, Base64.DEFAULT);
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                //              System.out.print(base64);


                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Basic " + base64);
                return params;
            }


        };


        requestQueue.add(objectRequest);

    }

    public void LoadSpinner(ArrayList<String> optionsArray){
        //Spinner spinnerOptions = findViewById(R.id.spinnerOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, optionsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

    }

    // submit via button
    public void submitAnswer(View view)
    {
        //String selectedAnswer = spinnerOptions.getSelectedItem().toString();
        tts.stop();
        mySR.cancel();
        submitButton.setEnabled(false);
        int index = spinnerOptions.getSelectedItemPosition();
        String responseID = responseIDArray.get(index).toString();
        String valueID = Integer.toString(index + 1);

        Log.e(TAG, "Submitting answer: responseID = " + responseID + ", valueID = " + valueID);

        getQuestions(gToken,responseID,valueID);
    }

    // submit via code
    public void submitAnswer()
    {
        //String selectedAnswer = spinnerOptions.getSelectedItem().toString();
        int index = spinnerOptions.getSelectedItemPosition();
        String responseID = responseIDArray.get(index).toString();
        String valueID = Integer.toString(index + 1);

        Log.e(TAG, "Submitting answer: responseID = " + responseID + ", valueID = " + valueID);

        getQuestions(gToken,responseID,valueID);
    }

    public void callExitPage(final String dateFinished, String itoken){

        tts.stop();
        tts.shutdown();

        // Make Request
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JsonObjectRequest objectRequest;
        String tokenURL = "https://www.assessmentcenter" +
                ".net/ac_api/2014-01/Results/" + itoken + ".json";



        objectRequest = new JsonObjectRequest(Request.Method.POST, tokenURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Results Text = ", response.toString());

                        String fileString;
                        FileOutputStream outputStream;

                        try {
                            fileString = response.toString();
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(fileString.getBytes());
                            outputStream.close();

                            Intent intent = new Intent(SurveyQuestion.this, ExitPage.class);
                            intent.putExtra("Date", dateFinished + " Results received.");
                            intent.putExtra("Filename", filename);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Intent intent = new Intent(SurveyQuestion.this, ExitPage.class);
                            intent.putExtra("Date", dateFinished +" Could not get results.");
                            intent.putExtra("Filename", "");
                            startActivity(intent);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response Error", error.toString());
            }
        }
        ) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                byte[] data = null;
                try {
                    data = totalToken.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //String base64Token = Base64.encodeToString(dataToken, Base64.DEFAULT);
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                //              System.out.print(base64);


                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Basic " + base64);
                return params;
            }


        };


        requestQueue.add(objectRequest);
    }

    /*public void getVoiceInput(View view)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }*/

    // Get voice input without displaying the default pop-up window.
    public void getVoiceInput_noUI()
    {
        if (!SpeechRecognizer.isRecognitionAvailable(this))
            Log.d(TAG, "No voice recognition available.");
        else {
            //tts.speak("Starting", TextToSpeech.QUEUE_FLUSH, null, null);
            SurveyQuestion.handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice" +
                    //".recognition.test");
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    mySR.startListening(intent);
                    Log.d(TAG, "started listening");
                    submitButton.setEnabled(true);
                }
            });

        }
    }

    // Get voice input after reading out a tts message determined by "code"."
    // code == 0: repeat the question and answers.
    // code == 1: tell the user that they need to try again, and re-prompt
    public void getVoiceInput_noUI(int code)
    {
        if (!SpeechRecognizer.isRecognitionAvailable(this))
            Log.d(TAG, "No voice recognition available.");
        else {
            if (code == 0)
            {
                SurveyQuestion.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] randOptions = {"OK", "Sure", "Of course"};
                        int rndIdx = new Random().nextInt(randOptions.length);
                        tts.speak(randOptions[rndIdx], TextToSpeech.QUEUE_FLUSH,null,null);
                        tts.playSilentUtterance(50,TextToSpeech.QUEUE_ADD,null);
                        tts.speak(gQuestion,TextToSpeech.QUEUE_ADD,null, null);

                        String currentAnswer;
                        for (int i = 0; i < optionsArray.size(); i++)
                        {
                            if (i == optionsArray.size()-1)
                            {
                                currentAnswer = "or " + optionsArray.get(i) + "?";
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,ANSWERS_DONE);
                            }
                            else {
                                currentAnswer = optionsArray.get(i) + ", ";
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                                tts.playSilentUtterance(100,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                    }
                });
            }
            else // code == 1
            {
                SurveyQuestion.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak("Sorry, I didn't catch that.  Please repeat your answer.", TextToSpeech.QUEUE_FLUSH,
                                null, ANSWERS_DONE);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // set spinner position based on received string
                    String mString = result.toString().toLowerCase();

                    int idx = getIndex(spinnerOptions, mString);
                    if (idx != -1) {
                        spinnerOptions.setSelection(idx);
                    }

                }
                break;
            }

        }
    }

    //Get the index of the spinner element matching myString.  Return -1 if no match found
    private int getIndex(Spinner spinner, String myString){

        int index = -1;

        for (int i=0;i<spinner.getCount();i++){
            if (myString.contains(spinner.getItemAtPosition(i).toString().toLowerCase())){
                index = i;
                break;
            }
        }
        return index;
    }
}



