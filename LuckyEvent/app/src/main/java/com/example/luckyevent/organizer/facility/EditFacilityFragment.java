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
import com.example.luckyevent.organizer.OrganizerMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class helps the organizer to edit the required details of their facility and save changes
 * to firestore. Once the organizer clicks save changes, it displays the updated details
 * in a new screen.
 *
 * @author Aagam
 * @see OrganizerMenuActivity
 * @version 1
 * @since 1
 */

public class EditFacilityFragment extends Fragment {
    private TextInputEditText editName;
    private TextInputEditText editEmail;
    private TextInputEditText editAddress;
    private TextInputEditText editPhone;
    private FirebaseFirestore firestore;
    private MaterialButton saveChangesButton;
    private String facilityID;

    /**
     * Required empty constructor for Fragment initialization
     */
    public EditFacilityFragment(){
        // Empty Constructor
    }

    /**
     * Creates and initializes the fragment's view hierarchy
     * Sets up UI components and event listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.edit_facility, container, false);

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
        fetchFacilityDetails();
        setupSaveChangesButton();

        return rootView;
    }

    /**
     * Initializes all UI components and Firebase instance
     * Connects TextInputLayouts
     *
     * @param rootView The root view of the fragment
     */
    private void initializeViews(View rootView) {

        TextInputLayout editFacilityNameLayout = rootView.findViewById(R.id.input_editFacilityName);
        editName = (TextInputEditText) editFacilityNameLayout.getEditText();

        TextInputLayout editFacilityEmailLayout = rootView.findViewById(R.id.input_editFacilityEmail);
        editEmail = (TextInputEditText) editFacilityEmailLayout.getEditText();

        TextInputLayout editFacilityAddressLayout = rootView.findViewById(R.id.input_editFacilityAddress);
        editAddress = (TextInputEditText) editFacilityAddressLayout.getEditText();

        TextInputLayout editFacilityPhoneLayout = rootView.findViewById(R.id.input_editFacilityPhone);
        editPhone = (TextInputEditText) editFacilityPhoneLayout.getEditText();

        saveChangesButton = rootView.findViewById(R.id.saveFacilityChanges);
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Sets up the save changes button click listener
     * Validates inputs before proceeding with event creation
     */
    private void setupSaveChangesButton() {
        saveChangesButton.setOnClickListener(v -> {
            if (validateInputs()) {
                updateFacilityFields();
            }
        });
    }

    /**
     * Validates all required input fields
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (editName.getText().toString().trim().isEmpty() ||
                editEmail.getText().toString().trim().isEmpty() ||
                editAddress.getText().toString().trim().isEmpty() ||
                editPhone.getText().toString().trim().isEmpty()) {

            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Fetches the facility details from Firestore and populates the input fields.
     *
     * This method retrieves the current user's facility ID from the "loginProfile" collection
     * and then fetches the facility details from the "facilities" collection. The retrieved
     * details (Name, Email, Address, and Phone) are displayed in the respective input fields.
     *
     * If the user is not authenticated or the fetch operation fails, appropriate messages
     * are displayed to the user.
     */
    private void fetchFacilityDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = user.getUid();
        DocumentReference facilityIDRef = firestore.collection("loginProfile").document(userID);
        facilityIDRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                facilityID = document.getString("myFacility");
                if (facilityID != null) {
                    // Fetch facility details based on facilityID
                    firestore.collection("facilities").document(facilityID).get().addOnCompleteListener(facilityTask -> {
                        if (facilityTask.isSuccessful() && facilityTask.getResult() != null) {
                            DocumentSnapshot facilityDoc = facilityTask.getResult();
                            if (facilityDoc.exists()) {
                                // Populate the input fields with facility data
                                editName.setText(facilityDoc.getString("Name"));
                                editEmail.setText(facilityDoc.getString("Email"));
                                editAddress.setText(facilityDoc.getString("Address"));
                                editPhone.setText(facilityDoc.getString("Phone"));
                            } else {
                                Toast.makeText(getContext(), "Facility not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch facility details", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch facility ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the facility fields in Firestore with the data entered by the user.
     *
     * This method retrieves the current user's ID from Firebase Authentication, then fetches the user's
     * associated facility ID from the "loginProfile" collection. Once the facility ID is retrieved, it
     * updates the corresponding facility document with the user's input for Name, Email, Address, and Phone.
     *
     * If the user is not authenticated, a toast message is shown.
     */
    private void updateFacilityFields() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = user.getUid();
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        DocumentReference facilityIDRef = firestore.collection("loginProfile").document(userID);
        facilityIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    facilityID = document.getString("myFacility");
                    updateFirestoreFacility(facilityID, name, email, address, phone);
                }
            }
        });
    }

    /**
     * Updates the facility details in Firestore with the provided information.
     *
     * This method updates the "Name", "Email", "Address", and "Phone" fields of the facility document
     * in Firestore based on the given facility ID. Upon successful update, a success message is displayed
     * and the user is navigated to the "DisplayFacilityFragment". If the update fails, an error message is shown.
     *
     * @param facilityID The ID of the facility document to be updated in Firestore.
     * @param name The updated name of the facility.
     * @param email The updated email of the facility.
     * @param address The updated address of the facility.
     * @param phone The updated phone number of the facility.
     */
    private void updateFirestoreFacility(String facilityID, String name, String email, String address, String phone){
        firestore.collection("facilities")
                .document(facilityID)
                .update("Name", name, "Email", email, "Address", address, "Phone", phone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Facility updated successfully", Toast.LENGTH_SHORT).show();
                        // Navigate back or show success message
                        navigateToDisplayFacilityFragment(facilityID);
                    } else {
                        Toast.makeText(getContext(), "Failed to update facility", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Navigates to the display facility screen after successful facility updation
     *
     * @param facilityID The ID of the created event
     */
    private void navigateToDisplayFacilityFragment(String facilityID){
        DisplayFacilityFragment displayFacilityFragment = new DisplayFacilityFragment();

        // Pass the facilityId to the next fragment
        Bundle bundle = new Bundle();
        bundle.putString("facilityId", facilityID);
        displayFacilityFragment.setArguments(bundle);

        // Use FragmentTransaction to replace the current fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.OrganizerMenuFragment, displayFacilityFragment);
        transaction.addToBackStack(null);  // Optional: Add the transaction to the back stack so user can navigate back
        transaction.commit();
    }
}
