package com.example.luckyevent.organizer.sendNotification;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Sends a notification to the given group of entrants.
 *
 * @author Mmelve
 * @version 1
 * @since 1
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private ArrayList<String> entrantIds;
    private String eventId;
    private String title;
    private String description;
    private CollectionReference profilesRef;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("loginProfile");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        entrantIds = intent.getStringArrayListExtra("entrantIds");
        eventId = intent.getStringExtra("eventId");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");

        if (!entrantIds.isEmpty()) {
            notifyEntrants();
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Creates a document that represents the notification. Then, adds the document to each
     * entrant's "notifications" sub-collection.
     */
    private void notifyEntrants() {
        for (String entrantId: entrantIds) {
            String notifId = generateNotifId();
            Map<String, Object> notif = new HashMap<>();
            notif.put("notifId", notifId);
            notif.put("title", title);
            notif.put("content", description);
            profilesRef.document(entrantId).collection("notifications").document(notifId)
                    .set(notif)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Notification successfully written!");
                        showToast("Entrants have been notified!");
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error writing notification", e);
                        showToast("Couldn't send notification.");
                    });
        }
    }

    /**
     * Generates a document ID for the notification by using the current date and time, as well as
     * the event ID.
     * @return A unique ID for the notification document.
     */
    private String generateNotifId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss_", Locale.CANADA);
        String currentTime = LocalDateTime.now().format(formatter);
        return currentTime + eventId;
    }

    /**
     * Displays a toast message to the user about whether or not the notification sent.
     * @param message The message shown to the user.
     */
    private void showToast(String message) {
        Handler main = new Handler(getMainLooper());
        main.post(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
}
