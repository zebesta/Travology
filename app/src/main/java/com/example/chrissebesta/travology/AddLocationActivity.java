package com.example.chrissebesta.travology;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class AddLocationActivity extends AppCompatActivity {
    private Button mEnterButton;
    private EditText mLocationText;
    private List<Address> mAddresses;
    private Button mGeoButton;
    public final String LAT_TAG = "LAT TAG IT";
    public final String LONG_TAG = "LONG TAG IT";
    private long mLat;
    private long mLong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        buttonListener();

        mGeoButton = (Button) findViewById(R.id.geo_button);
        mGeoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(LAT_TAG, mLat);
                intent.putExtra(LONG_TAG, mLong);
                startActivity(intent);
            }
        });

    }

    private void buttonListener() {
        mEnterButton = (Button) findViewById(R.id.add_location_enter_button);

        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                mLocationText = (EditText) findViewById(R.id.add_location_entry_text);
                String text = String.valueOf(mLocationText.getText());

                //text = "No text entered yet!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                try {
                    getCoordsFromEntry(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getCoordsFromEntry(String text) throws IOException {
        Geocoder gc = new Geocoder(this);
        mAddresses = gc.getFromLocationName(text, 1);
        Address ad = mAddresses.get(0);
        Log.d("ADDRESSES", "Country: " + ad.getCountryName() + " Lat: " + ad.getLatitude() + " Long: " + ad.getLongitude());

        mLat = (long) ad.getLatitude();
        mLong = (long) ad.getLongitude();
    }
}
