package com.barmej.blueseacaptain.domain;

import com.barmej.blueseacaptain.domain.entity.Captain;
import com.barmej.blueseacaptain.domain.entity.FullStatus;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.inteerface.CallBack;
import com.barmej.blueseacaptain.inteerface.StatusCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class TripManager {

    /*
     Reference costatnts of trips and captains
     */
    private static final String TRIP_REF_PATH = "trips";
    private static final String CAPTAINS_REF_PATH = "captains";
    /*
      Inastance reference of TripManager
     */
    private static TripManager inatance;

    /*
     Database object
     */
    private FirebaseDatabase database;

    /*
     Trip and Captain references
     */
    private Trip trip;
    private Captain captain;

    private ValueEventListener tripStatusListener;
    private StatusCallBack statusCallBack;

    /*
     Costructor
     */
    public TripManager() {
        database = FirebaseDatabase.getInstance();

    }

    /*
     getInstance method to reuse object reference
     */
    public static TripManager getInstance() {
        if (inatance == null) {
            inatance = new TripManager();
        }
        return inatance;

    }

    public void getCaptainProfile(final String captainId, final CallBack callback) {
        database.getReference( CAPTAINS_REF_PATH ).child( captainId ).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                captain = snapshot.getValue( Captain.class );
                if (captain != null) {
                    callback.onComplete( true );

                } else {
                    callback.onComplete( false );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }


    public void startListiningForStatus(StatusCallBack statusCallBack) {
        this.statusCallBack = statusCallBack;
        tripStatusListener = database.getReference( TRIP_REF_PATH ).child( captain.getAssignedTrip() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trip = snapshot.getValue( Trip.class );

                        if (trip != null) {
                            FullStatus fullStatus = new FullStatus();
                            fullStatus.setCaptain( captain );
                            fullStatus.setTrip( trip );
                            notifyListiner( fullStatus );

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

    /*
      listiner to notify about statusCallBack
     */
    private void notifyListiner(FullStatus fullStatus) {
        if (statusCallBack != null) {
            statusCallBack.onUpdate( fullStatus );

        }

    }

    /*
       Tracing current location and update its status
     */
    public void updateCurrentLocation(double lat, double lng) {
        trip.setCurrentLat( lat );
        trip.setCurrentLng( lng );
        database.getReference( TRIP_REF_PATH ).child( trip.getId() ).setValue( trip );
    }


    /*
      Tracing trip when it arrived to destination
     */
    public void updateToArrivedToDestination() {
        trip.setStatus( Trip.Status.ARRIVED.name() );
        database.getReference( TRIP_REF_PATH ).child( trip.getId() ).setValue( trip );
        captain.setStatus( Captain.status.AVAILABEL.name() );
        captain.setAssignedTrip( null );
        trip = null;

        database.getReference( CAPTAINS_REF_PATH ).child( captain.getId() ).setValue( captain );
        FullStatus fullStatus = new FullStatus();
        fullStatus.setCaptain( captain );
        notifyListiner( fullStatus );
    }

    /*
    Stop eventListener to trip status
     */
    public void stopListiningToStatus() {
        if (tripStatusListener != null) {
            database.getReference().child( trip.getId() ).removeEventListener( tripStatusListener );
        }
        statusCallBack = null;
    }

}
