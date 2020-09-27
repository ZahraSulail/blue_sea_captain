package com.barmej.blueseacaptain.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.Trip;
import com.barmej.blueseacaptain.fragments.TripListFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripItemsAdapter extends RecyclerView.Adapter<TripItemsAdapter.TripViewHolder> {

    private List<Trip> mTripList;

    public TripItemsAdapter(List<Trip> tripList, TripListFragment tripListFragment){
        this.mTripList = tripList;
    }
    @NonNull
    @Override
    public TripItemsAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.item_trip, parent, false );
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripItemsAdapter.TripViewHolder holder, int position) {
         holder.bind( mTripList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTripList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {

        TextView mDataTextView;
        TextView mPositinTextView;
        TextView mDestinationTextView;
        TextView mAvailableSeatsTextView;
        TextView mBookedSeatsTextView;
        Trip trip;
        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            mDataTextView = itemView.findViewById( R.id.text_view_date );
            mPositinTextView = itemView.findViewById( R.id.text_view_position);
            mDestinationTextView = itemView.findViewById( R.id.text_view_destination );
            mAvailableSeatsTextView = itemView.findViewById( R.id.text_view_available_seats );
            mBookedSeatsTextView = itemView.findViewById( R.id.text_view_booked_seats );
        }

        public void bind(Trip trip){
            mDataTextView.setText( trip.getFormattedDate());
            mPositinTextView.setText(trip.getPositionSeaPortName());
            mDestinationTextView.setText( trip.getDestinationSeaportName());
            mAvailableSeatsTextView.setText( trip.getAvailableSeats());
            mBookedSeatsTextView.setText( trip.getBookedSeats());
        }
    }
}
