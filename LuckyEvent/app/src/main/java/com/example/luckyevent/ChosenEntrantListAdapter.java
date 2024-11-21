package com.example.luckyevent;

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

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

/**
 * A class implementing the list adapter for an event's list of chosen entrants.
 *
 * @author Mmelve
 * @see Entrant
 * @version 1
 * @since 1
 */
public class ChosenEntrantListAdapter extends ArrayAdapter<Entrant> {
    private ArrayList<Entrant> chosenEntrants;
    private Context context;
    private CollectionReference chosenEntrantsRef;
    private String eventId;

    public ChosenEntrantListAdapter(@NonNull Context context, ArrayList<Entrant> entrants, CollectionReference chosenEntrantsRef, String eventId) {
        super(context, 0, entrants);
        this.context = context;
        this.chosenEntrants = entrants;
        this.chosenEntrantsRef = chosenEntrantsRef;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_entrant, parent, false);
        }

        Entrant entrant = chosenEntrants.get(position);

        // uncomment code once profileImageUrl field is added to entrant documents in
        // waitingList/chosenEntrants subcollection
//        ImageView profileImage = view.findViewById(R.id.profile_image);
//        Glide.with(context).load(entrant.getProfileImageUrl()).into(profileImage);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        String entrantFullName = entrant.getName();
        textViewTitle.setText(entrantFullName);

        TextView textViewContent = view.findViewById(R.id.text_content);
        textViewContent.setText(entrant.getInvitationStatus());

        Button cancelEntrantButton = view.findViewById(R.id.button_cancelEntrant);
        Button sampleNewEntrantButton = view.findViewById(R.id.button_sampleNewEntrant);

        if (entrant.getInvitationStatus().equals("Pending")) {
            textViewContent.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            sampleNewEntrantButton.setVisibility(View.GONE);

            // cancel entrant that did not sign up
            cancelEntrantButton.setOnClickListener(v -> chosenEntrantsRef.document(entrant.getEntrantId())
                    .update("invitationStatus", "Cancelled")
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Entrant cancelled.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Error cancelling entrant.", Toast.LENGTH_SHORT).show()));
        } else if (entrant.getInvitationStatus().equals("Enrolled")) {
            textViewContent.setTextColor(ContextCompat.getColor(context, R.color.green));
            cancelEntrantButton.setVisibility(View.GONE);
            sampleNewEntrantButton.setVisibility(View.GONE);
        } else {
            textViewContent.setTextColor(ContextCompat.getColor(context, R.color.red));
            cancelEntrantButton.setVisibility(View.GONE);

            // sample new entrant if one declined or is cancelled
            sampleNewEntrantButton.setOnClickListener(v -> {
                // fix LotteryService class
                Toast.makeText(context, "Sampling new entrant... ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LotteryService.class);
                intent.putExtra("eventId", eventId);
//                context.startService(intent);
            });
        }

        return view;
    }
}
