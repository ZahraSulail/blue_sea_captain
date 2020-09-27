package com.barmej.blueseacaptain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.adapter.TripItemsAdapter;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TripListFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private TripItemsAdapter mAdapter;
    private ArrayList<Trip> mTrips;
    DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        mRecyclerView = view.findViewById( R.id.recycler_view );
        mRecyclerView.setLayoutManager( new LinearLayoutManager( getContext()));
        mTrips = new ArrayList<>();
        mAdapter = new TripItemsAdapter( mTrips, TripListFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        mTrips = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child( "Trip_Details" );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTrips.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Trip trip = snapshot.getValue(Trip.class);
                    mTrips.add( trip );
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
            }
        } );

    }
}
