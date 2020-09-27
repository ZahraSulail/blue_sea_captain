package com.barmej.blueseacaptain.ctivities;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.annotation.RequiresApi;
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
    Trip mTrip;
    DatabaseReference databaseReference;
    long dateTime;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_trip );

        mPositionTextInputLayout = findViewById( R.id.text_input_position);
        mDestinationTextInputLayout = findViewById( R.id.text_input_destination );
        mAvailableSeatsTextIputLayout = findViewById( R.id.text_input_available_seats );
        mPositionEditText = findViewById( R.id.edit_text_position );
        mDestinationEditText = findViewById( R.id.edit_text_destination );
        mAvailableSeatsEditText = findViewById( R.id.edit_text_available_seats );
        mAddTripButton = findViewById( R.id.button_add_a_trip);
        mDatePicker = findViewById( R.id.date_picker );

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trip_Details");
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
        //dateTime = Long.parseLong( mDatePicker.getMonth() + "/" + mDatePicker.getDayOfMonth() + "/" + mDatePicker.getYear());


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
       // mTrip.setDateTime(dateTime);
        databaseReference.push().setValue(mTrip);


    }





}



