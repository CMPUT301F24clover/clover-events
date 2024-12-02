package com.example.luckyevent.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.activities.BrowseImagesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This activity displays the fragments needed by the admin to remove profiles, remove event posters and qr codes, remove facilities,
 * and remove images that are owned by organizers.
 * Navigation is carried out via a bottom navigation bar.
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
            // Navigate to the events page when the event item is clicked
            else if (item.getItemId() == R.id.event_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminBrowseEvents())
                        .commit();
            }
            // Navigate to the facilities page when the facility item is clicked
            else if (item.getItemId() == R.id.facility_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminBrowseFacilities())
                        .commit();
            }
            // Navigate to the browse images page when the images item is clicked
            else if (item.getItemId() == R.id.images_item) {
                // Start the BrowseImagesActivity
                Intent intent = new Intent(AdminMenuActivity.this, BrowseImagesActivity.class);
                startActivity(intent);
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
