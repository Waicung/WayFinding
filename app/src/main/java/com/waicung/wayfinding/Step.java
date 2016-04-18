package com.waicung.wayfinding;

/**
 * Created by waicung on 14/04/2016.
 */
public class Step {
    private Point start_point;
    private Point end_point;
    private String instruction;
    private int duration;
    private int distance;

    public Step(Point start_point, Point end_point, String instruction, int duration, int distance){
        this.start_point = start_point;
        this.end_point = end_point;
        this.instruction = instruction;
        this.duration = duration;
        this.distance = distance;

    }

    public String toString(){
        String instructions = instruction;
        return instruction;
    }

    public int getDuration(){
        return this.duration;
    }

    public int getDistance(){
        return this.distance;
    }

    public Step clone(){
        Point new_start_point = this.start_point.clone();
        Point new_end_point= this.end_point.clone();
        String new_instruction = this.instruction;
        int new_duration=this.duration;
        int new_distance=this.distance;
        return new Step(new_start_point,new_end_point,new_instruction,new_duration,new_distance);
    }


}
