package com.example.chrissebesta.travology;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chrissebesta on 3/29/16.
 * Custom class to store description, lat, and long in a simple, easily readable place
 * Intended to be used with GeodataAdapter to show data back to user.
 */
public class Geodata {
    public String description;
    public LatLng latLng;

    public Geodata(String description, LatLng latLng){
        this.description = description;
        this.latLng = latLng;
    }
    public Geodata(){}

    //getter and setter methods
    public void setDescription(String description){this.description = description;}
    public String getDescription(){return description;}
    public void setLatLng(LatLng latLng){this.latLng=latLng;}
    public LatLng getLatLng(){return latLng;}
}
