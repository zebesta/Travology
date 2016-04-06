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
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_2,parent,false);
        //set on touch listener here so that each item from the cursor adapter has an animated touch listener
        view.setOnTouchListener(mTouchListener);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(android.R.id.text1);
        TextView tvDetails = (TextView) view.findViewById(android.R.id.text2);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_CITY_NAME)) + ", " +
                cursor.getString(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_COUNTRY));

        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_COORD_LAT));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(GeoContract.GeoEntry.COLUMN_COORD_LAT));
        // Populate fields with extracted properties

        String ll = "Lat/Long is: "+latitude+" / "+longitude;
        tvName.setText(name);
        tvDetails.setText(ll);

    }

    //TODO: Handle adding view to each item in here? Need to be able to get ID/place in listView as well.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
