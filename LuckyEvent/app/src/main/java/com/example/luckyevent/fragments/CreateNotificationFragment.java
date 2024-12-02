package com.example.luckyevent.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.luckyevent.NotificationService;
import com.example.luckyevent.R;

import java.util.ArrayList;

/**
 * A dialog fragment that inquires the user (organizer) for the information needed to create a
 * notification: a notification title and a notification description. Once the user inputs the
 * required information, NotificationService class is called to send the notification to the
 * desired list of entrants.
 *
 * @author Mmelve
 * @see NotificationService
 * @version 1
 * @since 1
 */
public class CreateNotificationFragment extends DialogFragment {
    private ArrayList<String> entrantIdsList;
    private String eventId;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.create_personal_notification, null);
        EditText editTitle = view.findViewById(R.id.edit_notif_title);
        EditText editDesc = view.findViewById(R.id.edit_notif_desc);

        if (getArguments() != null) {
            entrantIdsList = getArguments().getStringArrayList("entrantIdsList");
            eventId = getArguments().getString("eventId");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setTitle("New Notification")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", null)
                .create();

        dialog.setOnShowListener(dialog1 -> {
            Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            confirmButton.setOnClickListener(v -> {
                String title = editTitle.getText().toString();
                String description = editDesc.getText().toString();

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getContext(), "Missing field(s). Try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (title.length() > 50) {
                    Toast.makeText(getContext(), "Title is too long. Try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.length() > 200) {
                    Toast.makeText(getContext(), "Description is too long. Try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!entrantIdsList.isEmpty()) {
                    addNotif(entrantIdsList, title, description);
                } else {
                    Toast.makeText(getContext(), "No one to send notification to.", Toast.LENGTH_SHORT).show();
                }

                dismiss();
            });
        });
        return dialog;
    }

    /**
     * Passes all the necessary data to the NotificationService class so that it can send
     * the notification to the desired list of entrants.
     * @param entrantIdsList A list of the ids of entrants that will receive the notification.
     * @param title The title of the notification.
     * @param description The description of the notification.
     */
    private void addNotif(ArrayList<String> entrantIdsList, String title, String description) {
        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        serviceIntent.putStringArrayListExtra("entrantIds", entrantIdsList);
        serviceIntent.putExtra("eventId", eventId);
        serviceIntent.putExtra("title", title);
        serviceIntent.putExtra("description", description);
        getContext().startService(serviceIntent);
    }

    static CreateNotificationFragment newInstance() {
        return new CreateNotificationFragment();
    }
}
