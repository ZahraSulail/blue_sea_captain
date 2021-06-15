package com.barmej.blueseacaptain.ctivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.fragments.TripListFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /*
     TripListFragment Object
     */
    TripListFragment mTripListFragment;

    /*
     FirebaseAuth object to get user authentication for login or logout from firebase
     */
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FragmentManager to find Fragment by id
        FragmentManager manager = getSupportFragmentManager();
        mTripListFragment = new TripListFragment();
        manager.beginTransaction().add(R.id.layout_main, mTripListFragment).commit();

        /*
         Get instance from FoirebaseAuth
         */
        mAuth = FirebaseAuth.getInstance();
    }

    /*
     Menu allows user to logout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_log_out) {
            if (mAuth.getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}


