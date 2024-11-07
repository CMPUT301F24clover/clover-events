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
import com.example.luckyevent.R;

public class EventDetailsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_event_details, container, false);

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");

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
}
