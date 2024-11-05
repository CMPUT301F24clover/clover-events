package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.luckyevent.Entrant;
import com.example.luckyevent.R;
import com.example.luckyevent.EntrantListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of entrants associated with a given event and a given list name. List of
 * entrants can be any of the following: waiting list, list of chosen entrants, list of enrolled
 * entrants, or list of cancelled entrants.
 *
 * @author Mmelve
 * @see Entrant
 * @see EntrantListAdapter
 * @version 1
 * @since 1
 */
public class DisplayEntrantsActivity extends AppCompatActivity {
    private List<String> entrantsIdList;
    private List<Entrant> entrantsList;
    private String listName;
    private EntrantListAdapter listAdapter;
    private FirebaseFirestore db;
    private DocumentReference eventRef;
    private ListenerRegistration reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_list_screen);

        Intent intent = getIntent();
        String screenTitle = intent.getStringExtra("screenTitle");
        String eventId = intent.getStringExtra("eventId");
        listName = intent.getStringExtra("listName");

        ListView listview = findViewById(R.id.customListView);
        entrantsList = new ArrayList<>();
        listAdapter = new EntrantListAdapter(this, entrantsList);
        listview.setAdapter(listAdapter);

        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setTitle(screenTitle);

        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events").document(eventId);

        getIdsList();

        // create notification button
        FloatingActionButton notification_button = findViewById(R.id.create_notification_fab);
        notification_button.setOnClickListener(view -> Toast.makeText(DisplayEntrantsActivity.this, "Notification fragment not yet implemented", Toast.LENGTH_SHORT).show());
    }

    /**
     * Retrieves the desired list of entrant userIds from the document of the given event.
     */
    private void getIdsList() {
        reg = eventRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                entrantsIdList = (List<String>) snapshot.get(listName);
                    if (entrantsIdList != null) {
                        getEntrantsList();
                    }
            }
        });
    }

    /**
     * Uses the list of userIds to access each entrant's information in the database. This
     * information is used to create a list of Entrant objects.
     */
    private void getEntrantsList() {
        entrantsList.clear();
        for (String id: entrantsIdList) {
            db.collection("loginProfile").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Entrant entrant = new Entrant((String) snapshot.get("firstName"), (String) snapshot.get("lastName"), (String) snapshot.get("userName"));
                        entrantsList.add(entrant);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Removes listener once activity stops.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }
}
