package com.barmej.blueseacaptain.ctivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.fragments.MapFragment;

public class AddNewTripActivity extends AppCompatActivity {

    private MapFragment mapsContainerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_trip_info );

        mapsContainerFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container );
    }
}