package com.example.luckyevent.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.OrganizerSession;
import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.example.luckyevent.fragments.CreateEventFragment;
import com.example.luckyevent.fragments.CreateFacilityFragment;
import com.example.luckyevent.fragments.DisplayFacilityFragment;
import com.example.luckyevent.fragments.DisplayNotificationsFragment;
import com.example.luckyevent.fragments.DisplayOrganizerEventsFragment;
import com.example.luckyevent.fragments.EventSettingsFragment;
import com.example.luckyevent.fragments.OrganizerHomePageFragment;
//import com.example.luckyevent.fragments.CreateEventFragment;
//import com.example.luckyevent.fragments.MyEventsFragment;
//import com.example.luckyevent.fragments.MyFacilityFragment;
//import com.example.luckyevent.fragments.EventSettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 *Displays the fragments needed for the organizer to interact with their events and the entrants. It contains
 * the bottom navigation bar needed to navigate between fragments and activities. These fragments and
 * activities are the organizer home page, events owned by the organizer, events settings and the create profile
 * section
 *
 * @author Tola, Aagam
 * @see OrganizerSession
 * @version 1
 * @since 1
 */
public class OrganizerMenuActivity extends AppCompatActivity implements OrganizerHomePageFragment.OnOrganizerNavigateListener {

    BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_menu_activity_layout);

        firestore = FirebaseFirestore.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigationViewOrganizer);

        /**
         *When an element of the bottomNavigationView is clicked, it navigates to it corresponding
         * fragment
         */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new OrganizerHomePageFragment())
                        .commit();
                return true;
            }

            else if (item.getItemId() == R.id.events_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new DisplayOrganizerEventsFragment())
                        .commit();
                return true;
            }

            else if (item.getItemId() == R.id.profile_item) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                DocumentReference facilityIDRef = firestore.collection("loginProfile").document(userID);
                facilityIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.contains("myFacility")) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.OrganizerMenuFragment, new DisplayFacilityFragment())
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.OrganizerMenuFragment, new CreateFacilityFragment())
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }
                    }
                });
            }

            else if (item.getItemId() == R.id.settings_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new EventSettingsFragment())
                        .commit();
                return true;
            }
            return false;
        });

        // Load the dashboard fragment by default
        bottomNavigationView.setSelectedItemId(R.id.home_item);
    }

    /**
     * This method navigates the organizer to create a new event page upon clicking
     * the create event card
     */
    @Override
    public void onNavigateToCreateEvent() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, new CreateEventFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * This method navigates the organizer to view their active events upon clicking
     * the My events card
     */
    @Override
    public void onNavigateToMyEvents() {
        bottomNavigationView.setSelectedItemId(R.id.events_item);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, new DisplayOrganizerEventsFragment())
                .commit();
    }

    /**
     * This method navigates the organizer to view their facility profile upon clicking
     * the My Facility card
     */
    @Override
    public void onNavigateToMyFacility() {
        bottomNavigationView.setSelectedItemId(R.id.profile_item);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        DocumentReference facilityIDRef = firestore.collection("loginProfile").document(userID);
        facilityIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.contains("myFacility")) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.OrganizerMenuFragment, new DisplayFacilityFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getApplicationContext(), "No existing facility found. Please Create a new Facility.", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.OrganizerMenuFragment, new CreateFacilityFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
    }

    @Override
    public void onNavigateToEventSettings() {
        bottomNavigationView.setSelectedItemId(R.id.settings_item);
    }
}