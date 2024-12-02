package com.example.luckyevent.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.luckyevent.Facility;
import com.example.luckyevent.FacilityListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Amna
 * Fragment that Allows admins to remove and search facilties
 */
public class AdminBrowseFacilities extends Fragment {
    private FirebaseFirestore db;
    private List<Facility> facilityList;
    private FacilityListAdapter adapter;
    private static final String TAG = "AdminBrowseFacilities";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        facilityList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.browse_facilities_screen, container, false);
        Log.d(TAG, "layout inflated");
        ListView listView = rootView.findViewById(R.id.facListView);

        // Initialize adapter and set it to the ListView
        adapter = new FacilityListAdapter(requireContext(), facilityList);
        listView.setAdapter(adapter);

        // retrieve facilities from the database
        db.collection("facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Facility> newFacilties = new ArrayList<>();
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No facilities found.");
                            Toast.makeText(getContext(), "No active facilities found.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot doc : task.getResult()) {
                                try {
                                    String name = doc.getString("Name");
                                    String address = doc.getString("Address");
                                    String email = doc.getString("Email");
                                    String phone = doc.getString("Phone");
                                    long createdAt = doc.getLong("createdAt");
                                    String organizerId = doc.getString("organizerId");
                                    String status = doc.getString("status");

                                    // add the information to a facility object and add it to the list
                                    Facility facility = new Facility(name, address, email, phone, createdAt, organizerId, status);
                                    newFacilties.add(facility);
                                } catch (Exception e) {
                                    Log.e(TAG, "unable to retrieve facility data: ", e);
                                }
                            }
                            adapter.updateList(newFacilties);
                        }
                    } else {
                        Log.e(TAG, "Error fetching facilities: ", task.getException());
                        Toast.makeText(getContext(), "Failed to fetch facilities.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set up SearchView for filtering
        SearchView searchView = rootView.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByName(query);
                Log.d(TAG, "Search submitted: " + query);
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterByName(newText);
                Log.d(TAG, "Search text changed: " + newText);
                return true;
            }
        });


        return rootView;
    }
}


