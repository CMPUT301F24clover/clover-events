package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.ChosenEntrantListAdapter;
import com.example.luckyevent.Entrant;
import com.example.luckyevent.EntrantListAdapter;
import com.example.luckyevent.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
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
    private ArrayList<String> entrantIdsList;
    private ArrayList<Entrant> entrantsList;
    private ChosenEntrantListAdapter chosenEntrantListAdapter;
    private EntrantListAdapter entrantListAdapter;
    private CollectionReference profilesRef;
    private CollectionReference entrantsRef;
    private ListenerRegistration reg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_of_entrants_screen, container, false);

        Toolbar toolbar = view.findViewById(R.id.topBar);

        // set up back button
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();  // Optional: Navigate back
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        toolbar.setNavigationOnClickListener(v -> callback.handleOnBackPressed());

        if (getArguments() != null) {
            String screenTitle = getArguments().getString("screenTitle");
            String eventId = getArguments().getString("eventId");
            String invitationStatus = getArguments().getString("invitationStatus", "n/a");

            toolbar.setTitle(screenTitle);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            profilesRef = db.collection("loginProfile");
            entrantsRef = db.collection("events").document(eventId).collection("chosenEntrants");

            ListView listview = view.findViewById(R.id.customListView);
            entrantIdsList = new ArrayList<>();
            entrantsList = new ArrayList<>();

            if (screenTitle.equals("List of Chosen Entrants")) {
                chosenEntrantListAdapter = new ChosenEntrantListAdapter(getContext(), entrantsList, entrantsRef, eventId);
                listview.setAdapter(chosenEntrantListAdapter);
                getEntrantsList(invitationStatus);
            } else {
                entrantListAdapter = new EntrantListAdapter(getContext(), entrantsList);
                listview.setAdapter(entrantListAdapter);
                getEntrantsList(invitationStatus);
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
     * Finds the user IDs of the desired entrants, passes each ID to getEntrant and receives the
     * Entrant object associated with the ID, then adds the Entrant object to a list.
     * @param invitationStatus The current status of a chosen entrant's invitation. Can be one of
     *                         the following: Pending, Cancelled, Enrolled, Declined, null.
     */
    private void getEntrantsList(String invitationStatus) {
        Query query;
        if (invitationStatus.equals("n/a")) {
            query = entrantsRef;
        } else {
            query = entrantsRef.whereEqualTo("invitationStatus", invitationStatus);
        }

        reg = query.addSnapshotListener((snapshot, error) -> {
            entrantIdsList.clear();
            entrantsList.clear();
            if (error != null) {
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                ArrayList<Task<Entrant>> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot entrantDocument : snapshot) {
                    String entrantId = entrantDocument.getString("userId");
                    String status = entrantDocument.getString("invitationStatus");
                    if (entrantId != null) {
                        entrantIdsList.add(entrantId);
                        tasks.add(getEntrant(entrantId, status));
                    }
                }

                // update list adapter after all Entrant objects have been added to the list
                Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                    for (Task<?> entrantTask : tasks) {
                        if (entrantTask.isSuccessful()) {
                            Entrant entrant = (Entrant) entrantTask.getResult();
                            if (entrant != null) {
                                entrantsList.add(entrant);
                            }
                        }
                    }
                    if (invitationStatus.equals("n/a")) {
                        chosenEntrantListAdapter.notifyDataSetChanged();
                    } else {
                        entrantListAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                TextView textView = getView().findViewById(R.id.text_emptyList);
                textView.setText(R.string.no_entrants);
            }
        });
    }

    /**
     * Finds the remaining data needed to create an Entrant object and creates it.
     * @param entrantId The ID of the entrant. Also the ID of the document that represents the user.
     * @param invitationStatus The current status of a chosen entrant's invitation. Can be one of
     *                         the following: Pending, Cancelled, Enrolled, Declined.
     * @return Task<Entrant> A Task object that contains the Entrant associated with the given
     * entrant ID if the task succeeds.
     */
    private Task<Entrant> getEntrant(String entrantId, String invitationStatus) {
        return profilesRef.document(entrantId).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                String name = String.format("%s %s", document.getString("firstName"), document.getString("lastName"));
                // uncomment code once profileImageUrl field is added to entrant documents in
                // waitingList/chosenEntrants subcollection
//                String profileImageUrl = document.getString("profileImageUrl");
                return new Entrant(entrantId, name, invitationStatus);
//                return new Entrant(entrantId, name, invitationStatus, profileImageUrl);
            } else {
                return null;
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
