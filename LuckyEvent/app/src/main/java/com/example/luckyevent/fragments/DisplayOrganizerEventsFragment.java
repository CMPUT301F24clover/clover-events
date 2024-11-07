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

public class DisplayOrganizerEventsFragment extends Fragment {
    private List<String> eventIdsList;
    private List<String> eventNamesList;
    private EventListAdapter listAdapter;
    private FirebaseFirestore db;
    private DocumentReference orgDocRef;

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

            if (eventIdsList != null) {
                getEventNamesList();
            } else {
                Toast.makeText(getContext(), "Event list does not exist.", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void getEventIdsList() {
        orgDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    eventIdsList = (List<String>) snapshot.get("myEvents");
                }
            }
        });
    }

    private void getEventNamesList() {
        eventNamesList.clear();
        for (String id: eventIdsList) {
            db.collection("events").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String eventName = (String) snapshot.get("eventName");
                        eventNamesList.add(eventName);

                    }
                }
            });
        }
        listAdapter.notifyDataSetChanged();
    }
}
