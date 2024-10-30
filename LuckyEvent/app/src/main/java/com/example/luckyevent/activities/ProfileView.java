package com.example.luckyevent.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luckyevent.R;

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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerProfile();
            }
        });
    }
    private void registerProfile(){
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        String[] firstLastName = name.split(" ");
        String firstName = firstLastName[0];
        String lastName = firstLastName[1];

        profileController.registerProfile(firstName,lastName,email,phoneNumber);

    }
    public void loadProfileItems(ProfileSetup profile){
        nameEditText.setText(profile.getName());
        emailEditText.setText(profile.getEmail());
        phoneNumberEditText.setText(profile.getPhoneNumber());
    }
}
