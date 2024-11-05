package com.example.luckyevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Sends a notification to the winners and losers of a given lottery regarding the results.
 *
 * @author Mmelve
 * @version 1
 * @see Lottery
 * @since 1
 */
public class AutoNotificationService extends Service {
    private static final String TAG = "AutoNotificationService";
    private String eventName;
    private List<String> winners;
    private List<String> losers;
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

        eventName = intent.getStringExtra("eventName");
        Lottery lottery = intent.getSerializableExtra("lottery", Lottery.class);
        if (lottery != null) {
            winners = lottery.getWinners();
            losers = lottery.getEntrants();

            if (!winners.isEmpty()) {
                notifyWinners();
            }
            if (!losers.isEmpty()) {
                notifyLosers();
            }
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
     * Creates a document that represents the automatic notification for all the winners of the
     * lottery. Then, adds the document to each winner's "notifications" collection.
     */
    private void notifyWinners() {
        for (String id: winners) {
            String notifId = generateNotifId();
            Map<String, Object> notif = new HashMap<>();
            notif.put("title", "Congratulations!");
            notif.put("content", String.format("You have been chosen to sign up for %s!", eventName));
            profilesRef.document(id).collection("notifications").document(notifId)
                    .set(notif)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Notification successfully written"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing notification", e));
        }
    }

    /**
     * Creates a document that represents the automatic notification for all the losers of the
     * lottery. Then, adds the document to each loser's "notifications" collection.
     */
    private void notifyLosers() {
        for (String id : losers) {
            String notifId = generateNotifId();
            Map<String, Object> notif = new HashMap<>();
            notif.put("title", "Sorry...");
            notif.put("content", String.format("You have not been chosen to sign up for %s.", eventName));
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
