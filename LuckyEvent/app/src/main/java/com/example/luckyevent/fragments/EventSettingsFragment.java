package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.AdapterListUpdateCallback;

import com.example.luckyevent.EventSetting;
import com.example.luckyevent.EventSettingsListAdapter;
import com.example.luckyevent.Profile;
import com.example.luckyevent.ProfileListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventSettingsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<EventSetting> eventList;
    private EventSettingsListAdapter adapter;
    private String TAG = "EventSettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.event_settings, container, false);

        ListView listView = rootView.findViewById(R.id.event_settings_list);
        adapter = new EventSettingsListAdapter(getActivity(), getActivity(), eventList);
        listView.setAdapter(adapter);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String organizerId = firebaseUser.getUid();
        db.collection("loginProfile")
                .document(organizerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        ArrayList<String> eventIds = (ArrayList<String>) document.get("myEvents");
                        for(String eventId : eventIds){
                            fetchEventDetails(eventId);
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch event IDs: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching myEvents: ", e);
                });

        return rootView;
    }

    private void fetchEventDetails(String eventId) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String eventName = document.getString("eventName");
                        String eventDate = document.getString("date");
                        String eventDescription = document.getString("description");

                        if(eventDate == null || eventName == null ||  eventDescription == null){
                            Log.e(TAG, "Failed to fetch event details: ", task.getException());
                        }

                        else{
                            EventSetting eventSetting = new EventSetting(eventId, eventName, eventDate, eventDescription);
                            eventList.add(eventSetting);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch event details: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event details: ", e);
                });
    }
}

