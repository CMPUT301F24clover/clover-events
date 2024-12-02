package com.example.luckyevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.luckyevent.organizer.conductLottery.Lottery;
import com.example.luckyevent.organizer.sendNotification.NotificationService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SampleOneEntrantService extends Service {
    private static final String TAG = "SampleOneEntrantService";
    private String eventId;
    private String eventName;
    private String userId;
    private ArrayList<String> entrantIdsList;
    private Lottery lottery;
    private FirebaseFirestore db;
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
        userId = intent.getStringExtra("userId");

        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events").document(eventId);
        waitingListRef = eventRef.collection("waitingList");

        getEventName();

        startSample();

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
     * Retrieves the event name from the document of the given event.
     */
    private void getEventName() {
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot snapshot = task.getResult();
                eventName = (String) snapshot.get("eventName");
            }
        });
    }

    /**
     * Retrieves the waiting list from the document of the given event and uses it to conduct the
     * lottery.
     */
    private void startSample() {
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
                    Toast.makeText(this, "Waiting list is empty. Cannot sample new entrant.", Toast.LENGTH_SHORT).show();
                    stopSelf();
                } else {
                    lottery = new Lottery(entrantIdsList, 1);
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
     * Updates the database to reflect the sample results.
     */
    private void setResult() {
        for (String winnersId : lottery.getWinners()) {
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                // prepare data that will be added to event's chosen entrants list
                Map<String, Object> chosenEntrant = new HashMap<>();
                chosenEntrant.put("userId", winnersId);
                chosenEntrant.put("invitationStatus", "Pending");

                transaction.set(eventRef.collection("chosenEntrants").document(winnersId), chosenEntrant);
                transaction.delete(waitingListRef.document(winnersId));
                transaction.update(db.collection("loginProfile").document(winnersId).collection("eventsJoined").document(eventId), "status", "Chosen");
                transaction.update(eventRef.collection("chosenEntrants").document(userId), "findReplacement", false);
                return null;
            }).addOnSuccessListener(result -> Log.d(TAG, "Successfully set lottery results in the database"))
            .addOnFailureListener(e -> Log.w(TAG, "Error setting lottery results in the database", e));
        }

        startNextActivity();
    }

    /**
     * Notifies the new sampled entrant
     */
    private void startNextActivity() {
        if (!lottery.getWinners().isEmpty()) {
            Intent intentWinners = new Intent(this, NotificationService.class);
            intentWinners.putStringArrayListExtra("entrantIds", lottery.getWinners());
            intentWinners.putExtra("eventId", eventId);
            intentWinners.putExtra("title", "Congratulations!");
            intentWinners.putExtra("description", String.format("You have been chosen to sign up for %s! Go to 'My Waiting Lists' to accept your invitation.", eventName));
            startService(intentWinners);
        }
        stopSelf();
    }
}
