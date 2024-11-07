package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.luckyevent.Notification;
import com.example.luckyevent.NotificationListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the list of user notifications for a given user.
 *
 * @author Mmelve
 * @see Notification
 * @see NotificationListAdapter
 * @version 1
 * @since 1
 */
public class DisplayNotificationsActivity extends AppCompatActivity {
    private List<Notification> notifsList;
    private NotificationListAdapter listAdapter;
    private CollectionReference notifRef;
    private ListenerRegistration reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.entrant_list_screen);

        Intent intent = getIntent();
        String entrantId = intent.getStringExtra("entrantId");

        ListView listview = findViewById(R.id.customListView);
        notifsList = new ArrayList<>();
        listAdapter = new NotificationListAdapter(this, notifsList);
        listview.setAdapter(listAdapter);

        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setTitle("Notifications");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        notifRef = db.collection("loginProfile").document(entrantId).collection("notifications");

        getNotifsList();
    }

    /**
     * Retrieves all the documents in a given user's collection of notifications. These documents
     * are used to create a list of Notification objects.
     */
    private void getNotifsList() {
        reg = notifRef.addSnapshotListener((snapshot, error) -> {
            notifsList.clear();
            if (error != null) {
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                for (DocumentSnapshot notifSnapshot : snapshot.getDocuments()) {
                    String title = notifSnapshot.getString("title");
                    String content = notifSnapshot.getString("content");
                    Notification notif = new Notification(title, content);
                    notifsList.add(notif);
                    listAdapter.notifyDataSetChanged();
                }
            }

        });
    }

    /**
     * Removes listener once activity stops.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }
}
