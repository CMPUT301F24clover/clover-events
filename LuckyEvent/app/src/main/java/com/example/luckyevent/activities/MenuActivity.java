package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import com.example.luckyevent.fragments.HomePageFragment;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.example.luckyevent.fragments.TestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity implements HomePageFragment.OnNavigateListener {

    BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        db = FirebaseFirestore.getInstance();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new HomePageFragment())
                        .commit();
                return true;
            }
            else if(item.getItemId() == R.id.profile_item){
                findProfile();
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

    private void findProfile() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            String userID = firebaseUser.getUid();

            db.collection("loginProfile")
                    .document(userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()){
                                Boolean hasUserProfile = doc.getBoolean("hasUserProfile");
                                if (Boolean.TRUE.equals(hasUserProfile)){
                                    Intent intent = new Intent(MenuActivity.this,ViewProfileActivity.class);
                                    intent.putExtra("profileID",userID);
                                    startActivity(intent);
                                } else{
                                    Toast.makeText(MenuActivity.this,"No profile found, please register your profile",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MenuActivity.this,RegisterProfileActivity.class);
                                    intent.putExtra("profileID",userID);
                                    startActivity(intent);
                                }
                            } else{
                                Toast.makeText(MenuActivity.this,"Document does not exist",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MenuActivity.this,"Unable to find user",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(MenuActivity.this,"No user currently logged in",Toast.LENGTH_SHORT).show();
        }
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
