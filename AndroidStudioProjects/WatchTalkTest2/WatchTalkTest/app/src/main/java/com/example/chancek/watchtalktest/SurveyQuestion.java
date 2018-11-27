package com.example.chancek.watchtalktest;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

public class SurveyQuestion extends WearableActivity {

    TextToSpeech tts;
<<<<<<< HEAD
    Spinner spinner;
    ArrayList<String> responses = new ArrayList<String>();
    /*SurveyResponse Responses = (SurveyResponse) getIntent().getSerializableExtra
            ("Serialized_Responses");*/
=======
>>>>>>> 4c39d01850158e316a66aa23aff0f471aecb93f4




    // Initialize variables that store registration ID and token
    String textID = "0ED7B052-FDB2-4EDF-9B4B-E732F69DDF7A";
    String textToken = "3171FF33-83C5-4221-9BB0-051DC747AEB9";
    String totalToken = textID + ":" + textToken;

    ArrayList responseIDArray = new ArrayList();

    String gtoken;
    String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_question);

        // Enables Always-on
        setAmbientEnabled();

<<<<<<< HEAD
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

=======

        // Generate participant token
         getToken(totalToken);


>>>>>>> 4c39d01850158e316a66aa23aff0f471aecb93f4
    }


    public void getQuestions (String itoken, String responseID, String valueID){
        // Make Request
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        gtoken = itoken;

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
                                callExitPage(dateFinished);
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

                            // Make array for map
                            JSONArray mapArray = response.getJSONArray("Items").getJSONObject(0).getJSONArray("Elements").getJSONObject(2).getJSONArray("Map");

                            // Make array for options
                            ArrayList optionsArray = new ArrayList();


                            for (int i = 0; i < mapArray.length(); i++) {
                                optionsArray.add(mapArray.getJSONObject(i).get("Description"));
                                responseIDArray.add(mapArray.getJSONObject(i).get("ItemResponseOID"));
                            }


                            //Load Spinner with Options
                            LoadSpinner(optionsArray);

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
    public void getToken(final String totalToken){


        // Make Request
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JsonObjectRequest objectRequest;
        String tokenURL = "https://www.assessmentcenter.net/ac_api/2014-01/Assessments/C1E44752-BCBD-4130-A307-67F6758F3891.json";



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

    public void LoadSpinner(ArrayList optionsArray){
        Spinner spinnerOptions = findViewById(R.id.spinnerOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, optionsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

    }

    public void submitAnswer(View view)
    {
        Spinner spinnerOptions = findViewById(R.id.spinnerOptions);
        String selectedAnswer = spinnerOptions.getSelectedItem().toString();
        int index = spinnerOptions.getSelectedItemPosition();
        String responseID = responseIDArray.get(index).toString();
        String valueID = Integer.toString(index + 1);
        getQuestions(gtoken,responseID,valueID);

    }

    public void callExitPage(String dateFinished){
        Intent intent = new Intent(this, ExitPage.class);
        intent.putExtra("Date", dateFinished);
        startActivity(intent);
    }

}



