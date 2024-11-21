package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.LotteryService;
import com.example.luckyevent.QRDownloadService;
import com.example.luckyevent.R;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Set;

/**
 * Displays all the event details of the the event selected. The organizer can also view the waiting list
 * and the list of enrolled, cancelled and chosen entrants. The sampling of entrants from the waiting list
 * is conducted in this fragment
 *
 * @author Amna, Mmelve, Seyi,
 * @version 1
 * @since 1
 */
public class EventDetailsFragment extends Fragment {
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_event_details, container, false);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");
            db.collection("events")
                    .document(eventId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                TextView eventTitle = view.findViewById(R.id.textView_eventTitle2);
                                eventTitle.setText(document.getString("eventName"));

                                TextView eventDescription = view.findViewById(R.id.textView_description2);
                                eventDescription.setText(document.getString("description"));

                                TextView eventDate = view.findViewById(R.id.textView_dateTime2);
                                eventDate.setText(document.getString("date"));

                                TextView eventSize = view.findViewById(R.id.textView_capacity2);
                                eventSize.setText(document.getString("WaitListSize") + " Entrants in Waiting List:");
                            }
                        } else {
                            Log.e("EventDetailsFragment", "Error getting document", task.getException());
                        }
                    });

            // Set click listener for QR code text
            TextView qrCodeText = view.findViewById(R.id.event_qr_link);
            qrCodeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), QRDownloadService.class);
                    intent.putExtra("eventId", eventId);
                    getContext().startService(intent);
                    //qrCodeText.setVisibility(View.GONE);

                }
            });



            TextView sampleEntrants = view.findViewById(R.id.sample_entrant);
            sampleEntrants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Conducting lottery...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LotteryService.class);
                    intent.putExtra("eventId", eventId);
                    getContext().startService(intent);
                    Toast.makeText(getActivity(), "Click on 'View List of Chosen Entrants' for results.", Toast.LENGTH_SHORT).show();
                    sampleEntrants.setVisibility(View.GONE);
                }
            });

            Button waitingListButton = view.findViewById(R.id.waiting_list_button);
            waitingListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("screenTitle", "Waiting List");
                    bundle.putString("eventId", eventId);
                    bundle.putString("listName", "waitingList");
                    goToList(bundle);
                }
            });

            Button chosenEntrantsButton = view.findViewById(R.id.chosen_entrant_button);
            chosenEntrantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("screenTitle", "List of Chosen Entrants");
                    bundle.putString("eventId", eventId);
                    bundle.putString("listName", "chosenEntrants");
                    goToList(bundle);
                }
            });

            Button enrolledEntrantsButton = view.findViewById(R.id.enrolled_entrant_button);
            enrolledEntrantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("screenTitle", "List of Enrolled Entrants");
                    bundle.putString("eventId", eventId);
                    bundle.putString("listName", "enrolledEntrants");
                    goToList(bundle);
                }
            });

            Button cancelledEntrantsButton = view.findViewById(R.id.cancelled_entrant_button);
            cancelledEntrantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("screenTitle", "List of Cancelled Entrants");
                    bundle.putString("eventId", eventId);
                    bundle.putString("listName", "cancelledEntrants");
                    goToList(bundle);
                }
            });
        }

        return view;
    }

    /**
     *This function sets the bundle needed for the DisplayEntrantsFragment and navigates to it
     */
    private void goToList(Bundle bundle) {
        DisplayEntrantsFragment displayEntrantsFragment = new DisplayEntrantsFragment();
        displayEntrantsFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, displayEntrantsFragment)
                .addToBackStack(null)
                .commit();
    }
}
