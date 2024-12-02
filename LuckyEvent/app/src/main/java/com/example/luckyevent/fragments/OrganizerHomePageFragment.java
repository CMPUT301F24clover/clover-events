package com.example.luckyevent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.OrganizerSession;
import com.example.luckyevent.R;
import com.example.luckyevent.activities.OrganizerMenuActivity;

/**
 * Displays a welcome message, brief details of organizer owned events and it also gives the organizer the ability
 * to access other fragments via buttons. This is the first fragment the organizer will see upon successfully
 * signing in
 *
 * @author Tola
 * @see OrganizerMenuActivity
 * @version 1
 * @since 1
 */
public class OrganizerHomePageFragment extends Fragment {

    private OnOrganizerNavigateListener navigateListener;

    public interface OnOrganizerNavigateListener {
        void onNavigateToCreateEvent();
        void onNavigateToMyEvents();
        void onNavigateToMyFacility();
        void onNavigateToEventSettings();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnOrganizerNavigateListener) {
            navigateListener = (OnOrganizerNavigateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnOrganizerNavigateListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_homepage, container, false);

        // Set facility name
        String facilityName = OrganizerSession.getInstance().getFacilityName();
        Log.d("OrganizerHomePageFragment", "Facility Name: " + facilityName);
        TextView welcomeTextView = view.findViewById(R.id.welcomeMessage);

        if (facilityName != null) {
            String welcomeMessage = getString(R.string.welcome_message, facilityName);
            welcomeTextView.setText(welcomeMessage);
        } else {
            welcomeTextView.setText("Welcome!");
        }

        // Set up navigation card clicks

        Button createEventButton = view.findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToCreateEvent();
                }
            }
        });

        Button myEventsButton = view.findViewById(R.id.myEventsButton);
        myEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToMyEvents();
                }
            }
        });

        Button myFacilityButton = view.findViewById(R.id.myFacilityButton);
        myFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToMyFacility();
                }
            }
        });

        Button eventSettingsButton = view.findViewById(R.id.eventSettingsButton);
        eventSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigateListener != null) {
                    navigateListener.onNavigateToEventSettings();
                }
            }
        });

        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        navigateListener = null;
    }
}