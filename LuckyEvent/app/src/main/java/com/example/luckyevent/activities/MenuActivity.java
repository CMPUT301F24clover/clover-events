package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.fragments.DisplayNotificationsFragment;
import com.example.luckyevent.fragments.DisplayWaitingListsFragment;
import com.example.luckyevent.fragments.HomePageFragment;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Displays the fragments needed for the user to sign up for events and receive notifications. It contains
 * the bottom navigation bar needed to navigate between fragments and activities. These fragments and
 * activities are the entrant home page, entrant profile section, scan qr code section  and the notifications page
 *
 * @author Amna, Mmelve, Seyi, Divij
 * @see com.example.luckyevent.UserSession
 * @version 2
 * @since 1
 */
public class MenuActivity extends AppCompatActivity implements HomePageFragment.OnNavigateListener {

    BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewEntrant);
        db = FirebaseFirestore.getInstance();

        /*
         * When an element of the this bottomNavigationView is clicked, it navigates to it corresponding
         * fragment
         */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new HomePageFragment())
                        .commit();
                return true;
            }
            else if (item.getItemId() == R.id.profile_item) {
                findProfile();
                return true;
            }

            else if (item.getItemId() == R.id.camera_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new ScanQrFragment())
                        .commit();
                return true;
            }

            else if (item.getItemId() == R.id.waiting_list_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new DisplayWaitingListsFragment())
                        .commit();
                return true;
            }

            else if (item.getItemId() == R.id.notification_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MenuFragment, new DisplayNotificationsFragment())
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
    /**
     * Uses the user id to find the profile id of a user. This profile id is passed onto the ViewProfileActivity
     * before it is navigated to
     */
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
                                // Initialize notification preference in UserSession
                                Boolean notificationDisabled = doc.getBoolean("notificationsDisabled");
                                UserSession.getInstance().setNotificationDisabled(
                                        notificationDisabled!= null ? notificationDisabled : false
                                );
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

    /**
     * Navigates to the ScanQrFragment when the camera_item is clicked
     */
    @Override
    public void onNavigateToScanQr() {
        bottomNavigationView.setSelectedItemId(R.id.camera_item);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MenuFragment, new ScanQrFragment())
                .addToBackStack(null)
                .commit();
    }
    /**
     * Navigates to the DisplayWaitingListsFragment when the waiting_list_item is clicked
     */
    @Override
    public void onNavigateToWaitingList() {
        bottomNavigationView.setSelectedItemId(R.id.waiting_list_item);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MenuFragment, new DisplayWaitingListsFragment())
                .addToBackStack(null)
                .commit();
    }
    /**
     * Navigates to the DisplayNotificationsFragment when the notification_item is clicked
     */
    @Override
    public void onNavigateToNotifications() {
        bottomNavigationView.setSelectedItemId(R.id.notification_item);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MenuFragment, new DisplayNotificationsFragment())
                .addToBackStack(null)
                .commit();
    }
    /**
     * Navigates to the ViewProfileActivity when the profile_item is clicked
     */
    @Override
    public void onNavigateToProfile() {
        findProfile();

    }


}
