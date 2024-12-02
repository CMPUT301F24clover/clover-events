package com.example.luckyevent.entrant.displayNotifsScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.R;
import com.example.luckyevent.entrant.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;

/**
 * Displays a list of the current user's notifications.
 *
 * @author Mmelve
 * @see Notification
 * @see NotificationListAdapter
 * @version 1
 * @since 1
 */
public class DisplayNotificationsFragment extends Fragment {
    private ArrayList<Notification> notifsList;
    private NotificationListAdapter listAdapter;
    private TextView textView;
    private CollectionReference notifRef;
    private ListenerRegistration reg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_screen, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String entrantId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            notifRef = db.collection("loginProfile").document(entrantId).collection("notifications");

            ListView listview = view.findViewById(R.id.customListView);
            notifsList = new ArrayList<>();
            listAdapter = new NotificationListAdapter(getContext(), notifsList, notifRef);
            listview.setAdapter(listAdapter);

            Toolbar toolbar = view.findViewById(R.id.topBar);
            toolbar.setTitle("Notifications");

            textView = view.findViewById(R.id.text_emptyList);
            textView.setVisibility(View.GONE);

            // If notifications are disabled, prevent the retrieval of the notification list
            if (UserSession.getInstance().isNotificationDisabled()){
                textView.setVisibility(View.VISIBLE);
                textView.setText(R.string.disable_notifications);
            } else {
                getNotifsList();
            }
        }

        return view;
    }

    /**
     * Retrieves all the documents in the user's sub-collection of notifications. These documents
     * are used to create a list of Notification objects.
     */
    private void getNotifsList() {
        reg = notifRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, error) -> {
            notifsList.clear();
            if (error != null) {
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                for (DocumentSnapshot notifDocument : snapshot.getDocuments()) {
                    String notifId = notifDocument.getString("notifId");
                    String title = notifDocument.getString("title");
                    String content = notifDocument.getString("content");
                    if (notifId != null && title != null && content != null) {
                        Notification notif = new Notification(notifId, title, content);
                        notifsList.add(notif);
                    }
                }
                listAdapter.notifyDataSetChanged();
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(R.string.no_notifications);
            }
        });
    }

    /**
     * Removes listener once fragment stops.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    /**
     * Removes listener once view is detached from fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reg != null) {
            reg.remove();
        }
    }
}
