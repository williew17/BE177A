package com.example.chancek.watchtalktest;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
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
import java.util.ArrayList;
import java.util.Locale;

public class SurveyQuestion extends WearableActivity {

    TextToSpeech tts;
    Spinner spinner;
    ArrayList<String> responses = new ArrayList<String>();
    /*SurveyResponse Responses = (SurveyResponse) getIntent().getSerializableExtra
            ("Serialized_Responses");*/

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_question);

        // Enables Always-on
        setAmbientEnabled();

        //Setup Spinner with answer choices
        spinner = (Spinner) findViewById(R.id.spinner);


        //Get possible answers.
        //TODO: answers from PROMIS
        responses.add("Not at all");
        responses.add("A little bit");
        responses.add("Somewhat");
        responses.add("Quite a bit");
        responses.add("Very much");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_spinner_item, responses);

        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices_array, android.R.layout.simple_spinner_item);*/

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);




        /*String message = "<speak>In the past 7 days, how much did pain interfere with your day to day activities?</speak>";

        String str;
        for (int i = 0; i < responses.size(); i++)
        {
            str = responses.get(i);
            if (i == responses.size()-1)
            {
                str = "Or " + str + "?";
            }
            else
            {
                str = str + ", ";
            }
            message = message + str;
        }*/
        tts=new TextToSpeech(SurveyQuestion.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // Setup text-to-speech
                if(status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);

                    if(result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }

                    else
                    {
                        String message = "<speak>In the past 7 days, how much did pain interfere with your day to day activities?</speak>";

                        /*String str;
                        for (int i = 0; i < responses.size(); i++)
                        {
                            str = responses.get(i);
                            if (i == responses.size()-1)
                            {
                                str = "Or " + str + "?";
                            }
                            else
                            {
                                str = str + ", ";
                            }
                            message = message + str;
                        }*/
                        tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                }
                else
                    Log.e("error", "Initialization Failed!");
            }
        });
        //tts.shutdown();

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

    public void getVoiceInput(View view)
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

                    spinner.setSelection(getIndex(spinner, mString));

                }
                break;
            }

        }
    }

    //Get the index of the spinner element matching myString.  Return 0 if no match found
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (myString.contains(spinner.getItemAtPosition(i).toString().toLowerCase())){
                index = i;
                break;
            }
        }
        return index;
    }
}
