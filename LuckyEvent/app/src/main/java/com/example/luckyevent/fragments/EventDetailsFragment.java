package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.LotteryService;
import com.example.luckyevent.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

            ImageView qrCodeImageView = view.findViewById(R.id.event_qr_link);
            qrCodeImageView.setOnClickListener(v -> {
                showQrCodeDialog();
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
                    Toast.makeText(getActivity(), "Display list of enrolled entrants not yet implemented.", Toast.LENGTH_SHORT).show();
                }
            });

            Button cancelledEntrantsButton = view.findViewById(R.id.cancelled_entrant_button);
            cancelledEntrantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Display list of cancelled entrants not yet implemented.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    private void goToList(Bundle bundle) {
        DisplayEntrantsFragment displayEntrantsFragment = new DisplayEntrantsFragment();
        displayEntrantsFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.OrganizerMenuFragment, displayEntrantsFragment)
                .addToBackStack(null)
                .commit();
    }

    // This method handles the click and shows the QR code in a dialog
    private void showQrCodeDialog() {
        // Prepare an ImageView for the dialog
        ImageView imageView = new ImageView(getActivity());

        // Firebase Storage reference for the QR code image (example path)
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference qrCodeRef = storage.getReference().child("events/qrContent");

        // Use Picasso to load the image from Firebase Storage into the ImageView
        qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(imageView);
        }).addOnFailureListener(exception -> {
            Log.e("EventDetailsFragment", "Error loading QR code", exception);
            Toast.makeText(getActivity(), "Error loading QR code.", Toast.LENGTH_SHORT).show();
        });

        // Create and show an AlertDialog with the QR code ImageView
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Event QR Code")
                .setView(imageView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

