package com.barmej.blueseacaptain.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.inteerface.OnTripClickListiner;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripItemsAdapter extends RecyclerView.Adapter<TripItemsAdapter.TripViewHolder> {
    /*
      List of items
     */
    private List<Trip> mTripsList;

    /*
     An interface to use it in TripListFragment
     */
    private OnTripClickListiner mTripClickListiner;

    /*
     Costructor
     */
     public TripItemsAdapter(List<Trip> tripList, OnTripClickListiner tripClickListiner){
         this.mTripsList = tripList;
         this.mTripClickListiner = tripClickListiner;
     }


    @NonNull
    @Override
    public TripItemsAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.item_trip, parent, false );
         return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripItemsAdapter.TripViewHolder holder, int position) {
         holder.bind( mTripsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTripsList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {

         /*
          Define views required to display trip items
          */
         TextView mDateTextview;
         TextView mDataTextView;
         TextView mPositionTextView;
         TextView mDestinationTextView;
         TextView mAvailableSeatsTextView;
         TextView mBookedSeatsTextView;
         Trip trip;
        /*
          View holder constractor
         */
        public TripViewHolder(@NonNull View itemView) {
            super( itemView );
            // Find view by id's and join them to the variables
            mDateTextview = itemView.findViewById( R.id.item_text_view_date);
            mPositionTextView = itemView.findViewById( R.id.item_text_view_position);
            mDestinationTextView = itemView.findViewById( R.id.item_text_view_destination);
            mAvailableSeatsTextView = itemView.findViewById( R.id.item_text_view_avalable_seats);
            mBookedSeatsTextView = itemView.findViewById( R.id.item_text_view_booked_seats);

            //Set onTripClick interface
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTripClickListiner.onTripClick(trip);
                }
            } );
        }
        public void bind(Trip trip){
            mDataTextView.setText( trip.getFormattedDate());
            mPositionTextView.setText(trip.getPositionSeaPortName());
            mDestinationTextView.setText( trip.getDestinationSeaportName());
            mAvailableSeatsTextView.setText( String.valueOf( trip.getAvailableSeats()));
            mBookedSeatsTextView.setText( String.valueOf( trip.getBookedSeats()));
        }
    }
}
