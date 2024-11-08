package com.example.luckyevent.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.ScanQR;
import com.example.luckyevent.firebase.FirebaseDB;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity deals with event creation and  QR code generation. These functions work together to
 * store the generated QR codes and events both locally and on firestore. Additional functions are present
 * to store the generated event ids in the entrant's profile
 *
 * @author Aagam, Tola, Amna
 * @see ScanQrFragment
 * @see ScanQR

 * @version 1
 * @since 1
 */
public class GenerateQrActivity extends AppCompatActivity {
    private TextInputEditText eventName;
    private TextInputEditText date;
    private TextInputEditText description;
    private AutoCompleteTextView waitListSize;
    private AutoCompleteTextView sampleSize;
    private MaterialButton createEventButton;
    private FirebaseFirestore firestore;
    private int selectedWaitListSize;
    private int selectedSampleSize;
    private QRCodeGeneratedListener qrCodeGeneratedListener;

    public interface QRCodeGeneratedListener {
        void onQRCodeGenerated(Bitmap qrBitmap, String eventId);
    }

    public void setQRCodeGeneratedListener(QRCodeGeneratedListener listener) {
        this.qrCodeGeneratedListener = listener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        initializeViews();
        setupDropdowns();
        setupCreateEventButton();
    }

    private void initializeViews() {
        waitListSize = findViewById(R.id.waitingListSizeDropdown);
        sampleSize = findViewById(R.id.sampleSizeDropdown);

        TextInputLayout eventNameLayout = findViewById(R.id.input_eventName);
        eventName = (TextInputEditText) eventNameLayout.getEditText();

        TextInputLayout dateLayout = findViewById(R.id.input_date);
        date = (TextInputEditText) dateLayout.getEditText();

        TextInputLayout descriptionLayout = findViewById(R.id.input_description);
        description = (TextInputEditText) descriptionLayout.getEditText();

        createEventButton = findViewById(R.id.button_createEvent);
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupDropdowns() {
        List<Integer> waitingListChoices = Arrays.asList(20, 40, 60, 80, 100);
        List<Integer> sampleListChoices = Arrays.asList(10, 30, 50, 70, 90);

        ArrayAdapter<Integer> waitingListAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, waitingListChoices
        );

        ArrayAdapter<Integer> sampleSizeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, sampleListChoices
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

            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
                        if (qrCodeGeneratedListener != null) {
                            qrCodeGeneratedListener.onQRCodeGenerated(qrBitmap, eventId);
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update QR code info", Toast.LENGTH_SHORT).show());

        } catch (WriterException e) {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
  

    /**
     *This function saves the inputted event info into firestore. An event id generated alongside for
     * reference
     */
    private void saveEventInfoToFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
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
        eventInfo.put("waitListMembers", Arrays.asList());
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
                    Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     *This function adds the event id of an event to the profile document of an entrant.
     */
    private void addEventToProfile(String userID, String eventID) {
        Map<String, Object> eventIdMap = new HashMap<>();
        eventIdMap.put("eventID", eventID);
        eventIdMap.put("addedAt", System.currentTimeMillis());

        firestore.collection("loginProfile")
                .document(userID)
                .collection("myEvents")
                .document(eventID)
                .set(eventIdMap)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add event to profile", Toast.LENGTH_SHORT).show());
    }
}