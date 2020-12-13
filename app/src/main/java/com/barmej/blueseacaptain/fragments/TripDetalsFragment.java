package com.barmej.blueseacaptain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.FullStatus;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.inteerface.TripCommunicationInterface;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class TripDetalsFragment extends Fragment implements OnMapReadyCallback {

    public static final String INITIAL_STATUS_EXTRA = "INITIAL_STATUS_EXTRA";

    /*
      A constant for trips data fro TripListFragment
     */
    public static final String TRIP_DATA = "trip_data";

    /*
      Bundle for saving mapView
     */
    private static final String MAPVIEW_BUNDLE_KEY = "mapViewBundleKey";

    /*
      Devind the views required to display on this fragment
     */
    private CardView mMainCardView;
    private TextView mDataTextView;
    private TextView mPositionTextView;
    private TextView mDestinationTextView;
    private TextView mAvailableSeatsTextView;
    private TextView mBookedSeatsTextView;
    private MapView mMapView;
    private MaterialButton mStartMaterialButton;
    private MaterialButton mArrivedMaterialButton;

    /*
     DatabaseReference object
     */
    DatabaseReference databaseReference;

    /*
     Trip object
     */
    Trip mTrip;

    /*
     Google map variable
     */
    private GoogleMap mGoogleMap;

    /*
      Bundle object
     */
    Bundle mapViewBundle;

    /*
       TripCommunicationInterface  object

     */

    TripCommunicationInterface tripCommunicationInterface;



    public static TripDetalsFragment getInstance(FullStatus status) {
        TripDetalsFragment detalsFragment = new TripDetalsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable( INITIAL_STATUS_EXTRA, status );
        detalsFragment.setArguments( bundle );
        return detalsFragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_trip_details, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        mMainCardView = view.findViewById( R.id.card_view_main );
        mDataTextView = view.findViewById( R.id.det_text_view_date );
        mPositionTextView = view.findViewById( R.id.det_text_view_position );
        mDestinationTextView = view.findViewById( R.id.det_text_view_destination );
        mAvailableSeatsTextView = view.findViewById( R.id.det_text_view_available_seats );
        mBookedSeatsTextView = view.findViewById( R.id.det_text_view_booked_seats );
        mArrivedMaterialButton = view.findViewById( R.id.button_arrived );
        mStartMaterialButton = view.findViewById( R.id.button_start );

        mMapView = view.findViewById( R.id.map_view );
        mMapView.onCreate( savedInstanceState );
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle( MAPVIEW_BUNDLE_KEY );
        }
        mMapView.onCreate( mapViewBundle );
        mMapView.getMapAsync( this );

        FullStatus status = (FullStatus) getArguments().getSerializable( INITIAL_STATUS_EXTRA );
        //updateStartAndDestinationPoints( status );

          /*
            getArguments back
           */
        mTrip = status.getTrip();
        mDataTextView.setText( mTrip.getFormattedDate());
        mPositionTextView.setText( mTrip.getPositionSeaPortName() );
        mDestinationTextView.setText( mTrip.getDestinationSeaportName() );
        mAvailableSeatsTextView.setText( String.valueOf( mTrip.getAvailableSeats() ) );
        mBookedSeatsTextView.setText( String.valueOf( mTrip.getBookedSeats() ) );
        /*
         Start button setOnClickLisitiner
         */
        mStartMaterialButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setStartPoint();
            }
        } );

        /*
         Arrived button setOnClickLisitiner
         */
        mArrivedMaterialButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setDestinationPont();
            }
        } );
    }

    /*
      onSaveInstanceState to save map
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle( MAPVIEW_BUNDLE_KEY );
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle( MAPVIEW_BUNDLE_KEY, mapViewBundle );
        }
        mMapView.onSaveInstanceState( mapViewBundle );
    }

    /*
      onMapReady method to get trip's lat's amd latng's
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mTrip.getCurrentLat() != 0 && mTrip.getCurrentLng() != 0) {
            LatLng currentLatLng = new LatLng( mTrip.getCurrentLat(), mTrip.getCurrentLng() );
            googleMap.addMarker( new MarkerOptions() )
                    .setPosition( currentLatLng );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( currentLatLng, 16 );
            googleMap.moveCamera( cameraUpdate );

            return;
        }

        if(mTrip.getPositionLat() != 0 && mTrip.getPositionLng() != 0){
            LatLng positionLatLng = new LatLng( mTrip.getPositionLat(), mTrip.getPositionLng());
            googleMap.addMarker( new MarkerOptions() )
                    .setPosition( positionLatLng );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( positionLatLng, 16 );
            googleMap.moveCamera( cameraUpdate );

            return;
        }

        if(mTrip.getDestinationLat() != 0 && mTrip.getDestinationLng() != 0){
            LatLng destinationLatng = new LatLng( mTrip.getDestinationLat(), mTrip.getDestinationLng());
            googleMap.addMarker( new MarkerOptions() )
                    .setPosition(destinationLatng );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( destinationLatng, 16 );
            googleMap.moveCamera( cameraUpdate );

        }
    }

    /*
      Map lifeCycle methods
     */
    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }





}
