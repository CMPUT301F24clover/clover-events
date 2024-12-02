package com.example.luckyevent.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.organizer.displayHomePage.OrganizerSession;
import com.example.luckyevent.R;
import com.example.luckyevent.organizer.eventCreation.CreateEventFragment;
import com.example.luckyevent.organizer.facility.CreateFacilityFragment;
import com.example.luckyevent.fragments.DisplayFacilityFragment;
import com.example.luckyevent.organizer.eventDetails.DisplayOrganizerEventsFragment;
import com.example.luckyevent.organizer.eventSettings.EventSettingsFragment;
import com.example.luckyevent.organizer.displayHomePage.OrganizerHomePageFragment;
//import com.example.luckyevent.organizer.eventCreation.CreateEventFragment;
//import com.example.luckyevent.fragments.MyEventsFragment;
//import com.example.luckyevent.fragments.MyFacilityFragment;
//import com.example.luckyevent.organizer.eventSettings.EventSettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                        .addToBackStack(null)
                        .commit();
                return true;
            }

            else if (item.getItemId() == R.id.events_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new DisplayOrganizerEventsFragment())
                        .addToBackStack(null)
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
                return true;
            }

            else if (item.getItemId() == R.id.settings_item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.OrganizerMenuFragment, new EventSettingsFragment())
                        .addToBackStack(null)
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
    }

    /**
     * This method navigates the organizer to view their facility profile upon clicking
     * the My Facility card
     */
    @Override
    public void onNavigateToMyFacility() {
        bottomNavigationView.setSelectedItemId(R.id.profile_item);
    }

    @Override
    public void onNavigateToEventSettings() {
        bottomNavigationView.setSelectedItemId(R.id.settings_item);
    }
}