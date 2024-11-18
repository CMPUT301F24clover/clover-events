package com.example.luckyevent.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.luckyevent.R;
import com.google.android.material.button.MaterialButton;
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

public class CreateEventFragment extends Fragment {

    private TextInputEditText eventName;
    private TextInputEditText date;
    private TextInputEditText description;
    private AutoCompleteTextView waitListSize;
    private AutoCompleteTextView sampleSize;
    private MaterialButton createEventButton;
    private FirebaseFirestore firestore;
    private int selectedWaitListSize;
    private int selectedSampleSize;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.create_event, container, false);

        initializeViews(rootView);
        setupDropdowns();
        setupCreateEventButton();

        return rootView;
    }

    private void initializeViews(View rootView) {
        waitListSize = rootView.findViewById(R.id.waitingListSizeDropdown);
        sampleSize = rootView.findViewById(R.id.sampleSizeDropdown);

        TextInputLayout eventNameLayout = rootView.findViewById(R.id.input_eventName);
        eventName = (TextInputEditText) eventNameLayout.getEditText();

        TextInputLayout dateLayout = rootView.findViewById(R.id.input_date);
        date = (TextInputEditText) dateLayout.getEditText();

        TextInputLayout descriptionLayout = rootView.findViewById(R.id.input_description);
        description = (TextInputEditText) descriptionLayout.getEditText();

        createEventButton = rootView.findViewById(R.id.button_createEvent);
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupDropdowns() {
        List<Integer> waitingListChoices = Arrays.asList(20, 40, 60, 80, 100);
        List<Integer> sampleListChoices = Arrays.asList(10, 30, 50, 70, 90);

        ArrayAdapter<Integer> waitingListAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, waitingListChoices
        );

        ArrayAdapter<Integer> sampleSizeAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, sampleListChoices
        );

        waitListSize.setAdapter(waitingListAdapter);
        sampleSize.setAdapter(sampleSizeAdapter);

        waitListSize.setOnItemClickListener((adapterView, view, i, l) ->
                selectedWaitListSize = waitingListChoices.get(i));

        sampleSize.setOnItemClickListener((adapterView, view, i, l) ->
                selectedSampleSize = sampleListChoices.get(i));
    }

    private void setupCreateEventButton() {
        createEventButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveEventInfoToFirestore();
            }
        });
    }

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

    private void generateQRCode(String eventId) {
        try {
            // Create QR content with prefix for identification
            String qrContent = "LuckyEvent_" + eventId;

            // Generate QR code bitmap
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrBitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);

            // Update the event with QR code info
            Map<String, Object> qrUpdate = new HashMap<>();
            qrUpdate.put("qrContent", qrContent);

            firestore.collection("events")
                    .document(eventId)
                    .update(qrUpdate)
                    .addOnSuccessListener(aVoid -> {
                        // Handle the QR code generated event
                        // You can pass the qrBitmap to some listener or UI component if necessary
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to update QR code info", Toast.LENGTH_SHORT).show());

        } catch (WriterException e) {
            Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEventInfoToFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = user.getUid();
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

        firestore.collection("events")
                .add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    addEventToProfile(userID, eventId);
                    generateQRCode(eventId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create event", Toast.LENGTH_SHORT).show();
                });
    }

    private void addEventToProfile(String userID, String eventID) {
        firestore.collection("loginProfile")
                .document(userID)
                .update("myEvents", FieldValue.arrayUnion(eventID))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add event to profile", Toast.LENGTH_SHORT).show());
    }
}
