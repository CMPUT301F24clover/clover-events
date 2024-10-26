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

public class EntrantSignUpActivity extends AppCompatActivity {
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private EditText userName;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private FirebaseDB firebaseDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDB = new FirebaseDB(this);
        setContentView(R.layout.screen_template_entrant_signup);
        userName = findViewById(R.id.SignUpUsernameInput);
        password = findViewById(R.id.SignUpPasswordInput);
        firstName = findViewById(R.id.SignUpFirstNameInput);
        lastName = findViewById(R.id.SignUpLastNameInput);

        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    String userNameInput = userName.getText().toString().trim();
                    String passwordInput = password.getText().toString().trim();
                    String firstNameInput = firstName.getText().toString().trim();
                    String lastNameInput = lastName.getText().toString().trim();

                    firebaseDB.signUp(userNameInput, passwordInput, firstNameInput, lastNameInput, "entrant", null, null, new FirebaseDB.SignInCallback() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(EntrantSignUpActivity.this, HomePageActivity.class);
                            startActivity(intent);
                            finish(); // Optional
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(EntrantSignUpActivity.this, "Sign-Up failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }





}
