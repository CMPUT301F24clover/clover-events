package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.LotteryService;
import com.example.luckyevent.QRDownloadService;
import com.example.luckyevent.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * EventDetailsFragment displays comprehensive information about a specific event and provides
 * access to various event management features.
 *
 * Key Features:
 * - Displays event details (title, description, date, capacity)
 * - Manages different entrant lists (waiting, chosen, enrolled, cancelled)
 * - Handles QR code generation and download
 * - Controls entrant sampling/lottery process
 * - Provides access to event poster management
 *
 * The fragment serves as a central hub for event organizers to manage all aspects
 * of their event and its participants.
 *
 * @author Amna, Mmelve, Seyi, Tola
 * @version 1
 * @since 1
 */
public class EventDetailsFragment extends Fragment {
    private static final String TAG = "EventDetailsFragment";
    private FirebaseFirestore db;
    private String eventId;

    /**
     * Creates and initializes the fragment's view hierarchy
     * Handles event data loading and UI setup
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_event_details, container, false);
        db = FirebaseFirestore.getInstance();

        // Validate arguments and event ID
        if (!validateArguments()) {
            return view;
        }

        Log.d(TAG, "Loading event details for eventId: " + eventId);

        // Load and display event details from Firestore
        loadEventDetails(view);

        // Setup click listeners for various UI elements
        setupClickListeners(view, eventId);

        return view;
    }

    /**
     * Validates fragment arguments and event ID
     * @return true if arguments are valid, false otherwise
     */
    private boolean validateArguments() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e(TAG, "Arguments bundle is null");
            Toast.makeText(getContext(), "Error: Event details not available", Toast.LENGTH_SHORT).show();
            return false;
        }

        eventId = arguments.getString("eventId");
        if (eventId == null || eventId.isEmpty()) {
            Log.e(TAG, "Event ID is null or empty");
            Toast.makeText(getContext(), "Error: Invalid event ID", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Loads event details from Firestore and updates the UI
     * @param view The root view of the fragment
     */
    private void loadEventDetails(View view) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        updateEventUI(view, document);
                    } else {
                        Log.e(TAG, "Document does not exist for eventId: " + eventId);
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting document for eventId: " + eventId, e);
                    Toast.makeText(getContext(), "Error loading event details", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the UI with event details from Firestore
     * Handles null values and potential exceptions
     *
     * @param view The root view containing UI elements
     * @param document Firestore document containing event data
     */
    private void updateEventUI(View view, DocumentSnapshot document) {
        try {
            // Initialize UI elements
            TextView eventTitle = view.findViewById(R.id.textView_eventTitle2);
            TextView eventDescription = view.findViewById(R.id.textView_description2);
            TextView eventDate = view.findViewById(R.id.textView_dateTime2);
            TextView eventSize = view.findViewById(R.id.textView_capacity2);

            // Extract data from document
            String name = document.getString("eventName");
            String description = document.getString("description");
            String date = document.getString("date");
            String waitListSize = document.getString("WaitListSize");

            // Update UI with data (using default values if null)
            eventTitle.setText(name != null ? name : "N/A");
            eventDescription.setText(description != null ? description : "No description available");
            eventDate.setText(date != null ? date : "Date not set");
            eventSize.setText((waitListSize != null ? waitListSize : "0") + " Entrants in Waiting List:");
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
            Toast.makeText(getContext(), "Error displaying event details", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up click listeners for various interactive elements
     * Includes QR code generation, sampling, and poster management
     *
     * @param view The root view containing clickable elements
     * @param eventId The ID of the current event
     */
    private void setupClickListeners(View view, String eventId) {
        // QR Code Generation
        TextView qrCodeText = view.findViewById(R.id.event_qr_link);
        qrCodeText.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), QRDownloadService.class);
                intent.putExtra("eventId", eventId);
                getContext().startService(intent);
            }
        });

        // Entrant Sampling/Lottery
        TextView sampleEntrants = view.findViewById(R.id.sample_entrant);
        sampleEntrants.setOnClickListener(v -> {
            db.collection("events").document(eventId).collection("chosenEntrants").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        if (getActivity() != null) {
                            startLotteryProcess(sampleEntrants);
                        }
                    } else {
                        Toast.makeText(getContext(), "Lottery has already been conducted.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        // Event Poster Management
        TextView eventPoster = view.findViewById(R.id.event_poster_link);
        eventPoster.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            goToPoster(bundle);
        });

        // Setup list management buttons
        setupButtons(view, eventId);
    }

    /**
     * Initiates the lottery process for selecting entrants
     * @param sampleEntrants TextView to hide after lottery completion
     */
    private void startLotteryProcess(TextView sampleEntrants) {
        Toast.makeText(getActivity(), "Conducting lottery...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LotteryService.class);
        intent.putExtra("eventId", eventId);
        getContext().startService(intent);
        Toast.makeText(getActivity(), "Click on 'View List of Chosen Entrants' for results.", Toast.LENGTH_SHORT).show();
        sampleEntrants.setVisibility(View.GONE);
    }

    /**
     * Sets up click listeners for all list management buttons
     * Includes waiting, chosen, enrolled, and cancelled entrants
     *
     * @param view The root view containing the buttons
     * @param eventId The ID of the current event
     */
    private void setupButtons(View view, String eventId) {
        // Configure each button with appropriate parameters
        setupListButton(view, R.id.waiting_list_button, "Waiting List", eventId, null, true);
        setupListButton(view, R.id.chosen_entrant_button, "List of Chosen Entrants", eventId, null, false);
        setupListButton(view, R.id.enrolled_entrant_button, "List of Enrolled Entrants", eventId, "Enrolled", false);
        setupListButton(view, R.id.cancelled_entrant_button, "List of Cancelled Entrants", eventId, "Cancelled", false);
    }

    /**
     * Helper method to setup individual list management buttons
     */
    private void setupListButton(View view, int buttonId, String title, String eventId,
                                 String status, boolean isWaitingList) {
        Button button = view.findViewById(buttonId);
        button.setOnClickListener(v -> {
            Fragment fragment = isWaitingList ?
                    new DisplayEventWaitingListFragment() :
                    new DisplayEntrantsFragment();

            Bundle bundle = new Bundle();
            bundle.putString("screenTitle", title);
            bundle.putString("eventId", eventId);
            if (status != null) {
                bundle.putString("invitationStatus", status);
            }

            goToList(bundle, fragment);
        });
    }

    /**
     * Navigates to the appropriate list display fragment
     * @param bundle Bundle containing navigation parameters
     * @param displayFragment Target fragment to display
     */
    private void goToList(Bundle bundle, Fragment displayFragment) {
        displayFragment.setArguments(bundle);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, displayFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Navigates to the event poster management fragment
     * @param bundle Bundle containing event information
     */
    private void goToPoster(Bundle bundle) {
        EventPosterFragment eventPosterFragment = new EventPosterFragment();
        eventPosterFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, eventPosterFragment)
                .addToBackStack(null)
                .commit();
    }
}
