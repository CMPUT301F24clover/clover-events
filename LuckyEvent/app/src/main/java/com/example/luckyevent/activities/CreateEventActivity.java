package com.example.luckyevent.activities;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckyevent.R;
import java.io.OutputStream;

public class CreateEventActivity extends AppCompatActivity implements GenerateQrActivity.QRCodeGeneratedListener {
    private String currentEventId;
    private Bitmap currentQRBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialize GenerateQrActivity
        GenerateQrActivity generateQrActivity = new GenerateQrActivity();
        generateQrActivity.setQRCodeGeneratedListener(this);
    }

    @Override
    public void onQRCodeGenerated(Bitmap qrBitmap, String eventId) {
        currentQRBitmap = qrBitmap;
        currentEventId = eventId;

        // Auto-download QR code
        downloadQRCode();

        Toast.makeText(this, "Event created successfully! QR code saved.", Toast.LENGTH_LONG).show();
    }

    private void downloadQRCode() {
        if (currentQRBitmap == null) {
            return;
        }

        try {
            String fileName = "event_qr_" + currentEventId + ".png";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri imageUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    try (OutputStream outputStream = getContentResolver().openOutputStream(imageUri)) {
                        currentQRBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    }
                }
            } else {
                // Older versions handling
                java.io.File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                java.io.File file = new java.io.File(path, fileName);
                try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
                    currentQRBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
        }
    }
}