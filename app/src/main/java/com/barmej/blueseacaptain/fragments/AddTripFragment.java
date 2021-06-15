package com.barmej.blueseacaptain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.barmej.blueseacaptain.Constants;
import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.ctivities.MainActivity;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddTripFragment extends Fragment {

    private TextInputLayout mStartTextInputLayout;
    private TextInputLayout mDestinationTextInputLayout;
    private TextInputLayout mAvailableSeatsTextIputLayout;
    private TextInputEditText mStartEditText;
    private TextInputEditText mDestinationEditText;
    private TextInputEditText mAvailableSeatsEditText;
    private DatePicker mDatePicker;
    private Trip mTrip;
    private DatabaseReference databaseReference;
    private LatLng mStartPointSelectedLatng;
    private LatLng mDestinationSelectedLatng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_trip, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        mStartPointSelectedLatng = bundle.getParcelable(Constants.START_POINT_LATNG);
        mDestinationSelectedLatng = bundle.getParcelable(Constants.DESTINATION_LATNG);

        /*
         Find views by Ids and assigned them to variables
         */
        mStartTextInputLayout = view.findViewById(R.id.text_input_layout_start);
        mStartEditText = view.findViewById(R.id.text_input_edit_text_start);
        mDestinationTextInputLayout = view.findViewById(R.id.text_input_layout_destination);
        mDestinationEditText = view.findViewById(R.id.text_input_edit_text_destination);
        mAvailableSeatsTextIputLayout = view.findViewById(R.id.text_input_layout_available_seats);
        mAvailableSeatsEditText = view.findViewById(R.id.text_input_edit_text_avalable_seats);
        MaterialButton mAddTripButton = view.findViewById(R.id.button_add);
        mDatePicker = view.findViewById(R.id.date_picker);

        /*
         Get instance from DatabaseRefernece
         */
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.TRIP_REF_PATH);
        mTrip = new Trip();

        /*
        mAddTripButton to add a new trip to firebase real time databass
         */
        mAddTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTripToFirebase();
            }
        });


    }

    /*
     addTripToFirebase method to add a new trip firebaseDatabse
     */
    private void addTripToFirebase() {

        mStartTextInputLayout.setError(null);
        mDestinationTextInputLayout.setError(null);
        mAvailableSeatsTextIputLayout.setError(null);

        if (TextUtils.isEmpty(mStartEditText.getText())) {
            mStartTextInputLayout.setError(getString(R.string.error_msg_position));
            return;
        }
        if (TextUtils.isEmpty(mDestinationEditText.getText())) {
            mDestinationTextInputLayout.setError(getString(R.string.error_msg_destination));
            return;
        }
        if (TextUtils.isEmpty(mAvailableSeatsEditText.getText())) {
            mAvailableSeatsTextIputLayout.setError(getString(R.string.eroor_msg_available_seats));
            return;
        }

        //Trip information added to firebase
        addNewTrip();
    }

    /*
 Add New trip inforamtion to firebase
 */
    public void addNewTrip() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.set(Calendar.MONTH, mDatePicker.getMonth());
        calendar.set(Calendar.YEAR, mDatePicker.getYear());

        mTrip.setStatus(Trip.Status.AVAILABLE.name());
        mTrip.setStartPortName(mStartEditText.getText().toString());
        mTrip.setDestinationSeaportName(mDestinationEditText.getText().toString());
        mTrip.setAvailableSeats(Integer.parseInt(mAvailableSeatsEditText.getText().toString()));
        mTrip.setDateTime(calendar.getTimeInMillis());

        mTrip.setStartLat(mStartPointSelectedLatng.latitude);
        mTrip.setStartLng(mStartPointSelectedLatng.longitude);

        mTrip.setDestinationLat(mDestinationSelectedLatng.latitude);
        mTrip.setDestinationLng(mDestinationSelectedLatng.longitude);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        final String date = simpleDateFormat.format(new Date(mTrip.getDateTime()));
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                if (trip == null) {
                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + date).setValue(mTrip).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), R.string.trip_added, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(getContext(), R.string.there_is_a_trip_in_this_time, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}