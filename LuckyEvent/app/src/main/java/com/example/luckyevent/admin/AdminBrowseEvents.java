package com.example.luckyevent.admin;

import android.os.Bundle;
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
    private Button searchButton;
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventDisplayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // here im inflating the fragment layout
        View rootView = inflater.inflate(R.layout.admin_browse_events, container, false);
        ListView listView = rootView.findViewById(R.id.eventListView);
        searchButton = rootView.findViewById(R.id.search_event_button);
        searchEditText = rootView.findViewById(R.id.searchEditText);

        //set up the adapter for the list view
        adapter = new EventListAdapter(requireContext(), eventDisplayList);
        listView.setAdapter(adapter);

        // when they click on the search button get their input and call the searchEvents function
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString().trim();
                if (query.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a search first", Toast.LENGTH_SHORT).show();
                }else{
                    searchEvents(query);
                }
            }
        });

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
                                    String qrContent = doc.contains("qrContent") ? doc.getString("qrContent") : "";

                                    // create and EventDisplay object and add it to the list
                                    EventDisplay eventDisplay = new EventDisplay(eventName, description, date, dueDate, time, orgID, status, qrContent);
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

        return rootView;
    }
    // filter the results based on the search
    private void searchEvents(String query){
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        List<EventDisplay> searchResults = new ArrayList<>();
                        task.getResult().forEach(doc ->{
                            String eventName = doc.getString("eventName");
                            if (eventName != null && eventName.toLowerCase().contains(query.toLowerCase())){ // find the results that match the input
                                String description = doc.getString("description");
                                String date = doc.getString("date");
                                String dueDate = doc.getString("dueDate");
                                String time = doc.getString("time");
                                String orgID = doc.getString("organizerId");
                                String status = doc.getString("status");
                                String qrContent = doc.contains("qrContent") ? doc.getString("qrContent") : "";

                                // create a new eventDisplay object and add it to the results
                                EventDisplay eventDisplay = new EventDisplay(eventName, description, date, dueDate, time, orgID, status, qrContent);
                                searchResults.add(eventDisplay);

                            }
                        });
                        adapter.updateList(searchResults);

                        if (searchResults.isEmpty()){
                            Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "Error searching", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
