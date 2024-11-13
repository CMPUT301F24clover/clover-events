package com.example.luckyevent;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.OutputStream;

public class QRDownloadService extends Service {
    private static final String TAG = "QRDownloadService";
    private FirebaseFirestore db;
    private String eventId;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "QR Download Service created");
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "QR Download Service started");

        if (intent == null) {
            Log.e(TAG, "Intent is null");
            stopSelf();
            return START_NOT_STICKY;
        }

        eventId = intent.getStringExtra("eventId");
        if (eventId == null) {
            Log.e(TAG, "Event ID not provided");
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            stopSelf();
            return START_NOT_STICKY;
        }

        DocumentReference docRef = db.collection("events").document(eventId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String qrContent = document.getString("qrContent");
                    if (qrContent != null) {
                        generateAndSaveQRCode(qrContent);
                    } else {
                        Log.e(TAG, "QR content is null");
                        Toast.makeText(this, "QR code content not found", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    }
                } else {
                    Log.e(TAG, "Document does not exist");
                    Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            } else {
                Log.e(TAG, "Error getting document", task.getException());
                Toast.makeText(this, "Failed to fetch QR code data", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void generateAndSaveQRCode(String qrContent) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrBitmap = barcodeEncoder.encodeBitmap(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );
            saveQRCodeToStorage(qrBitmap);
        } catch (WriterException e) {
            Log.e(TAG, "QR generation error", e);
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    private void saveQRCodeToStorage(Bitmap bitmap) {
        String filename = "Event_QR_" + eventId + ".png";
        boolean saved = false;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (imageUri != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(imageUri)) {
                        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                }
            } else {
                String path = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        filename,
                        "QR Code for event " + eventId
                );
                saved = path != null;
            }

            if (saved) {
                Toast.makeText(this, "QR code saved to gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving QR code", e);
            Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
        } finally {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "QR Download Service destroyed");
    }
}