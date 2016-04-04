package com.example.chrissebesta.travology;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.chrissebesta.travology.data.GeoContract;
import com.example.chrissebesta.travology.data.GeoDbHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public final String LAT_TAG = "LAT TAG IT";
    public final String LONG_TAG = "LONG TAG IT";
    private double mLat;
    private double mLong;
    private ArrayList<LatLng> mCoordinates;
    ;
    public final String GEO_MAPS_DATA_TAG = "GEOTAGIT";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get coordinate data from bundle placed in intent
        Intent intent = getIntent();
        //mCoordinates = getIntent().getParcelableArrayListExtra("coordinates");

        //mLat = intent.getLongExtra(LAT_TAG, 0);
        //mLong = intent.getLongExtra(LONG_TAG, 0);

        //Log.d("EXTRA", "Got extras and they are: LAT: " + mLat + " LONG: " + mLong);
        //Log.d("EXTRA", "The array received has a size of: " + mCoordinates.size());
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Pull maps data from SQL database instead of from intent
        final GeoDbHelper helper = new GeoDbHelper(getBaseContext());
        final SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
        cursor.moveToFirst();

        //Cycle through the SQL database and pull the relevant data for each entry
        for (int i = 0; i < cursor.getCount(); i++) {
            String cityName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_CITY_NAME));
            String countryName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COUNTRY));
            LatLng latLng = new LatLng(cursor.getDouble(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COORD_LAT)), cursor.getDouble(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COORD_LONG)));
            mMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.google_maps))
                    .title(cityName + ", " +countryName)
                    .position(latLng));
            cursor.moveToNext();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }


        cursor.close();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.chrissebesta.travology/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.chrissebesta.travology/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
