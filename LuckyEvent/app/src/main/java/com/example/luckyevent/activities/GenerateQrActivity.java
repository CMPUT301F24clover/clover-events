package com.example.luckyevent.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
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
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
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
    MaterialButton createEventButton;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;
    private int selectedWaitListSize;
    private int selectedSampleSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        waitListSize = findViewById(R.id.waitingListSizeDropdown);
        sampleSize = findViewById(R.id.sampleSizeDropdown);

        TextInputLayout eventNameLayout = findViewById(R.id.input_eventName);
        eventName = (TextInputEditText) eventNameLayout.getEditText();

        TextInputLayout dateLayout = findViewById(R.id.input_date);
        date = (TextInputEditText) dateLayout.getEditText();

        TextInputLayout descriptionLayout = findViewById(R.id.input_description);
        description = (TextInputEditText) descriptionLayout.getEditText();

        List<Integer> waitingListChoices = Arrays.asList(20,40,60,80,100);
        List<Integer> sampleListChoices = Arrays.asList(10,30,50,70,90);


        ArrayAdapter<Integer> waitingListAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, waitingListChoices
        );

        ArrayAdapter<Integer> sampleSizeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,sampleListChoices
        );


        waitListSize.setAdapter(waitingListAdapter);
        sampleSize.setAdapter(sampleSizeAdapter);


        //get the item the user clicked on for the waitlist and sample size
        waitListSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedWaitListSize = waitingListChoices.get(i);
            }
        });
        sampleSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSampleSize = sampleListChoices.get(i);
            }
        });

        // Initialize createEventButton
        createEventButton = findViewById(R.id.button_createEvent);

        // Initialize Firebase
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventInfoToFirestore();
            }
        });
    }
    /*
    private void generateQRCode() {
        if (eventName == null || date == null || description == null || waitListSize == null || sampleSize == null) { // CHANGED: Added null checks
            Toast.makeText(this, "One or more input fields are not initialized properly", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = eventName.getText().toString();
        String eventDate = date.getText().toString();
        String eventDescription = description.getText().toString();
        String waitingListSizeValue = waitListSize.getText().toString();
        String samplingSizeValue = sampleSize.getText().toString();

        // Concatenate the input values
        String qrText = "Event Name: " + name + "\n" +
                "Date: " + eventDate + "\n" +
                "Description: " + eventDescription + "\n" +
                "Wait List: " + waitingListSizeValue + "\n" +
                "Sample Size: " + samplingSizeValue;


        if (!qrText.isEmpty()) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(qrText, BarcodeFormat.QR_CODE, 400, 400);
                uploadQRCodeToFirebase(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadQRCodeToFirebase(Bitmap bitmap) {
        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Create a reference to Firebase Storage
        StorageReference storageRef = firebaseStorage.getReference().child("qr_codes/" + System.currentTimeMillis() + ".png");
        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveQRCodeInfoToFirestore(uri.toString());
            });
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            exception.printStackTrace();
        });
    }
    */
    /**
     *This function saves the inputted event info into firestore. An event id generated alongside for
     * reference
     */
    private void saveEventInfoToFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { // CHANGED: Added null check for FirebaseUser
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = user.getUid();
        // Create a map to store event information
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName.getText().toString());
        eventInfo.put("date", date.getText().toString());
        eventInfo.put("description", description.getText().toString());
        eventInfo.put("WaitListSize", selectedWaitListSize);
        eventInfo.put("sampleSize", selectedSampleSize);
        //eventInfo.put("qrCodeUrl", qrCodeUrl);
        eventInfo.put("userID", userID);


        // Save the event information to Firestore
        firestore.collection("events").add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    String eventID = documentReference.getId();
                    addEventToProfile(userID,eventID);
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    e.printStackTrace();
                });
    }

    /**
     *This function adds the event id of an event to the profile document of an entrant.
     */
    private void addEventToProfile(String userID, String eventID) {
        Map<String, Object> eventIdMap = new HashMap<>();
        eventIdMap.put("eventID",eventID);

        firestore.collection("loginProfile")
                .document(userID)
                .collection("myEvents")
                .document(eventID)
                .set(eventIdMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GenerateQrActivity.this, "Added event ID to profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GenerateQrActivity.this, "Unable to add event ID to profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
