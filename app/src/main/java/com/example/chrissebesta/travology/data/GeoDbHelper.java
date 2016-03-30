package com.example.chrissebesta.travology.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chrissebesta on 3/30/16.
 */
public class GeoDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "geo.db";

    public GeoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_GEO_TABLE = "CREATE TABLE " + GeoContract.GeoEntry.TABLE_NAME + " (" +
                GeoContract.GeoEntry._ID + " INTEGER PRIMARY KEY," +
                GeoContract.GeoEntry.COLUMN_PLACE_CODE + " TEXT NOT NULL, " +
                GeoContract.GeoEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                GeoContract.GeoEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
                GeoContract.GeoEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                GeoContract.GeoEntry.COLUMN_COORD_LONG + " REAL NOT NULL, "
                + ");";


        sqLiteDatabase.execSQL(SQL_CREATE_GEO_TABLE);
        Log.d("SQL STRING IS: ", SQL_CREATE_GEO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GeoContract.GeoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
