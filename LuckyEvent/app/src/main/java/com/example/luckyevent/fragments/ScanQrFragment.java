package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.example.luckyevent.activities.DisplayQrActivity;
import com.google.android.material.button.MaterialButton;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Displays a screen that allows the user to scan generated QR codes. On successfully scanning the QR code the user
 * is redirected to a screen containing the event details. The Scan QR feature was made possible using
 * Journeyapp's barcodescanner api.
 *
 * @author Aagam, Tola
 * @see DisplayQrActivity
 * @version 1
 * @since 1
 */
public class ScanQrFragment extends Fragment {

    private MaterialButton scanButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_scan_qr_screen, container, false);
        scanButton = view.findViewById(R.id.button_startScanning);
        scanButton.setOnClickListener(v -> scanCode());
        return view;
    }

    /**
     * Sets the scan options needed to scan the QR and calls the DisplayQrActivity if the contents of the QR code
     * has been successfully scanned
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            Intent intent = new Intent(getActivity(), DisplayQrActivity.class);
            startActivity(intent);
        }
    });
}
