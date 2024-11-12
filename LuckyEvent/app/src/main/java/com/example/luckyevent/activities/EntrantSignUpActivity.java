package com.example.luckyevent.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EntrantSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private FirebaseDB firebaseDB;
    private ImageView gobackButton;
    private ImageView profileImage;
    private static final int selectAmount = 1;
    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDB = new FirebaseDB(this);
        setContentView(R.layout.screen_template_entrant_signup);
        userName = findViewById(R.id.SignUpUsernameInput);
        password = findViewById(R.id.SignUpPasswordInput);
        firstName = findViewById(R.id.SignUpFirstNameInput);
        lastName = findViewById(R.id.SignUpLastNameInput);
        gobackButton = findViewById(R.id.previousIcon);
        profileImage = findViewById(R.id.profile_image);

        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(view -> {
            String userNameInput = userName.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String firstNameInput = firstName.getText().toString().trim();
            String lastNameInput = lastName.getText().toString().trim();
            String initials = lastNameInput.isEmpty() ? "" : String.valueOf(lastNameInput.charAt(0));

            if (userNameInput.isEmpty() || passwordInput.isEmpty()) {
                String message = userNameInput.isEmpty() && passwordInput.isEmpty() ?
                        "Username field and password field are required" :
                        (userNameInput.isEmpty() ? "Username field is required" : "Password field is required");

                showToast(message);
            } else {
                if (imageUri == null) {
                    generateDefaultProfileImage(initials, new ProfileImageCallback() {
                        @Override
                        public void onImageGenerated(Uri generatedUri) {
                            firebaseDB.uploadProfileToFirebase(generatedUri, userNameInput, new FirebaseDB.UploadCallback() {
                                @Override
                                public void onUploadSuccess() {
                                    Log.e("EntrantSignUp", "Upload successful. Proceeding to sign up.");
                                    signUpUser(userNameInput, passwordInput, firstNameInput, lastNameInput, generatedUri);
                                }

                                @Override
                                public void onUploadFailure(String errorMessage) {
                                    showToast("Failed to upload profile image: " + errorMessage);
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            showToast("Failed to generate profile image");
                        }
                    });
                } else {
                    Log.e("EntrantSignUp", "imageUri:" + imageUri);
                    firebaseDB.uploadProfileToFirebase(imageUri, userNameInput, new FirebaseDB.UploadCallback() {
                        @Override
                        public void onUploadSuccess() {
                            signUpUser(userNameInput, passwordInput, firstNameInput, lastNameInput, imageUri);
                        }

                        @Override
                        public void onUploadFailure(String errorMessage) {
                            showToast("Failed to upload profile image: " + errorMessage);
                        }
                    });
                }

            }
        });

        gobackButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantSignUpActivity.this, LoginActivity.class));
            finish();
        });

        FloatingActionButton addImageButton = findViewById(R.id.button_add_image);
        addImageButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), selectAmount);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selectAmount && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImage);
        }
    }

    public void generateDefaultProfileImage(String initials, ProfileImageCallback callback) {
        String avatarUrl = "https://ui-avatars.com/api/?name=" + initials + "&size=128&background=0D8ABC&color=fff";

        Picasso.get()
                .load(avatarUrl)
                .into(new com.squareup.picasso.Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        File file = new File(getFilesDir(), "default_profile_image.jpg");
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Uri imageUri = Uri.fromFile(file);
                            Log.e("EntrantSignUp", "imageUri:" + imageUri);
                            callback.onImageGenerated(imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.e("EntrantSignUpActivity", "Failed to download image", e);
                        callback.onFailure();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
    }

    private void signUpUser(String userName, String password, String firstName, String lastName, Uri profileImageUri) {
        firebaseDB.signUp(userName, password, firstName, lastName, "entrant", null, null, new FirebaseDB.SignInCallback() {
            @Override
            public void onSuccess() {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                UserSession.getInstance().setUserId(firebaseUser.getUid());
                UserSession.getInstance().setFirstName(firstName);

                startActivity(new Intent(EntrantSignUpActivity.this, MenuActivity.class));
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                showToast("Sign-Up failed: " + errorMessage);
            }
        });
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(EntrantSignUpActivity.this, message, Toast.LENGTH_SHORT).show()
        );
    }

    public interface ProfileImageCallback {
        void onImageGenerated(Uri imageUri);
        void onFailure();
    }


}
