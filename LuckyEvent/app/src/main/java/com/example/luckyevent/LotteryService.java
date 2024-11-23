package com.example.luckyevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Conducts the lottery: samples from the waiting list to get a list of winners and losers.
 *
 * @author Mmelve
 * @see Lottery
 * @see NotificationService
 * @version 2
 * @since 1
 */
public class LotteryService extends Service {
    private static final String TAG = "LotteryService";
    private String eventId;
    private String eventName;
    private ArrayList<String> entrantIdsList;
    private int sampleSize;
    private Lottery lottery;
    private DocumentReference eventRef;
    private CollectionReference waitingListRef;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        eventId = intent.getStringExtra("eventId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events").document(eventId);
        waitingListRef = eventRef.collection("waitingList");

        startLottery();

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
     * Retrieves the waiting list from the document of the given event and uses it to conduct the
     * lottery.
     */
    private void startLottery() {
        getSampleSize();

        waitingListRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                entrantIdsList = new ArrayList<>();
                for (QueryDocumentSnapshot entrantDocument : task.getResult()) {
                    String entrantId = entrantDocument.getString("userId");
                    if (entrantId != null) {
                        entrantIdsList.add(entrantId);
                    }
                }

                if (entrantIdsList.isEmpty()) {
                    Toast.makeText(this, "Waiting list is not available. Cannot initiate lottery.", Toast.LENGTH_SHORT).show();
                    stopSelf();
                } else {
                    lottery = new Lottery(entrantIdsList, sampleSize);
                    lottery.selectWinners();
                    setResult();
                }
            } else {
                Toast.makeText(this, "Cannot retrieve waiting list from database.", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });
    }

    /**
     * Retrieves the event sample size from the document of the given event.
     */
    private void getSampleSize() {
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot snapshot = task.getResult();
                eventName = (String) snapshot.get("eventName");
                try {
                    sampleSize = (int) snapshot.get("sampleSize");
                } catch (Exception e1) {
                    try {
                        sampleSize = Math.toIntExact((long) snapshot.get("sampleSize"));
                    } catch (Exception e2) {
                        Toast.makeText(this, "sampleSize type is invalid", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    }
                }
            }
        });
    }

    /**
     * Updates the document of the given event to reflect changes in the waiting list and winners
     * list.
     */
    private void setResult() {
        for (String winnersId : lottery.getWinners()) {
            waitingListRef.document(winnersId).delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Waitlisted entrant successfully deleted!"))
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error deleting waitlisted entrant", e);
                    });

            Map<String, Object> chosenEntrant = new HashMap<>();
            chosenEntrant.put("userId", winnersId);
            chosenEntrant.put("invitationStatus", "Pending");

            eventRef.collection("chosenEntrants").document(winnersId)
                    .set(chosenEntrant)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Chosen entrant successfully added!"))
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding chosen entrant", e);
                    });
        }

        startNextActivity();
    }

    /**
     * Notifies all of the lottery participants of the results.
     */
    private void startNextActivity() {
        if (!lottery.getWinners().isEmpty()) {
            Intent intentWinners = new Intent(this, NotificationService.class);
            intentWinners.putStringArrayListExtra("entrantIds", lottery.getWinners());
            intentWinners.putExtra("eventId", eventId);
            intentWinners.putExtra("title", "Congratulations!");
            intentWinners.putExtra("description", String.format("You have been chosen to sign up for %s! Go to 'My Waiting Lists' to accept your invitation.", eventName));
            startService(intentWinners);
            if (!lottery.getEntrants().isEmpty()) {
                Intent intentLosers = new Intent(this, NotificationService.class);
                intentLosers.putStringArrayListExtra("entrantIds", lottery.getEntrants());
                intentLosers.putExtra("eventId", eventId);
                intentLosers.putExtra("title", "Hello there!");
                intentLosers.putExtra("description", String.format("We regret to inform you that you have not been chosen to sign up for %s. Thank you for your interest.", eventName));
                startService(intentLosers);
            }
        }
        stopSelf();
    }
}
