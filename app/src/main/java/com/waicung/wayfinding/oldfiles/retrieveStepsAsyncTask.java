package com.waicung.wayfinding.oldfiles;

import android.os.AsyncTask;

import com.waicung.wayfinding.models.Step;

import java.util.ArrayList;

/**
 * Created by waicung on 19/04/2016.
 */
public class retrieveStepsAsyncTask extends AsyncTask{
    int user_id;

    @Override
    protected Object doInBackground(Object[] params) {
        user_id = Integer.parseInt((String)params[0]);
        //TODO add user_id as parameter
        RemoteDirectionHelper RH = new RemoteDirectionHelper();
        ArrayList<Step> steps = RH.getSteps();
        ArrayList<String> instructions = new ArrayList<>();
        for(Step s: steps){
            instructions.add(s.toString());
        }
        return instructions;
    }
}
