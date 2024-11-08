package com.example.luckyevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private ArrayList<String> entrants;
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

        entrants = intent.getStringArrayListExtra("entrants");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");

        if (!entrants.isEmpty()) {
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
     * entrant's "notifications" collection.
     */
    private void notifyEntrants() {
        for (String id: entrants) {
            String notifId = generateNotifId();
            Map<String, Object> notif = new HashMap<>();
            notif.put("title", title);
            notif.put("content", description);
            profilesRef.document(id).collection("notifications").document(notifId)
                    .set(notif)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Notification successfully written"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing notification", e));
        }
    }

    /**
     * Generates a document ID for the notification by using the current date and time.
     * @return A string containing the current date and time.
     */
    private String generateNotifId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CANADA);
        Date currentTime = Calendar.getInstance().getTime();
        return sdf.format(currentTime);
    }
}
