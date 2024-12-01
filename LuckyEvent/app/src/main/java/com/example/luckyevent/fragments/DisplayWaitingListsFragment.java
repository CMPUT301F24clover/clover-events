package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.Event;
import com.example.luckyevent.EventListAdapter;
import com.example.luckyevent.R;
import com.example.luckyevent.activities.EntrantEventDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * Displays a list of the events the current user has joined.
 *
 * @author Mmelve
 * @see Event
 * @see EventListAdapter
 * @version 1
 * @since 1
 */
public class DisplayWaitingListsFragment extends Fragment {
    private ArrayList<Event> waitingLists;
    private EventListAdapter listAdapter;
    private TextView textView;
    private FirebaseFirestore db;
    private CollectionReference eventsJoinedRef;
    private ListenerRegistration reg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_screen, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String entrantId = firebaseUser.getUid();
            eventsJoinedRef = db.collection("loginProfile").document(entrantId).collection("eventsJoined");

            Toolbar toolbar = view.findViewById(R.id.topBar);
            toolbar.setTitle("My Waiting Lists");

            textView = view.findViewById(R.id.text_emptyList);
            textView.setVisibility(View.GONE);

            ListView listView = view.findViewById(R.id.customListView);
            waitingLists = new ArrayList<>();
            listAdapter = new EventListAdapter(getContext(), waitingLists);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener((parent, v, position, id) -> {
                String selectedEventId = waitingLists.get(position).getEventId();
                goToEventDetails(selectedEventId);
            });

            getWaitingLists();
        }

        return view;
    }

    /**
     * Retrieves information about which events a given user has joined and their waiting list
     * for each event. Each relevant document from the database is used to create an Event object.
     */
    private void getWaitingLists() {
        reg = eventsJoinedRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                waitingLists.clear();

                for (DocumentSnapshot waitingListSnapshot : snapshot.getDocuments()) {
                    String eventId = waitingListSnapshot.getString("eventId");
                    if (eventId != null) {
                        getEventInfo(eventId);
                    }
                }
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText("No waiting lists");
            }
        });

    }

    /**
     * Retrieves information about a given event from the database. Uses this data, along with the
     * data received from getWaitingLists(), to create a Event object. This object is then
     * added to the list of Event objects that will be displayed in a ListView.
     * @param eventId The document ID of an event in the events collection.
     */
    private void getEventInfo(String eventId) {
        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String dateTime = String.format("%s · %s", snapshot.get("date"), snapshot.get("time"));
                    Event waitingList = new Event(eventId, (String) snapshot.get("eventName"), dateTime, (String) snapshot.get("description"));
                    waitingLists.add(waitingList);
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void goToEventDetails(String selectedEventId) {
        Intent intent = new Intent(getActivity(), EntrantEventDetailsActivity.class);
        intent.putExtra("eventId", selectedEventId);
        startActivity(intent);
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

    /**
     * Removes listener once view is detached from fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reg != null) {
            reg.remove();
        }
    }
}