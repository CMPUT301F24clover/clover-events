package com.example.luckyevent.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import com.example.luckyevent.fragments.HomePageFragment;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.example.luckyevent.fragments.TestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity implements HomePageFragment.OnNavigateListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new HomePageFragment())
                        .commit();
                return true;
            }
            else if(item.getItemId() == R.id.profile_item){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new TestFragment())
                        .commit();
                return true;
            }

            else if(item.getItemId() == R.id.camera_item){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new ScanQrFragment())
                        .commit();
                return true;
            }

            return false;
        });

        // Load the home fragment by default
        bottomNavigationView.setSelectedItemId(R.id.home_item);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.MenuFragment, new HomePageFragment())
                .commit();
    }

    @Override
    public void onNavigateToScanQr() {
        bottomNavigationView.setSelectedItemId(R.id.camera_item);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MenuFragment, new ScanQrFragment())
                .addToBackStack(null)
                .commit();
    }
}
