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

/**
 * @author Amna
 * this is the controller class that handles profile creation, editing profile fields, and allows users to view profile details
 */
public class ProfileController {
    private ProfileSetup model;
    private FirebaseFirestore db;
    private Context context;


    /**
     * uses the profileSetup model
     * @param model
     * @param context
     */
    public ProfileController(ProfileSetup model, Context context){
        this.model = model;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    /**
     * Registers a profile for the current user by finding the current users id, then add the fields to the profile
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param onSuccessListener
     */
    public void registerProfile(String firstName, String lastName, String email, String  phoneNumber,OnSuccessListener<String> onSuccessListener){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid(); // retrieve the user ID

        // set up the model data
        model.setName(firstName +" "+ lastName);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);

        // find the current signed in user based on the user ID
        db.collection("loginProfile")
                .document(userID)
                .get()
                .addOnCompleteListener(task ->  {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                // get the first and last name from the document
                                String dbfirstName = doc.getString("firstName");
                                String dblastName = doc.getString("lastName");
                                if (firstName.equals(dbfirstName) && lastName.equals(dblastName)) {
                                    // check if the name the user entered matches the one in the db
                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("Email", email);
                                    data.put("Phone Number", phoneNumber.isEmpty() ? "" : phoneNumber);
                                    data.put("hasUserProfile", true);
                                    //update the fields in the db document
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

    /**
     * Edits/updates the first and last name, email, and phone number fields in the database,
     * @param docID this is the document id of the profile that needs to be updated
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     */
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

        // update the fields in the db document
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

    /**
     * Loads up the users profile information (which includes their name, email, and phone number (which is a field that can be empty since its optional)
     * @param docID
     * @param onSuccessListener
     */
    public void loadProfile(String docID, OnSuccessListener<ProfileSetup> onSuccessListener){
        if (docID == null) {
            Toast.makeText(context, "Document ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference profileIDRef = db.collection("loginProfile").document(docID);
        profileIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        // get the given fields and set it in the model
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
