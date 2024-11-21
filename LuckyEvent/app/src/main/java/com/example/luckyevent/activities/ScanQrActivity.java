package com.example.luckyevent.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.firebase.FirebaseDB;
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
public class ScanQrActivity extends AppCompatActivity {
    MaterialButton scanButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_scan_qr_screen);
        scanButton = findViewById(R.id.button_startScanning);

        scanButton.setOnClickListener(v->{
            scanCode();
        });
    }

    /**
     * Sets the scan options needed to scan the QR and calls the DisplayQrActivity if the contents of the QR code
     * has been successfully scanned
     */
    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents()!=null)
        {
            Intent intent = new Intent(ScanQrActivity.this, DisplayQrActivity.class);
            startActivity(intent);
        }
    });
}
