package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.example.luckyevent.activities.EntrantEventDetailsActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
/**
 * Displays a screen that allows the user to scan generated QR codes. On successfully scanning the QR code the user
 * is redirected to a screen containing the event details. The Scan QR feature was made possible using
 * Journeyapp's barcodescanner api.
 *
 * @author Aagam, Tola
 * @see EntrantEventDetailsActivity
 * @version 2
 * @since 1
 */




public class ScanQrFragment extends Fragment {
    private static final String TAG = "ScanQrFragment";
    private static final String QR_PREFIX = "LuckyEvent_";

    private MaterialButton scanButton;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_scan_qr_screen, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize scan button
        scanButton = view.findViewById(R.id.button_startScanning);
        scanButton.setOnClickListener(v -> startScanning());

        return view;
    }

    private void startScanning() {
        ScanOptions options = new ScanOptions()
                .setPrompt("Volume up to flash on")
                .setBeepEnabled(true)
                .setOrientationLocked(true)
                .setCaptureActivity(CaptureActivity.class);

        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String qrContent = result.getContents();

                    // Verify QR code format
                    if (qrContent.startsWith(QR_PREFIX)) {
                        String eventId = qrContent.substring(QR_PREFIX.length());
                        validateAndLaunchEventDetails(eventId);
                    } else {
                        Toast.makeText(getContext(), "Invalid QR code format", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void validateAndLaunchEventDetails(String eventId) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        launchEventDetailsActivity(documentSnapshot.getId());
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event", e);
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                });
    }

    private void launchEventDetailsActivity(String eventId) {
        Intent intent = new Intent(getActivity(), EntrantEventDetailsActivity.class);
        intent.putExtra("eventId", eventId);
        // Clear the back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}