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

    private FirebaseFirestore db;
    private String TAG = "Profile Controller";


    public ProfileController(ProfileSetup model, ProfileView view){
        this.model = model;

        this.db = FirebaseFirestore.getInstance();
    }
    public void registerProfile(String firstName, String lastName, String email, String  phoneNumber,OnSuccessListener<String> onSuccessListener){
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

                            HashMap<String, Object> data = new HashMap<>();
                            data.put("Email", email);
                            data.put("Phone Number", phoneNumber);
                            data.put("hasUserProfile",true);

                            profileRef.update(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG,"Profile updates with email and phone number");
                                            onSuccessListener.onSuccess(profileRef.getId());
                                        }
                                    });

                        } else{
                            Log.d(TAG,"Unable to find document matching this name",task.getException());
                        }
                    }
                });

    }

    public void editProfile(String docID, String firstName, String lastName, String email, String  phoneNumber){
        model.setName(firstName +" "+ lastName);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);

        DocumentReference profileRef = db.collection("loginProfile").document(docID);

        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("firstName", firstName);
        updateData.put("lastName", lastName);
        updateData.put("Email", email);
        updateData.put("Phone Number", phoneNumber);
        profileRef.update(updateData);



    }
    public void loadProfile(String docID, OnSuccessListener<ProfileSetup> onSuccessListener){
        DocumentReference profileIDRef = db.collection("loginProfile").document(docID);
        profileIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        String firstName = doc.getString("firstName");
                        String lastName = doc.getString("lastName");
                        String email = doc.getString("Email");
                        String phoneNumber = doc.getString("Phone Number");

                        model.setName(firstName + " " + lastName);
                        model.setEmail(email);
                        model.setPhoneNumber(phoneNumber);

                        onSuccessListener.onSuccess(model);


                    }
                }
                else{
                    Log.d(TAG,"Unable to find document matching this name",task.getException());
                }
            }
        });
    }
    public interface ProfileRegisteredListener{
        void onProfileRegistered(String documentID);

    }
}
