package com.barmej.blueseacaptain.domain;

import androidx.annotation.NonNull;

import com.barmej.blueseacaptain.Constants;
import com.barmej.blueseacaptain.domain.entity.Captain;
import com.barmej.blueseacaptain.domain.entity.FullStatus;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.inteerface.CallBack;
import com.barmej.blueseacaptain.inteerface.StatusCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripManager {


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
        database.getReference(Constants.CAPTAINS_REF_PATH ).child( captainId ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("captain: " + snapshot.toString());
                captain = snapshot.getValue( Captain.class );
                if(captain != null){
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


    public void getTripAndNotifyStatuss(StatusCallBack statusCallBack) {
        this.statusCallBack = statusCallBack;
        if(captain.getAssignedTrip() == null) return;
        tripStatusListener = database.getReference( Constants.TRIP_REF_PATH ).child( captain.getAssignedTrip() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //System.out.println("Trip_Details: " + snapshot.toString());
                        trip = snapshot.getValue( Trip.class );
                        trip.setId(snapshot.getKey());
                        if (trip != null) {
                            FullStatus fullStatus = new FullStatus();
                            fullStatus.setCaptain( captain );
                            fullStatus.setTrip( trip );
                            notifyListiner( fullStatus );
                        } else {
                            throw new RuntimeException("Trip not exist");
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
        System.out.println("Lat: " + lat + " Lng: " + lng );
        trip.setCurrentLat( lat );
        trip.setCurrentLng( lng );
        // هنا يوجد خطا يحتاج اصلاح
        database.getReference( Constants.TRIP_REF_PATH ).child( trip.getId() ).setValue( trip );
    }


    /*
      Tracing trip when it arrived to destination
     */
    public void updateToArrivedToDestination() {
        trip.setStatus( Trip.Status.ARRIVED.name() );
        database.getReference( Constants.TRIP_REF_PATH ).child( trip.getId() ).setValue( trip );
        captain.setStatus( Captain.Status.AVAILABEL.name() );
        captain.setAssignedTrip( null );
        trip = null;
        database.getReference( Constants.CAPTAINS_REF_PATH ).child( captain.getId() ).setValue( captain );
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

    public void assignTrip(String tripId, OnSuccessListener<Void> onSuccessListener) {
        captain.setAssignedTrip(tripId);
        System.out.println("Captin ID: " + captain.getId() + " Captin Name" + captain.getName() + " Trip ID: " + captain.getAssignedTrip());
        database.getReference().child(Constants.CAPTAINS_REF_PATH).child(captain.getId()).setValue(captain).addOnSuccessListener(onSuccessListener).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        trip.setStatus( Trip.Status.GOING_TO_DESTINATION.name() );
        database.getReference( Constants.TRIP_REF_PATH ).child( trip.getId() ).setValue( trip ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Updated");
            }
        });
        FullStatus fullStatus = new FullStatus();
        fullStatus.setTrip( trip );
        notifyListiner( fullStatus );


    }

}
