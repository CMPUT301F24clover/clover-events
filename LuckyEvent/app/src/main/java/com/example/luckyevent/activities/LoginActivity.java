package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays the fields and buttons needed to sign in as an entrant or administrator. The user also
 * has the option to sign up as entrant, sign in or sign up as organizer or, sign in with their
 * device id
 *
 * @author Seyi
 * @see UserSession
 * @see FirebaseDB

 * @version 2
 * @since 1
 */
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText username;
    private TextInputEditText password;
    private FirebaseDB firebaseDB;
    private FirebaseFirestore db;
    private TextView registerText;
    private TextView organizerText;
    private androidx.appcompat.widget.AppCompatButton signInButton;
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private androidx.appcompat.widget.AppCompatButton organizerSignInButton;
    private androidx.appcompat.widget.AppCompatButton continueAsGuestButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.main_login_new);
        username = findViewById(R.id.username_editText);
        password = findViewById(R.id.password_editText);
        firebaseDB = new FirebaseDB(this);

        /* commented this out since the log out button isn't implemented
        firebaseDB.autoSignin(new FirebaseDB.SignInCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        */


        signInButton = findViewById(R.id.sign_in_button);


        signInButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When clicked, the activity uses FireBaseDB's SignIn function to sign in the user using the
             * fields provided. It navigates to the MenuActivity if it is successful in signing the user
             */
            @Override
            public void onClick(View view) {
                    String userInput = username.getText().toString().trim();
                    String passwordInput = password.getText().toString().trim();


                if (userInput.isEmpty() || passwordInput.isEmpty()) {
                    if (userInput.isEmpty() && passwordInput.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                    } else if (userInput.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    firebaseDB.signIn(userInput, passwordInput, new FirebaseDB.SignInCallback() {
                        @Override
                        public void onSuccess() {
                            // Gets the user id of the currently signed in user and stores it for future reference
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
                            Log.d("LoginActivity", "User ID: " + userId);
                            UserSession.getInstance().setUserId(userId);

                            db.collection("loginProfile")
                                    .whereEqualTo("userId", userId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            // Gets the user details out of the target document and stores it for future refenrce
                                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                            String firstName = document.getString("firstName");
                                            String userName = document.getString("userName");
                                            String role  = document.getString("role");
                                            Boolean notificationsDisabled = document.getBoolean("notificationsDisabled");

                                            // Handles the accounts that have been created before the 
                                            if (notificationsDisabled == null){
                                                notificationsDisabled = false;
                                            }

                                            Log.d("LoginActivity", "firstName: " + firstName);
                                            UserSession.getInstance().setFirstName(firstName);
                                            UserSession.getInstance().setUserName(userName);
                                            UserSession.getInstance().setNotificationDisabled(notificationsDisabled);

                                            // Retrieves the user's profile picture if the user is an entrant
                                            if (role.equals("entrant")) {
                                                db.collection("profileImages")
                                                        .document(document.getString("userId"))
                                                        .get()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful() && task1.getResult() != null) {
                                                                // Retrieves the image url from the target document and stores it for future refence
                                                                DocumentSnapshot imageDocument = task1.getResult();
                                                                String imageUrl = imageDocument.getString("imageUrl");
                                                                UserSession.getInstance().setProfileUri(imageUrl);
                                                            } else {
                                                                Log.e("LoginActivity", "User does not have a profile pictue");
                                                            }
                                                        });


                                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            else if(role.equals("admin")){
                                                Intent intent = new Intent(LoginActivity.this, AdminMenuActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                        } else {
                                            Log.e("LoginActivity", "Failed to retrieve user profile data.");
                                        }

                                    });

                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(LoginActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "Sign-in failed: " + errorMessage);
                        }
                    }, true);
                }

            }
        });


        signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            /**
             * Navigates to the EntrantSignUp Activity when clicked
             */
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, EntrantSignUpActivity.class);
                startActivity(intent);
                finish(); // Optional
            }

        });

        continueAsGuestButton = findViewById(R.id.continue_as_guest_button);
        continueAsGuestButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates to the RegisterDeviceActivity when clicked
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterDeviceActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });

        organizerSignInButton = findViewById(R.id.sign_in_as_organizer_button);
        organizerSignInButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates to the OrganizerSignInActivity when clicked
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OrganizerSignInActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });



    }
}


