package com.waicung.wayfinding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


/**
 * Created by waicung on 27/04/2016.
 */
public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List list;
    private int iconID;

    public CustomAdapter(Context contect, List list, int iconID){
        this.context = contect;
        this.list = list;
        this.iconID = iconID;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItme;
        listItme = inflater.inflate(R.layout.list_item, parent, false);
        ImageButton ic = (ImageButton) listItme.findViewById(R.id.icon_button);
        TextView tv = (TextView) listItme.findViewById(R.id.tv_step);
        tv.setText((CharSequence) list.get(position));
        ic.setImageResource(iconID);

        return null;
    }
}
