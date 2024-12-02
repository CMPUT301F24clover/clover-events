package com.example.luckyevent.organizer.displayEntrantsScreen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.luckyevent.R;
import com.example.luckyevent.organizer.SampleReplacementService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
/**
 * A class implementing the list adapter for an event's list of chosen entrants.
 *
 * @author Mmelve, Tola
 * @see Entrant
 * @version 1
 * @since 1
 */

public class ChosenEntrantListAdapter extends ArrayAdapter<Entrant> {
    private ArrayList<Entrant> chosenEntrants;
    private Context context;
    private FirebaseFirestore db;
    private CollectionReference chosenEntrantsRef;
    private String eventId;

    public ChosenEntrantListAdapter(@NonNull Context context, ArrayList<Entrant> entrants, CollectionReference chosenEntrantsRef, String eventId) {
        super(context, 0, entrants);
        this.context = context;
        this.chosenEntrants = entrants;
        this.chosenEntrantsRef = chosenEntrantsRef;
        this.eventId = eventId;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_entrant, parent, false);
        }

        Entrant entrant = chosenEntrants.get(position);

        // Handle profile image
        ShapeableImageView profileImage = view.findViewById(R.id.profile_image);
        loadProfileImage(entrant.getEntrantId(), profileImage);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        String entrantFullName = entrant.getName();
        textViewTitle.setText(entrantFullName);

        TextView textViewContent = view.findViewById(R.id.text_content);
        String invitationStatus = entrant.getInvitationStatus();
        textViewContent.setText(invitationStatus);

        Button cancelEntrantButton = view.findViewById(R.id.button_cancelEntrant);
        Button sampleNewEntrantButton = view.findViewById(R.id.button_sampleNewEntrant);

        setupButtonsBasedOnStatus(invitationStatus, entrant, cancelEntrantButton, sampleNewEntrantButton, textViewContent);

        return view;
    }

    /**
     * Loads the profile image for the given user ID into the provided ImageView.
     * @param userId
     * @param imageView
     */

    private void loadProfileImage(String userId, ShapeableImageView imageView) {
        db.collection("profileImages")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("imageUrl")) {
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Load image using Picasso
                            Picasso.get()
                                    .load(imageUrl)
                                    .placeholder(R.drawable.baseline_account_circle_24)
                                    .error(R.drawable.baseline_account_circle_24)
                                    .into(imageView);
                        } else {
                            // Load a default image if URL doesn't exist
                            imageView.setImageResource(R.drawable.baseline_account_circle_24);
                        }
                    } else {
                        // Load a default image if document doesn't exist
                        imageView.setImageResource(R.drawable.baseline_account_circle_24);
                    }
                })
                .addOnFailureListener(e -> {
                    // Load a default image if there's an error
                    imageView.setImageResource(R.drawable.baseline_account_circle_24);
                });
    }

    /**
     * Sets up the buttons based on the invitation status of the entrant.
     * @param invitationStatus
     * @param entrant
     * @param cancelEntrantButton
     * @param sampleNewEntrantButton
     * @param textViewContent
     */

    private void setupButtonsBasedOnStatus(String invitationStatus, Entrant entrant,
                                           Button cancelEntrantButton, Button sampleNewEntrantButton,
                                           TextView textViewContent) {
        if (invitationStatus.equals("Pending")) {
            textViewContent.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            sampleNewEntrantButton.setVisibility(View.GONE);

            // cancel entrant that did not sign up
            cancelEntrantButton.setOnClickListener(v -> {
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    transaction.update(chosenEntrantsRef.document(entrant.getEntrantId()), "invitationStatus", "Cancelled");
                    transaction.update(chosenEntrantsRef.document(entrant.getEntrantId()), "findReplacement", true);
                    transaction.update(db.collection("loginProfile").document(entrant.getEntrantId())
                            .collection("eventsJoined").document(eventId), "status", "Cancelled");
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    sampleNewEntrantButton.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Entrant cancelled", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(context, eventId, Toast.LENGTH_SHORT).show());
            });

        } else if (invitationStatus.equals("Enrolled")) {
            textViewContent.setTextColor(ContextCompat.getColor(context, R.color.green));
            cancelEntrantButton.setVisibility(View.GONE);
            sampleNewEntrantButton.setVisibility(View.GONE);

        } else {
            textViewContent.setTextColor(ContextCompat.getColor(context, R.color.red));
            cancelEntrantButton.setVisibility(View.GONE);

            // check if cancelled/declined entrant has already been replaced
            chosenEntrantsRef.document(entrant.getEntrantId()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        if (doc.contains("findReplacement") && Boolean.TRUE.equals(doc.getBoolean("findReplacement"))) {
                            // sample replacement entrant
                            sampleNewEntrantButton.setOnClickListener(v -> {
                                Toast.makeText(context, "Sampling new entrant... ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, SampleReplacementService.class);
                                intent.putExtra("eventId", eventId);
                                intent.putExtra("userId", entrant.getEntrantId());
                                context.startService(intent);
                            });
                        } else {
                            sampleNewEntrantButton.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
}