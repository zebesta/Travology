package com.example.chrissebesta.travology;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;

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
import com.google.maps.android.geojson.GeoJsonLayer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //TODO: need to make this variable persist through creation and stop/recreate/whatever
    //Default to city mode if there hasnt been a previous decision made here
    //boolean cityMode = true;
    private GeoJsonLayer geoJsonLayer;
    public final String CITY_MODE = "CityMode";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState!=null){
//            cityMode = savedInstanceState.getBoolean(CITY_MODE);
//        }
//        Log.d("CITY", "The current cityMode value is: " + cityMode);


        //load layout
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //load floating action button and allow it to be the boolean switch between city and country mode
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.mapFab);
        fab.setImageResource(R.drawable.google_maps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!cityMode) {
//                    cityMode = true;
//                } else if(cityMode) {
//                    cityMode = false;
//                }
                Intent intent = new Intent(getApplicationContext(), CountryWebView.class);
                startActivity(intent);

                //recreate activity so that the new city mode property can take effect
                //recreate();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putBoolean(CITY_MODE, cityMode);
//        Log.d("CITY","Saving the value: "+cityMode + " to the outState bundle");
//        super.onSaveInstanceState(outState);
//    }

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
        //if (cityMode) {
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
                        .title(cityName + ", " + countryName)
                        .position(latLng));
                cursor.moveToNext();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }


            cursor.close();
//        } else {
//            //map type for
//            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//            //read geoJson from raw resource file to draw world map
//            Log.d("GEO", "Reading JSON?");
//
//            try {
//                JSONObject jsonObj = new JSONObject(String.valueOf(R.raw.geo_json_less_fields));
//                JSONArray jsonArr = jsonObj.getJSONArray("features");
//                JSONObject jsonCountry = jsonArr.getJSONObject(1);
//                Log.d("GEO", "Attempting to read this shit and reading: "+String.valueOf(jsonCountry));
//            } catch (JSONException e) {
//                Log.d("GEO", "Catch error");
//                e.printStackTrace();
//            }
//            Log.d("GEO", "Done reading JSON");
//
//
//            Log.d("GEO", "Starting to get the GeoJSON from raw file");
//            try {
//                geoJsonLayer = new GeoJsonLayer(mMap, R.raw.geo_json_less_fields, getBaseContext());
//                //GeoJsonFeature geoJsonFeature = new GeoJsonFeature()
//                //GeoJsonFeature geoFeature = new GeoJsonFeature();
//                //GeoJsonGeometryCollection geoCollection = new gGe
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.d("GEO", "Starting to add GeoJSON to map");
//
//            geoJsonLayer.addLayerToMap();
//            Log.d("GEO", "Done adding GeoJSON to map");
//
////            //Pull maps data from SQL database instead of from intent
////            final GeoDbHelper helper = new GeoDbHelper(getBaseContext());
////            final SQLiteDatabase db = helper.getWritableDatabase();
////            Cursor cursor = db.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
////            cursor.moveToFirst();
////
////            //Cycle through the SQL database and pull the relevant data for each entry
////            for (int i = 0; i < cursor.getCount(); i++) {
////                String cityName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_CITY_NAME));
////                String countryName = cursor.getString(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COUNTRY));
////                LatLng latLng = new LatLng(cursor.getDouble(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COORD_LAT)), cursor.getDouble(cursor.getColumnIndex(GeoContract.GeoEntry.COLUMN_COORD_LONG)));
////                mMap.addMarker(new MarkerOptions()
////                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.google_maps))
////                        .title(cityName + ", " + countryName)
////                        .position(latLng));
////                cursor.moveToNext();
////                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////            }
////
////
////            cursor.close();
//            //Build map using KML files
////            try {
////                Log.d("GEO", "Starting to get the KML from raw file");
////                KmlLayer kmlMap = new KmlLayer(mMap,R.raw.world_map_kml_simplified, this);
////                Log.d("GEO", "Done getting the KML from raw file, adding to map");
////                //Iterable<KmlContainer> kmlContainer = kmlMap.getContainers();
////                //String what = kmlContainer.toString();
////                //Log.d("GEO", "What is: "+what);
////                kmlMap.addLayerToMap();
////                Log.d("GEO", "Done adding to map");
////            } catch (XmlPullParserException e) {
////                e.printStackTrace();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//        }
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
