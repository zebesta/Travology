package com.example.chrissebesta.travology;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class GeoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_list);

        ListView lv = (ListView) findViewById(R.id.list_view_geo_activity);
        //GeodataAdapter adapter = new GeodataAdapter()
    }
}
