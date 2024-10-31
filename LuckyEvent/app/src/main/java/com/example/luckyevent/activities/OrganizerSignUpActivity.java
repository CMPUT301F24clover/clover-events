package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrganizerSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText organizerName;
    private EditText facilityCode;
    private FirebaseDB firebaseDB;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_template_organizer_signup);

        userName = findViewById(R.id.SignUpUsernameInput);
        password = findViewById(R.id.SignUpPasswordInput);
        firstName = findViewById(R.id.SignUpFirstNameInput);
        lastName = findViewById(R.id.SignUpLastNameInput);
        organizerName = findViewById(R.id.SignUpOrganizationNameInput);
        facilityCode = findViewById(R.id.SignUpFacilityCodeInput);

        firebaseDB = new FirebaseDB(this);

        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String userNameInput = userName.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();
                String firstNameInput = firstName.getText().toString().trim();
                String lastNameInput = lastName.getText().toString().trim();
                String organizerNameInput = organizerName.getText().toString().trim();
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

                            Intent intent = new Intent(OrganizerSignUpActivity.this, HomePageActivity.class);
                            startActivity(intent);
                            finish(); // Optional
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(OrganizerSignUpActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

}
