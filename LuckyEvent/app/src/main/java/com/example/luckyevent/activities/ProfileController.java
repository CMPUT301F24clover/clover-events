package com.example.luckyevent.activities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileController {
    private ProfileSetup model;
    private FirebaseFirestore db;
    private Context context;

    public ProfileController(ProfileSetup model, Context context){
        this.model = model;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }
    public void registerProfile(String firstName, String lastName, String email, String  phoneNumber,OnSuccessListener<String> onSuccessListener){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        model.setName(firstName +" "+ lastName);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);

        db.collection("loginProfile")
                .document(userID)
                .get()
                .addOnCompleteListener(task ->  {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                String dbfirstName = doc.getString("firstName");
                                String dblastName = doc.getString("lastName");
                                if (firstName.equals(dbfirstName) && lastName.equals(dblastName)) {
                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("Email", email);
                                    data.put("Phone Number", phoneNumber.isEmpty() ? "" : phoneNumber);
                                    data.put("hasUserProfile", true);
                                    doc.getReference().update(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(context, "Profile registered successfully", Toast.LENGTH_SHORT).show();
                                                    onSuccessListener.onSuccess(doc.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Unable to register Profile", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(context,"The entered name does not match the one on your login profile. Please use that one",Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(context,"No profile match this name",Toast.LENGTH_SHORT).show();
                            }

                        } else{
                            Toast.makeText(context,"Unable to find profile name",Toast.LENGTH_SHORT).show();
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

        profileRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed to update profile",Toast.LENGTH_SHORT).show();
                    }
                });
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
                        Toast.makeText(context,"Profile loaded",Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(context,"Profile not found",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(context,"Unable to load profile",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
