package com.example.chrissebesta.travology;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chrissebesta on 3/29/16.
 * Array adapter for the Geodata class to convert description and lat and long to a listview.
 */
public class GeodataAdapter extends ArrayAdapter<Geodata> {

    public GeodataAdapter(Context context, List<Geodata> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Geodata geodata = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
        }

        TextView tv1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView tv2 = (TextView) convertView.findViewById(android.R.id.text2);

        tv1.setText(geodata.description);

        String coordinates = String.valueOf(geodata.latLng);
        tv2.setText(coordinates);

        return convertView;
    }
}

