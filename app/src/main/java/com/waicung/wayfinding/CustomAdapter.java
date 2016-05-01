package com.waicung.wayfinding;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.waicung.wayfinding.models.*;


/**
 * Created by waicung on 27/04/2016.
 */
public class CustomAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Step> steps;
    private int step_number;
    String TAG = "cAdapter";


    public CustomAdapter(Context context, ArrayList<Step> steps, int step_number) {
        super(context, R.layout.list_item, steps);
        this.context = context;
        this.steps = steps;
        this.step_number = step_number;
    }

    private static class ViewHolder{
        RelativeLayout wholeItem;
        TextView instructionView;
        RelativeLayout panel;
        ImageView markView;
        ImageView crossView;
        ImageView feedbackView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Step step = (Step) getItem(position);
        final ViewHolder viewHolder;
        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            viewHolder.wholeItem = (RelativeLayout) convertView.findViewById(R.id.list_item_relativeLayout);
            viewHolder.instructionView = (TextView)convertView.findViewById(R.id.tv_step);
            viewHolder.panel = (RelativeLayout)convertView.findViewById(R.id.control_panel);
            viewHolder.markView = (ImageView)convertView.findViewById(R.id.mark_iv);
            viewHolder.crossView = (ImageView) convertView.findViewById(R.id.cross_iv);
            viewHolder.feedbackView = (ImageView) convertView.findViewById(R.id.feedback_iv);
            if(position==step_number-1){
                Log.i(TAG, "position show: " + position);
                viewHolder.panel.setVisibility(View.VISIBLE);
            }
            else{
                if(position>step_number-1){
                    Log.i(TAG, "Hide all: " + position);
                    viewHolder.wholeItem.setVisibility(View.GONE);
                }
                Log.i(TAG, "position hide: " + position);
                viewHolder.panel.setVisibility(View.GONE);
            }
            result = convertView;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;

        }


        viewHolder.instructionView.setText(step.getInstruction());

        viewHolder.markView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent step_number = new Intent("Achieve");
                step_number.putExtra("step", position+1);
                step_number.putExtra("success", 1);
                //Broadcast to mainActivity
                LocalBroadcastManager.getInstance(context).sendBroadcast(step_number);
                Snackbar.make(v, "Finish instruction " + (position+1), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                //CustomAdapter.notifyDataSetChanged();

            }
        });

        viewHolder.crossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent unreachable = new Intent("Achieve");
                unreachable.putExtra("step", position+1);
                unreachable.putExtra("success", 0);
                unreachable.putExtra("event", "problem detected");
                LocalBroadcastManager.getInstance(context).sendBroadcast(unreachable);
            }
        });

        viewHolder.feedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Provide feedback for instruction " + (position+1), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
        return convertView;
    }
}
