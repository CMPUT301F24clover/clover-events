package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.EventListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * DisplayOrganizerEventsFragment displays a list of events created by the current organizer.
 *
 * Features:
 * - Displays all events associated with the logged-in organizer
 * - Events are sorted by creation date
 * - Provides navigation to detailed view of each event
 * - Handles empty state display
 * @author Mmelve, Tola
 *
 * The fragment integrates with Firebase Firestore to fetch event data and
 * maintains a sorted list of events for display.
 */
public class DisplayOrganizerEventsFragment extends Fragment {
    // Lists to store event data
    private ArrayList<String> eventIdsList;        // Stores event IDs from user profile
    private ArrayList<EventListAdapter.EventItem> eventItems;  // Stores complete event objects
    private EventListAdapter listAdapter;          // Adapter for ListView display

    // Firebase references
    private FirebaseFirestore db;                  // Firestore database instance
    private DocumentReference orgDocRef;           // Reference to organizer's profile document

    // Navigation data
    private String selectedEventId;                // Currently selected event for navigation

    /**
     * Creates and initializes the fragment's view hierarchy.
     * Sets up the ListView, adapter, and Firebase connections.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_screen, container, false);

        // Initialize ListView and adapter
        setupListView(view);

        // Set toolbar title
        Toolbar toolbar = view.findViewById(R.id.topBar);
        toolbar.setTitle("My Events");

        // Initialize Firebase connection and load data
        initializeFirebase();

        // Show empty state if no events exist
        if (eventItems.isEmpty()) {
            TextView textView = view.findViewById(R.id.text_emptyList);
            textView.setText("No events");
        }

        return view;
    }

    /**
     * Sets up the ListView with its adapter and click listener
     * @param view The root view containing the ListView
     */
    private void setupListView(View view) {
        ListView listView = view.findViewById(R.id.customListView);
        eventItems = new ArrayList<>();
        listAdapter = new EventListAdapter(getContext(), eventItems);
        listView.setAdapter(listAdapter);

        // Setup click listener for navigation to event details
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedEventId = eventItems.get(position).getEventId();
            goToEventDetails();
        });
    }

    /**
     * Initializes Firebase connections and starts data loading
     * if user is authenticated
     */
    private void initializeFirebase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String organizerId = firebaseUser.getUid();
            db = FirebaseFirestore.getInstance();
            orgDocRef = db.collection("loginProfile").document(organizerId);

            getEventIdsList();
        }
    }

    /**
     * Retrieves the list of event IDs from the organizer's profile
     * Triggers event details fetch if successful
     */
    private void getEventIdsList() {
        orgDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    eventIdsList = (ArrayList<String>) snapshot.get("myEvents");
                    if (eventIdsList != null) {
                        getEventDetails();
                    } else {
                        Toast.makeText(getContext(), "myEvents list does not exist.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Fetches detailed information for each event in the eventIdsList
     * Updates the UI with sorted event items as they are retrieved
     *
     * Note: Events are sorted by creation timestamp to maintain consistent ordering
     */
    private void getEventDetails() {
        eventItems.clear();
        for (String id : eventIdsList) {
            db.collection("events").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Extract event details
                        String eventName = snapshot.getString("eventName");
                        Long createdAt = snapshot.getLong("createdAt");
                        if (createdAt == null) {
                            createdAt = System.currentTimeMillis(); // Fallback timestamp
                        }

                        // Create and add new event item
                        EventListAdapter.EventItem eventItem =
                                new EventListAdapter.EventItem(id, eventName, createdAt);
                        eventItems.add(eventItem);

                        // Sort events by creation time and update UI
                        Collections.sort(eventItems);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Navigates to the EventDetailsFragment for the selected event
     * Passes the event ID through a bundle for the destination fragment
     */
    private void goToEventDetails() {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", selectedEventId);

        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
        eventDetailsFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, eventDetailsFragment)
                .addToBackStack(null)
                .commit();
    }
}