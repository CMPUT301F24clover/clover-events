package com.example.luckyevent.firebase;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.luckyevent.ScanQR;
import com.example.luckyevent.fragments.ScanQrFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This class deals with the login section of the app. It works directly with firestore and firebase storage to store the
 * credentials and images of entrants and organizers
 *
 * @author seyi
 * @version 2
 * @since 1
 */
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

    /**
     * This function deals with the entrant and organizer sign up functionality. New documents are
     * created in the loginProfile collection for each entrant and organizer
     */
    public void signUp(String userName, String password, String firstName, String lastName, String role, String organizationName, String facilityCode , SignInCallback callback) {
        db.collection("loginProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    // Check if the user's username exists in the database and create a new firebase auth account if the username doesn't exist
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        String loginEmail = userName + emailPlaceholder;

                        // If the firebase auth account has been successfully created, add the user's details into the database
                        firebaseAuth.createUserWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userId = firebaseUser.getUid();

                                        Map<String, Object> loginMap = new HashMap<>();
                                        //Modify the user's details based on their role
                                        if("entrant".equals(role)){
                                            loginMap.put("userName", userName);
                                            loginMap.put("firstName",firstName);
                                            loginMap.put("lastName", lastName);
                                            loginMap.put("userId", userId); // I stored the userId generated from the firebaseAuth
                                            loginMap.put("hasUserProfile", false);
                                            loginMap.put("notificationsDisabled", false);

                                            if (userName.toLowerCase().contains("admin".toLowerCase())) {
                                                loginMap.put("role", "admin");
                                            } else {
                                                loginMap.put("role", role); // The only work around for firebase's lack of enums
                                            }


                                        }
                                        else{loginMap.put("userName", userName);
                                            loginMap.put("firstName",firstName);
                                            loginMap.put("lastName", lastName);
                                            loginMap.put("userId", userId);
                                            loginMap.put("role", role);
                                            loginMap.put("facilityName", organizationName);
                                            loginMap.put("facilityCode",facilityCode);
                                            loginMap.put("hasEventProfile", false);}

                                        //Store the details into the loginProfile collection
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
                    }
                    //If the username is already in use, use a toast to inform the user that the username is taken
                    else {
                        errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error occurred";
                        callback.onFailure("Username Is already taken: " + errorMessage);
                    }
                });
    }

    /**
     * This function deals with the signing in functionality of the app
     * It uses the login profile collection to check if the user is registered before attempting
     * to sign in via firebase auth
     */
    public void signIn(String userName, String password,SignInCallback callback, Boolean signInOnMain) {
        // Checks if their username exists in the database
        db.collection("loginProfile").whereEqualTo("userName", userName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String role = document.getString("role");
                        // This is a workaround firebase auth's inability to register users using a username and password
                        String loginEmail = userName.toLowerCase() + emailPlaceholder;

                        // If the user's role is an entrant or admin and they signed in on the main page, proceed with the sign in process
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
                        // If the username is not found, inform the user by calling on the onFailure method
                        callback.onFailure("Username not found");
                    }
                });
    }

    /**
     * This function allows the entrant to sign in via device id
     * A new firebase auth account is created if the device id is not present in the loginProfile
     * collection
     */
    public void deviceSignIn(SignInCallback callback){
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // These are fake emails and passwords created for the use of firebase auth
        String loginEmail = deviceID + emailPlaceholder;
        String password = deviceID + "clover";

        db.collection("loginProfile").whereEqualTo("userName", deviceID).get()
                .addOnCompleteListener(task -> {
                    // Check if the device is already registered
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        firebaseAuth.signInWithEmailAndPassword(loginEmail, password)
                                .addOnCompleteListener(authTask -> {
                                    //If give the sign in process is successful proceed call on the onSuccess method, else call on the onFailure method
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
                                        loginMap.put("notificationsDisabled", false);
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

    /**
     * This method implements  the auto sign in feature
     * It was implemented using FirebaseAuth's getCurrentUser function
     */
    public void autoSignin (SignInCallback callback){

        //We can use the firebaseAuth to check if user is signed in into this device and get their device id
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            callback.onSuccess();
        } else {
            callback.onFailure("No user is currently signed in.");
        }
    }

    /**
     * This a callback method that is used for the confirmation of a successful upload to firebase storage
     */
    public interface UploadCallback {
        void onUploadSuccess();
        void onUploadFailure(String errorMessage);
    }

    /**
     * This a callback method that is used for confirming if the users image uri has successfully updated on firebase storage
     */
    public interface UpdateProfPicCallBack{
        void onUpdateSuccess();
        void onUpdateFailure(String errorMessage);
    }

    /**
     * This method uploads the provided image uri into the project's firebase storage
     * All documents in this collection are identified by the user's user id
     */
    public void uploadProfileToFirebase(Uri imageUri, String userId, UploadCallback callback) {
        if (firebaseAuth.getCurrentUser() != null) {
            Log.e("FirebaseDB", "uploadProfileToFirebase: A user is logged in");
        } else {
            Log.e("FirebaseDB", "uploadProfileToFirebase: No user is logged in");
        }

        Log.e("FirebaseDB", "imageUri:" + imageUri);
        if (imageUri != null) {
            try {
                // This portion of the code tries convert the image uri into an input stream
                // It then uploads the converted input stream into the firebase storage
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://luckyevent-22fbd.firebasestorage.app");
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                String path = "userProfilePics/" + userId;
                StorageReference storageRef = storage.getReference().child(path);

                assert inputStream != null;
                storageRef.putStream(inputStream)
                        .addOnSuccessListener(taskSnapshot -> {
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                //After succesfully uploading the image to firebase storage
                                // the image url is retrieved and stored into firestore
                                String downloadUrl = uri.toString();
                                saveProfileToFirestore(downloadUrl, userId);
                                callback.onUploadSuccess();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseDb", "Error: " + e.getMessage(), e);
                            callback.onUploadFailure(e.getMessage());
                        });
            } catch (FileNotFoundException e) {
                Log.e("FirebaseDB", "File not found: " + e.getMessage(), e);
                callback.onUploadFailure(e.getMessage());
            }
        } else {
            Log.e("FirebaseDB", "Image Uri is null.");
            callback.onUploadFailure("Image Uri is null.");
        }
    }


    /**
     * This method saves the provided image url into firestore
     * All documents created are named after the user id provided
     */
    private void saveProfileToFirestore(String downloadUrl, String userId) {
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", downloadUrl);
        db.collection("profileImages").document(userId).set(imageData)
                .addOnSuccessListener(documentReference ->
                                Log.e("FirebaseDB","Image URL saved to Firestore")
                )
                .addOnFailureListener(e ->
                        Log.e("FirebaseDB","Failed to save URL to Firestore: " + e.getMessage())

                );
    }

    /**
     * This method updates the image uris in firebase storage and firestore
     * by deleting the existing image and uploading the replacement
     * All documents modified are named after the user id provided
     */
    public void updateProfilePicture(Uri imageUri, String userId, UpdateProfPicCallBack callBack){
        try {

            // Uses the user id to get a storage reference of our target document/
            // We then use the storage refence to delete the target document
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://luckyevent-22fbd.firebasestorage.app");
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Log.e("FirebaseDB", "updateProfilePicture:" + userId );
            String path = "userProfilePics/" + userId;
            StorageReference storageRef = storage.getReference().child(path);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>(){
                @Override
                public void onSuccess(Void unused) {
                    Log.d("FirebaseDB", "updateProfilePicture: file successfully deleted");
                    // After successfully deleting the document from firebase storage
                    // We then upload the replacement into firebase storage
                    storageRef.putStream(inputStream)
                            .addOnSuccessListener(taskSnapshot -> {
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    db.collection("profileImages").document(userId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                                                    saveProfileToFirestore(downloadUrl,userId);
                                                    callBack.onUpdateSuccess();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Firestore", "Error deleting document", e);
                                                    callBack.onUpdateFailure(e.getMessage());
                                                }
                                            });

                                });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirebaseDb", "Error: " + e.getMessage(), e);
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("FirebaseDB", "updateProfilePicture: Deletion failed");
                }
            });
        }
        catch (FileNotFoundException e) {
            Log.e("FirebaseDB", "File not found: " + e.getMessage(), e);
        }
    }
}
