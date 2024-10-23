package com.example.luckyevent.firebase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDB {
    public final String logTag = getClass().getSimpleName();
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private Context context;
    private final String emailPlaceholder = "@clover.com";

    public interface SignInCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public FirebaseDB(Context context){
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        this.context = context;
    }

    public void entrantSignUp(String userName, String password, String firstName, String lastName) {
        db.collection("loginEntrantProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        String loginEmail = userName + emailPlaceholder;

                        firebaseAuth.createUserWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();

                                        Map<String, Object> loginMap = new HashMap<>();
                                        loginMap.put("userName", userName);
                                        loginMap.put("firstName",firstName);
                                        loginMap.put("lastName", lastName);
                                        loginMap.put("userId", userId);// I stored the userId generated from the firebaseAuth
                                        loginMap.put("hasEventProfile", false);

                                        db.collection("loginEntrantProfile").document(userId).set(loginMap)
                                                .addOnCompleteListener(dbTask -> {
                                                    if (dbTask.isSuccessful()) {
                                                        Toast.makeText(context, "Successfully registered!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void signIn(String userName, String password,SignInCallback callback) {
        db.collection("loginEntrantProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String loginEmail = userName.toLowerCase() + emailPlaceholder;

                        firebaseAuth.signInWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure("Password is incorrect: " + authTask.getException().getMessage());
                                    }
                                });
                    } else {
                        callback.onFailure("Username not found");
                    }
                });
    }

    public void oragnizerSignUp(String userName, String password, String firstName, String lastName,String organizationName, int facilityCode) {
        db.collection("loginOranizerProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        String loginEmail = userName + emailPlaceholder;

                        firebaseAuth.createUserWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();

                                        Map<String, Object> loginMap = new HashMap<>();
                                        loginMap.put("userName", userName);
                                        loginMap.put("firstName",firstName);
                                        loginMap.put("lastName", lastName);
                                        loginMap.put("oragnizationName", organizationName);
                                        loginMap.put("facilityCode",facilityCode);
                                        loginMap.put("userId", userId);// I stored the userId generated from the firebaseAuth
                                        loginMap.put("hasFacilityProfile", false);

                                        db.collection("loginEntrantProfile").document(userId).set(loginMap)
                                                .addOnCompleteListener(dbTask -> {
                                                    if (dbTask.isSuccessful()) {
                                                        Toast.makeText(context, "Successfully registered!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show();
                    }
                });
    }





}
