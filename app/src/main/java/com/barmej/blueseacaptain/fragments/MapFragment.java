package com.barmej.blueseacaptain.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.barmej.blueseacaptain.Constants;
import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.ctivities.AddTripActivity;
import com.barmej.blueseacaptain.inteerface.PermissionFailListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Marker startPointMarker;
    private Marker destinationMarker;
    private PermissionFailListener permissionFailListener;
    private Button selectStartPointButton;
    private Button selectDestinationPointButtont;
    private LatLng mStartPointLatng;
    private LatLng mDestinationLatng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_map, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.fragment_map );
        if (mapFragment != null) {
            mapFragment.getMapAsync( (OnMapReadyCallback) this );
        }

        selectStartPointButton = view.findViewById( R.id.button_select_start );
        selectDestinationPointButtont = view.findViewById( R.id.button_select_destination );

        selectStartPointButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStartPointLatng != null) {
                    setStartPointMarker( mStartPointLatng );
                    selectStartPointButton.setVisibility( View.GONE );
                    selectDestinationPointButtont.setVisibility( View.VISIBLE );
                } else {
                    Toast.makeText( getContext(), R.string.add_start_point, Toast.LENGTH_SHORT ).show();
                }

            }
        } );

        selectDestinationPointButtont.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestinationLatng != null) {
                    setDestinationMarker( mDestinationLatng );
                    Intent intent = new Intent( getContext(), AddTripActivity.class );
                    intent.putExtra( Constants.START_POINT_LATNG, mStartPointLatng );
                    intent.putExtra(Constants.DESTINATION_LATNG, mDestinationLatng );
                    getActivity().finish();
                    startActivity( intent );
                    //TODO: move to AddNewTripActivity
                }else{
                    Toast.makeText( getContext(), R.string.add_destination_point, Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    /*
        Check Location Permission
     */
    private void checkLocationPermissionAndSetUpUserLocation() {
        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_DENIED) {
            setUpUserLocation();
        } else {
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION );

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(selectStartPointButton.getVisibility() == View.VISIBLE) {
                    setStartPointMarker( latLng );
                } else {
                    setDestinationMarker( latLng );
                }
            }
        } );

        checkLocationPermissionAndSetUpUserLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (permissions.length == 1 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUserLocation();
            } else {
                if (permissionFailListener != null) {
                    permissionFailListener.onPermissionFail();
                }
            }
        } else {
            super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        }
    }

    /*
Update user location method
*/
    private void setUpUserLocation() {
        if (mMap == null) return;
        if (ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled( true );
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient( getActivity() );
        locationClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatng = new LatLng( location.getLatitude(), location.getLongitude() );
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom( currentLatng, 16 );
                    mMap.moveCamera( update );
                }

            }
        } );
    }


    /*
    Remove map location layout
   */
    public void removeMapLocationLayout() {
        if (mMap == null) return;
        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled( false );
        }
    }

    /*
    Set the map marker to be in the center
   */
    public LatLng captureCenter() {
        if (mMap == null) return null;
        return mMap.getCameraPosition().target;
    }

     /*
   Position marker on the map
   */
    public void setStartPointMarker(LatLng target) {
        this.mStartPointLatng = target;
        if (mMap == null) return;
        if (startPointMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.position );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target );
            startPointMarker = mMap.addMarker( options );
        } else {
            startPointMarker.setPosition( target );
        }
    }

      /*
     Destination marker on the map
    */

    public void setDestinationMarker(LatLng target) {
       this.mDestinationLatng = target;
        if (mMap == null) return;
        if (destinationMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.destination );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target );
            destinationMarker = mMap.addMarker( options );
        } else {
            destinationMarker.setPosition( target );
        }
    }

    /*
     Clear map and reset markers status
     */
   /* public void reset() {
        if (mMap == null) return;
        mMap.clear();
        startPointMarker = null;
        destinationMarker = null;
        setUpUserLocation();
    }*/



    public void setOnPermissionFailListener(PermissionFailListener permissionFailListener) {
        this.permissionFailListener = permissionFailListener;
    }

}