package com.example.luckyevent.organizer.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.shared.Event;
import com.example.luckyevent.shared.EventListAdapter;
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
 * Features:
 * - Displays all events associated with the logged-in organizer
 * - Events are sorted by creation date
 * - Provides navigation to detailed view of each event
 * - Handles empty state display
 * The fragment integrates with Firebase Firestore to fetch event data and
 * maintains a sorted list of events for display.
 *
 * @author Mmelve, Tola
 * @see Event
 * @see EventListAdapter
 * @version 2
 * @since 1
 */
public class DisplayOrganizerEventsFragment extends Fragment {
    // Lists to store event data
    private ArrayList<String> eventIdsList;        // Stores event IDs from user profile
    private ArrayList<Event> events;               // Stores complete event objects
    private EventListAdapter listAdapter;          // Adapter for ListView display

    // UI elements
    private TextView textView;

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

        // Set Toolbar title
        Toolbar toolbar = view.findViewById(R.id.topBar);
        toolbar.setTitle("My Events");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Enable the back button
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        textView = view.findViewById(R.id.text_emptyList);
        textView.setVisibility(View.GONE);

        // Initialize Firebase connection and load data
        initializeFirebase();

        return view;
    }

    /**
     * Sets up the ListView with its adapter and click listener
     * @param view The root view containing the ListView
     */
    private void setupListView(View view) {
        ListView listView = view.findViewById(R.id.customListView);
        events = new ArrayList<>();
        listAdapter = new EventListAdapter(getContext(), events);
        listView.setAdapter(listAdapter);

        // Setup click listener for navigation to event details
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedEventId = events.get(position).getEventId();
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
                    if (eventIdsList != null && !eventIdsList.isEmpty()) {
                        getEventDetails();
                    } else {
                        // Show empty state if no events exist
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("No events");
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
        events.clear();
        for (String id : eventIdsList) {
            db.collection("events").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Extract event details
                        String eventName = snapshot.getString("eventName");
                        Long createdAt = snapshot.getLong("createdAt");
                        String eventDateTime = snapshot.getString("dateAndTime");
                        String eventDesc = snapshot.getString("description");
                        if (createdAt == null) {
                            createdAt = System.currentTimeMillis(); // Fallback timestamp
                        }

                        // Create and add new event
                        Event event = new Event(id, eventName, createdAt, eventDateTime, eventDesc);
                        events.add(event);

                        // Sort events by creation time and update UI
                        Collections.sort(events);
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