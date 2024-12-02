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

import java.util.ArrayList;

/**
 * A class implementing the list adapter for an event's list of entrants, specifically the list of
 * waitlisted entrants, enrolled entrants, and cancelled entrants.
 *
 * @author Mmelve
 * @see Entrant
 * @version 3
 * @since 1
 */
public class EntrantListAdapter extends ArrayAdapter<Entrant> {
    private ArrayList<Entrant> entrants;
    private Context context;

    public EntrantListAdapter(@NonNull Context context, ArrayList<Entrant> entrants) {
        super(context, 0, entrants);
        this.context = context;
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.box_content_entrant, parent, false);
        }

        Entrant entrant = entrants.get(position);

        // uncomment code once profileImageUrl field is added to entrant documents in
        // waitingList/chosenEntrants subcollection
//        ImageView profileImage = view.findViewById(R.id.profile_image);
//        Glide.with(context).load(entrant.getProfileImageUrl()).into(profileImage);

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
}
