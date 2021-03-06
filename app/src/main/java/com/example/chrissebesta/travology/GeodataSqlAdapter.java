package com.example.chrissebesta.travology;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.chrissebesta.travology.data.GeoContract;

/**
 * Created by chrissebesta on 3/30/16.
 */
public class GeodataSqlAdapter extends CursorAdapter {

    View.OnTouchListener mTouchListener;

    public GeodataSqlAdapter(Context context, Cursor c, int flags, View.OnTouchListener listener) {
        super(context, c, flags);
        mTouchListener = listener;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_view_layout,parent,false);
        //set on touch listener here so that each item from the cursor adapter has an animated touch listener
        view.setOnTouchListener(mTouchListener);


        return view;
    }

    @Override
    public void bindView(View locationView, Context context, Cursor cursor) {
        //Attach data to view with getter and setter methods
        // Find fields to populate in inflated template
        TextView countryTextView = (TextView) locationView.findViewById(R.id.country_text_view);
        TextView cityTextView = (TextView) locationView.findViewById(R.id.city_text_view);
        TextView placeCode = (TextView) locationView.findViewById(R.id.place_code_text_view);

        String country = cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_COUNTRY));
        String city = cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_CITY_NAME));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_ADDRESS));
        String place = cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_PLACE_CODE));

        //Extract values from cursor and set member variables for the view so that the view contains all the information necessary
        //locationView.setmPlaceCode(cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_PLACE_CODE)));
        //locationView.setmCityName(city);
        //locationView.setmCountryCode(country);
        //locationView.setmLatitude(cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_COORD_LAT)));
        //locationView.setmLongitude(cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_COORD_LONG)));


        //Set country and city, set the invisible place code to use for deleting from list
        countryTextView.setText(country);
        cityTextView.setText(address);
        placeCode.setText(place);

    }

    //TODO: Handle adding view to each item in here? Need to be able to get ID/place in listView as well.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
