package com.example.luckyevent.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.luckyevent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Amna
 * Adapter that displays the events in a list view
 */
public class EventListAdapter extends ArrayAdapter<EventDisplay> {
    private List<EventDisplay> originalList;
    private List<EventDisplay> displayList;
    private String TAG = "EventListAdapter";
    private FirebaseFirestore db;

    /**
     *  initializes the adapter with the event information
     * @param context
     * @param eventDisplays // the list of events we want to display
     */
    public EventListAdapter(Context context, List<EventDisplay> eventDisplays) {
        super(context, 0, eventDisplays);
        this.originalList = new ArrayList<>(eventDisplays);
        this.displayList = new ArrayList<>(eventDisplays);
        this.db = FirebaseFirestore.getInstance();

    }

    public int getCount(){
        return displayList.size(); // return the size of the list
    }
    public EventDisplay getItem(int position){
        return  displayList.get(position); // return the position of an event
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_event_item, parent,false);
        }

        EventDisplay eventDisplay = getItem(position);
        if (eventDisplay != null){
            TextView eventNameTextView = convertView.findViewById(R.id.eventName);
            TextView eventDescriptionTextView = convertView.findViewById(R.id.eventDescription);
            Button removeEventButton = convertView.findViewById(R.id.eventRemoveButton);
            Button removeQrCodeButton = convertView.findViewById(R.id.eventRemoveQrData);

            //set the name and description
            eventNameTextView.setText(eventDisplay.getEventName());
            eventDescriptionTextView.setText(eventDisplay.getDescription());

            //remove button stuff here:
            removeEventButton.setOnClickListener(view -> {
                db.collection("events")
                        .whereEqualTo("eventName", eventDisplay.getEventName())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String docID = task.getResult().getDocuments().get(0).getId();
                                db.collection("events").document(docID)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Event Removed", Toast.LENGTH_SHORT).show();
                                            displayList.remove(position);
                                            notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Unable to remove event", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "No event found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error finding event", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });

            // remove qr hashed data
            removeQrCodeButton.setOnClickListener(view -> {
                db.collection("events")
                        .whereEqualTo("eventName", eventDisplay.getEventName())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                String docID = task.getResult().getDocuments().get(0).getId();
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                if (doc.contains("qrContent")){
                                    db.collection("events").document(docID)
                                            .update("qrContent","")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), "QR code removed", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Unable to remove QR code", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(getContext(), "QR code doesn't exist", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "No event found ", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(getContext(), "Error removing QR code", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });

        } else {
            Toast.makeText(getContext(), "Event doesn't exist", Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    /**
     * updates the event list
     * @param newEventDisplay
     */
    public void updateList(List<EventDisplay> newEventDisplay){
        originalList.clear();
        originalList.addAll(newEventDisplay);
        displayList.clear();
        displayList.addAll(newEventDisplay);
        notifyDataSetChanged();

    }

    /**
     *  filters the event based on input in the search bar
     * @param query // the input entered in the search
     */
    public void filterByName(String query){
        displayList.clear();
        if (query.isEmpty()){
            displayList.addAll(originalList);
        }else{
            for (EventDisplay eventDisplay : originalList){
                if (eventDisplay.getEventName().toLowerCase().contains(query.toLowerCase())){
                    displayList.add(eventDisplay);
                }
            }
        }
        notifyDataSetChanged();
    }

}
