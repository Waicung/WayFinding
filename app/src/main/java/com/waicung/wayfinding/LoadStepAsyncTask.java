package com.waicung.wayfinding;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by waicung on 03/04/2016.
 * getting direction steps though Google direction API
 * Result as a list(ArrayList) of instructions
 */
public class LoadStepAsyncTask extends AsyncTask{
    @Override
    protected ArrayList<String> doInBackground(Object[] params) {
        com.waicung.wayfinding.DirectionHelper DH = new com.waicung.wayfinding.DirectionHelper();
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
                for(int i=1; i<steps.length();i++){
                    JSONObject step = steps.getJSONObject(i);
                    //Log.v("Instruction",line);
                    //line = Html.fromHtml(line).toString();
                    //line.replaceAll("\\<.*?\\>", "");
                    //Log.v("XInstruction",line);
                    instructions.add(step.getString("html_instructions").replaceAll("\\<.*?\\>", ""));
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
