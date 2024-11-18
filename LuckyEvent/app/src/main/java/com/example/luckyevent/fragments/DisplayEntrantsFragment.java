package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.ChosenEntrantListAdapter;
import com.example.luckyevent.Entrant;
import com.example.luckyevent.EntrantListAdapter;
import com.example.luckyevent.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Displays a list of entrants associated with a given event and a given invitation status. List of
 * entrants can be any of the following: list of chosen entrants, list of enrolled entrants, or
 * list of cancelled entrants.
 *
 * @author Mmelve
 * @see Entrant
 * @see EntrantListAdapter
 * @see ChosenEntrantListAdapter
 * @version 2
 * @since 1
 */
public class DisplayEntrantsFragment extends Fragment {
    private String invitationStatus;
    private ArrayList<String> entrantIdsList;
    private ArrayList<Entrant> entrantsList;
    private ChosenEntrantListAdapter chosenEntrantListAdapter;
    private EntrantListAdapter entrantListAdapter;
    private CollectionReference entrantsRef;
    private ListenerRegistration reg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_of_entrants_screen, container, false);

        if (getArguments() != null) {
            String screenTitle = getArguments().getString("screenTitle");
            String eventId = getArguments().getString("eventId");
            invitationStatus = getArguments().getString("invitationStatus");

            Toolbar toolbar = view.findViewById(R.id.topBar);
            toolbar.setTitle(screenTitle);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            entrantsRef = db.collection("events").document(eventId).collection("chosenEntrants");

            ListView listview = view.findViewById(R.id.customListView);
            entrantIdsList = new ArrayList<>();
            entrantsList = new ArrayList<>();

            if (invitationStatus.equals("Enrolled") || invitationStatus.equals("Cancelled")) {
                entrantListAdapter = new EntrantListAdapter(getContext(), entrantsList);
                listview.setAdapter(entrantListAdapter);
            } else {
                chosenEntrantListAdapter = new ChosenEntrantListAdapter(getContext(), entrantsList, entrantsRef, eventId);
                listview.setAdapter(chosenEntrantListAdapter);
            }

            getEntrantsList();

            if (entrantIdsList.isEmpty()) {
                TextView textView = view.findViewById(R.id.text_emptyList);
                textView.setText(R.string.no_entrants);
            }

            // create notification button
            FloatingActionButton notification_button = view.findViewById(R.id.create_notification_fab);
            notification_button.setOnClickListener(v -> {
                if (entrantIdsList.isEmpty()) {
                    Toast.makeText(getContext(), "No one to send notifications to.", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("entrantIdsList", entrantIdsList);
                    bundle.putString("eventId", eventId);
                    CreateNotificationFragment createNotificationFragment = new CreateNotificationFragment();
                    createNotificationFragment.setArguments(bundle);
                    createNotificationFragment.show(getParentFragmentManager(), "dialog");
                }
            });
        }

        return view;
    }

    /**
     * Retrieves the documents representing the desired entrants and uses them to create a list of
     * Entrant objects.
     */
    private void getEntrantsList() {
        reg = entrantsRef.whereEqualTo("invitationStatus", invitationStatus)
                .addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
            entrantIdsList.clear();
            entrantsList.clear();
            if (error != null) {
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                for (QueryDocumentSnapshot entrantDocument : snapshot) {
                    String entrantId = entrantDocument.getString("userId");
                    String name = entrantDocument.getString("name");
                    // delete if statement later
                    if (entrantId != null && name != null) {
                        entrantIdsList.add(entrantId);
                        Entrant entrant = new Entrant(entrantId, name, invitationStatus);
                        entrantsList.add(entrant);
                    }
                    // uncomment code once profileImageUrl field is added to entrant documents in
                    // waitingList/chosenEntrants subcollection
//                    String profileImageUrl = entrantDocument.getString("profileImageUrl");
//                    if (entrantId != null && name != null && profileImageUrl != null) {
//                        entrantIdsList.add(entrantId);
//                        Entrant entrant = new Entrant(entrantId, name, invitationStatus, profileImageUrl);
//                        entrantsList.add(entrant);
//                    }
                }
                if (invitationStatus.equals("Enrolled") || invitationStatus.equals("Cancelled")) {
                    entrantListAdapter.notifyDataSetChanged();
                } else {
                    chosenEntrantListAdapter.notifyDataSetChanged();
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
