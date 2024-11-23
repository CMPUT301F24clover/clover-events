package com.example.luckyevent.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.fragments.AdminHomePageFragment;
import com.example.luckyevent.fragments.AdminProfilesFragment;
import com.example.luckyevent.fragments.CreateEventFragment;
import com.example.luckyevent.fragments.DisplayOrganizerEventsFragment;
import com.example.luckyevent.fragments.HomePageFragment;
import com.example.luckyevent.fragments.OrganizerHomePageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMenuActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu_activity_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewAdmin);

        bottomNavigationView.setSelectedItemId(R.id.home_item);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminHomePageFragment())
                        .commit();

            } else if (item.getItemId() == R.id.profile_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminMenuFragment, new AdminProfilesFragment())
                        .commit();
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home_item);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminMenuFragment, new AdminHomePageFragment())
                .commit();
    }

}
