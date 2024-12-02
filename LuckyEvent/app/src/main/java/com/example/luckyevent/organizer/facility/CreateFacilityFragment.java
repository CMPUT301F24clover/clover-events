package com.example.luckyevent.organizer.facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.R;
import com.example.luckyevent.activities.OrganizerMenuActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a new facility profile when the organizer clicks on the My Facility button only if there
 * isn't already a facility associated with an organizer. Once the organizer has entered all the
 * fields, displays the details on a new screen.
 *
 * @author Aagam
 * @see OrganizerMenuActivity
 * @version 1
 * @since 1
 */

public class CreateFacilityFragment extends Fragment {
    TextInputEditText facilityName;
    TextInputEditText facilityEmail;
    TextInputEditText facilityAddress;
    TextInputEditText facilityPhone;
    private FirebaseFirestore firestore;
    private MaterialButton createFacilityButton;
    private String facilityId;

    /**
     * Required empty constructor for Fragment initialization
     */
    public CreateFacilityFragment() {
    }

    /**
     * Creates and initializes the fragment's view hierarchy
     * Sets up UI components and event listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.create_facility, container, false);

        // Set Toolbar title
        Toolbar toolbar = rootView.findViewById(R.id.topBar);
        toolbar.setTitle("My Facility");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Enable the back button
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        initializeViews(rootView);
        setupCreateFacilityButton();

        return rootView;
    }

    /**
     * Initializes all UI components and Firebase instance
     * Connects TextInputLayouts
     *
     * @param rootView The root view of the fragment
     */
    private void initializeViews(View rootView) {

        TextInputLayout facilityNameLayout = rootView.findViewById(R.id.input_facilityName);
        facilityName = (TextInputEditText) facilityNameLayout.getEditText();

        TextInputLayout facilityEmailLayout = rootView.findViewById(R.id.input_facilityEmail);
        facilityEmail = (TextInputEditText) facilityEmailLayout.getEditText();

        TextInputLayout facilityAddressLayout = rootView.findViewById(R.id.input_facilityAddress);
        facilityAddress = (TextInputEditText) facilityAddressLayout.getEditText();

        TextInputLayout facilityPhoneLayout = rootView.findViewById(R.id.input_facilityPhone);
        facilityPhone = (TextInputEditText) facilityPhoneLayout.getEditText();

        createFacilityButton = rootView.findViewById(R.id.button_createFacility);
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Sets up the create facility button click listener
     * Validates inputs before proceeding with event creation
     */
    private void setupCreateFacilityButton() {
        createFacilityButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveFacilityInfoToFirestore();
            }
        });
    }

    /**
     * Validates all required input fields
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (facilityName.getText().toString().trim().isEmpty() ||
                facilityEmail.getText().toString().trim().isEmpty() ||
                facilityAddress.getText().toString().trim().isEmpty() ||
                facilityPhone.getText().toString().trim().isEmpty()) {

            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Saves facility information to Firebase Firestore
     * Creates new event document and updates user profile
     */
    private void saveFacilityInfoToFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = user.getUid();
        String facilityNameText = facilityName.getText().toString().trim();
        String facilityEmailText = facilityEmail.getText().toString().trim();
        String facilityAddressText = facilityAddress.getText().toString().trim();
        String facilityPhoneText = facilityPhone.getText().toString().trim();

        Map<String, Object> facilityInfo = new HashMap<>();
        facilityInfo.put("Name", facilityNameText);
        facilityInfo.put("Email", facilityEmailText);
        facilityInfo.put("Address", facilityAddressText);
        facilityInfo.put("Phone", facilityPhoneText);
        facilityInfo.put("organizerId", userID);
        facilityInfo.put("status", "active");
        facilityInfo.put("createdAt", System.currentTimeMillis());

        firestore.collection("facilities")
                .add(facilityInfo)
                .addOnSuccessListener(documentReference -> {
                    facilityId = documentReference.getId();
                    addFacilityToProfile(userID, facilityId);
                    Toast.makeText(getContext(), "Facility Added Successfully!", Toast.LENGTH_SHORT).show();
                    navigateToDisplayFacilityFragment(facilityId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create Facility", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Adds the created facility ID to the user's profile
     *
     * @param userID The ID of the current user
     * @param facilityID The ID of the created event
     */
    private void addFacilityToProfile(String userID, String facilityID) {
        firestore.collection("loginProfile")
                .document(userID)
                .update("myFacility", facilityID)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add Facility to profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigates to the display facility screen after successful facility creation
     *
     * @param facilityId The ID of the created event
     */
    private void navigateToDisplayFacilityFragment(String facilityId) {
        DisplayFacilityFragment displayFacilityFragment = new DisplayFacilityFragment();

        // Pass the facilityId to the next fragment
        Bundle bundle = new Bundle();
        bundle.putString("facilityId", facilityId);
        displayFacilityFragment.setArguments(bundle);

        // Use FragmentTransaction to replace the current fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.OrganizerMenuFragment, displayFacilityFragment);
        transaction.addToBackStack(null);  // Optional: Add the transaction to the back stack so user can navigate back
        transaction.commit();
    }
}
