package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventSettingDetailsFragment extends Fragment {
    private FirebaseFirestore db;
    private EditText waitingListSizeEditText;
    private EditText sampleSizeEditText;
    private CheckBox geolocationCheckbox;
    private MaterialButton updateEventPosterButton;
    private MaterialButton saveChangesButton;
    private Long currentWaitlist;
    private String TAG = "EventSettingDetailsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_settings_details, container, false);

        // Set Toolbar title
        Toolbar toolbar = rootView.findViewById(R.id.topBar);
        toolbar.setTitle("Event Settings");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Enable the back button
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        db = FirebaseFirestore.getInstance();

        waitingListSizeEditText = rootView.findViewById(R.id.waiting_list_size_edit_text);
        sampleSizeEditText = rootView.findViewById(R.id.sample_size_edit_edit_text);
        geolocationCheckbox = rootView.findViewById(R.id.checkBox2);
        updateEventPosterButton = rootView.findViewById(R.id.update_poster_button);
        saveChangesButton = rootView.findViewById(R.id.save_changes_button);

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");

            db.collection("events")
                    .document(eventId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            Long waitingListSize = document.getLong("waitListSize");
                            Long sampleSize = document.getLong("sampleSize");
                            Boolean geolocationRequired = document.getBoolean("geolocationRequired");
                            currentWaitlist = document.getLong("currentWait");
                            updateScreen(waitingListSize, sampleSize, geolocationRequired);

                        } else {
                            Log.e(TAG, "Failed to fetch event IDs: ", task.getException());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching myEvents: ", e);
                    });

            updateEventPosterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("eventId", eventId);

                    EventPosterFragment eventPosterFragment = new EventPosterFragment();
                    eventPosterFragment.setArguments(bundle);

                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.OrganizerMenuFragment, eventPosterFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            saveChangesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int waitingListSizeInput = Integer.parseInt(waitingListSizeEditText.getText().toString().trim());
                    int sampleSizeInput  =  Integer.parseInt(sampleSizeEditText.getText().toString().trim());
                    Boolean geolocationRequired = geolocationCheckbox.isChecked();

                    if(currentWaitlist == null){
                        currentWaitlist = 0L;
                    }

                    if(waitingListSizeInput < sampleSizeInput || waitingListSizeInput < currentWaitlist){
                        if (waitingListSizeInput < sampleSizeInput){
                            Toast.makeText(getContext(), "Waiting list size cannot be smaller than sample size!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Waiting list size cannot be smaller than the current waitlist size!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Map<String, Object> map = new HashMap<>();
                        map.put("waitListSize", waitingListSizeInput);
                        map.put("sampleSize", sampleSizeInput);
                        map.put("geolocationRequired", geolocationRequired);
                        db.collection("events").document(eventId).update(map)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        geolocationCheckbox.setChecked(geolocationRequired);
                                        Toast.makeText(getContext(), "Successfully updated event settings!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "onClick: Failed upload changes");
                                    }
                                });
                    }
                }
            });


        }







        return rootView;
    }

    public void updateScreen(Long waitingListSize, Long sampleSize, Boolean geolocationRequired) {
        if (waitingListSize != null) {
            waitingListSizeEditText.setText(String.valueOf(waitingListSize));
        } else {
            waitingListSizeEditText.setText("");
        }

        if (sampleSize != null) {
            sampleSizeEditText.setText(String.valueOf(sampleSize));
        } else {
            sampleSizeEditText.setText("");
        }

        if (geolocationRequired != null) {
            geolocationCheckbox.setChecked(geolocationRequired);
        } else {
            geolocationCheckbox.setChecked(false);
        }
    }

}
