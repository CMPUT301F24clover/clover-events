package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.Notification;
import com.example.luckyevent.NotificationListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class DisplayNotificationsFragment extends Fragment {
    private List<Notification> notifsList;
    private NotificationListAdapter listAdapter;
    private CollectionReference notifRef;
    private ListenerRegistration reg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_list_screen, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String entrantId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            notifRef = db.collection("loginProfile").document(entrantId).collection("notifications");

            ListView listview = view.findViewById(R.id.customListView);
            notifsList = new ArrayList<>();
            listAdapter = new NotificationListAdapter(getContext(), notifsList);
            listview.setAdapter(listAdapter);

            Toolbar toolbar = view.findViewById(R.id.topBar);
            toolbar.setTitle("Notifications");

            getNotifsList();

        }

        return view;
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
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Removes listener once activity stops.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }
}
