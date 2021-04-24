package com.barmej.blueseacaptain.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.barmej.blueseacaptain.Constants;
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


public class MapFragment extends Fragment implements OnMapReadyCallback {

    /*
     Variables references
     */
    private GoogleMap mMap;
    private Marker startPointMarker;
    private Marker destinationMarker;
    private Marker tripMarker;
    private PermissionFailListener permissionFailListener;
    private Button selectStartPointButton;
    private Button selectDestinationPointButtont;
    private LatLng mStartPointLatng;
    private LatLng mDestinationLatng;
    private LatLng tripLalng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        Find fragment by id and assign it to mapFragment variables
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }

        /*
         Find views by id and assign them to variables
         */
        selectStartPointButton = view.findViewById(R.id.button_select_start);
        selectDestinationPointButtont = view.findViewById(R.id.button_select_destination);

        /*
        click on this button to select start point coordinates
         */
        selectStartPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartPointLatng != null) {
                    setStartPointMarker(mStartPointLatng);
                    selectStartPointButton.setVisibility(View.GONE);
                    selectDestinationPointButtont.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), R.string.add_start_point, Toast.LENGTH_SHORT).show();
                }

            }
        });

         /*
        click on this button to select destination point coordinates then move to AddTripFragment
         */
        selectDestinationPointButtont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestinationLatng != null) {
                    setDestinationMarker(mDestinationLatng);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.START_POINT_LATNG, mStartPointLatng);
                    bundle.putParcelable(Constants.DESTINATION_LATNG, mDestinationLatng);
                    AddTripFragment addTripFragment = new AddTripFragment();
                    addTripFragment.setArguments(bundle);
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.map_container, addTripFragment)
                                .commit();
                        selectDestinationPointButtont.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getContext(), R.string.add_destination_point, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
       Check Location Permission
     */
    private void checkLocationPermissionAndSetUpUserLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            setUpUserLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);

        }
    }

    /*
     onMapReady method to set start and destination points on the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectStartPointButton.getVisibility() == View.VISIBLE) {
                    setStartPointMarker(latLng);
                } else {
                    setDestinationMarker(latLng);
                }
            }
        });

        checkLocationPermissionAndSetUpUserLocation();
    }

    /*
     onRequestPermissionsResult to check request location code
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (permissions.length == 1 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpUserLocation();
            } else {
                if (permissionFailListener != null) {
                    permissionFailListener.onPermissionFail();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*
   Update user location method when run the app
   */
    private void setUpUserLocation() {
        if (mMap == null) return;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatng, 16);
                    mMap.moveCamera(update);
                }

            }
        });
    }


    /*
    Remove map location layout after app closing
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
    public void setStartPointMarker(LatLng target) {
        this.mStartPointLatng = target;
        if (mMap == null) return;
        if (startPointMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.position);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            startPointMarker = mMap.addMarker(options);
        } else {
            startPointMarker.setPosition(target);
        }
    }

      /*
     Destination marker on the map
    */

    public void setDestinationMarker(LatLng target) {
        this.mDestinationLatng = target;
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
     Get the ship marker on the map during the trip
     */
    public void setTripMarker(LatLng target) {

        this.tripLalng = target;
        if (mMap == null) return;
        if (tripMarker == null) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource((R.drawable.boat));
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.position(target);
            tripMarker = mMap.addMarker(options);
        } else {
            tripMarker.setPosition(target);
        }

    }

    /*
     Clear map and reset markers status after arrived
     */
    public void reset() {
        if (mMap == null) return;
        mMap.clear();
        startPointMarker = null;
        destinationMarker = null;
        setUpUserLocation();
    }

    public void setOnPermissionFailListener(PermissionFailListener permissionFailListener) {
        this.permissionFailListener = permissionFailListener;
    }

}