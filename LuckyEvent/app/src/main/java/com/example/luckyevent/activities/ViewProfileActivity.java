package com.example.luckyevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView nameText, emailText, phoneText;
    private Button editButton;
    private ImageButton backButton;
    private ProfileController profileController;
    private ProfileSetup profileSetup;
    private Button logOutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        nameText = findViewById(R.id.nameField);
        emailText = findViewById(R.id.emailField);
        phoneText = findViewById(R.id.phoneField);
        editButton = findViewById(R.id.editButton);
        backButton = findViewById(R.id.imageButton);
        logOutButton = findViewById(R.id.button3);

        profileSetup = new ProfileSetup();
        profileController = new ProfileController(profileSetup, this);
        String documentID = getIntent().getStringExtra("profileID");

        profileController.loadProfile(documentID, profile ->{
            if (profile != null) {
                nameText.setText(profile.getName());
                emailText.setText(profile.getEmail());
                phoneText.setText(profile.getPhoneNumber());
                Toast.makeText(ViewProfileActivity.this,"Profile loaded",Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(ViewProfileActivity.this,"Unable to load profile",Toast.LENGTH_SHORT).show();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("profileID",documentID);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
