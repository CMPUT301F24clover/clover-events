package com.example.luckyevent.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.luckyevent.Profile;
import com.example.luckyevent.ProfileListAdapter;
import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * Displays the ui elements that are needed for the deletion of profiles by the administrator
 * This fragments modifies the target document's fields to accomplish this
 * The ProfileListAdapter also allows the admin to browse the profile elements
 *
 * @author Seyi
 * @see UserSession
 * @see AdminMenuActivity
 * @version 1
 * @since 1
 */
public class AdminProfilesFragment extends Fragment {
    private static final String TAG = "AdminProfileFragment";
    private FirebaseFirestore db;
    private List<Profile> userList;
    private ProfileListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.admin_edit_profile_page, container, false);

        // Initialize the variables according to their corresponding UI elements
        ListView listView = rootView.findViewById(R.id.profile_listview);
        SearchView searchView = rootView.findViewById(R.id.searchView);
        adapter = new ProfileListAdapter(requireContext(), userList);
        listView.setAdapter(adapter);

        // Get all the documents that have their role field as entrant
        db.collection("loginProfile")
                .whereEqualTo("role", "entrant")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Log.d(TAG, "fetchProfiles: Got a profile");
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        // For each document retrieved, use the fields to create a new profile and add that profile into a userList
                        for(DocumentSnapshot document :documents){
                            String userName = document.getString("userName");
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String userId = document.getString("userId");
                            userList.add(new Profile(userName, firstName, lastName, userId));
                            adapter.notifyDataSetChanged();

                        }
                        Log.d(TAG, "fetchProfiles: userList size = " + userList.size());
                        Log.d(TAG, "fetchProfiles: adapter size = " + adapter.getCount());
                    }
                })
                .addOnFailureListener(task -> {
                    Log.e(TAG, "fetchProfiles: Failed to get profile", task);
                });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // By default when the query is submitted, filter the adapter by username
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByUserName(query);
                return true;
            }

            // By default when the query text is changed, filter the adapter by username
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterByUserName(newText);
                return true;
            }
        });


        return rootView;
    }

}
