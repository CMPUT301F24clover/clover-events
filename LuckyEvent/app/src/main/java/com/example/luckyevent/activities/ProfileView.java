package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;
import com.google.android.gms.tasks.OnSuccessListener;

public class ProfileView extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private Button registerButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.RegisterButton);
        ProfileSetup profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);

    }

}
