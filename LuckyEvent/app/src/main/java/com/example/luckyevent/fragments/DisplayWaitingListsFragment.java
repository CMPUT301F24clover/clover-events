package com.example.luckyevent.fragments;

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

import com.example.luckyevent.R;
import com.example.luckyevent.WaitingList;
import com.example.luckyevent.WaitingListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * Displays a list of the waiting lists the current user has joined.
 *
 * @author Mmelve
 * @see WaitingList
 * @see WaitingListAdapter
 * @version 1
 * @since 1
 */
public class DisplayWaitingListsFragment extends Fragment {
    private ArrayList<WaitingList> waitingLists;
    private WaitingListAdapter listAdapter;
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

            ListView listView = view.findViewById(R.id.customListView);
            waitingLists = new ArrayList<>();
            listAdapter = new WaitingListAdapter(getContext(), waitingLists);
            listView.setAdapter(listAdapter);

            Toolbar toolbar = view.findViewById(R.id.topBar);
            toolbar.setTitle("My Waiting Lists");

            getWaitingLists();

            if (waitingLists.isEmpty()) {
                TextView textView = view.findViewById(R.id.text_emptyList);
                textView.setText("No waiting lists");
            }
        }

        return view;
    }

    /**
     * Retrieves information about which events a given user has joined and their waiting list
     * status for each event. Each relevant document from the database is used to create a
     * WaitingList object.
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
                    String status = waitingListSnapshot.getString("status");
                    if (eventId != null && status != null) {
                        getEventInfo(eventId, status);
                    }
                }
            }
        });

    }

    /**
     * Retrieves information about a given event from the database. Uses this data, along with the
     * data received from getWaitingLists(), to create a WaitingList object. This object is then
     * added to the list of WaitingList objects that will be displayed in a ListView.
     * @param eventId The document ID of an event in the events collection.
     * @param status An attribute of a WaitingList object.
     */
    private void getEventInfo(String eventId, String status) {
        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    WaitingList waitingList = new WaitingList((String) snapshot.get("eventName"), (String) snapshot.get("dateTime"), (String) snapshot.get("description"), status);
                    waitingLists.add(waitingList);
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
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
