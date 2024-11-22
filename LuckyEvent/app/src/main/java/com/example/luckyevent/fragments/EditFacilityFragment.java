package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.R;
import com.example.luckyevent.activities.OrganizerMenuActivity;
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

    public EditFacilityFragment(){
        // Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.edit_facility, container, false);

        initializeViews(rootView);
        setupSaveChangesButton();

        return rootView;
    }

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

    private void setupSaveChangesButton() {
        saveChangesButton.setOnClickListener(v -> {
            if (validateInputs()) {
                updateFacilityFields();
            }
        });
    }

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
