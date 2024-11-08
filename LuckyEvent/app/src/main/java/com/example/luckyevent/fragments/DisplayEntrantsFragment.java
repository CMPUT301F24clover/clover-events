package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.Entrant;
import com.example.luckyevent.EntrantListAdapter;
import com.example.luckyevent.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

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
public class DisplayEntrantsFragment extends Fragment {
    private ArrayList<String> entrantIdsList;
    private ArrayList<Entrant> entrantsList;
    private String listName;
    private EntrantListAdapter listAdapter;
    private FirebaseFirestore db;
    private DocumentReference eventRef;
    private ListenerRegistration reg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_list_screen, container, false);

        if (getArguments() != null) {
            String screenTitle = getArguments().getString("screenTitle");
            String eventId = getArguments().getString("eventId");
            listName = getArguments().getString("listName");

            ListView listview = view.findViewById(R.id.customListView);
            entrantsList = new ArrayList<>();
            listAdapter = new EntrantListAdapter(getContext(), entrantsList);
            listview.setAdapter(listAdapter);

            Toolbar toolbar = view.findViewById(R.id.topBar);
            toolbar.setTitle(screenTitle);

            db = FirebaseFirestore.getInstance();
            eventRef = db.collection("events").document(eventId);

            getEntrantIdsList();

            // create notification button
            FloatingActionButton notification_button = view.findViewById(R.id.create_notification_fab);
            notification_button.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("entrantIdsList", entrantIdsList);
                CreateNotificationFragment createNotificationFragment = new CreateNotificationFragment();
                createNotificationFragment.setArguments(bundle);
                createNotificationFragment.show(getParentFragmentManager(), "dialog");
            });
        }

        return view;
    }

    /**
     * Retrieves the desired list of entrant userIds from the document of the given event.
     */
    private void getEntrantIdsList() {
        reg = eventRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                entrantIdsList = (ArrayList<String>) snapshot.get(listName);
                if (entrantIdsList != null) {
                    if (entrantIdsList.isEmpty()) {
                        Toast.makeText(getContext(), "List is empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        createEntrantsList();
                    }
                } else {
                    Toast.makeText(getContext(), "List does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Uses the list of userIds to access each entrant's information in the database. This
     * information is used to create a list of Entrant objects.
     */
    private void createEntrantsList() {
        entrantsList.clear();
        for (String id: entrantIdsList) {
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
     * Removes listener once fragment stops.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }
}
