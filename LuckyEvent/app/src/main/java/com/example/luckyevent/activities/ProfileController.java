package com.example.luckyevent.activities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileController {
    private ProfileSetup model;
    private ProfileView view;
    private FirebaseFirestore db;
    private String TAG = "Profile Controller";


    public ProfileController(ProfileSetup model, ProfileView view){
        this.model = model;
        this.view = view;
        this.db = FirebaseFirestore.getInstance();
    }
    public void registerProfile(String firstName, String lastName, String email, String  phoneNumber){
        model.setName(firstName +" "+ lastName);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);

        db.collection("loginProfile")
                .whereEqualTo("firstName",firstName)
                .whereEqualTo("lastName",lastName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().isEmpty()){
                                Log.d(TAG,"Profile under this account already registered");
                            }
                            else{
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("firstName", firstName);
                                data.put("lastName", lastName);
                                data.put("Email", email);
                                data.put("Phone Number", phoneNumber);
                                data.put("hasUserProfile",true);

                                db.collection("Profiles").add(data)
                                        .addOnSuccessListener(documentReference -> {
                                            String docID = documentReference.getId();
                                            loadProfile(docID);
                                        });
                            }
                        } else{
                            Log.d(TAG,"Unable to find document matching this name",task.getException());
                        }
                    }
                });

    }

    public void editProfile(String firstName, String lastName, String email, String  phoneNumber){
        model.setName(firstName +" "+ lastName);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);

        db.collection("loginProfile")
                .whereEqualTo("firstName",firstName)
                .whereEqualTo("lastName",lastName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentReference profileRef = task.getResult().getDocuments().get(0).getReference();
                            HashMap<String, Object> updateData = new HashMap<>();
                            updateData.put("email", email);
                            updateData.put("phoneNumber", phoneNumber);
                            profileRef.update(updateData);

                        }
                        else{
                            Log.d(TAG,"Unable to find document matching this name",task.getException());
                        }
                    }
                });
    }
    public void loadProfile(String docID){
        DocumentReference profileIDRef = db.collection("loginProfile").document(docID);
        profileIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        String firstName = doc.getString("firstName");
                        String lastName = doc.getString("lastName");
                        String email = doc.getString("email");
                        String phoneNumber = doc.getString("phoneNumber");

                        model.setName(firstName + " " + lastName);
                        model.setEmail(email);
                        model.setPhoneNumber(phoneNumber);

                        view.loadProfileItems(model);
                    }
                }
                else{
                    Log.d(TAG,"Unable to find document matching this name",task.getException());
                }
            }
        });
    }
}
