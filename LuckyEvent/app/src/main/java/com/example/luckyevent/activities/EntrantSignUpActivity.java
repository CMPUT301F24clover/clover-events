package com.example.luckyevent.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This activity displays the ui elements needed for the registration of entrants into the system
 * A profile picture is generated alongside the user's details, if profile picture is not provided
 * The user's details and profile picture is stored via firestore and firebase storage
 *
 * @author Seyi
 * @see FirebaseDB,UserSession
 * @version 2
 * @since 1
 */
public class EntrantSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private TextView goBackText;
    private FirebaseDB firebaseDB;
    private ImageView gobackButton;
    private ImageView profileImage;
    private static final int selectAmount = 1;
    private Uri imageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the firebaseDB variable
        firebaseDB = new FirebaseDB(this);

        // Initialize the variables representing the ui elements
        setContentView(R.layout.entrant_signup_new);
        userName = findViewById(R.id.username_editText);
        password = findViewById(R.id.password_editText);
        firstName = findViewById(R.id.firstname_editText);
        lastName = findViewById(R.id.lastname_editText);
        gobackButton = findViewById(R.id.previous_icon);
        profileImage = findViewById(R.id.profile_image);
        goBackText = findViewById(R.id.previous_textView);

        signUpButton = findViewById(R.id.create_account_button);

        // When clicked, check if the necessary fields filled and sign in
        signUpButton.setOnClickListener(view -> {
            String userNameInput = userName.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String firstNameInput = firstName.getText().toString().trim();
            String lastNameInput = lastName.getText().toString().trim();

            if (userNameInput.isEmpty() || passwordInput.isEmpty()) {
                String message = userNameInput.isEmpty() && passwordInput.isEmpty() ?
                        "Username field and password field are required" :
                        (userNameInput.isEmpty() ? "Username field is required" : "Password field is required");

                showToast(message);
            } else {
                String initials = String.valueOf(userNameInput.charAt(0));
                signUpUser(userNameInput,passwordInput, firstNameInput,lastNameInput,initials);
            }
        });

        // When clicked go back to the previous activity (LoginActivity)
        gobackButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantSignUpActivity.this, LoginActivity.class));
            finish();
        });

        goBackText.setOnClickListener(v -> {
            startActivity(new Intent(EntrantSignUpActivity.this, LoginActivity.class));
            finish();
        });

        // When clicked prompt the user to select
        ImageView addImageButton = findViewById(R.id.button_add_image);
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

    /**
    * This method generates a profile image using a the initials of user
    * This was done with the help of the picasso api
    * @param initials This a string that contains the first letter of the users username
     * @param callback This is a callback that is used to inform other methods if the generate default image was successful
    */
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

    /**
     * This method signs up the entrant by storing all the provided info into firestore and firebase storage
     * FirebaseAuth is also utilized in the account creation process
     * The user is redirected to the homepage if the sign up process was successful
     * @param userName This string contains the username of the user
     * @param password This sting contains the password inputted by the user
     * @param firstName This string contains the user's firstname
     * @param lastName This string contains the user's lastname
     * @param initials This string contains the first letter of the user's username
     */
    private void signUpUser(String userName, String password, String firstName, String lastName, String initials) {
        firebaseDB.signUp(userName, password, firstName, lastName, "entrant", null, null, new FirebaseDB.SignInCallback() {
            @Override
            public void onSuccess() {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                // Stores the user's info into an instance of the user session class
                //
                UserSession.getInstance().setUserId(firebaseUser.getUid());
                UserSession.getInstance().setFirstName(firstName);
                UserSession.getInstance().setUserName(userName);

                if (imageUri == null) {
                    // Generates a default profile image if the user is unable to provide one
                    generateDefaultProfileImage(initials, new ProfileImageCallback() {
                        @Override
                        public void onImageGenerated(Uri generatedUri) {
                            firebaseDB.uploadProfileToFirebase(generatedUri,firebaseUser.getUid(), new FirebaseDB.UploadCallback() {
                                @Override
                                public void onUploadSuccess() {
                                    Log.e("EntrantSignUp", "Upload successful. Proceeding to sign up.");
                                    UserSession.getInstance().setProfileUri(generatedUri.toString());

                                    // Handles the case where the user is an admin
                                    if (userName.toLowerCase().contains("admin".toLowerCase())) {
                                        startActivity(new Intent(EntrantSignUpActivity.this, AdminMenuActivity.class));
                                        finish();
                                    }
                                    else {
                                        UserSession.getInstance().setNotificationDisabled(false);
                                        startActivity(new Intent(EntrantSignUpActivity.this, MenuActivity.class));
                                        finish();
                                    }

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
                    // Uses the provide profile picture to sign up the user
                    Log.e("EntrantSignUp", "imageUri:" + imageUri);
                    firebaseDB.uploadProfileToFirebase(imageUri, firebaseUser.getUid(), new FirebaseDB.UploadCallback() {
                        @Override
                        public void onUploadSuccess() {
                            UserSession.getInstance().setProfileUri(imageUri.toString());

                            // Handles the case where the user is an admin
                            if (userName.toLowerCase().contains("admin".toLowerCase())) {
                                startActivity(new Intent(EntrantSignUpActivity.this, AdminMenuActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(EntrantSignUpActivity.this, MenuActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onUploadFailure(String errorMessage) {
                            showToast("Failed to upload profile image: " + errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showToast("Sign-Up failed: " + errorMessage);
            }
        });
    }

    /**
     * This method uses the string provided to show a toast on the current screen
     * @param message This is a string that contains the message that is to be displayed
    * */
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
