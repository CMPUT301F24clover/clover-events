package com.example.luckyevent.fragments;

import android.content.Context;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;

import com.example.luckyevent.OrganizerSession;
import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.activities.GenerateQrActivity;

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
        TextView welcomeTextView = view.findViewById(R.id.welcomeMessage_org);

        if (facilityName != null) {
            String welcomeMessage = getString(R.string.welcome_message, facilityName);
            welcomeTextView.setText(welcomeMessage);
        } else {
            welcomeTextView.setText("Welcome!");
        }

        Button testCreate = view.findViewById(R.id.testCreateEvent);
        testCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GenerateQrActivity.class);
                startActivity(intent);
            }
        });

        // Set up navigation card clicks
        //CardView createEventCard = view.findViewById(R.id.createEventCard);
        //CardView myEventsCard = view.findViewById(R.id.myEventsCard);
        //CardView myFacilityCard = view.findViewById(R.id.myFacilityCard);
        //CardView eventSettingsCard = view.findViewById(R.id.eventSettingsCard);

/*
        createEventCard.setOnClickListener(view1 -> {
            if (navigateListener != null) {
                navigateListener.onNavigateToCreateEvent();
            }
        });


        myEventsCard.setOnClickListener(view1 -> {
            if (navigateListener != null) {
                navigateListener.onNavigateToMyEvents();
            }
        });

        myFacilityCard.setOnClickListener(view1 -> {
            if (navigateListener != null) {
                navigateListener.onNavigateToMyFacility();
            }
        });

        eventSettingsCard.setOnClickListener(view1 -> {
            if (navigateListener != null) {
                navigateListener.onNavigateToEventSettings();
            }
        });

 */

        return view;


    }



    @Override
    public void onDetach() {
        super.onDetach();
        navigateListener = null;
    }
}