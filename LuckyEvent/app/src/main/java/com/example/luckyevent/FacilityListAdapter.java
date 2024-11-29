package com.example.luckyevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FacilityListAdapter extends ArrayAdapter<Facility> {
    private List<Facility> originalList;
    private List<Facility> displayList;
    private String TAG = "FacilityListAdapter";
    private FirebaseFirestore db;

    public FacilityListAdapter(Context context, List<Facility> facilities){
        super(context, 0, facilities);
        this.originalList = facilities;
        this.displayList = new ArrayList<>(facilities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.facility_box, parent, false);
        }
        db = FirebaseFirestore.getInstance();

        Facility facility = displayList.get(position);
        TextView facilityNameTextView = convertView.findViewById(R.id.facName);
        facilityNameTextView.setText(facility.getName());

        TextView facilityAddressTextView = convertView.findViewById(R.id.facDescription);
        facilityAddressTextView.setText(facility.getAddress());
        Button removeFacility = convertView.findViewById(R.id.facRemoveButton);

        // Here you can finish implementing the remove button stuff
        removeFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("facilities")
                        .whereEqualTo("organizerId",facility.getOrganizerId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                String docID = doc.getId();

                                db.collection("facilities")
                                        .document(docID)
                                        .update("status","inactive")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(),"Facility Removed",Toast.LENGTH_SHORT).show();
                                                displayList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),"Facility was not removed due to an error",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
        });
        return convertView;
    }

    public int getCount(){
        return displayList.size();
    }
    public void filterByName(String query){
        displayList.clear();
        if (query.isEmpty()){
            displayList.addAll(originalList);
        } else {
            for (Facility facility : originalList){
                if (facility.getName().toLowerCase().contains(query.toLowerCase())){
                    displayList.add(facility);
                }
            }
        }
        notifyDataSetChanged();
    }
}
