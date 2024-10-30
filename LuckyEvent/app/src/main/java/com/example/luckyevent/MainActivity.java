package com.example.luckyevent;

import static android.media.MediaSyncEvent.createEvent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private boolean loggedIn;
    private EditText editTextDescription, editTextStartDate, editTextEndDate;
    private ImageView imageViewQRCode;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize Firebase Database
        db = FirebaseFirestore.getInstance();

        if (!loggedIn) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.entrant_screen_template);
            Toolbar toolbar = findViewById(R.id.topBar);
            toolbar.setTitle("Home");
            editTextDescription = findViewById(R.id.editTextDescription);
            editTextStartDate = findViewById(R.id.editTextStartDate);
            editTextEndDate = findViewById(R.id.editTextEndDate);
            imageViewQRCode = findViewById(R.id.imageViewQRCode);
            Button buttonCreateEvent = findViewById(R.id.buttonCreateEvent);
            Button buttonScanQR = findViewById(R.id.scan_button);

            buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createEvent();
                }
            });

            buttonScanQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startScan();
                }
            });
        }
    }

    private void createEvent() {
        String description = editTextDescription.getText().toString();
        String startDate = editTextStartDate.getText().toString();
        String endDate = editTextEndDate.getText().toString();

        String eventDetails = "Description: " + description + "\nStart: " + startDate + "\nEnd: " + endDate;

        generateQRCode(eventDetails);
    }

    private void generateQRCode(String eventDetails) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(eventDetails, BarcodeFormat.QR_CODE, 400, 400);
            imageViewQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void startScan() {
        ScanQR.installScanner(this, new ScanQR.InstallScannerCallback() {
            @Override
            public void onInstallSuccess() {
                ScanQR.startScan(MainActivity.this, new ScanQR.ScanCallback() {
                    @Override
                    public void onScanSuccess(String result) {
                        Toast.makeText(MainActivity.this, "Scan Result: " + result, Toast.LENGTH_SHORT).show();
                        addToWaitlist(result);  // Add the scanned result to waitlist
                    }

                    @Override
                    public void onScanCancelled() {
                        Toast.makeText(MainActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onScanFailure(Exception e) {
                        Toast.makeText(MainActivity.this, "Scan failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onInstallFailure() {
                Toast.makeText(MainActivity.this, "Failed to install scanner module", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToWaitlist(String entrantInfo) {
        // Create a new entrant document with a unique ID
        Map<String, Object> entrant = new HashMap<>();
        entrant.put("info", entrantInfo);

        db.collection("waitlist") // Replace "waitlist" with your collection name
                .add(entrant)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Added to waitlist successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to add to waitlist.", Toast.LENGTH_SHORT).show();
                });
    }
    /*
    private void getWaitlist() {
        db.collection("waitlist")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Firestore", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }*/
}
