package com.waicung.wayfinding.models;

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

    public Step(){

    }

    public void setInstruction(String instruction){
        this.instruction = instruction;
    }

    public String toString(){
        return instruction;
    }

    public int getDuration(){
        return this.duration;
    }

    public int getDistance(){
        return this.distance;
    }

    public Step clone(){
        if(start_point!=null &&
                end_point!=null) {
            Point new_start_point = this.start_point.clone();
            Point new_end_point = this.end_point.clone();
            int new_duration = this.duration;
            int new_distance = this.distance;
            String new_instruction = this.instruction;
            return new Step(new_start_point,new_end_point,new_instruction,new_duration,new_distance);
        }
        else {
            String new_instruction = this.instruction;
            Step new_step = new Step();
            new_step.setInstruction(new_instruction);
            return new_step;
        }

    }

    public String getInstruction(){
        return this.instruction;
    }


}
