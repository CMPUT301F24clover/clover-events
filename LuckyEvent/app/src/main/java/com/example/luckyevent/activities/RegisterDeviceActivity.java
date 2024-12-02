package com.example.luckyevent.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays a message informing the user of the option to sign in via device id. Clicking the register device
 * button signs in the user using their device id.
 *
 * @author Seyi
 * @see FirebaseDB
 * @version 1
 * @since 1
 */
public class RegisterDeviceActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton registerButton;
    private FirebaseDB firebaseDB;
    private FirebaseFirestore db;
    private ImageView gobackButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_registration);
        registerButton = findViewById(R.id.RegisterButton);
        gobackButton = findViewById(R.id.previousIcon);
        firebaseDB = new FirebaseDB(this);
        db = FirebaseFirestore.getInstance();

        /*
         * Registers the users device device when clicked. This is done with the help of the deviceSignIn
         * function from the FireBaseDB class. The class uses the device id to sign in if the id is in the database, otherwise
         * a new account is created. Successfully registering the device navigates to the next activity (MenuActivity)
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDB.deviceSignIn(new FirebaseDB.SignInCallback(){
                    @Override
                    public void onSuccess() {

                        String avatarUrl = "https://ui-avatars.com/api/?name=" + "U" + "&size=128&background=0D8ABC&color=fff";

                        Picasso.get()
                                .load(avatarUrl)
                                .into(new com.squareup.picasso.Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        File file = new File(getFilesDir(), "default_profile_image.jpg");
                                        try (FileOutputStream out = new FileOutputStream(file)) {
                                            Uri imageUri = Uri.fromFile(file);
                                            Log.e("EntrantSignUp", "imageUri:" + imageUri);

                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            String userId = firebaseUser.getUid();

                                            FirebaseStorage storage = FirebaseStorage.getInstance("gs://luckyevent-22fbd.firebasestorage.app");
                                            InputStream inputStream = RegisterDeviceActivity.this.getContentResolver().openInputStream(imageUri);

                                            String path = "userProfilePics/" + userId;
                                            StorageReference storageRef = storage.getReference().child(path);

                                            storageRef.putStream(inputStream)
                                                    .addOnSuccessListener(taskSnapshot -> {
                                                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                            Map<String, Object> imageData = new HashMap<>();
                                                            imageData.put("imageUrl", imageUri);
                                                            db.collection("profileImages").document(userId).set(imageData)
                                                                    .addOnSuccessListener(documentReference ->
                                                                            Log.e("FirebaseDB","Image URL saved to Firestore")
                                                                    )
                                                                    .addOnFailureListener(e ->
                                                                            Log.e("FirebaseDB","Failed to save URL to Firestore: " + e.getMessage())

                                                                    );
                                                        });
                                                    });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                                                                    }
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        Log.e("EntrantSignUpActivity", "Failed to download image", e);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    }
                                });

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = firebaseUser.getUid();
                        UserSession.getInstance().setUserId(userId);
                        UserSession.getInstance().setFirstName(null);

                        Intent intent = new Intent(RegisterDeviceActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(RegisterDeviceActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        /*
         * Goes back to the previous activity when clicked (RegisterDeviceActivity)
         */
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterDeviceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
