package com.waicung.wayfinding;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by waicung on 03/04/2016.
 * JSON from Google direction API as input
 * Result as a list(ArrayList) of direction instructions
 */
public class LoadStepAsyncTask extends AsyncTask{
    @Override
    protected ArrayList<String> doInBackground(Object[] params) {
        DirectionHelper DH = new DirectionHelper("-37.799538,144.958053","-37.799901,144.943267");
        ArrayList<String> instructions = new ArrayList<>();
        String Str = DH.getJSONStr();

        if(Str!=null){
            try {
                JSONObject response = new JSONObject(Str);
                JSONArray routes = response.getJSONArray("routes");
                JSONObject sub_routes = routes.getJSONObject(0);
                JSONArray legs = sub_routes.getJSONArray("legs");
                JSONObject sub_legs = legs.getJSONObject(0);
                JSONArray steps = sub_legs.getJSONArray("steps");
                for(int i=0; i<steps.length();i++){
                    JSONObject step = steps.getJSONObject(i);
                    //Add html_instructions to a List without html tags
                    String string = step.getString("html_instructions").replaceAll("\\<.*?\\>", "");
                    //last instruction contain 'destination information', which needs to be split
                    if(i==steps.length()-1){
                        int destination = string.indexOf("Destination");
                        String sub1 = string.substring(0,destination);
                        String sub2 = string.substring(destination,string.length());
                        instructions.add(sub1);
                        instructions.add(sub2);
                    }
                    else{
                    instructions.add(string);}
                }
            }
            catch(JSONException e){}

        }
        return instructions;
    }

    public void onPostExecute(Void result){
        super.onPostExecute(result);

    }

}
