package com.example.chrissebesta.travology;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by chrissebesta on 3/28/16.
 */
public class PlacesAddedAdapter extends ArrayAdapter<Place> {
    public PlacesAddedAdapter(Context context, ArrayList<Place> places) {
        super(context, 0, places);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get Place data for this position
        Place place = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        // Lookup view for data population, coming from default simple list item
        TextView placeName = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        placeName.setText(place.getName());
        // Return the completed view to render on screen
        return convertView;

    }
}
