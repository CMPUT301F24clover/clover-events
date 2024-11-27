package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.OrganizerSession;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays the fields needed to sign in as an organizer. This activity also gives the user the option
 * to sign up if they are not registered in the system
 * This activity navigates to the MenuActivity if the user successfully signs in as an organizer
 *
 * @author Seyi
 * @see FirebaseDB
 * @see UserSession
 * @version 1
 * @since 1
 */
public class OrganizerSignInActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private FirebaseDB firebaseDB;
    private FirebaseFirestore db;
    private TextView registerText;
    private androidx.appcompat.widget.AppCompatButton signInButton;
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private ImageView gobackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_template_organizer_login);

        db = FirebaseFirestore.getInstance();
        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        firebaseDB = new FirebaseDB(this);

        signInButton = findViewById(R.id.SignInButton);
        gobackButton = findViewById(R.id.previousIcon);

        /*
         *When clicked, this activity collects all the text provided in the field and signs in using FireBaseDB's
         * SignIn function. It navigates to the OrganizerMenuActivity when it has successfully registered
         */
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = username.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (userInput.isEmpty() || passwordInput.isEmpty()) {
                    if (userInput.isEmpty() && passwordInput.isEmpty()) {
                        Toast.makeText(OrganizerSignInActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                    } else if (userInput.isEmpty()) {
                        Toast.makeText(OrganizerSignInActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrganizerSignInActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    firebaseDB.signIn(userInput, passwordInput, new FirebaseDB.SignInCallback() {
                        @Override
                        public void onSuccess() {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
                            Log.d("OrganizerSignInActivity", "User ID: " + userId);

                            // Query loginProfile to check if user is an organizer
                            db.collection("loginProfile")
                                    .document(userId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String role = document.getString("role");
                                                Log.d("OrganizerSignInActivity", "Role: " + role);

                                                if ("organizer".equals(role)) {
                                                    // Set all organizer session data
                                                    //OrganizerSession.getInstance().setUserId(userId);
                                                    OrganizerSession.getInstance().setFacilityCode(document.getString("facilityCode"));
                                                    OrganizerSession.getInstance().setFirstName(document.getString("firstName"));
                                                    OrganizerSession.getInstance().setLastName(document.getString("lastName"));
                                                    OrganizerSession.getInstance().setUserName(document.getString("userName"));
                                                    OrganizerSession.getInstance().setFacilityName(document.getString("organizationName"));
                                                    OrganizerSession.getInstance().setRole(role);

                                                    // Navigate to organizer menu
                                                    Intent intent = new Intent(OrganizerSignInActivity.this, OrganizerMenuActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(OrganizerSignInActivity.this, "This account is not registered as an organizer", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth.getInstance().signOut();
                                                }
                                            } else {
                                                Toast.makeText(OrganizerSignInActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        } else {
                                            Log.e("OrganizerSignInActivity", "Failed to retrieve user profile data.");
                                            Toast.makeText(OrganizerSignInActivity.this, "Failed to retrieve user profile", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(OrganizerSignInActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("OrganizerSignInActivity", "Sign-in failed: " + errorMessage);
                        }
                    }, false);
                }
            }
        });

        /*
         *When clicked, this activity navigates to the OrganizerSignUpActivity. The user can sign up
         * as an organizer in that activity
         */
        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerSignInActivity.this, OrganizerSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*
         *When clicked, this activity navigates to the previous activity (LoginActivity)
         */
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerSignInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}