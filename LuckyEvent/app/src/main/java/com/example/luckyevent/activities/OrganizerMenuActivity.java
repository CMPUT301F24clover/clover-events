package com.example.luckyevent.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import com.example.luckyevent.fragments.CreateEventFragment;
import com.example.luckyevent.fragments.OrganizerHomePageFragment;
//import com.example.luckyevent.fragments.CreateEventFragment;
//import com.example.luckyevent.fragments.MyEventsFragment;
//import com.example.luckyevent.fragments.MyFacilityFragment;
//import com.example.luckyevent.fragments.EventSettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizerMenuActivity extends AppCompatActivity implements OrganizerHomePageFragment.OnOrganizerNavigateListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_menu_activity_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewOrganizer);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new OrganizerHomePageFragment())
                        .commit();
                return true;
            }
            /*
            else if (item.getItemId() == R.id.my_events_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new MyEventsFragment())
                        .commit();
                return true;
            }

             */


            return false;
        });

        // Load the dashboard fragment by default
        bottomNavigationView.setSelectedItemId(R.id.home_item);
    }

    // Implement OnOrganizerNavigateListener methods
    @Override
    public void onNavigateToCreateEvent() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, new CreateEventFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNavigateToMyEvents() {
        bottomNavigationView.setSelectedItemId(R.id.events_item);
    }

    @Override
    public void onNavigateToMyFacility() {
        bottomNavigationView.setSelectedItemId(R.id.profile_item);
    }

    @Override
    public void onNavigateToEventSettings() {
        bottomNavigationView.setSelectedItemId(R.id.settings_item);
    }
}