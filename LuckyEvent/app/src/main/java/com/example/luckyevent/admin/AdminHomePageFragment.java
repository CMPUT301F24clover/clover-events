package com.example.luckyevent.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.example.luckyevent.organizer.loginSection.OrganizerSignInActivity;
import com.example.luckyevent.shared.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHomePageFragment extends Fragment {

    private FirebaseFirestore db;

    private TextView countTotalEvents, countTotalProfiles, countTotalOrganizations, countTotalImages;
    private MaterialButton logOutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.admin_homepage, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Find TextViews by ID
        countTotalEvents = rootView.findViewById(R.id.countTotalEvents);
        countTotalProfiles = rootView.findViewById(R.id.countTotalProfiles);
        countTotalOrganizations = rootView.findViewById(R.id.countTotalOrganizations);
        countTotalImages = rootView.findViewById(R.id.countTotalImages);
        logOutButton = rootView.findViewById(R.id.log_out_button);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Load statistics from Firebase
        loadStatistics();

        return rootView;
    }

    private void loadStatistics() {
        // Count Total Events
        db.collection("events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalEvents = querySnapshot.size();
                    countTotalEvents.setText(String.valueOf(totalEvents));
                });

        // Count Total Profiles (Corrected collection name)
        db.collection("loginProfile")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalProfiles = querySnapshot.size();
                    countTotalProfiles.setText(String.valueOf(totalProfiles));
                });

        // Count Total Organizations (No matching collection; consider removing this or updating logic)
        db.collection("facilities")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalOrganizations = querySnapshot.size();
                    countTotalOrganizations.setText(String.valueOf(totalOrganizations));
                });

        // Count Total Images (Corrected collection name)
        db.collection("profileImages")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalImages = querySnapshot.size();
                    countTotalImages.setText(String.valueOf(totalImages));
                });
    }
}