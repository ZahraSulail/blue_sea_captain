package com.barmej.blueseacaptain.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barmej.blueseacaptain.R;
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



public class TripTrackingMapFragment extends Fragment implements OnMapReadyCallback{

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Marker positionMarker;
    private Marker destinationMarker;
    private Marker currentLocationMarker;
    private TextView tripStatusTextView;
    private PermissionFailListener permissionFailListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate( R.layout.fragment_trip_tracking_map, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.tracking_map );
        if(mapFragment != null){
            mapFragment.getMapAsync( (OnMapReadyCallback) this );
        }

        tripStatusTextView = view.findViewById( R.id.text_view_trip_status );
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
        checkLocationPermissionAndSetUpUserLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (permissions.length == 1 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUserLocation();
            } else {
                if(permissionFailListener != null){
                    permissionFailListener.onPermissionFail();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                if(location != null){
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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
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
    public void setPositionMarker(LatLng target) {
        if (mMap == null) return;
        if (positionMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.position);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            positionMarker = mMap.addMarker(options);
        } else {
            positionMarker.setPosition(target);
        }
    }

    /*
 Destination marker on the map
 */

    public void setDestinationMarker(LatLng target) {
        if (mMap == null) return;
        if (destinationMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.destination);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            destinationMarker = mMap.addMarker(options);
        } else {
            destinationMarker.setPosition(target);
        }
    }

    /*
 Ship marker on the map
 */
    public void setShipMarker(LatLng shipLatng) {
        if (mMap == null) return;
        if (currentLocationMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.boat );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( shipLatng );
            currentLocationMarker = mMap.addMarker( options );
        } else {
           currentLocationMarker.setPosition( shipLatng );
        }
    }

    public void reset() {
        if (mMap == null) return;
        mMap.clear();
        positionMarker = null;
        destinationMarker = null;
        currentLocationMarker = null;
        setUpUserLocation();
    }
public void showTripCurrentLocationOnMap(LatLng tripLatlng){
    CameraUpdate update = CameraUpdateFactory.newLatLngZoom( tripLatlng, 16 );
    mMap.moveCamera( update );
}

  public void setOnPermissionFailListener(PermissionFailListener permissionFailListener){
        this.permissionFailListener = permissionFailListener;
  }

}