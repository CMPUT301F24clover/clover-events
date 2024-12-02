package com.example.luckyevent.fragments;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.R;
import com.example.luckyevent.organizer.OrganizerMenuActivity;
import com.example.luckyevent.organizer.facility.EditFacilityFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * If a facility already exists it just displays the facility details. This class offers the
 * organizer to edit the details if necessary
 *
 * @author Aagam
 * @see OrganizerMenuActivity
 * @version 1
 * @since 1
 */

public class DisplayFacilityFragment extends Fragment {
    private FirebaseFirestore db;
    private TextView facilityName, facilityEmail, facilityAddress, facilityPhone;
    private String facilityID;

    /**
     * Creates and initializes the fragment's view hierarchy
     * Sets up UI components and event listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_facility, container, false);

        // Set Toolbar title
        Toolbar toolbar = view.findViewById(R.id.topBar);
        toolbar.setTitle("My Facility");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Enable the back button
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        db = FirebaseFirestore.getInstance();

        facilityName = view.findViewById(R.id.facility_nameField);
        facilityEmail = view.findViewById(R.id.facility_emailField);
        facilityAddress = view.findViewById(R.id.facility_addressField);
        facilityPhone = view.findViewById(R.id.facility_phoneField);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        DocumentReference facilityIDRef = db.collection("loginProfile").document(userID);
        facilityIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    facilityID = document.getString("myFacility");
                    viewFacilityDetails(facilityID);
                }
            }
        });

        Button editFacilityButton = view.findViewById(R.id.editFacilityButton);
        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEditFacilityFragment(facilityID);
            }
        });

        return view;
    }

    /**
     * Fetches and displays the details of a facility from Firestore based on the provided facility ID.
     *
     * This method retrieves the facility information (Name, Email, Address, Phone) from the Firestore
     * database and populates the corresponding UI fields. If the facility does not exist or an error occurs,
     * an appropriate message is shown to the user.
     *
     * @param facilityID The ID of the facility whose details are to be fetched from Firestore.
     */
    public void viewFacilityDetails(String facilityID){
        db.collection("facilities")
                .document(facilityID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("Name");
                            String email = document.getString("Email");
                            String address = document.getString("Address");
                            String phone = document.getString("Phone");

                            facilityName.setText(name);
                            facilityEmail.setText(email);
                            facilityAddress.setText(address);
                            facilityPhone.setText(phone);
                        } else {
                            Toast.makeText(getContext(), "Facility not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("DisplayFacilityFragment", "Error getting document", task.getException());
                        Toast.makeText(getContext(), "Failed to load facility details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Navigates to the edit facility screen after clicking the edit button
     *
     * @param facilityId The ID of the created event
     */
    private void navigateToEditFacilityFragment(String facilityId) {
        EditFacilityFragment editFacilityFragment = new EditFacilityFragment();

        // Pass the facilityId to the next fragment
        Bundle bundle = new Bundle();
        bundle.putString("facilityId", facilityId);
        editFacilityFragment.setArguments(bundle);

        // Use FragmentTransaction to replace the current fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.OrganizerMenuFragment, editFacilityFragment);
        transaction.addToBackStack(null);  // Optional: Add the transaction to the back stack so user can navigate back
        transaction.commit();
    }
}
