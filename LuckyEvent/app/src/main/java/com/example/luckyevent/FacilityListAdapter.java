package com.example.luckyevent;

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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Amna
 * Adapter to use for displaying list of faciltites and functionality to remove faciltites and search by facility name
 */
public class FacilityListAdapter extends ArrayAdapter<Facility> {
    private List<Facility> originalList;
    private List<Facility> displayList;
    private static final String TAG = "FacilityListAdapter";
    private FirebaseFirestore db;


    /**
     * initalize the adapter with the facilitates
     * @param context
     * @param facilities
     */
    public FacilityListAdapter(Context context, List<Facility> facilities) {
        super(context, 0, facilities);
        this.originalList = new ArrayList<>(facilities);
        this.displayList = new ArrayList<>(facilities);
        this.db = FirebaseFirestore.getInstance();
        Log.d(TAG, "adapter created with " + facilities.size() + " facilities.");
    }
    public int getCount(){
        return displayList.size(); // get the size of list
    }

    public Facility getItem(int position){
        return displayList.get(position); // get position of the item
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.facility_box, parent, false);
        }


        Facility facility = getItem(position);
        if (facility != null) {
            TextView facilityNameTextView = convertView.findViewById(R.id.facName);
            TextView facilityAddressTextView = convertView.findViewById(R.id.facDescription);
            Button removeFacility = convertView.findViewById(R.id.facRemoveButton);
            facilityNameTextView.setText(facility.getName());
            facilityAddressTextView.setText(facility.getAddress());

            // remove button functionality
            removeFacility.setOnClickListener(view -> {
                db.collection("facilities")
                        .whereEqualTo("organizerId", facility.getOrganizerId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String docID = task.getResult().getDocuments().get(0).getId();
                                db.collection("facilities").document(docID)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Facility Removed", Toast.LENGTH_SHORT).show();
                                            displayList.remove(position); // remove the item from the display
                                            notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "unable to remove facility: ", e);
                                            Toast.makeText(getContext(), "error removing the facility", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Log.e(TAG, "facility not found");
                                Toast.makeText(getContext(), "Facility doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        } else {
            Log.e(TAG, "Facility doesn't exist at: " + position);
        }
        return convertView;
    }

    /**
     * update the facility list and update the adapter
     * @param newFacilties
     */
    public void updateList(List<Facility> newFacilties){
        originalList.clear();
        originalList.addAll(newFacilties); // update the list with the facilities
        displayList.clear();
        displayList.addAll(newFacilties); // update the display with the facilities
        notifyDataSetChanged(); // notify the adapter
    }


}

