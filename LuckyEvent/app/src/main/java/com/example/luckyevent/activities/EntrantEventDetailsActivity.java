package com.example.luckyevent.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.luckyevent.R;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

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
 * @author Aagam, Tola, Amna, Mmelve
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
    private MaterialButton leaveButton;
    private MaterialButton acceptInviteButton;
    private MaterialButton declineInviteButton;
    private TextView invitationResponse;

    // Firebase and Location Services
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;
    private CollectionReference eventsJoinedRef;
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get and validate event ID from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Error: Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        DocumentReference facilityIDRef = db.collection("loginProfile").document(userID);
        facilityIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String facilityID = document.getString("myFacility");
                    getAddress(facilityID);
                }
            }
        });

        loadEventDetails();
    }

    /**
     * Initializes all UI components
     */
    private void initializeViews() {
        Log.d(TAG, "Initializing views");
        eventTitleView = findViewById(R.id.textView_eventTitle);
        dateTimeView = findViewById(R.id.textView_dateTime);
        locationView = findViewById(R.id.textView_location);
        capacityView = findViewById(R.id.textView_capacity);
        descriptionView = findViewById(R.id.textView_description);
        joinButton = findViewById(R.id.button_join);
        leaveButton = findViewById(R.id.button_leave);
        acceptInviteButton = findViewById(R.id.button_acceptInvitation);
        declineInviteButton = findViewById(R.id.button_declineInvitation);
        invitationResponse = findViewById(R.id.textView_invitationResponse);

        setButtons();
    }

    /**
     * Finds the waiting list status of the user, which is used to determine which buttons should be
     * made available to them.
     */
    private void setButtons() {
        if (user != null) {
            userId = user.getUid();
            Log.d(TAG, "Here is the user ID: " + userId);
            Log.d(TAG, "Here is the event ID: " + eventId);

            eventsJoinedRef = db.collection("loginProfile").document(userId).collection("eventsJoined");

            eventsJoinedRef.document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Task successful");
                    if (task.getResult().exists()) {
                        Log.d(TAG, "Task result exists");
                        String status = task.getResult().getString("status");
                        Log.d(TAG, "Here is the status: " + status);
                        setButtonCases(status);
                    } else {
                        Log.d(TAG, "Task not successful");
                        setButtonCases("n/a");
                    }
                }
            });
        } else {
            setButtonsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, null);
            joinButton.setOnClickListener(v -> Toast.makeText(this, "User must be logged in to join waiting list", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Determines which buttons should be made available to the user based on their waiting list
     * status. Also, sets up button click events so the user can interact with the waiting
     * list/event.
     * @param status The parameter that determines how the user can interact with the waiting list
     *               and what buttons are made available to them.
     */
    private void setButtonCases(String status) {
        switch (status) {
            case "Waitlisted":
                setButtonsVisibility(View.GONE, View.VISIBLE, View.GONE, View.GONE, null);
                leaveButton.setOnClickListener(v -> leaveWaitListAlert());
                break;
            case "Chosen":
                setButtonsVisibility(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, null);
                acceptInviteButton.setOnClickListener(v -> acceptInviteAlert());
                declineInviteButton.setOnClickListener(v -> declineInviteAlert());
                break;
            case "Enrolled":
                setButtonsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, "Enrolled");
                invitationResponse.setTextColor(ContextCompat.getColor(this, R.color.green));
                break;
            case "Declined":
                setButtonsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, "Declined");
                invitationResponse.setTextColor(ContextCompat.getColor(this, R.color.black));
                break;
            case "n/a":
                setButtonsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, null);
                joinButton.setOnClickListener(v -> handleJoinButtonClick());
                break;
        }
    }

    /**
     * Sets the visibility of all the buttons.
     * @param joinButtonVisibility The visibility of the Join Waiting List button.
     * @param leaveButtonVisibility The visibility of the Leave Waiting List button.
     * @param acceptButtonVisibility The visibility of the Accept Invitation button.
     * @param declineButtonVisibility The visibility of the Decline Invitation button.
     * @param response The text that reiterates whether the user accepts or declines the invitation.
     */
    private void setButtonsVisibility(int joinButtonVisibility, int leaveButtonVisibility, int acceptButtonVisibility, int declineButtonVisibility, String response) {
        joinButton.setVisibility(joinButtonVisibility);
        leaveButton.setVisibility(leaveButtonVisibility);
        acceptInviteButton.setVisibility(acceptButtonVisibility);
        declineInviteButton.setVisibility(declineButtonVisibility);
        if (response != null) {
            invitationResponse.setText(response);
            invitationResponse.setVisibility(View.VISIBLE);
        } else {
            invitationResponse.setVisibility(View.GONE);
        }
    }

    /**
     * Retrieves the address of a facility from Firestore and displays it in the specified TextView.
     *
     * This method fetches the "Address" field from the Firestore document corresponding to the given facility ID.
     * If the address is found, it updates the provided TextView with the address. If the address is not found or
     * if an error occurs during the retrieval, appropriate messages are shown to the user.
     *
     * @param facilityId The ID of the facility whose address is to be retrieved from Firestore.
     */
    private void getAddress(String facilityId){
        db.collection("facilities")
                .document(facilityId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String location = document.getString("Address");
                            locationView.setText(location != null ? location : "Location not set");
                        } else {
                            Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("EntrantEventDetailsActivity", "Error getting document", task.getException());
                        Toast.makeText(this, "Failed to load event address", Toast.LENGTH_SHORT).show();
                    }
                });
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
            // Create an AlertDialog to confirm if the user wants to proceed with geolocation
            new AlertDialog.Builder(this)
                    .setTitle("Warning:")
                    .setMessage("Geo-location is required in order to join the waitlist. Do you want to continue?")
                    .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If user confirms, check location permissions
                            if (ContextCompat.checkSelfPermission(EntrantEventDetailsActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // Request location permissions
                                ActivityCompat.requestPermissions(EntrantEventDetailsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_CODE);
                            } else {
                                // Permissions granted, proceed to get location and join
                                getCurrentLocationAndJoin();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If user cancels, do nothing
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            // If geolocation is not required, just join the waitlist directly
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
     * Adds user to event waiting list using a Firestore transaction.
     * Updates the event's waiting list sub-collection, event document, and the user's events
     * joined sub-collection atomically.
     *
     * @param latitude User's latitude (null if geolocation not required).
     * @param longitude User's longitude (null if geolocation not required).
     */
    private void joinWaitList(Double latitude, Double longitude) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // Prepare data that will be added to event's waiting list
            Map<String, Object> waitingListEntry = new HashMap<>();
            waitingListEntry.put("userId", userId);
            if (latitude != null && longitude != null) {
                waitingListEntry.put("latitude", latitude);
                waitingListEntry.put("longitude", longitude);
            }

            // Prepare data that will be added to a user's joined events
            Map<String, Object> eventJoinedEntry = new HashMap<>();
            eventJoinedEntry.put("eventId", eventId);
            eventJoinedEntry.put("status", "Waitlisted");

            // Execute transaction to update all documents atomically
            DocumentReference eventRef = db.collection("events").document(eventId);
            transaction.set(eventRef.collection("waitingList").document(userId), waitingListEntry);
            transaction.update(eventRef, "currentWaitList", FieldValue.increment(1));
            transaction.set(eventsJoinedRef.document(eventId), eventJoinedEntry);

            return null;
        }).addOnSuccessListener(result -> {
            Toast.makeText(EntrantEventDetailsActivity.this, "Successfully joined waiting list", Toast.LENGTH_SHORT).show();

            // Update UI on successful join
            currentWaitList++;
            capacityView.setText(String.format("Waiting List: %d/%d", currentWaitList, waitListSize));
            setButtonCases("Waitlisted");
        }).addOnFailureListener(e ->Toast.makeText(EntrantEventDetailsActivity.this, "Failed to join waiting list", Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays an alert confirming that a user wants to leave the waiting list.
     */
    private void leaveWaitListAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Waiting List Confirmation")
                .setMessage("Are you sure you want to leave the waiting list?")
                .setPositiveButton("Yes", ((dialog, which) -> leaveWaitList()))
                .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                .show();
    }

    /**
     * Removes the user from the event's waiting list sub-collection, removes the event from the
     * user's events joined sub-collection, and updates the number of entrants in the waiting list.
     */
    private void leaveWaitList() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference eventRef = db.collection("events").document(eventId);
            transaction.delete(eventRef.collection("waitingList").document(userId));
            transaction.update(eventRef, "currentWaitList", FieldValue.increment(-1));
            transaction.delete(eventsJoinedRef.document(eventId));
            return null;
        }).addOnSuccessListener(result -> {
            Toast.makeText(EntrantEventDetailsActivity.this, "You have left the waiting list", Toast.LENGTH_SHORT).show();
            currentWaitList--;
            capacityView.setText(String.format("Waiting List: %d/%d", currentWaitList, waitListSize));
            setButtonCases("n/a");
        }).addOnFailureListener(e -> Toast.makeText(EntrantEventDetailsActivity.this, "Failed to leave the waitlist", Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays an alert confirming that a user wants to accept the invite to sign up for the event.
     */
    private void acceptInviteAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Accept Invitation Confirmation")
                .setMessage("Would you like to accept the invitation to sign up for the event?")
                .setPositiveButton("Yes", ((dialog, which) -> acceptInvite()))
                .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                .show();
    }

    /**
     * Updates the invitation status of the user in the event's chosen entrants sub-collection and
     * the status of the event in the user's events joined sub-collection to reflect the accepted
     * invite.
     */
    private void acceptInvite() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference entrantRef = db.collection("events")
                    .document(eventId).collection("chosenEntrants").document(userId);

            DocumentReference joinedEventRef = eventsJoinedRef.document(eventId);

            transaction.update(entrantRef, "invitationStatus", "Enrolled");
            transaction.update(joinedEventRef, "status", "Enrolled");
            return null;
        }).addOnSuccessListener(result -> {
            Toast.makeText(EntrantEventDetailsActivity.this, "You have accepted the invitation and signed up", Toast.LENGTH_SHORT).show();
            setButtonCases("Enrolled");
        }).addOnFailureListener(e -> Toast.makeText(EntrantEventDetailsActivity.this, "Failed to accept the invitation", Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays an alert confirming that a user wants to decline the invite to sign up for the event.
     */
    private void declineInviteAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Decline Invitation Confirmation")
                .setMessage("Are you sure you want to decline the invitation to sign up for the event?")
                .setPositiveButton("Yes", ((dialog, which) -> declineInvite()))
                .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                .show();
    }

    /**
     * Updates the invitation status of the user in the event's chosen entrants sub-collection and
     * the status of the event in the user's events joined sub-collection to reflect the declined
     * invite.
     */
    private void declineInvite() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference entrantRef = db.collection("events")
                    .document(eventId).collection("chosenEntrants").document(userId);

            DocumentReference joinedEventRef = eventsJoinedRef.document(eventId);

            transaction.update(entrantRef, "invitationStatus", "Declined");
            transaction.update(joinedEventRef, "status", "Declined");
            return null;
        }).addOnSuccessListener(result -> {
            Toast.makeText(EntrantEventDetailsActivity.this, "You have declined the invitation and signed up", Toast.LENGTH_SHORT).show();
            setButtonCases("Declined");
        }).addOnFailureListener(e -> Toast.makeText(EntrantEventDetailsActivity.this, "Failed to decline the invitation", Toast.LENGTH_SHORT).show());
    }
}