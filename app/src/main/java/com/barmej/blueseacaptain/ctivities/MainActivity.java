package com.barmej.blueseacaptain.ctivities;

import android.os.Bundle;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.fragments.TripListFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    /*
      TripListFragment Object
     */
    TripListFragment mTripListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //FragmentManager to find Fragment by id
        FragmentManager manager = getSupportFragmentManager();
        mTripListFragment = (TripListFragment) manager.findFragmentById( R.id.trip_list_container);
    }
}