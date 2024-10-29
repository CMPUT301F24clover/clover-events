package com.example.luckyevent.firebase;

import android.content.Context;
import android.provider.Settings;
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
    private String errorMessage;

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

    public void signUp(String userName, String password, String firstName, String lastName, String role, String organizationName, String facilityCode , SignInCallback callback) {
        db.collection("loginProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        String loginEmail = userName + emailPlaceholder;

                        firebaseAuth.createUserWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();

                                        Map<String, Object> loginMap = new HashMap<>();
                                        if("entrant".equals(role)){
                                            loginMap.put("userName", userName);
                                            loginMap.put("firstName",firstName);
                                            loginMap.put("lastName", lastName);
                                            loginMap.put("userId", userId);// I stored the userId generated from the firebaseAuth
                                            loginMap.put("role", role);// The only work around for firebase's lack of enums
                                            loginMap.put("hasUserProfile", false);
                                        }
                                        else{loginMap.put("userName", userName);
                                            loginMap.put("firstName",firstName);
                                            loginMap.put("lastName", lastName);
                                            loginMap.put("userId", userId);
                                            loginMap.put("role", role);
                                            loginMap.put("organizationName", organizationName);
                                            loginMap.put("facilityCode",facilityCode);
                                            loginMap.put("hasEventProfile", false);}


                                        db.collection("loginProfile").document(userId).set(loginMap)
                                                .addOnCompleteListener(dbTask -> {
                                                    if (dbTask.isSuccessful()) {
                                                        callback.onSuccess();
                                                    } else {
                                                        errorMessage = dbTask.getException() != null
                                                                ? dbTask.getException().getMessage()
                                                                : "Unknown error occurred";
                                                        callback.onFailure("Registration failed: " + errorMessage);
                                                    }
                                                });
                                    } else {
                                        errorMessage = authTask.getException() != null
                                                ? authTask.getException().getMessage()
                                                : "Unknown error occurred";
                                        callback.onFailure("Registration failed: " + errorMessage);
                                    }
                                });
                    } else {
                        errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error occurred";
                        callback.onFailure("Username Is already taken: " + errorMessage);
                    }
                });
    }

    public void signIn(String userName, String password,SignInCallback callback, Boolean signInOnMain) {
        db.collection("loginProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String role = document.getString("role");

                        String loginEmail = userName.toLowerCase() + emailPlaceholder;
                        if((("entrant".equals(role) || "admin".equals(role)) && signInOnMain == true) || "organizer".equals(role) && signInOnMain == false){
                            firebaseAuth.signInWithEmailAndPassword(loginEmail, password)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                            callback.onSuccess();
                                        } else {
                                            callback.onFailure("Password is incorrect: " + authTask.getException().getMessage());
                                        }
                                    });
                        }
                        else{
                            if(signInOnMain == true){
                                callback.onFailure("Admin or entrant role is required to sign in");
                            }
                            else{
                                callback.onFailure("Organizer role is required to sign in");
                            }
                        }
                    } else {
                        callback.onFailure("Username not found");
                    }
                });
    }

    public void deviceSignIn(SignInCallback callback){
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        //these are fake emails and passwords to user firebase auth
        String loginEmail = deviceID + emailPlaceholder;
        String password = deviceID + "clover";

        db.collection("loginProfile").whereEqualTo("userName", deviceID).get()
                .addOnCompleteListener(task -> {
                    //check if the device is already registered
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        firebaseAuth.signInWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        callback.onSuccess();
                                    } else {
                                        String errorMessage = authTask.getException() != null
                                                ? authTask.getException().getMessage()
                                                : "Password is incorrect";
                                        callback.onFailure("Device registration failed: " + errorMessage);
                                    }
                                });

                    }
                    else{
                        //creates a new user if the user is not in the user base
                        firebaseAuth.createUserWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();

                                        Map<String, Object> loginMap = new HashMap<>();
                                        loginMap.put("userName", deviceID);//I used the deviceID as the username
                                        loginMap.put("firstName",null);
                                        loginMap.put("lastName", null);
                                        loginMap.put("userId", userId);// I stored the userId generated from the firebaseAuth
                                        loginMap.put("role", "entrant");
                                        loginMap.put("hasUserProfile", false);
                                        callback.onSuccess();
                                        db.collection("loginProfile").document(userId).set(loginMap)
                                                .addOnCompleteListener(dbTask -> {
                                                    if (dbTask.isSuccessful()) {
                                                        callback.onSuccess();
                                                    } else {
                                                        errorMessage = dbTask.getException() != null
                                                                ? dbTask.getException().getMessage()
                                                                : "Unknown error occurred";
                                                        callback.onFailure("Device registration failed: " + errorMessage);
                                                    }
                                                });
                                    }
                                    else{
                                        errorMessage = authTask.getException() != null
                                                ? authTask.getException().getMessage()
                                                : "Unknown error occurred";
                                        callback.onFailure("Device registration failed: " + errorMessage);
                                    }
                                        });

                    }
                });


    }

    public void autoSignin (SignInCallback callback){

        //We can use the firebaseAuth to check if user is signed in into this device and get their device id
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            callback.onSuccess();
        } else {
            callback.onFailure("No user is currently signed in.");
        }
    }

    public void joinWaitlist(){

    }




}
