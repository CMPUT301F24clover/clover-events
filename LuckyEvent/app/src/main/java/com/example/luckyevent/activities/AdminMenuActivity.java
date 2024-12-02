package com.example.luckyevent.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.fragments.AdminBrowseEvents;
import com.example.luckyevent.fragments.AdminBrowseFacilities;
import com.example.luckyevent.fragments.AdminHomePageFragment;
import com.example.luckyevent.fragments.AdminProfilesFragment;
import com.example.luckyevent.fragments.CreateEventFragment;
import com.example.luckyevent.fragments.DisplayOrganizerEventsFragment;
import com.example.luckyevent.fragments.HomePageFragment;
import com.example.luckyevent.fragments.OrganizerHomePageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This activity displays the fragments needed by the admin to remove profiles, remove event posters and qr codes, remove facilities,
 * and remove images that are owned by organizers.
 * Navigation is carried out via a bottom navigation bar
 *
 * @author Seyi
 * @version 1
 * @since 1
 */
public class AdminMenuActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu_activity_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewAdmin);

        bottomNavigationView.setSelectedItemId(R.id.home_item);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Navigate to the admin home page screen when the home item is clicked
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminHomePageFragment())
                        .commit();

            }
            // Navigate to the admin profile screen when the profile item is clicked
            else if (item.getItemId() == R.id.profile_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminProfilesFragment())
                        .commit();
            }
            else if (item.getItemId() == R.id.event_item){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminBrowseEvents())
                        .commit();
            }
            else if (item.getItemId() == R.id.facility_item){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminBrowseFacilities())
                        .commit();
            }
            return true;
        });

        // Load in the admin menu fragment by default
        bottomNavigationView.setSelectedItemId(R.id.home_item);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminMenuFragment, new AdminHomePageFragment())
                .commit();
    }

}
