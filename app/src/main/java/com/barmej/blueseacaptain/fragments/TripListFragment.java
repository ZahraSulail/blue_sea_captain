package com.barmej.blueseacaptain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.blueseacaptain.Constants;
import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.adapter.TripItemsAdapter;
import com.barmej.blueseacaptain.ctivities.AddNewTripActivity;
import com.barmej.blueseacaptain.domain.entity.Captain;
import com.barmej.blueseacaptain.domain.entity.FullStatus;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.inteerface.OnTripClickListiner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripListFragment extends Fragment implements OnTripClickListiner {

    /*
      Add trip button to start AddTripActivity
     */
    private Button mAddButton;
    /*
      Recycler view variable
     */
    private RecyclerView mRecyclerView;

    /*
      TripItemsAdapter object
     */
    private TripItemsAdapter mAdapter;

    /*
      mTrips ArrayList
     */
    private ArrayList<Trip> mTrips;

    /*
     Object Captain
     */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_trips_list, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );


        mRecyclerView = view.findViewById( R.id.recycler_view );
        mRecyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        mTrips = new ArrayList<>();
        mAdapter = new TripItemsAdapter( mTrips, TripListFragment.this );
        mRecyclerView.setAdapter( mAdapter );
        mRecyclerView.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ) );
        Captain captain = new Captain();
        mAddButton = view.findViewById( R.id.button_add_trip );

        System.out.println( "CREATED!!" );
        FirebaseDatabase.getInstance().getReference(Constants.TRIP_REF_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mTrips.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        trip.setId(dataSnapshot.getKey());

                        if(!trip.getStatus().equals(Trip.Status.ARRIVED.name())) {
                            //if (trip.getId().equals(captain.getId())) {
                                mTrips.add(trip);
                                System.out.println("Key: " + dataSnapshot.getKey());
                            //}
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    System.out.println( "snapshot" + snapshot.getKey() + " " + snapshot.getValue().toString() );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        //Add trip button
        mAddButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), AddNewTripActivity.class );
                startActivity( intent );

            }
        } );
    }

    @Override
    public void onTripClick(Trip trip) {
        FullStatus fullStatus = new FullStatus();
        fullStatus.setTrip( trip );
        System.out.println("Full Status: " + fullStatus.getTrip().getId());

        TripDetailsFragment detailsFragment = TripDetailsFragment.getInstance(fullStatus);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().
                replace( R.id.layout_main, detailsFragment)
                .addToBackStack(null)
                .commit();

        mAddButton.setVisibility( View.GONE );
    }


}