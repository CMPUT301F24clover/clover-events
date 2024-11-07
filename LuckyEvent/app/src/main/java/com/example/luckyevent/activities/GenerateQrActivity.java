package com.example.luckyevent.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class GenerateQrActivity extends AppCompatActivity {

    private TextInputEditText eventName;
    private TextInputEditText date;
    private TextInputEditText description;
    private TextInputEditText waitListSize;
    private TextInputEditText sampleSize;
    MaterialButton createEventButton;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        eventName = findViewById(R.id.input_eventName);
        date = findViewById(R.id.input_date);
        description = findViewById(R.id.input_description);
        createEventButton = findViewById(R.id.button_createEvent);
        waitListSize = findViewById(R.id.input_waitingListSize);
        sampleSize = findViewById(R.id.input_sampleSize);

        // Initialize Firebase
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });
    }

    private void generateQRCode() {
        String name = eventName.getText().toString();
        String eventDate = date.getText().toString();
        String eventDescription = description.getText().toString();
        String waitingList = waitListSize.getText().toString();
        String samplingSize = sampleSize.getText().toString();

        // Concatenate the input values
        String qrText = "Event Name: " + name + "\n" +
                "Date: " + eventDate + "\n" +
                "Description: " + eventDescription + "\n" +
                "Sample Size: " + samplingSize + "\n" +
                "Waiting List Size: " + waitingList;


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

    private void saveQRCodeInfoToFirestore(String qrCodeUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        // Create a map to store event information
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName.getText().toString());
        eventInfo.put("date", date.getText().toString());
        eventInfo.put("description", description.getText().toString());
        eventInfo.put("sampleSize", sampleSize.getText().toString());
        eventInfo.put("waitListSize", waitListSize.getText().toString());
        eventInfo.put("qrCodeUrl", qrCodeUrl);
        eventInfo.put("userID", userID);





        // Save the event information to Firestore
        firestore.collection("events")
                .add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    String eventID = documentReference.getId();
                    addEventToProfile(userID, eventID);
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    e.printStackTrace();
                });
    }

    private void addEventToProfile(String userID, String eventID) {
        Map<String, Object> eventIDMap = new HashMap<>();
        eventIDMap.put("eventID",eventID);

        firestore.collection("loginProfile")
                .document(userID)
                .collection("myEvents")
                .document(eventID)
                .set(eventIDMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Event name added to loginProfile
                        Toast.makeText(GenerateQrActivity.this,"Added event ID to profile",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GenerateQrActivity.this,"Error adding event ID to login profile",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
