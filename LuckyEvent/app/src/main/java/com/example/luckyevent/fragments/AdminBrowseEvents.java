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
import com.example.luckyevent.R;
import com.example.luckyevent.activities.EventDisplay;
import com.example.luckyevent.activities.EventListAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Amna
 * Fragment that Allows admins to browse events and remove events and hashed qr code data
 */
public class AdminBrowseEvents extends Fragment {
    private FirebaseFirestore db;
    private List<EventDisplay> eventDisplayList;
    private EventListAdapter adapter;
    private String TAG = "AdminBrowseEvents";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventDisplayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // here im inflating the fragment layout
        View rootView = inflater.inflate(R.layout.admin_browse_events, container, false);
        ListView listView = rootView.findViewById(R.id.eventListView);

        //set up the adapter for the list view
        adapter = new EventListAdapter(requireContext(), eventDisplayList);
        listView.setAdapter(adapter);

        // retrieve the events from the database
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        List<EventDisplay> newEventDisplay = new ArrayList<>();
                        if (task.getResult().isEmpty()){
                            Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                        }else {
                            // loop through each event
                            for (DocumentSnapshot doc : task.getResult()){
                                try {
                                    String eventName = doc.getString("eventName");
                                    String description = doc.getString("description");
                                    String date = doc.getString("date");
                                    String dueDate = doc.getString("dueDate");
                                    String time = doc.getString("time");
                                    String orgID = doc.getString("organizerId");
                                    String status = doc.getString("status");

                                    // create and EventDisplay object and add it to the list
                                    EventDisplay eventDisplay = new EventDisplay(eventName, description, date, dueDate, time, orgID, status);
                                    newEventDisplay.add(eventDisplay);
                                } catch (Exception e){
                                    Toast.makeText(getContext(), "Error getting event data", Toast.LENGTH_SHORT).show();
                                }
                            }
                            // update the adapter with the new items
                            adapter.updateList(newEventDisplay);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error getting event data", Toast.LENGTH_SHORT).show();
                    }
                });

        // here I implemented the searching functionality
        SearchView searchView = rootView.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                adapter.filterByName(newQuery);
                return true;
            }
        });
        return rootView;
    }

}
