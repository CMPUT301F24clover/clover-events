package com.example.luckyevent.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
    MaterialButton createEventButton;
    CheckBox geolocation;
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
        geolocation = findViewById(R.id.checkbox_geolocation);

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
        String geoLocation = geolocation.isChecked() ? "Geolocation enabled" : "Geolocation disabled";

        // Concatenate the input values
        String qrText = "Event Name: " + name + "\n" +
                "Date: " + eventDate + "\n" +
                "Description: " + eventDescription + "\n" +
                geoLocation;

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
        // Create a map to store event information
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventName", eventName.getText().toString());
        eventInfo.put("date", date.getText().toString());
        eventInfo.put("description", description.getText().toString());
        eventInfo.put("geolocation", geolocation.isChecked());
        eventInfo.put("qrCodeUrl", qrCodeUrl);

        // Save the event information to Firestore
        firestore.collection("events").add(eventInfo)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    e.printStackTrace();
                });
    }
}
