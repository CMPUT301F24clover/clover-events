package com.example.luckyevent.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EntrantEventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailsActivity";

    private TextView eventTitleView;
    private TextView dateTimeView;
    private TextView locationView;
    private TextView capacityView;
    private TextView descriptionView;
    private MaterialButton joinButton;

    private FirebaseFirestore db;
    private String eventId;
    private long currentWaitList;
    private long waitListSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_screen);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Get event ID from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Error: Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load event details
        loadEventDetails();
    }

    private void initializeViews() {
        eventTitleView = findViewById(R.id.textView_eventTitle);
        dateTimeView = findViewById(R.id.textView_dateTime);
        locationView = findViewById(R.id.textView_location);
        capacityView = findViewById(R.id.textView_capacity);
        descriptionView = findViewById(R.id.textView_description);
        joinButton = findViewById(R.id.button_join);

        joinButton.setOnClickListener(v -> joinWaitList());
    }

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

    private void updateUI(DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get event details
        String eventName = document.getString("eventName");
        String date = document.getString("date");
        String description = document.getString("description");
        currentWaitList = document.getLong("currentWaitList");
        waitListSize = document.getLong("waitListSize");

        // Update UI
        eventTitleView.setText(eventName);
        dateTimeView.setText(date);
        descriptionView.setText(description);
        capacityView.setText(String.format("Waiting List: %d/%d", currentWaitList, waitListSize));

        // Check if user is already in waiting list
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (document.exists() && document.contains("waitingList")) {
            java.util.List<String> waitingList =
                    (java.util.List<String>) document.get("waitingList");
            if (waitingList != null && waitingList.contains(userId)) {
                joinButton.setEnabled(false);
                joinButton.setText("Already in Waiting List");
            } else {
                setupJoinButton();
            }
        } else {
            setupJoinButton();
        }
    }

    private void setupJoinButton() {
        // Disable button if waitlist is full
        if (currentWaitList >= waitListSize) {
            joinButton.setEnabled(false);
            joinButton.setText("Waiting List Full");
        }
    }

    private void joinWaitList() {
        if (currentWaitList >= waitListSize) {
            Toast.makeText(this, "Waiting list is full", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("events")
                .document(eventId)
                .update(
                        "waitingList", FieldValue.arrayUnion(userId),
                        "currentWaitList", FieldValue.increment(1)
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Successfully joined waiting list", Toast.LENGTH_SHORT).show();
                    joinButton.setEnabled(false);
                    joinButton.setText("Already in Waiting List");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to join waiting list", Toast.LENGTH_SHORT).show();
                });
    }
}