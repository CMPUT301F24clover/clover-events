package com.example.luckyevent.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.luckyevent.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A class implementing the list adapter for a list of profiles.
 *
 * @author Seyi
 * @see Profile
 * @version 1
 * @since 1
 */
public class ProfileListAdapter extends ArrayAdapter<Profile> {
    private List<Profile> originalList;
    private List<Profile> displayList;
    private List<Profile> firstNameFilteredList;
    private List<Profile> lastNameFilteredList;
    private String TAG = "ProfileListAdapter";
    private FirebaseFirestore db;

    public ProfileListAdapter(Context context, List<Profile> Profiles) {
        super(context, 0, Profiles);
        this.originalList = Profiles;
        this.displayList = new ArrayList<>(Profiles);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent, false);
        }

        //Initialize firestore and all the ui elements needed
        db = FirebaseFirestore.getInstance();
        displayList.addAll(originalList);
        Profile profile = displayList.get(position);

        TextView usernameTextView = convertView.findViewById(R.id.userName_textView);
        usernameTextView.setText(profile.getUserName());

        TextView fullnameTextView = convertView.findViewById(R.id.fullName_textView);
        fullnameTextView.setText(profile.getFullName());

        MaterialButton removeProfileButton = convertView.findViewById(R.id.remove_profile_button);
        //When clicked, remove all traces of the entrants profile
        removeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the document id of the entrant and remove all fields relating to their profile
                db.collection("loginProfile")
                        .whereEqualTo("userId", profile.getUserId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String documentId = document.getId();

                                db.collection("loginProfile")
                                        .document(documentId)
                                        .update(
                                                "Email", FieldValue.delete(),
                                                "Phone Number", FieldValue.delete(),
                                                "hasUserProfile", false
                                        )
                                        .addOnSuccessListener(aVoid ->
                                                {
                                                    Toast.makeText(getContext(), "Profile removed successfully!", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Fields modified successfully");
                                                }
                                        )
                                        .addOnFailureListener(e -> Log.e(TAG, "Error modifying fields", e));
                            }
                        });

            }
        });



        return convertView;
    }

    @Override
    public int getCount() {
        return originalList.size();
    }

    @Override
    public Profile getItem(int position) {
        return originalList.get(position);
    }

    /**
     * Filters the display list according to the entrant's username by using the query given
     * @param query this string represents the target username we are looking for
     */
    public void filterByUserName(String query){
        displayList.clear();
        if (query.isEmpty()) {
            displayList.addAll(originalList);
        } else {
            for (Profile profile : originalList) {
                if (profile.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    displayList.add(profile);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Filters the display list according to the entrant's first name by using the query given
     * @param query this string represents the target username we are looking for
     */
    public void filterByFirstName(String query) {
            for (Profile profile : originalList) {
                if (profile.getFirstName().toLowerCase().contains(query)) {
                    firstNameFilteredList.add(profile);
                }
            }
        notifyDataSetChanged();
    }

    /**
     * Filters the display list according to the entrant's last name by using the query given
     * @param query this string represents the target username we are looking for
     */
    public void filterByLastName(String query) {
        for (Profile profile : originalList) {
            if (profile.getLastName().toLowerCase().contains(query)) {
                lastNameFilteredList.add(profile);
            }
        }
        notifyDataSetChanged();
    }

}
