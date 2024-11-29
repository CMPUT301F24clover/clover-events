package com.example.luckyevent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyevent.Facility;
import com.example.luckyevent.FacilityListAdapter;
import com.example.luckyevent.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseFacilities extends Fragment {
    private String TAG = "AdminBrowseFacilities";
    private FirebaseFirestore db;
    private List<Facility> facilityList;
    private FacilityListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        facilityList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.browse_facilities_screen, container, false);
        ListView listView = rootView.findViewById(R.id.facilitiesRecyclerView);
        SearchView searchView = rootView.findViewById(R.id.search_bar);

        adapter = new FacilityListAdapter(requireContext(), facilityList);
        listView.setAdapter(adapter);

        db.collection("facilities")
                .whereEqualTo("status", "active")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot doc : task.getResult()){
                            String name = doc.getString("Name");
                            String address = doc.getString("Address");
                            String email = doc.getString("Email");
                            String phone = doc.getString("Phone");
                            long createdAt = doc.getLong("createdAt");
                            String organizerId = doc.getString("organizerId");
                            String status = doc.getString("status");

                            facilityList.add(new Facility(name, address, email, phone, createdAt, organizerId, status));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to get facilities", Toast.LENGTH_SHORT).show();
                    }
                });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String neww) {
                adapter.filterByName(neww);
                return true;
            }
        });
        return rootView;
    }
}
