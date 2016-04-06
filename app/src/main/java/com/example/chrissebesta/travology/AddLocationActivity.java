package com.example.chrissebesta.travology;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.StringTokenizer;

//TODO: Pull out the longer on click/on touch handlers and put them in their own classes to reduce clutter in this class
public class AddLocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //Autocomplete text field for place finding
    private AutoCompleteTextView mLocationText;
    private PlaceAutocompleteAdapter mAdapter;
    protected GoogleApiClient mGoogleApiClient;

    //list view for locations added to SQL database
    ListView mPlaceListView;

    //swipe detection for non-animated removal of view
    //SwipeDetector swipeDetector = new SwipeDetector();
    BackgroundContainer mBackgroundContainer;

    //swipe detection for animated removal of view
    boolean mItemPressed = false;
    boolean mSwiping = false;
    boolean mSwipeDelete = false;
    long mIdToDeleteOnSwipe = -1;
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    GeodataSqlAdapter mSqlAdapter;
    SQLiteDatabase mDb;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();



    public final String LOG_TAG = this.getClass().getSimpleName();

    //Buttons
    private Button mGeoButton;
    private Button mAddLocButton;
    private Button mListButton;

    //SQL Variables
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

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);
        mPlaceListView = (ListView) findViewById(R.id.location_list);
        android.util.Log.d("Debug", "d=" + mPlaceListView.getDivider());
        //Get listview information

        //SQL listview population
        final GeoDbHelper helper = new GeoDbHelper(getBaseContext());
        mDb = helper.getWritableDatabase();
        Log.d("DB", "What is happening?: " + mDb);

        Cursor geoCursor = mDb.rawQuery("SELECT  * FROM " +GeoContract.GeoEntry.TABLE_NAME, null);

        View.OnTouchListener mTouchListener = new ListView.OnTouchListener(){
            float mDownX;
            private int mSwipeSlop = -1;

            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (mSwipeSlop < 0) {
                    mSwipeSlop = ViewConfiguration.get(AddLocationActivity.this).
                            getScaledTouchSlop();
                }

                //TODO: Need to properly get ID of the selected object from the view here and use it for animation and delete..
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        TextView tv = (TextView) v.findViewById(android.R.id.text1);
                        Log.d("TOUCH", "Action down event! Happening for view with id: "+v.getId());
                        Log.d("TOUCH", "Action down event! Happening for view with text: "+tv.getText());
                        if (mItemPressed) {
                            Log.d("TOUCH", "Multi-touch event! Happening for view with id: "+v.getId());
                            // Multi-item swipes not handled
                            return false;
                        }
                        mItemPressed = true;
                        mDownX = event.getX();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.d("TOUCH", "Action cancel event! Happening for view with id: "+v.getId());
                        v.setAlpha(1);
                        v.setTranslationX(0);
                        mItemPressed = false;
                        break;
                    case MotionEvent.ACTION_MOVE: {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        if (!mSwiping) {
                            if (deltaXAbs > mSwipeSlop) {
                                mSwiping = true;
                                mPlaceListView.requestDisallowInterceptTouchEvent(true);
                                mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                            }
                        }
                        if (mSwiping) {
                            v.setTranslationX((x - mDownX));
                            v.setAlpha(1 - deltaXAbs / v.getWidth());
                        }
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        Log.d("TOUCH", "Action up event! Happening for view with id: "+v.getId());
                        Log.d("TOUCH", "Action up event! Happening for view with mSwiping: "+mSwiping);

                        // User let go - figure out whether to animate the view out, or back into place
                        if (mSwiping) {
                            float x = event.getX() + v.getTranslationX();
                            float deltaX = x - mDownX;
                            float deltaXAbs = Math.abs(deltaX);
                            float fractionCovered;
                            float endX;
                            float endAlpha;
                            final boolean remove;
                            if (deltaXAbs > v.getWidth() / 4) {
                                // Greater than a quarter of the width - animate it out
                                fractionCovered = deltaXAbs / v.getWidth();
                                endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                                endAlpha = 0;
                                remove = true;
                            } else {
                                // Not far enough - animate it back
                                fractionCovered = 1 - (deltaXAbs / v.getWidth());
                                endX = 0;
                                endAlpha = 1;
                                remove = false;
                            }
                            // Animate position and alpha of swiped item
                            // NOTE: This is a simplified version of swipe behavior, for the
                            // purposes of this demo about animation. A real version should use
                            // velocity (via the VelocityTracker class) to send the item off or
                            // back at an appropriate speed.
                            long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                            mPlaceListView.setEnabled(false);
                            v.animate().setDuration(duration).
                                    alpha(endAlpha).translationX(endX).
                                    withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Restore animated values
                                            v.setAlpha(1);
                                            v.setTranslationX(0);
                                            if (remove) {
                                                Log.d("TOUCH", "Remove event being initialized");

                                                animateRemoval(mPlaceListView, v);

                                                //Non animated removal from list:
                                                //mSwipeDelete = true;
                                                //db.delete(GeoContract.GeoEntry.TABLE_NAME, GeoContract.GeoEntry._ID + "=" + mIdToDeleteOnSwipe, null);//id, null);
                                                //Cursor updatedCursor = db.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
                                                //sqlAdapter.swapCursor(updatedCursor);
                                                //sqlAdapter.notifyDataSetChanged();
                                            } else {
                                                mBackgroundContainer.hideBackground();
                                                mSwiping = false;
                                                mPlaceListView.setEnabled(true);
                                            }
                                            //reset id to -1 since item is no longer being observed
                                            mIdToDeleteOnSwipe = -1;
                                        }
                                    });
                        }
                    }
                    mItemPressed = false;
                    break;
                    default:
                        return false;
                }
                //TODO: Fixing block - fixing the animation back to normal since animationRemoval subroutine is not called during remove
                //Start block for fixing animation
                //mBackgroundContainer.hideBackground();
                //mSwiping = false;
                //mPlaceListView.setEnabled(true);
                //End block of fixing

                return true;
            }
        };

        mSqlAdapter = new GeodataSqlAdapter(getBaseContext(), geoCursor, 0, mTouchListener);



        mPlaceListView.setAdapter(mSqlAdapter);

        //geoCursor.close();
        mAddLocButton = (Button) findViewById(R.id.add_loc_button);
        mAddLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show that the coords are being added
                Log.d("ADD_LOC", "You're adding location!");

                Log.d("CV", contentValues.toString());

                //Prevent duplicate entries, place ID should be unique for each location added to the database
                String Query = "SELECT * FROM geo WHERE "+GeoContract.GeoEntry.COLUMN_PLACE_CODE+" = '"+  contentValues.get(GeoContract.GeoEntry.COLUMN_PLACE_CODE).toString() +"'";
                Log.d("QUERY", Query);
                Cursor cursor = mDb.rawQuery(Query, null);
                if(cursor.getCount() <= 0){
                    //if place is not already in the existing databse, add it
                    mDb.insert(GeoContract.GeoEntry.TABLE_NAME, null, contentValues);
                }else{
                    Toast.makeText(AddLocationActivity.this, "Location is already added!", Toast.LENGTH_SHORT).show();
                }
                cursor.close();

                //update the list view with the new updated database
                Cursor updatedCursor = mDb.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
                mSqlAdapter.swapCursor(updatedCursor);
                mSqlAdapter.notifyDataSetChanged();

                //clear text field after location is either added or attempted to be added
                mLocationText.setText("");
            }
        });
        mGeoButton = (Button) findViewById(R.id.geo_button);
        mGeoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start Maps activity to display added cities to the user
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        mListButton = (Button) findViewById(R.id.button_list);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete all rows from table
                mDb.delete(GeoContract.GeoEntry.TABLE_NAME, null, null);

                //notify listview adapter of changes
                Cursor updatedCursor = mDb.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
                mSqlAdapter.swapCursor(updatedCursor);
                mSqlAdapter.notifyDataSetChanged();
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
        mPlaceListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //set ID location here and handle insite touch listener? Would need to reset touch listened when finger is lifted
                mIdToDeleteOnSwipe = id;
            }
        });
