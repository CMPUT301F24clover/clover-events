package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays the fields and buttons needed to sign in as an entrant or administrator. The user also
 * has the option to sign up as entrant, sign in or sign up as organizer or, sign in with their
 * device id
 * @author Seyi
 * @see com.example.luckyevent.UserSession
 * @see FirebaseDB

 * @version 2
 * @since 1
 */
public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private FirebaseDB firebaseDB;
    private FirebaseFirestore db;
    private TextView registerText;
    private TextView organizerText;
    private androidx.appcompat.widget.AppCompatButton signInButton;
    private androidx.appcompat.widget.AppCompatButton signUpButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
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


        signInButton = findViewById(R.id.SignInButton);


        signInButton.setOnClickListener(new View.OnClickListener() {
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
                            //gets the currently signed in user
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
                            Log.d("LoginActivity", "User ID: " + userId);
                            UserSession.getInstance().setUserId(userId);

                            db.collection("loginProfile")
                                    .whereEqualTo("userId", userId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                            String firstName = document.getString("firstName");
                                            Log.d("LoginActivity", "firstName: " + firstName);
                                            UserSession.getInstance().setFirstName(firstName);


                                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                            finish();

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

        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, EntrantSignUpActivity.class);
                startActivity(intent);
                finish(); // Optional
            }

        });

        registerText = findViewById(R.id.RegisterText);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterDeviceActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });

        organizerText = findViewById(R.id.OrganizerText);
        organizerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OrganizerSignInActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });



    }
}


