package com.example.luckyevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Conducts the lottery: samples from the waiting list to get a list of winners and losers.
 *
 * @author Mmelve
 * @see Lottery
 * @version 1.5
 * @since 1
 */
public class LotteryService extends Service {
    private static final String TAG = "LotteryService";
    private String eventName;
    private ArrayList<String> waitingList;
    private int sampleSize;
    private Lottery lottery;
    private DocumentReference docRef;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        String eventId = intent.getStringExtra("eventId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docRef = db.collection("events").document(eventId);

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
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
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
                    waitingList = (ArrayList<String>) snapshot.get("waitingList");
                    if (waitingList == null || waitingList.isEmpty()) {
                        Toast.makeText(this, "Waiting list is not available. Cannot initiate lottery.", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    } else {
                        lottery = new Lottery(waitingList, sampleSize);
                        lottery.selectWinners();
                        setResult();
                    }
                } else {
                    Toast.makeText(this, "Cannot retrieve waiting list from database.", Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            } else {
                Toast.makeText(this, "Cannot retrieve waiting list from database.", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });
    }

    /**
     * Updates the document of the given event to reflect changes in the waiting list and winners
     * list.
     */
    private void setResult() {
        docRef
                .update("waitingList", lottery.getEntrants(),
                        "chosenEntrants", lottery.getWinners())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Document successfully updated!");
                    startNextActivity();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    stopSelf();
                });
    }

    /**
     * Notifies all of the lottery participants of the results.
     */
    private void startNextActivity() {
        if (!lottery.getWinners().isEmpty()) {
            Intent intentWinners = new Intent(this, NotificationService.class);
            intentWinners.putStringArrayListExtra("entrants", lottery.getWinners());
            intentWinners.putExtra("title", "Congratulations!");
            intentWinners.putExtra("description", String.format("You have been chosen to sign up for %s!", eventName));
            startService(intentWinners);
            if (!lottery.getEntrants().isEmpty()) {
                Intent intentLosers = new Intent(this, NotificationService.class);
                intentLosers.putStringArrayListExtra("entrants", lottery.getEntrants());
                intentLosers.putExtra("title", "Hello there!");
                intentLosers.putExtra("description", String.format("We regret to inform you that you have not been chosen to sign up for %s. Thank you for your interest.", eventName));
                startService(intentLosers);
            }
        }
        stopSelf();
    }
}
