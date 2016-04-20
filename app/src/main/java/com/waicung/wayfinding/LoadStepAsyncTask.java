package com.waicung.wayfinding;

import android.os.AsyncTask;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Used in Version 1.1
 * Created by waicung on 03/04/2016.
 * JSON from Google direction API as input
 * Result as a list(ArrayList) of direction instructions
 */
public class LoadStepAsyncTask extends AsyncTask{
    GGDirectionHelper DH;
    String[] route_info = {"distance", "duration"};
    String[] route_point = {"end_location", "start_location"};
    Route route;
    Point start_location, end_location;
    int distance, duration;
    ArrayList<Step> stepsList = new ArrayList<>();


    @Override
    protected Route doInBackground(Object[] params) {
        //check if origin and destination are provided
        if (params != null){
            Point origin = (Point) params[0];
            Point destination = (Point) params[1];
            DH = new GGDirectionHelper(origin,destination);
        }
        //TESTING purpose, use default origin and destination
        else{
            DH = new GGDirectionHelper();}
        String Str = DH.getJSONStr();

        //check if Google response a String
        if(Str!=null){
            try {
                JSONObject response = new JSONObject(Str);
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
                            int distance = value;
                            break;
                        case "duration":
                            int duration = value;
                            break;
                        default:
                            break;
                    }
                }
                for(String n: route_point){
                    JSONObject property = sub_legs.getJSONObject(n);
                    Double lng = property.getDouble("lng");
                    Double lat = property.getDouble("lat");
                    switch (n){
                        case "end_location":
                            end_location = new Point(lng,lat);
                            break;
                        case "start_location":
                            start_location = new Point(lng, lat);
                            break;
                        default:
                            break;
                    }
                }

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
                        int destination = html_instruction.indexOf("Destination");
                        String sub1 = html_instruction.substring(0,destination);
                        String sub2 = html_instruction.substring(destination,html_instruction.length());
                        html_instruction = sub1 + "," + sub2;
                    }
                    if(start_point!=null&&end_point!=null){
                        stepsList.add(new Step(start_point,end_point,html_instruction,duration,distance));
                    }

                }
            }
            catch(JSONException e){}

        }
        route = new Route(start_location,end_location,duration,distance,stepsList);
        return route;
    }

    public void onPostExecute(Void result){
        super.onPostExecute(result);

    }

}
