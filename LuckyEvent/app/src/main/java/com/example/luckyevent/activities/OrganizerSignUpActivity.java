package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.firebase.FirebaseDB;

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

                firebaseDB.signUp(userNameInput, passwordInput, firstNameInput, lastNameInput, "organizer", organizerNameInput, facilityCodeInput, new FirebaseDB.SignInCallback() {
                    @Override
                    public void onSuccess() {
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
        });
    }

}