//      Old non animated touch listener
        //mPlaceListView.setOnTouchListener(swipeDetector);

        //TODO: The ListViewRemovalAnimation attached the touch listener to each individual view item that is added
        //need to do something similar here, can handle it in GeodataSqlAdapter
        //mPlaceListView.setOnTouchListener(new ListView.OnTouchListener() {

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

            //Tokenize the String by Commas, the last chunk will be the country
            //TODO: THIS ASSUMES THAT THE COUNTRY IS ALWAYS THE LAST IN A SERIES OF COMMA DELIMITED STRINGS FOR THE ADDRESS
            Log.d("LOCAL", "The address is: " + place.getAddress());
            String address = place.getAddress().toString();
            String country = "";
            StringTokenizer st = new StringTokenizer(address, ", ");
            while(st.hasMoreElements()){
                country= st.nextToken();
            }

            Log.d(LOG_TAG, "The split address to country is: ." + country);
            //Clear all old content values and start with a clean slate before creating a new one
            contentValues.clear();
            //Add all the relevant values to the content values to be added to the SQL database later
            contentValues.put(GeoContract.GeoEntry.COLUMN_PLACE_CODE, place.getId());
            contentValues.put(GeoContract.GeoEntry.COLUMN_CITY_NAME, (String) place.getName());
            contentValues.put(GeoContract.GeoEntry.COLUMN_COUNTRY, country);//TODO: Need to resolve the actual country code here
            contentValues.put(GeoContract.GeoEntry.COLUMN_COORD_LAT, place.getLatLng().latitude);
            contentValues.put(GeoContract.GeoEntry.COLUMN_COORD_LONG, place.getLatLng().longitude);
            Log.d(LOG_TAG, "The content values are: " + contentValues.toString());

            Log.i(LOG_TAG, "Place details received: " + place.getName());

            places.release();
        }
    };



    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        Log.d("TOUCH", "Entering the animateRemoval subroutine");

        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = mSqlAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int viewId = viewToRemove.getId();
        Log.d("TOUCH", "The view id for the view trying to be removed is: " + viewId);
        TextView tv = (TextView) viewToRemove.findViewById(android.R.id.text1);
        //Remove based on matching city name
        //TODO: NEED TO FIX THIS TO USE ACTUAL UNIQUE PLACE IDs
        CharSequence cityName = tv.getText();
        mDb.delete(GeoContract.GeoEntry.TABLE_NAME, GeoContract.GeoEntry.COLUMN_CITY_NAME + "='" + cityName+"'", null);//id, null);
        Cursor updatedCursor = mDb.rawQuery("SELECT  * FROM " + GeoContract.GeoEntry.TABLE_NAME, null);
        mSqlAdapter.swapCursor(updatedCursor);
        mSqlAdapter.notifyDataSetChanged();
        //mSqlAdapter.remove(mSqlAdapter.getItem(position));

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = mSqlAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        mPlaceListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    mPlaceListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }
}
