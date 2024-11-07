package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class OrganizerSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText facilityName;
    private EditText facilityCode;
    private FirebaseDB firebaseDB;
    private ImageView gobackButton;
    private FirebaseFirestore db;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.screen_template_organizer_signup);

        userName = findViewById(R.id.SignUpUsernameInput);
        password = findViewById(R.id.SignUpPasswordInput);
        firstName = findViewById(R.id.SignUpFirstNameInput);
        lastName = findViewById(R.id.SignUpLastNameInput);
        facilityName = findViewById(R.id.SignUpFacilityNameInput);
        facilityCode = findViewById(R.id.SignUpFacilityCodeInput);
        gobackButton = findViewById(R.id.previousIcon);

        firebaseDB = new FirebaseDB(this);

        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String userNameInput = userName.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();
                String firstNameInput = firstName.getText().toString().trim();
                String lastNameInput = lastName.getText().toString().trim();
                String organizerNameInput = facilityName.getText().toString().trim();
                String facilityCodeInput = facilityCode.getText().toString().trim();


                if (userNameInput.isEmpty() || passwordInput.isEmpty()) {
                    if(userNameInput.isEmpty() && passwordInput.isEmpty()){
                        Toast.makeText(OrganizerSignUpActivity.this, "Username field and password field are required", Toast.LENGTH_SHORT).show();
                    }
                    else if(userNameInput.isEmpty()) {
                        Toast.makeText(OrganizerSignUpActivity.this, "Username field is required", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrganizerSignUpActivity.this, "Password field is required", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    firebaseDB.signUp(userNameInput, passwordInput, firstNameInput, lastNameInput, "organizer", organizerNameInput, facilityCodeInput, new FirebaseDB.SignInCallback() {
                        @Override
                        public void onSuccess() {
                            //gets the currently signed in user
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
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


                                            Intent intent = new Intent(OrganizerSignUpActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Log.e("LoginActivity", "Failed to retrieve user profile data.");
                                        }

                                    });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(OrganizerSignUpActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerSignUpActivity.this, OrganizerSignInActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });
    }

}
