package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.firebase.FirebaseDB;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private FirebaseDB firebaseDB;
    private androidx.appcompat.widget.AppCompatButton signInButton;
    private androidx.appcompat.widget.AppCompatButton signUpButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        firebaseDB = new FirebaseDB(this);

        signInButton = findViewById(R.id.SignInButton);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    String userInput = username.getText().toString().trim();
                    String passwordInput = password.getText().toString().trim();

                        firebaseDB.signIn(userInput, passwordInput, new FirebaseDB.SignInCallback() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                startActivity(intent);
                                finish(); // Optional
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(LoginActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
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

    }
}


