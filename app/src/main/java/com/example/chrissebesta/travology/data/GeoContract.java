package com.example.chrissebesta.travology.data;

import android.provider.BaseColumns;

/**
 * Created by chrissebesta on 3/30/16.
 */
public class GeoContract {
    public static final class GeoEntry implements BaseColumns {

        public static final String TABLE_NAME = "geo";
        public static final String COLUMN_PLACE_CODE = "place_code";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_COORD_LAT = "latitude_coordinate";
        public static final String COLUMN_COORD_LONG = "longitude_coordinate";


    }
}
