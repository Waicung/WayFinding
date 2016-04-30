package com.waicung.wayfinding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.waicung.wayfinding.config.ConfigHandler;
import com.waicung.wayfinding.models.AuthenNResponse;
import com.waicung.wayfinding.models.Point;
import com.waicung.wayfinding.models.Route;
import com.waicung.wayfinding.models.Step;
import com.waicung.wayfinding.webclient.GGDirectionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in Version 1.1
 * Created by waicung on 03/04/2016.
 * JSON from Google direction API as input
 * Result as a list(ArrayList) of direction instructions
 */
public class LoadRouteAsyncTask extends AsyncTask{
    Point start_location, end_location;
    int distance, duration;
    ArrayList<Step> stepsList = new ArrayList<>();
    ProgressDialog pd;
    Context context;

    LoadRouteAsyncTask(Context context){
        this.context = context;
        ConfigHandler config = new ConfigHandler(context, "config");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*pd = new ProgressDialog(context);
        pd.setMessage("loading Instructions");
        pd.show();*/

    }


    @Override
    protected Route doInBackground(Object[] params) {
        Route route;
        int status = (int)params[0];
        AuthenNResponse response = new AuthenNResponse();
        if (status == response.CODE_NOT_TESTED){
            route = getLocalDirection();
        }
        else {
            route = processGGDirection(getGGDirection());
        }
        return route;
    }


    @Override
    protected void onPostExecute(Object o) {
        if (pd != null)
        {
            pd.dismiss();
        }
    }

    protected Route getLocalDirection(){
        Route route = new Route();
        SharedPreferences sharePref = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String auth = sharePref.getString(context.getString(R.string.preference_authenN_response), null);
        Gson gson = new Gson();
        AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
        List<Step> steps = response.getSteps();
        route.setSteps(steps);
        return route;
    }

    protected String getGGDirection(){
        SharedPreferences sharePref = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        String auth = sharePref.getString(context.getString(R.string.preference_authenN_response), null);
        Gson gson = new Gson();
        AuthenNResponse response = gson.fromJson(auth, AuthenNResponse.class);
        Point start_point = response.getStart();
        Point end_point = response.getEnd();
        GGDirectionHelper DH;
        DH = new GGDirectionHelper(start_point, end_point);
        return DH.getJSONStr();
    }

    protected Route processGGDirection(String jsonString){
        Route route;
        String[] route_info = {"distance", "duration"};
        String[] route_point = {"end_location", "start_location"};

        try {
            JSONObject response = new JSONObject(jsonString);
            JSONArray routes = response.getJSONArray("routes");
            JSONObject sub_routes = routes.getJSONObject(0);
            JSONArray legs = sub_routes.getJSONArray("legs");
            JSONObject sub_legs = legs.getJSONObject(0);
            //get route information from legs
            for(String n: route_info){
                JSONObject property = sub_legs.getJSONObject(n);
                int value = property.getInt("value");
                switch (n){
                    case "distance":
                        this.distance = value;
                        break;
                    case "duration":
                        this.duration = value;
                        break;
                    default:
                        break;
                }

            }
            System.out.println("distance & duration: " + distance + ", " + duration );
            for(String n: route_point){
                JSONObject property = sub_legs.getJSONObject(n);
                Double lng = property.getDouble("lng");
                Double lat = property.getDouble("lat");
                switch (n){
                    case "end_location":
                        end_location = new Point(lat, lng);
                        break;
                    case "start_location":
                        start_location = new Point(lat, lng);
                        break;
                    default:
                        break;
                }
            }
            System.out.println("end: " + end_location.toString() + ", " + "start: " + start_location.toString());

            JSONArray steps = sub_legs.getJSONArray("steps");
            Point start_point = null;
            Point end_point = null;
            for(int i=0; i<steps.length();i++){
                JSONObject step = steps.getJSONObject(i);
                //Add html_instructions to a List without html tags
                String html_instruction = step.getString("html_instructions").replaceAll("\\<.*?\\>", "");
                int distance = step.getJSONObject("distance").getInt("value");
                int duration = step.getJSONObject("duration").getInt("value");
                for(String n: route_point){
                    JSONObject property = step.getJSONObject(n);
                    Double lng = property.getDouble("lng");
                    Double lat = property.getDouble("lat");
                    switch (n){
                        case "end_location":
                            end_point = new Point(lng,lat);
                            break;
                        case "start_location":
                            start_point= new Point(lng, lat);
                            break;
                        default:
                            break;
                    }
                }
                //last instruction contain 'destination information', which needs to be split
                if(i==steps.length()-1){
                    System.out.println("last instruction: " + html_instruction);
                    int destination = html_instruction.indexOf("Destination");
                    String sub1 = html_instruction.substring(0,destination);
                    String sub2 = html_instruction.substring(destination,html_instruction.length());
                    stepsList.add(new Step(start_point,end_point,sub1,duration,distance));
                    Step last = new Step();
                    last.setInstruction(sub2);
                    stepsList.add(last);
                }
                else {
                    stepsList.add(new Step(start_point,end_point,html_instruction,duration,distance));
                }

            }
            System.out.print("Google direction output: " + stepsList);
        }
        catch(JSONException e){
            System.out.println("JSON Exception: " + e.getMessage());
        }
        route = new Route(start_location,end_location,duration,distance,stepsList);
        return route;
    }
}
