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

import com.example.luckyevent.EventListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of events created by the current organizer. If the organizer clicks on one of
 * the events, they are taken to a screen displaying the selected event's details.
 */
public class DisplayOrganizerEventsFragment extends Fragment {
    private List<String> eventIdsList;
    private List<String> eventNamesList;
    private EventListAdapter listAdapter;
    private FirebaseFirestore db;
    private DocumentReference orgDocRef;
    private String selectedEventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_my_events_screen, container, false);

        ListView listView = view.findViewById(R.id.customListView);
        eventNamesList = new ArrayList<>();
        listAdapter = new EventListAdapter(getContext(), eventNamesList);
        listView.setAdapter(listAdapter);

        Toolbar toolbar = view.findViewById(R.id.topBar);
        toolbar.setTitle("My Events");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String organizerId = firebaseUser.getUid();
            db = FirebaseFirestore.getInstance();
            orgDocRef = db.collection("loginProfile").document(organizerId);

            getEventIdsList();
        }

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedEventId = eventIdsList.get(position);
            goToEventDetails();
        });

        return view;
    }

    /**
     * Retrieves the organizer's list of eventIds from their profile document.
     */
    private void getEventIdsList() {
        orgDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    eventIdsList = (List<String>) snapshot.get("myEvents");
                    if (eventIdsList != null) {
                        getEventNamesList();
                    } else {
                        Toast.makeText(getContext(), "myEvents list does not exist.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Uses the list of eventIds to get information about each event in the database. With this
     * information, we can create a list of event names.
     */
    private void getEventNamesList() {
        eventNamesList.clear();
        for (String id: eventIdsList) {
            db.collection("events").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String eventName = (String) snapshot.get("eventName");
                        eventNamesList.add(eventName);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Transitions to the next fragment. A bundle containing the eventId is passed so that the
     * next fragment can display the associated event's information.
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
