package com.barmej.blueseacaptain.ctivities;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.barmej.blueseacaptain.Constants;
import com.barmej.blueseacaptain.R;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddTripActivity extends AppCompatActivity {

    private TextInputLayout mPositionTextInputLayout;
    private TextInputLayout mDestinationTextInputLayout;
    private TextInputLayout mAvailableSeatsTextIputLayout;
    private TextInputEditText mPositionEditText;
    private TextInputEditText mDestinationEditText;
    private TextInputEditText mAvailableSeatsEditText;
    private MaterialButton mAddTripButton;
    private DatePicker mDatePicker;
    private Trip mTrip;
    private DatabaseReference databaseReference;
    private LatLng mStartPointSelectedLatng;
    private LatLng mDestinationSelectedLatng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_add_trip );

        mStartPointSelectedLatng = getIntent().getParcelableExtra( Constants.START_POINT_LATNG);
        mDestinationSelectedLatng = getIntent().getParcelableExtra(Constants.DESTINATION_LATNG);

        System.out.println(mStartPointSelectedLatng.latitude + "==========" + mStartPointSelectedLatng.longitude);
        mPositionTextInputLayout = findViewById( R.id.text_input_layout_position );
        mPositionEditText = findViewById( R.id.text_input_edit_text_position );
        mDestinationTextInputLayout = findViewById( R.id.text_input_layout_destination );
        mDestinationEditText = findViewById( R.id.text_input_edit_text_destination );
        mAvailableSeatsTextIputLayout = findViewById( R.id.text_input_layout_available_seats );
        mAvailableSeatsEditText = findViewById( R.id.text_input_edit_text_avalable_seats );
        mAddTripButton = findViewById( R.id.button_add );
        mDatePicker = findViewById( R.id.date_picker );

        databaseReference = FirebaseDatabase.getInstance().getReference().child( "Trip_Details" );

        mTrip = new Trip();

        mAddTripButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTripToFirebase();
            }
        } );

    }

    private void addTripToFirebase(){

        mPositionTextInputLayout.setError( null );
        mDestinationTextInputLayout.setError( null );
        mAvailableSeatsTextIputLayout.setError( null );

        if(TextUtils.isEmpty( mPositionEditText.getText())){
            mPositionTextInputLayout.setError( getString( R.string.error_msg_position));
            return;
        }
        if(TextUtils.isEmpty(mDestinationEditText.getText())){
            mDestinationTextInputLayout.setError( getString( R.string.error_msg_destination));
            return;
        }
        if(TextUtils.isEmpty( mAvailableSeatsEditText.getText())){
            mAvailableSeatsTextIputLayout.setError( getString( R.string.eroor_msg_available_seats));
            return;
        }

        addNewTrip();
    }

/*
 Add New trip inforamtion to firebase
 */
    public void addNewTrip() {

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.set( Calendar.MONTH, mDatePicker.getMonth() );
        calendar.set(Calendar.YEAR, mDatePicker.getYear());

        mTrip.setStatus( Trip.Status.AVAILABLE.name());
        mTrip.setStartPortName(mPositionEditText.getText().toString());
        mTrip.setDestinationSeaportName( mDestinationEditText.getText().toString());
        mTrip.setAvailableSeats( Integer.parseInt( mAvailableSeatsEditText.getText().toString()));
        mTrip.setDateTime( calendar.getTimeInMillis());

        mTrip.setStartLat( mStartPointSelectedLatng.latitude );
        mTrip.setStartLng( mStartPointSelectedLatng.longitude );

        mTrip.setDestinationLat( mDestinationSelectedLatng.latitude );
        mTrip.setDestinationLng( mDestinationSelectedLatng.longitude );

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        final String date = simpleDateFormat.format( new Date(mTrip.getDateTime()) );
        databaseReference.child( FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + date ).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                if(trip == null) {
                    databaseReference.child( FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + date ).setValue( mTrip ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(AddTripActivity.this, R.string.trip_added, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } );
                } else {
                    Toast.makeText( AddTripActivity.this, R.string.there_is_a_trip_in_this_time, Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}
