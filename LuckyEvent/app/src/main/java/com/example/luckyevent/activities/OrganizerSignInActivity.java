package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.example.luckyevent.UserSession;
import com.example.luckyevent.firebase.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrganizerSignInActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private FirebaseDB firebaseDB;
    private TextView registerText;
    private androidx.appcompat.widget.AppCompatButton signInButton;
    private androidx.appcompat.widget.AppCompatButton signUpButton;
    private ImageView gobackButton;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_template_organizer_login);

        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);

        firebaseDB = new FirebaseDB(this);

        signInButton = findViewById(R.id.SignInButton);
        gobackButton = findViewById(R.id.previousIcon);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userInput = username.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                firebaseDB.signIn(userInput, passwordInput, new FirebaseDB.SignInCallback() {
                    @Override
                    public void onSuccess() {
                        //gets the currently signed in user
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = firebaseUser.getUid();
                        UserSession.getInstance().setUserId(userId);

                        Intent intent = new Intent(OrganizerSignInActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish(); // Optional
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(OrganizerSignInActivity.this, "Sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                },false);

            }
        });

        signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerSignInActivity.this, OrganizerSignUpActivity.class);
                startActivity(intent);
                finish(); // Optional
            }

        });
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerSignInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional
            }
        });

    }
}
