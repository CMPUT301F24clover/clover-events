package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView nameText, emailText, phoneText;
    private Button editButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        nameText = findViewById(R.id.nameField);
        emailText = findViewById(R.id.emailField);
        phoneText = findViewById(R.id.phoneField);
        editButton = findViewById(R.id.editButton);

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, null);
        String documentID = getIntent().getStringExtra("profileID");

        profileController.loadProfile(documentID, profile ->{
            nameText.setText(profile.getName());
            emailText.setText(profile.getEmail());
            phoneText.setText(profile.getPhoneNumber());
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("profileID",documentID);
                startActivity(intent);
            }
        });

    }
}
