package com.waicung.wayfinding;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.waicung.wayfinding.models.*;


/**
 * Created by waicung on 27/04/2016.
 */
public class CustomAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Step> steps;
    public CustomAdapter(Context context, ArrayList<Step> steps) {
        super(context, R.layout.list_item, steps);
        this.context = context;
        this.steps = steps;
    }

    private static class ViewHolder{
        TextView instructionView;
        ImageView markView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Step step = (Step) getItem(position);
        final ViewHolder viewHolder;
        final View result;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            viewHolder.instructionView = (TextView)convertView.findViewById(R.id.tv_step);
            viewHolder.markView = (ImageView)convertView.findViewById(R.id.imageView);
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

                viewHolder.markView.setImageResource(R.drawable.ic_check);
                Intent step_number = new Intent("newStep");
                step_number.putExtra("step", position+1);
                LocalBroadcastManager.getInstance(context).sendBroadcast(step_number);
                Snackbar.make(v, "Step = " + position, Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                viewHolder.markView.setClickable(false);

            }
        });
        return convertView;
    }
}
