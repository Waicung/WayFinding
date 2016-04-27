package com.waicung.wayfinding.oldfiles;

import com.waicung.wayfinding.models.Step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by waicung on 19/04/2016.
 */
public class RemoteDirectionHelper {
    int user_id;
    String parameter;
    URL url;
    RemoteDirectionHelper(){

    }

    RemoteDirectionHelper(int user_id){
        this.user_id = user_id;
    }

    //TODO testing to be deleted
    public static void main(String arg[]){
        RemoteDirectionHelper RH = new RemoteDirectionHelper();
        ArrayList output = RH.getSteps();
        System.out.print(output);
    }

    public ArrayList<Step> getSteps(){
        try{
            //url = new URL("http://www.google.com/");}
            parameter = "user_id =" + user_id;
            //TODO parameter to be added
            url = new URL("http://localhost:8080/wayfinding/read_all_steps.php");
        }
        catch (MalformedURLException e){}
        URLConnection conn;
        StringBuilder Str = new StringBuilder();
        try {
            System.out.print(url);
            conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = in.readLine();
            while(line!=null){
                Str.append(line+'\n');
                line=in.readLine();
            }
        }
        catch (IOException e){}
        System.out.print(Str);
        return null;
    }
}
