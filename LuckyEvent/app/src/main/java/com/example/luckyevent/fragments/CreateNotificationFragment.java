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

public class CreateNotificationFragment extends DialogFragment {
    ArrayList<String> entrantIdsList;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.create_personal_notification, null);
        EditText editTitle = view.findViewById(R.id.edit_notif_title);
        EditText editDesc = view.findViewById(R.id.edit_notif_desc);

        if (getArguments() != null) {
            entrantIdsList = getArguments().getStringArrayList("entrantIdsList");
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

    private void addNotif(ArrayList<String> entrantIdsList, String title, String description) {
        Intent serviceIntent = new Intent(getContext(), NotificationService.class);
        serviceIntent.putStringArrayListExtra("entrants", entrantIdsList);
        serviceIntent.putExtra("title", title);
        serviceIntent.putExtra("description", description);
        getContext().startService(serviceIntent);
    }

    static CreateNotificationFragment newInstance() {
        return new CreateNotificationFragment();
    }
}
