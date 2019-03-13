package com.example.chancek.watchtalktest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.util.StringUtils;
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
    String testOID;
    String filename = "WatchTalkTest_Results.txt";
    String timesFilename = "WatchTalkTest_Times.txt";

    // Must quit app if SpeechRecognizer encounters errors 3 times in a row OR the user is silent
    // for 30*MAX_ERRORS seconds; these variables handle these cases.
    int MAX_ERRORS = 5;
    int numErrors;
    boolean promptQuit;

    TextToSpeech tts;
    String gQuestion;
    RadioGroup rGroup;
    Vibrator vibrator;
    Button submitButton;

    // UtteranceID strings for use with tts
    public static final String QUESTION_DONE = "QDone";
    public static final String ANSWERS_DONE = "ADone";

    //Private variables
    private SpeechRecognizer mySR;

    // Initialize variables that store registration ID and token
    String textID = "0ED7B052-FDB2-4EDF-9B4B-E732F69DDF7A";
    String textToken = "3171FF33-83C5-4221-9BB0-051DC747AEB9";
    String totalToken = textID + ":" + textToken;

    // array of response ID values
    ArrayList<String> responseIDArray = new ArrayList<>();

    // array of Value IDs
    ArrayList<String> valueIDArray = new ArrayList<>();

    // Make array for options
    ArrayList<String> optionsArray = new ArrayList<>();

    // setup timestamps (First array element is start time, subsequent elements are lengths of time
    // taken to finish the corresponding question)
    Long prevRecordedTime;
    ArrayList<Long> timeArray = new ArrayList<>();

    TextView textViewQuestion;
    ScrollView sView;

    String gToken;
    String URL;
    String TAG = "WATCH_TALK_TEST";

    // Initialize handler for running tts, voice recognition in main thread
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_question);

        // Enables Always-on
        setAmbientEnabled();

        Bundle extras = getIntent().getExtras();
        testOID = extras.getString("surveyOID");

        textViewQuestion = findViewById(R.id.textViewQuestion);
        String loadingText = "Loading...";
        textViewQuestion.setText(loadingText);
        sView = findViewById(R.id.myScrollView);

        handler = new Handler(getMainLooper());

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setEnabled(false);

        rGroup = findViewById(R.id.radioGroup);

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

        //spinnerOptions = findViewById(R.id.spinnerOptions);
        numErrors = 0;
        promptQuit = false;

        prevRecordedTime = System.currentTimeMillis();
        //timeArray.add(prevRecordedTime);

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
            Log.d(TAG,  "error " +  error);
            numErrors++;
            if (numErrors == MAX_ERRORS) {
                getVoiceInput_noUI(2); // prompt to quit
            }
            else if (numErrors > MAX_ERRORS){
                //Quit app
                QuitApp();
            }
            else {
                getVoiceInput_noUI(1); // prompt for repeat
            }
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

            // try to get a valid answer from string
            int idx = getExclusiveIndex(optionsArray, mString);
            int val_idx = getExclusiveIndex(valueIDArray, mString);

            // if a valid number is present...
            if (val_idx != -1) {
                if (idx == -1) // no valid answer
                    idx = val_idx;  // use number only
                else if (idx != val_idx) // valid number and string answer do not match
                    idx = -1;
            }

            // Valid answer received
            if (idx != -1) {
                numErrors = 0;
                promptQuit = false;

                RadioButton rb = (RadioButton) rGroup.getChildAt(idx);
                int rbID = rb.getId();
                rGroup.check(rbID);
                submitButton.setEnabled(false);

                String loadingText = "Loading...";
                textViewQuestion.setText(loadingText);
                sView.smoothScrollTo(0,findViewById(rbID).getTop());
                //scrollToView(sView,findViewById(rbID));
                /*new CountDownTimer(500,100){
                    public void onTick(long millisUntilFinished) {
                        //do nothing
                    }

                    public void onFinish() {
                        submitAnswer(idx);
                    }

                }.start();*/
                submitAnswer(idx);
            }
            else if (mString.contains("again") || mString.contains("repeat"))
            {
                numErrors = 0;
                promptQuit = false;
                // Repeat the question and answers, and prompt for voice input
                getVoiceInput_noUI(0);
            }
            else if (mString.contains("quit")){
                if (promptQuit){
                    QuitApp();
                }
                else{
                    getVoiceInput_noUI(2);
                }
            }
            else
            {
                // Invalid input
                numErrors++;
                if (numErrors == MAX_ERRORS) {
                    getVoiceInput_noUI(2); // prompt to quit
                }
                else if (numErrors > MAX_ERRORS){
                    //Quit app
                    QuitApp();
                }
                else {
                    getVoiceInput_noUI(1); // prompt for repeat
                }
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
        numErrors = 0;
        tts.stop();
        mySR.cancel();

        String loadingText = "Loading...";
        textViewQuestion.setText(loadingText);

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



                            //set question into text box only if date finished is null
                            textViewQuestion.setText(question);
                            sView.smoothScrollTo(0,textViewQuestion.getTop());
                            //scrollToView(sView,textViewQuestion);

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
                            valueIDArray.clear();
                            ClearRadioGroup();


                            for (int i = 0; i < mapArray.length(); i++) {
                                optionsArray.add(mapArray.getJSONObject(i).get("Description").toString());
                                responseIDArray.add(mapArray.getJSONObject(i).get(
                                        "ItemResponseOID").toString());
                                valueIDArray.add(mapArray.getJSONObject(i).get("Value").toString());
                            }

                            //Load radioGroup with Options
                            LoadRadioGroup(optionsArray, valueIDArray);

                            //use tts to speak answers
                            String currentAnswer;
                            for (int i = 0; i < optionsArray.size(); i++)
                            {
                                if (i == optionsArray.size()-1)
                                {
                                    currentAnswer =
                                            "or " + valueIDArray.get(i) + ": " + optionsArray.get(i) + "?";
                                    tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,ANSWERS_DONE);
                                }
                                else {
                                    currentAnswer =
                                            valueIDArray.get(i) + ": " + optionsArray.get(i) + ", ";
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


                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Basic " + base64);
                return params;
            }


        };


        requestQueue.add(objectRequest);

    }

    /*
    public void LoadSpinner(ArrayList<String> optionsArray){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, optionsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

    }
    */

    // submit via button
    public void submitAnswer(View view)
    {
        //int index = spinnerOptions.getSelectedItemPosition();
        int index = GetCheckedIndex(rGroup);
        if (index == -1)
        {
            vibrator.vibrate(1000);
            return;
        }

        tts.stop();
        mySR.cancel();
        submitButton.setEnabled(false);
        numErrors = 0;
        promptQuit = false;

        String responseID = responseIDArray.get(index);
        String valueID = valueIDArray.get(index);
        //String valueID = Integer.toString(index + 1);

        Log.e(TAG, "Submitting answer: responseID = " + responseID + ", valueID = " + valueID);

        Long timeTaken = System.currentTimeMillis()-prevRecordedTime;
        timeArray.add(timeTaken);
        prevRecordedTime += timeTaken;

        getQuestions(gToken,responseID,valueID);
    }

    // submit via code
    public void submitAnswer(int answerIdx)
    {
        numErrors = 0;
        promptQuit = false;

        //int index = spinnerOptions.getSelectedItemPosition();
        //int index = GetCheckedIndex(rGroup);
        String responseID = responseIDArray.get(answerIdx);
        String valueID = valueIDArray.get(answerIdx);
        //String valueID = Integer.toString(answerIdx + 1);

        Log.e(TAG, "Submitting answer: responseID = " + responseID + ", valueID = " + valueID);

        Long timeTaken = System.currentTimeMillis()-prevRecordedTime;
        timeArray.add(timeTaken);
        prevRecordedTime += timeTaken;

        getQuestions(gToken,responseID,valueID);
    }

    public void callExitPage(final String dateFinished, String itoken){

        tts.stop();
        tts.shutdown();
        mySR.cancel();
        mySR.destroy();

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
                        FileOutputStream outputStream2;

                        try {
                            // write the Assessment Center response file to a .txt
                            fileString = response.toString();
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(fileString.getBytes());
                            outputStream.close();

                            //write the question/answer times and the date finished to a .txt
                            String timeStr;
                            StringBuilder sBuilder = new StringBuilder();
                            sBuilder.append(dateFinished);
                            sBuilder.append("\n");
                            for (Long timeStamp : timeArray)
                            {
                                sBuilder.append(timeStamp.toString());
                                sBuilder.append(",");
                            }

                            timeStr = sBuilder.toString();
                            outputStream2 = openFileOutput(timesFilename, Context.MODE_PRIVATE);
                            outputStream2.write(timeStr.getBytes());
                            outputStream2.close();

                            Intent intent = new Intent(SurveyQuestion.this, ExitPage.class);
                            intent.putExtra("Date", dateFinished);
                            intent.putExtra("Filename", filename);
                            intent.putExtra("TimesFilename", timesFilename);

                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Intent intent = new Intent(SurveyQuestion.this, ExitPage.class);
                            intent.putExtra("Date", dateFinished +" Could not get results.");
                            intent.putExtra("Filename", "");
                            intent.putExtra("TimesFilename", "");
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


                Map<String, String> params = new HashMap<>();
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
    // code == 2: inform the user that the app will quit in 30 seconds, ask for a valid answer or a
    //            "repeat" to resume OR "quit" to confirm quit
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
                                currentAnswer =
                                        "or " + valueIDArray.get(i) + ": " + optionsArray.get(i) + "?";
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,ANSWERS_DONE);
                            }
                            else {
                                currentAnswer =
                                        valueIDArray.get(i) + ": " + optionsArray.get(i) + ", ";
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                                tts.playSilentUtterance(100,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                    }
                });
            }
            else if (code == 1)
            {
                SurveyQuestion.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak("Sorry, I didn't catch that.  Please repeat your answer.", TextToSpeech.QUEUE_FLUSH,
                                null, ANSWERS_DONE);
                    }
                });
            }
            else // code == 2
            {
                promptQuit = true;
                SurveyQuestion.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak("The app will quit 30 seconds after this message ends, and " +
                                "all progress will be lost.", TextToSpeech.QUEUE_FLUSH, null, null);
                        tts.speak("To confirm quit, say: quit.", TextToSpeech.QUEUE_ADD, null,
                                null);

                        tts.speak("To resume your survey, " +
                                "respond with a valid answer to the question, or say: " +
                                        "repeat,",
                                TextToSpeech.QUEUE_ADD,null,null);

                        tts.speak("to have me repeat the question and answer selections.",
                                TextToSpeech.QUEUE_ADD, null, ANSWERS_DONE);
                    }
                });
            }
        }
    }

    /*
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
    }*/

    public void LoadRadioGroup(ArrayList<String> optionsArr, ArrayList<String> idArr){
        //RadioGroup rGroup = findViewById(R.id.radioGroup);
        rGroup.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0;i<optionsArr.size();i++){
            RadioButton rb = new RadioButton(this);
            String setStr = idArr.get(i) + " : " + optionsArr.get(i);
            rb.setText(setStr);
            rGroup.addView(rb,i);

        }

    }

    // Get the index of the selected radio button, assuming the RadioGroup only has radio buttons
    // as child objects.  Return -1 if no checked button is found.
    public int GetCheckedIndex(RadioGroup rg){
        int radioButtonID = rg.getCheckedRadioButtonId();
        if (radioButtonID == -1)
            return radioButtonID;
        View radioButton = rg.findViewById(radioButtonID);
        return rg.indexOfChild(radioButton);
    }

    /*
    public String GetStringOfChecked(RadioGroup rg){
        int idx = GetCheckedIndex(rg);
        RadioButton r = (RadioButton) rg.getChildAt(idx);
        return r.getText().toString();
    }
    */

    public void ClearRadioGroup() {
        //RadioGroup rGroup = findViewById(R.id.radioGroup);
        rGroup.clearCheck();
        rGroup.removeAllViews();
    }

    /**
     * Used to scroll to the given view.
     *
     * @param scrollViewParent Parent ScrollView
     * @param view View to which we need to scroll.
     */
    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumulated Offset.
     */
    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    //Get the index of the optionsArray element matching myString.  Return -1 if no match found
    private int getIndex(ArrayList<String> arr, String myString){

        int index = -1;
        myString = myString.toLowerCase();

        for (int i=0;i<arr.size();i++){
            if (myString.contains(arr.get(i).toLowerCase())){
                index = i;
                break;
            }
        }
        return index;
    }

    //Get the index of the array element matching myString.  Return -1 if no match found
    // or if too many matches
    private int getExclusiveIndex(ArrayList<String> arr, String myString){

        int index = -1;
        myString = myString.toLowerCase();

        for (int i=0;i<arr.size();i++){
            if (myString.contains(arr.get(i).toLowerCase())){
                if (index != -1)
                    return -1;
                index = i;
            }
        }
        return index;
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



