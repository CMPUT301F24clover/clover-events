package com.example.luckyevent.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.luckyevent.QRDownloadService;
import com.example.luckyevent.R;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * EntrantEventDetailsActivity handles the display and interaction with event details for entrants.
 * This activity allows users to:
 * - View complete event details including title, date, location, capacity, and description
 * - Join event waiting lists
 * - Handle location permissions and geolocation requirements for events
 * - Manage UI state based on waiting list status
 *
 * The activity integrates with Firebase Firestore for data persistence and
 * Google's location services for geolocation features.
 *
 * @author Aagam, Tola, Amna
 * @see ScanQrFragment
 * @version 2
 * @since 1
 */
public class EntrantEventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // UI Elements
    private TextView eventTitleView;
    private TextView dateTimeView;
    private TextView locationView;
    private TextView capacityView;
    private TextView descriptionView;
    private MaterialButton joinButton;

    // Firebase and Location Services
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    // Event Data
    private String eventId;
    private long currentWaitList;      // Current number of people in waiting list
    private long waitListSize;         // Maximum waiting list capacity
    private boolean geolocationRequired;  // Whether event requires location tracking

    /**
     * Initializes the activity, sets up UI components, and loads event details.
     * Handles error cases where event ID is missing.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_screen);

        // Initialize Firebase and location services
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeViews();

        // Get and validate event ID from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Error: Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEventDetails();
    }

    /**
     * Initializes all UI components and sets up click listeners
     */
    private void initializeViews() {
        eventTitleView = findViewById(R.id.textView_eventTitle);
        dateTimeView = findViewById(R.id.textView_dateTime);
        locationView = findViewById(R.id.textView_location);
        capacityView = findViewById(R.id.textView_capacity);
        descriptionView = findViewById(R.id.textView_description);
        joinButton = findViewById(R.id.button_join);

        joinButton.setOnClickListener(v -> handleJoinButtonClick());
    }

    /**
     * Fetches event details from Firestore and handles potential errors
     */
    private void loadEventDetails() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(this::updateUI)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event details", e);
                    Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    /**
     * Updates the UI with event details from Firestore
     * Also checks if user is already in waiting list and updates button state
     *
     * @param document Firestore document containing event details
     */
    private void updateUI(DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Extract event details from document
        String eventName = document.getString("eventName");
        String date = document.getString("date");
        String description = document.getString("description");
        currentWaitList = document.getLong("currentWaitList");
        waitListSize = document.getLong("waitListSize");
        geolocationRequired = Boolean.TRUE.equals(document.getBoolean("geolocationRequired"));

        // Update UI elements with event information
        eventTitleView.setText(eventName);
        dateTimeView.setText(date);
        descriptionView.setText(description);
        capacityView.setText(String.format("Waiting List: %d/%d", currentWaitList, waitListSize));

        // Check user's waiting list status
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("events")
                .document(eventId)
                .collection("waitingList")
                .document(userId)
                .get()
                .addOnSuccessListener(waitlistDoc -> {
                    if (waitlistDoc.exists()) {
                        joinButton.setEnabled(false);
                        joinButton.setText("Already in Waiting List");
                    } else {
                        setupJoinButton();
                    }
                });
    }

    /**
     * Configures join button state based on waiting list capacity
     */
    private void setupJoinButton() {
        if (currentWaitList >= waitListSize) {
            joinButton.setEnabled(false);
            joinButton.setText("Waiting List Full");
        }
    }

    /**
     * Handles join button click events
     * Checks waiting list capacity and geolocation requirements
     * Initiates location permission request if needed
     */
    private void handleJoinButtonClick() {
        if (currentWaitList >= waitListSize) {
            Toast.makeText(this, "Waiting list is full", Toast.LENGTH_SHORT).show();
            return;
        }

        if (geolocationRequired) {
            // Check and request location permissions if needed
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                getCurrentLocationAndJoin();
            }
        } else {
            joinWaitList(null, null);
        }
    }

    /**
     * Handles the result of location permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndJoin();
            } else {
                Toast.makeText(this, "Location permission is required to join this event",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Retrieves current location using FusedLocationProviderClient
     * Proceeds with joining waiting list once location is obtained
     */
    private void getCurrentLocationAndJoin() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        joinWaitList(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Unable to get location. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting location", e);
                    Toast.makeText(this, "Error getting location. Please try again.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Adds user to event waiting list using a Firestore transaction
     * Updates both waiting list collection and event document atomically
     *
     * @param latitude User's latitude (null if geolocation not required)
     * @param longitude User's longitude (null if geolocation not required)
     */
    private void joinWaitList(Double latitude, Double longitude) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Prepare waiting list entry data
        java.util.Map<String, Object> waitingListEntry = new java.util.HashMap<>();
        waitingListEntry.put("userId", userId);
        if (latitude != null && longitude != null) {
            waitingListEntry.put("latitude", latitude);
            waitingListEntry.put("longitude", longitude);
        }

        // Execute transaction to update both documents atomically
        db.runTransaction(transaction -> {
            // Add to waitingList collection
            transaction.set(
                    db.collection("events")
                            .document(eventId)
                            .collection("waitingList")
                            .document(userId),
                    waitingListEntry
            );

            // Update waiting list counter
            transaction.update(
                    db.collection("events").document(eventId),
                    "currentWaitList", FieldValue.increment(1)
            );

            return null;
        }).addOnSuccessListener(aVoid -> {
            // Update UI on successful join
            Toast.makeText(this, "Successfully joined waiting list", Toast.LENGTH_SHORT).show();
            joinButton.setEnabled(false);
            joinButton.setText("Already in Waiting List");
            currentWaitList++;
            capacityView.setText(String.format("Waiting List: %d/%d", currentWaitList, waitListSize));
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to join waiting list", e);
            Toast.makeText(this, "Failed to join waiting list", Toast.LENGTH_SHORT).show();
        });
    }
}