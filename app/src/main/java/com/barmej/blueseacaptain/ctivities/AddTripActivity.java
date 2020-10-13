package com.barmej.blueseacaptain.ctivities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddTripActivity extends AppCompatActivity {


    private TextInputLayout mPositionTextInputLayout;
    private TextInputLayout mDestinationTextInputLayout;
    private TextInputLayout mAvailableSeatsTextIputLayout;
    private TextInputEditText mPositionEditText;
    private TextInputEditText mDestinationEditText;
    private TextInputEditText mAvailableSeatsEditText;
    private MaterialButton mAddTripButton;
    private DatePicker mDatePicker;
    Trip mTrip;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        Toolbar toolbar = findViewById( R.id.toolBar_home);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled( true );


        setContentView( R.layout.activity_add_trip );
        mPositionTextInputLayout = findViewById( R.id.text_input_layout_position );
        mPositionEditText = findViewById( R.id.text_input_edit_text_position );
        mDestinationTextInputLayout = findViewById( R.id.text_input_layout_destination );
        mDestinationEditText = findViewById( R.id.text_input_edit_text_destination );
        mDestinationTextInputLayout = findViewById( R.id.text_input_layout_available_seats );
        mAvailableSeatsEditText = findViewById( R.id.text_input_edit_text_avalable_seats );
        mAddTripButton = findViewById( R.id.button_add );
        mDatePicker = findViewById( R.id.date_picker );

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

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.set( Calendar.MONTH, mDatePicker.getMonth() );
        calendar.set(Calendar.YEAR, mDatePicker.getYear());


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

        mTrip = new Trip();
        mTrip.setPositionSeaPortName( Objects.requireNonNull( mPositionEditText.getText() ).toString().trim());
        mTrip.setDestinationSeaportName( Objects.requireNonNull( mDestinationEditText.getText() ).toString().trim());
        mTrip.setAvailableSeats( Integer.parseInt( Objects.requireNonNull( mAvailableSeatsEditText.getText() ).toString().trim()));
        mTrip.setDateTime(calendar.getTimeInMillis());
        databaseReference.push().setValue(mTrip).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddTripActivity.this, R.string.trip_added , Toast.LENGTH_SHORT).show();
                finish();
            }
        } );


    }

    }
