package com.example.chrissebesta.travology;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chrissebesta.travology.data.GeoContract;
import com.example.chrissebesta.travology.data.GeoDbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class AddLocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Button mEnterButton;
    private AutoCompleteTextView mLocationText;
    private PlaceAutocompleteAdapter mAdapter;
    protected GoogleApiClient mGoogleApiClient;
    private ListView mPlaceListView;
    private Geodata mGeodata;
    private ArrayList<Geodata> mGeodataList = new ArrayList<>();

    public final String LOG_TAG = this.getClass().getSimpleName();


    private List<Address> mAddresses;
    private Button mGeoButton;
    private Button mAddLocButton;
    private Button mListButton;
    public final String LAT_TAG = "LAT TAG IT";
    public final String LONG_TAG = "LONG TAG IT";
    private LatLng mLLToAdd;
    private Place mPlaceToAdd;
    private long mLat;
    private long mLong;
    // Array list to store all added lat long coordinates
    ArrayList<LatLng> coordinates = new ArrayList<>();
    ArrayList<Place> mPlaces = new ArrayList<>();
    ArrayList<String> mPlaceNames = new ArrayList<>();

    //SQL Variable

    private ContentValues contentValues = new ContentValues();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        //TODO: Need to make this a fragment activity, will also help with Tablet UI implementation later

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.activity_add_location);
        // Register a listener that receives callbacks when a suggestion has been selected
        mLocationText = (AutoCompleteTextView) findViewById(R.id.add_location_entry_text);

        mLocationText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceListView = (ListView) findViewById(R.id.location_list);
        //Get listview information

        //SQL listview population
        SQLiteDatabase db = new GeoDbHelper(
                getBaseContext()).getWritableDatabase();

        Cursor todoCursor = db.rawQuery("SELECT  * FROM todo_items", null);
        final GeodataSqlAdapter sqlAdapter = new GeodataSqlAdapter(getBaseContext(), todoCursor, 0);

        //Non SQL listview population
//        final GeodataAdapter mGeodataAdapter = new GeodataAdapter(this, mGeodataList);
//        mPlaceListView.setAdapter(sqlAdapter);


        mAddLocButton = (Button) findViewById(R.id.add_loc_button);
        mAddLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show that the coords are being added
                Log.d("ADD_LOC", "You're adding location!");

                Log.d("CV", contentValues.toString());

                //SQL list population and database insertion
                db.insert(GeoContract.GeoEntry.TABLE_NAME, null, contentValues);
                sqlAdapter.notifyDataSetChanged();
                Log.d("DB", helper.getTableAsString(db, GeoContract.GeoEntry.TABLE_NAME));


                //add coordinates to the bundle
                coordinates.add(mLLToAdd);
                if(mGeodata != null) {
                    mGeodataList.add(mGeodata);
                    //Non SQL listview update
                    //mGeodataAdapter.notifyDataSetChanged();
                }

                //add place to array list of places
                mPlaces.add(mPlaceToAdd);
                Log.d("PLACE", "There are " + mPlaces.size() + " items in the places array");
                for(int j = 0; j<coordinates.size(); j++){
//                    Place testPlace = mPlaces.get(j);
//                    Log.d("PLACE", "The places in the list so far are: " + coordinates.get(j).toString());
//                    Log.d("PLACE", "The places in the list so far are: " + mPlaceNames.get(j));
                }
            }
        });
        mGeoButton = (Button) findViewById(R.id.geo_button);
        mGeoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("coordinates", coordinates);
                //bundle.putParcelableArrayList("places", places);
                intent.putExtras(bundle);
                Log.d(LOG_TAG, "first coordinate is: " + coordinates.get(0));
                //Log.d(LOG_TAG, "The first place is: "+places.get(0).getName());

                intent.putExtra(LAT_TAG, mLat);
                intent.putExtra(LONG_TAG, mLong);
                startActivity(intent);
            }
        });

        mListButton = (Button) findViewById(R.id.button_list);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Currently not being used, list is being displayed in the add location activity for now.
                //TODO: Will need to make the array PARCELABLE if I want to save to bundle and pass to activity
                //Need to consider sqitch to a SQL database, likely better scaleablility and cursor can handle more
                Intent intent = new Intent(getApplicationContext(), GeoListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("coordinates", coordinates);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        AutocompleteFilter.Builder fb = new AutocompleteFilter.Builder();
        fb.setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                fb.build());
        mLocationText.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button clearButton = (Button) findViewById(R.id.button_clear);
        assert clearButton != null;
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationText.setText("");
            }
        });

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(LOG_TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(LOG_TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(LOG_TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            //Clear all old content values and start with a clean slate before creating a new one
            contentValues.clear();
            //Add all the relevant values to the content values to be added to the SQL database later
            contentValues.put(GeoContract.GeoEntry.COLUMN_PLACE_CODE, place.getId());
            contentValues.put(GeoContract.GeoEntry.COLUMN_CITY_NAME, (String) place.getName());
            contentValues.put(GeoContract.GeoEntry.COLUMN_COUNTRY_CODE, "US");//TODO: Need to resolve the actual country code here
            contentValues.put(GeoContract.GeoEntry.COLUMN_COORD_LAT, place.getLatLng().latitude);
            contentValues.put(GeoContract.GeoEntry.COLUMN_COORD_LONG, place.getLatLng().longitude);
            Log.d("CV", contentValues.toString());


            LatLng placeLatLng = place.getLatLng();
            //add Lat Long for clicked item to
            mLLToAdd = placeLatLng;
            mPlaceToAdd = place;
            mGeodata = new Geodata((String) place.getName(), place.getLatLng());
            mPlaceNames.add((String) place.getName());
            String ll = placeLatLng.toString();
            Log.d("LATLONG", "Lat and long are: " + ll);
            mLat = (long) placeLatLng.latitude;
            mLong = (long) placeLatLng.longitude;

            Log.i(LOG_TAG, "Place details received: " + place.getName());

            places.release();
        }
    };



}
