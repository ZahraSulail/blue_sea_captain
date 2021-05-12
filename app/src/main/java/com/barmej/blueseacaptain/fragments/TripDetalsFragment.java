package com.barmej.blueseacaptain.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.barmej.blueseacaptain.Constants;
import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.TripManager;
import com.barmej.blueseacaptain.domain.entity.FullStatus;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.inteerface.StatusCallBack;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TripDetalsFragment extends Fragment implements OnMapReadyCallback {

    /*
     Map markers
     */
    private Marker positionMarker;
    private Marker destinationMarker;
    private Marker shipLocationMarker;

    /*
      Defind the views required to display on this fragment
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
    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationClient;

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
    MapFragment reference
     */
    MapFragment mapFragment;

    /*
     Trip latlng to get trip lattued and la
     */
    private LatLng tripLatlng;

    /*
      FullStatus object
     */
    private FullStatus status;

    private Marker marker;

    /*
     put serializable arguments to TripDetailsFragment
     */
    public static TripDetalsFragment getInstance(FullStatus status) {
        TripDetalsFragment detalsFragment = new TripDetalsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.INITIAL_STATUS_EXTRA, status);
        detalsFragment.setArguments(bundle);
        return detalsFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         Find views by Ids and assigned them to variables
         */
        mMainCardView = view.findViewById(R.id.card_view_main);
        mDataTextView = view.findViewById(R.id.det_text_view_date);
        mPositionTextView = view.findViewById(R.id.det_text_view_position);
        mDestinationTextView = view.findViewById(R.id.det_text_view_destination);
        mAvailableSeatsTextView = view.findViewById(R.id.det_text_view_available_seats);
        mBookedSeatsTextView = view.findViewById(R.id.det_text_view_booked_seats);
        mArrivedMaterialButton = view.findViewById(R.id.button_arrived);
        mStartMaterialButton = view.findViewById(R.id.button_start);

        /*
         Find map view by id and save it' insatanceState by bundle
         */
        mMapView = view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        // Get trip status by FullStatuse object
        status = (FullStatus) getArguments().getSerializable(Constants.INITIAL_STATUS_EXTRA);

        /*
         Get trip information received from firebase via onTripClick listener in TripListFragment
         */
        mTrip = status.getTrip();
        mDataTextView.setText(mTrip.getFormattedDate());
        mPositionTextView.setText(mTrip.getStartPortName());
        mDestinationTextView.setText(mTrip.getDestinationSeaportName());
        mAvailableSeatsTextView.setText(String.valueOf(mTrip.getAvailableSeats()));
        mBookedSeatsTextView.setText(String.valueOf(mTrip.getBookedSeats()));

        /*
         Start button setOnClickLisitiner
         */
        mStartMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TripManager.getInstance().assignTrip(mTrip.getId(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void o) {

                        TripManager.getInstance().getTripAndNotifyStatus(new StatusCallBack() {
                            @Override
                            public void onUpdate(FullStatus fullStatus) {
                                System.out.println("Trip Started");
                                updateStatus(fullStatus);
                            }
                        });

                    }
                });


            }
        });

        /*
         Arrived button setOnClickLisitiner
         */
        mArrivedMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripManager.getInstance().updateToArrivedToDestination();
                //هل نحتاج statusCallback على غرار زر بدا الرحلة


            }
        });
    }

    /*
      Map lifeCycle methods
     */
    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();

        TripManager.getInstance().getTripAndNotifyStatus(new StatusCallBack() {
            @Override
            public void onUpdate(FullStatus fullStatus) {
                updateStatus(fullStatus);
            }
        });
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
        TripManager.getInstance().stopListiningToStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    /*
    onSaveInstanceState to save map state
   */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Bundle to save state of the map
        mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    /*
      onMapReady method to get trip's lat's amd latng's
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mGoogleMap = googleMap;
        if (mTrip.getCurrentLat() != 0 && mTrip.getCurrentLng() != 0) {
            createOrUpdateMarker(mTrip.getCurrentLat(), mTrip.getCurrentLng());
        }

        if (mTrip.getStartLat() != 0 && mTrip.getStartLng() != 0) {
            LatLng positionLatLng = new LatLng(mTrip.getStartLat(), mTrip.getStartLng());
            googleMap.addMarker(new MarkerOptions().position(positionLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.position)));
        }

        if (mTrip.getDestinationLat() != 0 && mTrip.getDestinationLng() != 0) {
            LatLng destinationLatng = new LatLng(mTrip.getDestinationLat(), mTrip.getDestinationLng());
            googleMap.addMarker(new MarkerOptions().position(destinationLatng).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));
        }
    }

    private void createOrUpdateMarker(double currentLat, double currentLng) {
        mTrip.setCurrentLat(currentLat);
        mTrip.setCurrentLng(currentLng);
        LatLng currentLatLng = new LatLng(currentLat, currentLng);
        if (marker == null) {
            marker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.boat)).position(currentLatLng));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 12);
            mGoogleMap.moveCamera(cameraUpdate);
        } else {
            marker.setPosition(currentLatLng);
        }
    }


    public void updateStatus(FullStatus fullStatus) {

        String tripStatus = fullStatus.getTrip().getStatus();

        if (mTrip.getCurrentLat() != 0 && mTrip.getCurrentLng() != 0) {
            LatLng currentLatLng = new LatLng(mTrip.getCurrentLat(), mTrip.getCurrentLng());
            marker.setPosition(currentLatLng);
        }

        if(tripStatus.equals(Trip.Status.GOING_TO_DESTINATION.name())) {
            mStartMaterialButton.setVisibility(View.GONE);
            mArrivedMaterialButton.setVisibility(View.VISIBLE);
            startTracking();
        } else if (tripStatus.equals(Trip.Status.ARRIVED)) {
          // hide start and arrive buttons and show arrived label
            mStartMaterialButton.setVisibility(View.GONE);
            mArrivedMaterialButton.setVisibility(View.GONE);
            Toast.makeText(getContext(), R.string.trip_is_arrived, Toast.LENGTH_SHORT).show();
            // Stop tracking
            stopTracking();
        } else {
            mStartMaterialButton.setVisibility(View.VISIBLE);
            mArrivedMaterialButton.setVisibility(View.GONE);
        }
    }

    /*
     Start button that captain press on to start the trip and it will be called on mStartMaterialButton.setOnClickListener method
     */
    public void startTracking() {

        if (mTrip != null) {

            if (locationCallback == null) {

                locationClient = LocationServices.getFusedLocationProviderClient(getContext());

                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        Location lastLocation = locationResult.getLastLocation();

                        if (lastLocation != null) {

                            createOrUpdateMarker(lastLocation.getLatitude(), lastLocation.getLongitude());

                            // Call updateCurrentLocation from TripManager class
                            TripManager.getInstance().updateCurrentLocation(lastLocation.getLatitude(), lastLocation.getLongitude());

                        }

                    }
                };

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setFastestInterval(5000);
                locationRequest.setInterval(2000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                //Check for access location permission
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
                }

            }

        }

    }

    /*
     stopTracking method caaled after trip arriving
     */

    public void stopTracking(){
        TripManager.getInstance().stopListiningToStatus();

    }

    /*
    onRequestPermissionResult
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Toast.makeText(getContext(), R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}