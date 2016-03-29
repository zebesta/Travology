package com.example.chrissebesta.travology;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment {
    private ArrayList<LatLng> coordinates = new ArrayList<>();

    public ListActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        coordinates = intent.getParcelableArrayListExtra("coordinates");

        String[] testingStringArrayList = {"one", "two", "three"};
        ArrayAdapter<String> objAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                testingStringArrayList);
//        ListView lv = (ListView) getParentFragment().getView().findViewById(R.id.location_list_view_in_frag);
//
//        lv.setAdapter(objAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}
