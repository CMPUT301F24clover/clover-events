package com.example.luckyevent.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.luckyevent.QRDownloadService;
import com.example.luckyevent.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CreateEventFragment handles the creation of new events in the LuckyEvent application.
 * This fragment provides functionality for:
 * - Creating events with customizable parameters
 * - Generating QR codes for event identification
 * - Storing event data in Firebase Firestore
 * - Managing geolocation requirements
 * - Handling waiting list and sample size configurations
 *
 * The fragment integrates with Firebase for data persistence and uses ZXing for QR code generation.
 *
 * @author Aagam, Tola, Amna
 * @see ScanQrFragment
 * @see QRDownloadService
 * @version 2
 * @since 1
 */
public class CreateEventFragment extends Fragment {

    // UI Components
    private TextInputEditText eventName;
    private TextInputEditText dueDate;
    private TextInputEditText date;
    private TextInputEditText description;
    private AutoCompleteTextView waitListSize;
    private AutoCompleteTextView sampleSize;
    private MaterialButton createEventButton;
    private MaterialCheckBox geolocationCheckbox;

    // Firebase instance
    private FirebaseFirestore firestore;

    // Selected values for dropdowns
    private int selectedWaitListSize;
    private int selectedSampleSize;

    /**
     * Required empty constructor for Fragment initialization
     */
    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Creates and initializes the fragment's view hierarchy
     * Sets up UI components and event listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_event, container, false);

        initializeViews(rootView);
        setupDropdowns();
        setupCreateEventButton();

        return rootView;
    }

    /**
     * Initializes all UI components and Firebase instance
     * Sets default values for checkboxes and connects TextInputLayouts
     *
     * @param rootView The root view of the fragment
     */
    private void initializeViews(View rootView) {
        waitListSize = rootView.findViewById(R.id.waitingListSizeDropdown);
        sampleSize = rootView.findViewById(R.id.sampleSizeDropdown);
        geolocationCheckbox = rootView.findViewById(R.id.geolocation_checkbox);
        geolocationCheckbox.setChecked(false);  // Default value

        // Initialize TextInputLayouts and their EditTexts
        TextInputLayout eventNameLayout = rootView.findViewById(R.id.input_eventName);
        eventName = (TextInputEditText) eventNameLayout.getEditText();

        TextInputLayout dateLayout = rootView.findViewById(R.id.input_due_date);
        date = (TextInputEditText) dateLayout.getEditText();

        TextInputLayout descriptionLayout = rootView.findViewById(R.id.input_description);
        description = (TextInputEditText) descriptionLayout.getEditText();

        createEventButton = rootView.findViewById(R.id.button_createEvent);
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Configures dropdown menus for waiting list and sample sizes
     * Sets up adapters and item click listeners
     */
    private void setupDropdowns() {
        // Predefined choices for waiting list and sample sizes
        List<Integer> waitingListChoices = Arrays.asList(20, 40, 60, 80, 100);
        List<Integer> sampleListChoices = Arrays.asList(10, 30, 50, 70, 90);

        // Create and set adapters for dropdowns
        ArrayAdapter<Integer> waitingListAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, waitingListChoices
        );

        ArrayAdapter<Integer> sampleSizeAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, sampleListChoices
        );

        waitListSize.setAdapter(waitingListAdapter);
        sampleSize.setAdapter(sampleSizeAdapter);

        // Set up item selection listeners
        waitListSize.setOnItemClickListener((adapterView, view, i, l) ->
                selectedWaitListSize = waitingListChoices.get(i));

        sampleSize.setOnItemClickListener((adapterView, view, i, l) ->
                selectedSampleSize = sampleListChoices.get(i));
    }

    /**
     * Sets up the create event button click listener
     * Validates inputs before proceeding with event creation
     */
    private void setupCreateEventButton() {
        createEventButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveEventInfoToFirestore();
            }
        });
    }

    /**
     * Validates all required input fields
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (eventName.getText().toString().trim().isEmpty() ||
                date.getText().toString().trim().isEmpty() ||
                description.getText().toString().trim().isEmpty() ||
                waitListSize.getText().toString().trim().isEmpty() ||
                sampleSize.getText().toString().trim().isEmpty()) {

            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Generates QR code for the event and updates Firestore with QR content
     * Uses ZXing library for QR code generation
     *
     * @param eventId The unique identifier of the created event
     */
    private void generateQRCode(String eventId) {
        try {
            // Create QR content with application-specific prefix
            String qrContent = "LuckyEvent_" + eventId;

            // Generate QR code bitmap
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrBitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);

            // Update Firestore with QR content
            Map<String, Object> qrUpdate = new HashMap<>();
            qrUpdate.put("qrContent", qrContent);

            firestore.collection("events")
                    .document(eventId)
                    .update(qrUpdate)
                    .addOnSuccessListener(aVoid -> {
                        // QR code generation successful
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to update QR code info", Toast.LENGTH_SHORT).show());

        } catch (WriterException e) {
            Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves event information to Firebase Firestore
     * Creates new event document and updates user profile
     */
    private void saveEventInfoToFirestore() {
        if (!validateInputs()) {
            return;
        }

        // Verify user authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Creating event...", Toast.LENGTH_SHORT).show();

        String userID = user.getUid();
        // Prepare event data
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName.getText().toString().trim());
        eventInfo.put("date", date.getText().toString().trim());
        eventInfo.put("description", description.getText().toString().trim());
        eventInfo.put("waitListSize", selectedWaitListSize);
        eventInfo.put("sampleSize", selectedSampleSize);
        eventInfo.put("currentWaitList", 0);
        eventInfo.put("organizerId", userID);
        eventInfo.put("status", "active");
        eventInfo.put("createdAt", System.currentTimeMillis());
        eventInfo.put("geolocationRequired", geolocationCheckbox.isChecked());

        // Save event to Firestore
        firestore.collection("events")
                .add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    addEventToProfile(userID, eventId);
                    generateQRCode(eventId);
                    navigateToDisplayEvents(eventId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create event", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Adds the created event ID to the user's profile
     *
     * @param userID The ID of the current user
     * @param eventID The ID of the created event
     */
    private void addEventToProfile(String userID, String eventID) {
        firestore.collection("loginProfile")
                .document(userID)
                .update("myEvents", FieldValue.arrayUnion(eventID))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add event to profile", Toast.LENGTH_SHORT).show());
    }
    /**
     * Navigates to the event details screen after successful event creation
     *
     * @param eventId The ID of the created event
     */
    private void navigateToEventDetails(String eventId) {
        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("facilityId", eventId);
        eventDetailsFragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.OrganizerMenuFragment, eventDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}