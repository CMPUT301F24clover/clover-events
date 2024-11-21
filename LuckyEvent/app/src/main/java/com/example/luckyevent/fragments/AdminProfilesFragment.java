package com.example.luckyevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.Profile;
import com.example.luckyevent.ProfileListAdapter;
import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.activities.AdminMenuActivity;
import com.example.luckyevent.activities.LoginActivity;
import com.example.luckyevent.activities.MenuActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminProfilesFragment extends Fragment {
    private static final String TAG = "AdminProfileFragment";
    private ArrayList<String> documentIds = new ArrayList<>();
    private FirebaseFirestore db;
    private List<Profile> userList;
    private ProfileListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.admin_edit_profile_page, container, false);
        ListView listView = rootView.findViewById(R.id.profile_listview);
        SearchView searchView = rootView.findViewById(R.id.searchView);
        adapter = new ProfileListAdapter(requireContext(), userList);
        listView.setAdapter(adapter);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://luckyevent-22fbd.firebasestorage.app");
        StorageReference storageRef = storage.getReference().child("userProfilePics");

        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        documentIds.add(item.getName());

                        Log.d(TAG, "File: " + item.getName());
                    }
                    fetchProfiles();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error listing files: " + e.getMessage());
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByUserName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterByUserName(newText);
                return true;
            }
        });


        return rootView;
    }

    public void fetchProfiles(){
        for (String userName : documentIds) {
            db.collection("loginProfile")
                    .whereEqualTo("userName", userName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Log.d(TAG, "fetchProfiles: Got a profile");
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String userId = document.getString("userId");

                            userList.add(new Profile(userName, firstName, lastName, userId));
                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "fetchProfiles: userList size = " + userList.size());
                            Log.d(TAG, "fetchProfiles: adapter size = " + adapter.getCount());
                        }
                    })
                    .addOnFailureListener(task -> {
                        Log.e(TAG, "fetchProfiles: Failed to get profile", task);
                    });
        }
    }
}
