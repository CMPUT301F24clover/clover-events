package com.example.luckyevent.admin;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.luckyevent.organizer.facility.Facility;
import com.example.luckyevent.organizer.facility.FacilityListAdapter;
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
    private Button searchButton;
    private EditText searchEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        facilityList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.browse_facilities_screen, container, false);
        Log.d(TAG, "layout inflated");
        ListView listView = rootView.findViewById(R.id.facListView);
        searchButton = rootView.findViewById(R.id.search_facility_button);
        searchEditText = rootView.findViewById(R.id.searchEditText);

        // Initialize adapter and set it to the ListView
        adapter = new FacilityListAdapter(requireContext(), facilityList);
        listView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString().trim();
                if (query.isEmpty()){
                    Toast.makeText(getContext(), "Please enter text before searching", Toast.LENGTH_SHORT).show();
                } else{
                    searchFacilties(query);
                }
            }
        });

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


        return rootView;
    }
    private void searchFacilties(String query){
        db.collection("facilities")
                .get()
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful()){
                        List<Facility> resultsFacilties = new ArrayList<>();
                        task.getResult().forEach(doc ->{
                            String name = doc.getString("Name");
                            if (name != null && name.toLowerCase().contains(query.toLowerCase())){
                                String address = doc.getString("Address");
                                String email = doc.getString("Email");
                                String phone = doc.getString("Phone");
                                long createdAt = doc.getLong("createdAt");
                                String organizerId = doc.getString("organizerId");
                                String status = doc.getString("status");

                                // add the information to a facility object and add it to the list
                                Facility facility = new Facility(name, address, email, phone, createdAt, organizerId, status);
                                resultsFacilties.add(facility);
                            }
                        });
                        adapter.updateList(resultsFacilties);
                        if (resultsFacilties.isEmpty()){
                            Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "Error searching", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


