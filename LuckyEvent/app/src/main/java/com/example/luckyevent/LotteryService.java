package com.example.luckyevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Conducts the lottery: samples from the waiting list to get a list of winners and losers.
 *
 * @author Mmelve
 * @see Lottery
 * @version 1
 * @since 1
 */
public class LotteryService extends Service {
    private static final String TAG = "LotteryActivity";
    private String eventName;
    private List<String> waitingList;
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
        sampleSize = intent.getIntExtra("sampleSize", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docRef = db.collection("events").document(eventId);

        getWaitingList();

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
    private void getWaitingList() {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    eventName = (String) snapshot.get("eventTitle");
                    waitingList = (List<String>) snapshot.get("waitingList");
                    if (waitingList != null) {
                        lottery = new Lottery(waitingList, sampleSize);
                        lottery.selectWinners();
                        setResult();
                    } else {
                        Toast.makeText(this, "Waiting list is empty. Cannot initiate lottery.", Toast.LENGTH_SHORT).show();
                        stopSelf();
                    }
                } else {
                    stopSelf();
                }
            } else {
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
     * Starts the next activity/service.
     */
    private void startNextActivity() {
        if (!lottery.getWinners().isEmpty()) {
            Intent serviceIntent = new Intent(this, AutoNotificationService.class);
            serviceIntent.putExtra("lottery", lottery);
            serviceIntent.putExtra("eventName", eventName);
            startService(serviceIntent);
        }
        stopSelf();
    }
}
