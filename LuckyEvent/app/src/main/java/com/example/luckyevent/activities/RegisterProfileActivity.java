package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;

public class RegisterProfileActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button registerButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private String TAG = "RegisterProfile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.RegisterButton);

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, null);
        Log.d(TAG, "Activity created and ProfileController initialized");


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phoneNumber = phoneEditText.getText().toString();
                Log.d(TAG, "Register button clicked. Name: " + name + ", Email: " + email + ", Phone: " + phoneNumber);

                if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                    Log.e(TAG, "Error: One or more fields are empty.");
                    return;
                }

                String[] firstLastName = name.split(" ");
                String firstName = firstLastName[0];
                String lastName = firstLastName[1];
                Log.d(TAG, "Extracted First Name: " + firstName + ", Last Name: " + lastName);


                profileController.registerProfile(firstName,lastName,email,phoneNumber,documentID ->{
                    Log.d(TAG, "Profile registered with document ID: " + documentID);
                    Intent intent = new Intent(RegisterProfileActivity.this,ViewProfileActivity.class);
                    intent.putExtra("profileID",documentID);
                    startActivity(intent);
                });
            }
        });


    }
}
