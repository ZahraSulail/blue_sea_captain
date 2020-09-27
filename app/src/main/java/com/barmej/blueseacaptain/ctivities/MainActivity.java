package com.barmej.blueseacaptain.ctivities;

import android.os.Bundle;

import com.barmej.blueseacaptain.R;
import com.barmej.blueseacaptain.domain.entity.Captain;
import com.barmej.blueseacaptain.fragments.TripDetalsFragment;
import com.barmej.blueseacaptain.fragments.TripListFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    Captain captain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        captain = new Captain();
        setContentView( R.layout.activity_main);

        Toolbar toolbar = findViewById( R.id.toolBar_home );
        setSupportActionBar( toolbar );
        ViewPager viewPager = findViewById( R.id.view_pager_home );
        TabLayout tabLayout = findViewById( R.id.tab_layout_home );

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter( getSupportFragmentManager());
        pagerAdapter.addFragment( new TripListFragment());
        pagerAdapter.addFragment( new TripDetalsFragment());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);

    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super( fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT );
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get( position );
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.trips_list);
                case 1:
                    return getString(R.string.trip_details);
                default:
                    return null;
            }
        }

        public void addFragment(Fragment fragment){
            fragmentList.add( fragment );
        }
    }
}
