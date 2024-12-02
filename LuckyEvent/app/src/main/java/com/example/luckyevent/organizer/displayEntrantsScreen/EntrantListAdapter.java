package com.example.luckyevent.organizer.displayEntrantsScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.luckyevent.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A class implementing the list adapter for an event's list of entrants, specifically the list of
 * waitlisted entrants, enrolled entrants, and cancelled entrants.
 *
 * @author Mmelve, Tola
 * @see Entrant
 * @version 3
 * @since 1
 */

public class EntrantListAdapter extends ArrayAdapter<Entrant> {
    private ArrayList<Entrant> entrants;
    private Context context;
    private FirebaseFirestore db;

    public EntrantListAdapter(@NonNull Context context, ArrayList<Entrant> entrants) {
        super(context, 0, entrants);
        this.context = context;
        this.entrants = entrants;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_entrant, parent, false);
        }

        Entrant entrant = entrants.get(position);

        // Handle profile image
        ShapeableImageView profileImage = view.findViewById(R.id.profile_image);
        loadProfileImage(entrant.getEntrantId(), profileImage);

        TextView textViewTitle = view.findViewById(R.id.text_title);
        String entrantFullName = entrant.getName();
        textViewTitle.setText(entrantFullName);

        TextView textViewContent = view.findViewById(R.id.text_content);
        textViewContent.setVisibility(View.GONE);

        Button cancelEntrantButton = view.findViewById(R.id.button_cancelEntrant);
        Button sampleNewEntrantButton = view.findViewById(R.id.button_sampleNewEntrant);
        cancelEntrantButton.setVisibility(View.GONE);
        sampleNewEntrantButton.setVisibility(View.GONE);

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
                            // Load default image if URL doesn't exist
                            imageView.setImageResource(R.drawable.baseline_account_circle_24);
                        }
                    } else {
                        // Load default image if document doesn't exist
                        imageView.setImageResource(R.drawable.baseline_account_circle_24);
                    }
                })
                .addOnFailureListener(e -> {
                    // Load default image on failure
                    imageView.setImageResource(R.drawable.baseline_account_circle_24);
                });
    }
}