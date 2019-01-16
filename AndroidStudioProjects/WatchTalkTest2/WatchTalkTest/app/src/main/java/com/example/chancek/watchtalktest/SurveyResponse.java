/*
package com.example.chancek.watchtalktest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//com.example.chancek.watchtalktest.SurveyResponse class handles a HashMap containing key-val pairs of strings.  The key strings
// each represent the question number, while the value strings represent the response.  A value
// of "0" indicates that no response was recorded.
public class SurveyResponse implements Serializable {
    private Map<String,String> myMap = new HashMap<>();

    // Default constructor, assumes only one question.
    public SurveyResponse(){
    }

    // Construct a com.example.chancek.watchtalktest.SurveyResponse object for a survey with numFields questions.//
    // TODO: instead of numFields, use the output of a filestream to create myMap
    public SurveyResponse(int numFields){
        for (int i = 0; i < numFields; i++)
        {
            String key = String.valueOf(i);
            String val = String.valueOf(0);
            myMap.put(key,val);
        }
    }

    public void record(String key, String val){
        myMap.put(key,val);
    }

    public String getVal(String key){
        return myMap.get(key);
    }

    public void clearResponses(){
        myMap.clear();
    }

    // TODO: write to JSON
    public void writeToFile(){
        for (Map.Entry<String,String> pair : myMap.entrySet()){
            //iterate over the pairs
            System.out.println(pair.getKey()+" "+pair.getValue());
        }
    }

}
*/
