package com.barmej.blueseacaptain.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class TripDetalsFragment extends Fragment implements OnMapReadyCallback {

    public static final String TRIP_DATA = "trip_data";
    private static final String MAPVIEW_BUNDLE_KEY = "mapViewBundleKey";

    private CardView mMainCardView;
    private TextView mDataTextView;
    private TextView mPositionTextView;
    private TextView mDestinationTextView;
    private TextView mAvailableSeatsTextView;
    private TextView mBookedSeatsTextView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private MaterialButton mStartMaterialButton;
    private MaterialButton mArrivedMaterialButton;
    Trip mTrip;
    Bundle mapViewBundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_trip_details, container, false);
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
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle( MAPVIEW_BUNDLE_KEY );
        }
        mMapView.onCreate( mapViewBundle );
        mMapView.getMapAsync( this );

        /*
           getBundle to pass Trip data


        Bundle bundle = new Bundle();
        if(bundle != null){
            mTrip = (Trip) bundle.getSerializable(TRIP_DATA);
            if(mTrip != null){
                mDataTextView.setText( mTrip.getFormattedDate());
                mPositionTextView.setText( mTrip.getPositionSeaPortName());
                mDestinationTextView.setText( mTrip.getDestinationSeaportName());
                mAvailableSeatsTextView.setText( mTrip.getAvailableSeats());
                mBookedSeatsTextView.setText( mTrip.getBookedSeats());
            }
        }

         */
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle( MAPVIEW_BUNDLE_KEY );
        if(mapViewBundle ==null){
            mapViewBundle = new Bundle();
            outState.putBundle( MAPVIEW_BUNDLE_KEY, mapViewBundle );
        }
        mMapView.onSaveInstanceState( mapViewBundle );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        this.mGoogleMap = googleMap;
        LatLng latLng = new LatLng( mTrip.getCurrentLat(), mTrip.getCurrentLng());
        googleMap.addMarker( new MarkerOptions())
                .setPosition( latLng );
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLng, 16 );
        googleMap.moveCamera( cameraUpdate );

         */

    }

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
